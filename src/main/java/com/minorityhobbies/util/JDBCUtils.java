/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class JDBCUtils {
	private static final Logger LOGGER = Logger.getLogger(JDBCUtils.class
			.getName());

	private JDBCUtils() {
	}

	private static final class JdbcConnectionDetails {
		private final String jdbcDriverClassName;
		private final String jdbcUrl;
		private final String jdbcUsername;
		private final String jdbcPassword;

		public JdbcConnectionDetails(Properties props) {
			jdbcDriverClassName = props.getProperty("jdbc.driverClassName");
			jdbcUrl = props.getProperty("jdbc.url");
			jdbcUsername = props.getProperty("jdbc.username");
			jdbcPassword = props.getProperty("jdbc.password");
		}

		public String getJdbcDriverClassName() {
			return jdbcDriverClassName;
		}

		public String getJdbcUrl() {
			return jdbcUrl;
		}

		public String getJdbcUsername() {
			return jdbcUsername;
		}

		public String getJdbcPassword() {
			return jdbcPassword;
		}
	}

	public static abstract class SQLResultSetMapper<T> implements
			Closure<ResultSet, T> {
		@Override
		public final T invoke(ResultSet val) {
			try {
				return map(val);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		protected abstract T map(ResultSet val) throws SQLException;
	}

	/**
	 * Executes all scripts in the specified directory in file listing order.
	 * Uses 'go' preceded and proceeded by a newline as the statement separator.
	 * 
	 * @param dir
	 *            The directory to use (can be classpath relative).
	 * @param props
	 *            The database connection properties. This must contain the
	 *            following: <code>jdbc.url</code>
	 *            <code>jdbc.driverClassName</code> <code>jdbc.username</code>
	 *            <code>jdbc.password</code>
	 * @throws SQLException
	 */
	public static void executeScripts(String dir, Properties props)
			throws IOException, SQLException {
		File path = ClasspathUtils.getClasspathRelativePath(dir);
		Connection connection = null;
		try {
			connection = createConnection(props);
			for (File sqlFile : path.listFiles()) {
				if (sqlFile.isDirectory()) {
					continue;
				}

				Statement statement = null;
				BufferedReader reader = null;
				try {
					statement = connection.createStatement();

					reader = new BufferedReader(new FileReader(
							sqlFile));
					StringBuilder stmt = new StringBuilder();
					for (String line = null; (line = reader.readLine()) != null;) {
						if (line.trim().equalsIgnoreCase("go")) {
							statement.addBatch(stmt.toString());
							stmt = new StringBuilder();
						} else {
							stmt.append(line);
							stmt.append("\n");
						}
					}

					int[] updates = statement.executeBatch();
					int count = 1;
					for (int i : updates) {
						LOGGER.info(String.format(
								"%s: %d / %d: %d row(s) updated",
								sqlFile.getName(), count++, updates.length, i));
					}
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	public static Connection createConnection(Properties props)
			throws SQLException {
		JdbcConnectionDetails connDetails = new JdbcConnectionDetails(props);
		try {
			Class.forName(connDetails.getJdbcDriverClassName());
		} catch (ClassNotFoundException e) {
			throw new SQLException(String.format("Driver not found: %s",
					connDetails.getJdbcDriverClassName()));
		}
		Properties cprops = new Properties();
		cprops.setProperty("user", connDetails.getJdbcUsername());
		cprops.setProperty("password", connDetails.getJdbcPassword());

		return DriverManager.getConnection(connDetails.getJdbcUrl(), cprops);
	}

	public static <T> T executeUniqueQuery(Connection conn, String sql, Closure<ResultSet, T> mapper, Object... params) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i+1, params[i]);
			}

			ResultSet rs = null;
			try {
				rs = stmt.executeQuery();
				T result = null;
				while (rs.next()) {
					result = mapper.invoke(rs);
				}
				if (rs.next()) {
					throw new IllegalStateException("Expected result size = 1");
				}
				return result;
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> List<T> executeQuery(Connection conn, String sql,
			Closure<ResultSet, T> mapper, Object... params) {
		List<T> results = new LinkedList<T>();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i+1, params[i]);
			}
			
			ResultSet rs = null;
			try {
				rs = stmt.executeQuery();
				while (rs.next()) {
					results.add(mapper.invoke(rs));
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return results;
	}

	public static int executeSingleUpdate(Connection conn, String sql, Object... params) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i+1, params[i]);
			}
			
			try {
				return stmt.executeUpdate();
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static int[] executeMultipleUpdates(Connection conn, List<String> sql) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			try {
				for (String statement : sql) {
					stmt.addBatch(statement);
				}
				return stmt.executeBatch();
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean execute(Connection conn, String sql) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			try {
				return stmt.execute(sql);
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String convertDate(Calendar date) {
		return convertDate(date.getTime());
	}

	public static String convertDate(Date date) {
		return String.format("%1$tY-%1$tm-%1$td", date);
	}

	public static String convertDateTime(Calendar date) {
		return convertDateTime(date.getTime());
	}

	public static String convertDateTime(Date date) {
		return String.format("%1$tY-%1$tm-%1$td %1$TH:%1$TM:%1$TS", date);
	}
	
	public static String convertBoolean(boolean val) {
		return val ? "1" : "0";
	}
	
	public static <T> SQLResultSetMapper<T> newSingleResultMapper() {
		return new SQLResultSetMapper<T>() {
			@Override
			protected T map(ResultSet val) throws SQLException {
				@SuppressWarnings("unchecked")
				T result = (T) val.getObject(1);
				return result;
			}
		};
	}
	
	@FunctionalInterface
	public interface JdbcResultSetMapper<T> extends Function<ResultSet, T> {
	}
	
	public static <T> List<T> queryForList(DataSource dataSource, String sql,
			JdbcResultSetMapper<T> mapper) throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<T> results = new LinkedList<>();
					while (rs.next()) {
						mapper.apply(rs);
					}
					return results;
				}
			}
		}
	}
}

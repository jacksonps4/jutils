package com.minorityhobbies.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class URLUtils {
	private URLUtils() {
	}

	/**
	 * Useful for other classes which require a {@link URL} as a parameter. This
	 * method allows a URL to be generated from a String.
	 * 
	 * @param urlFilename
	 *            The filename to use for this URL. Can be anything.
	 * @param content
	 *            The payload for this URL. This will be returned when you call
	 *            url.openStream() and read the data.
	 * @return The URL backed by the specified content with the specified name.
	 */
	public static URL getStringBackedUrl(String urlFilename, String content) {
		return getStreamBackedUrl(urlFilename,
				new ByteArrayInputStream(content.getBytes()));
	}

	public static URL getStreamBackedUrl(String urlFilename,
			final InputStream content) {
		try {
			return new URL("bytes", "", 0, urlFilename, new URLStreamHandler() {
				@Override
				protected URLConnection openConnection(final URL url)
						throws IOException {
					return new URLConnection(url) {
						@Override
						public void connect() throws IOException {
						}

						@Override
						public InputStream getInputStream() throws IOException {
							return content;
						}
					};
				}
			});
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readUrlData(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
		if (in == null) {
			return null;
		}
		byte[] b = new byte[1024 * 64];
		StringBuilder data = new StringBuilder();
		for (int read = 0; (read = in.read(b)) > -1;) {
			data.append(new String(b, 0, read));
		}
		return data.toString();
	}
}

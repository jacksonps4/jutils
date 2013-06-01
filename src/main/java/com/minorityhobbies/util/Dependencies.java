/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.minorityhobbies.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Simple, minimalist dependency injection for Java. Modular design helps you to
 * build testable, extensible and maintainable applications.
 * 
 * An application module represents a subsystem with a defined public API which
 * may be used without any knowledge of the internal implementation of that API.
 * 
 * To create an application module, simply extend this class and create a
 * constructor which takes in all of its required dependencies as parameters. A
 * module can expose dependencies that can be used by other modules simply by
 * implementing a getter method to return that dependency. This gives the module
 * creator complete control over the scope of each dependency.
 * 
 * To bootstrap an application, create an instance of this class passing in any
 * singletons which are required by your modules as well as the module classes.
 * You can then call bootstrap() to instantiate and inject the dependencies into
 * your modules. This will then allow you to retrieve any of your dependencies
 * by calling the resolveDependency() method.
 * 
 * Below is an example of how usage of this class is typically achieved: <code>
 *   // contains application dependencies
 *   public class ApplicationModule extends Dependencies {
 *   	private final DataAccessObject dataModule;
 *   	private final AppServer appServer;
 *   
 *      // requires a DataModule
 *   	public ApplicationModule(DataAccessObject dataModule) {
 *   		this.dataModule = dataModule;
 *   		appServer = createAppServer();
 *    	}
 *    
 *      ...
 *      
 *      public AppServer getAppServer() {
 *      	return appServer;
 *     	}
 *   }
 *   
 *   // contains database dependencies
 *   public class DataModule extends Dependencies {
 *   	private final DataSource dataSource;
 *      private final DataAccessObject dao;
 *      
 *      // requires a DataSource
 *   	public DataModule(DataSource dataSource) {
 *   		this.dataSource = dataSource;
 *   		this.dao = createDao(dataSource);
 *   	}
 *   
 *      public DataAccessObject getDataAccessObject() {
 *      	return dao;
 *     	}
 *     
 *      ...
 *   }
 *   
 *   public class ApplicationLauncher {
 *   	public static void main(String[] args) {
 *   		Dependencies deps = null;
 *   		try {
 *   			DataSource ds = ... // create DataSource
 *   
 *   			// create dependency manager adding singletons and modules
 *   			deps = new Dependencies(ds, DataModule.class, ApplicationModule.class);
 *   			
 *   			// resolve dependencies within all modules
 *   			deps.bootstrap();
 *   
 *   			// do business logic
 *   			AppServer appServer = deps.resolveDependency(AppServer.class);
 *   			appServer.doSomething();
 *   		} finally {
 *   			if (deps != null) {
 *   				try {
 *   					deps.close();
 *   				} catch (IOException e) {
 *   					// log failure
 *   				}
 *   			}
 *   		}
 *   	}
 *   }
 * </code>
 * 
 */
public class Dependencies implements Closeable {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final Map<Class<?>, Object> singletons = new HashMap<Class<?>, Object>();
	private final Map<Class<?>, Method> instanceFinder = new HashMap<Class<?>, Method>();
	private final Class<?>[] moduleTypes;
	private List<Dependencies> rootModules = new LinkedList<Dependencies>();

	public Dependencies(Object[] singletons, Class<?>... modules) {
		super();
		this.moduleTypes = modules;
		for (Object o : singletons) {
			this.singletons.put(o.getClass(), o);
		}
	}

	protected Dependencies() {
		moduleTypes = null;
	}

	/**
	 * Initialises all modules by wiring up dependencies exposed in module
	 * getters to the constructors of other modules to satisfy all dependency
	 * requirements.
	 * 
	 * @throws DependencyException
	 *             If a required dependency type cannot be found or if a module
	 *             constructor throws an exception.
	 */
	public final void bootstrap() throws DependencyException {
		if (moduleTypes == null) {
			return;
		}

		for (Class<?> moduleType : moduleTypes) {
			BeanInfo bi = null;
			try {
				bi = Introspector.getBeanInfo(moduleType);
				PropertyDescriptor[] properties = bi.getPropertyDescriptors();
				for (PropertyDescriptor property : properties) {
					Method getter = property.getReadMethod();
					if (Class.class.equals(property.getPropertyType())) {
						continue;
					}
					if (getter != null) {
						instanceFinder.put(property.getPropertyType(), getter);
						logger.info(String.format(
								"Bound dependency of type %s => module %s",
								property.getPropertyType(), moduleType));
					}
				}
			} catch (IntrospectionException e) {
				throw new DependencyException(e);
			}
		}

		for (Class<?> moduleType : moduleTypes) {
			Constructor<?>[] ctors = moduleType.getConstructors();
			for (Constructor<?> ctor : ctors) {
				Class<?>[] parameterTypes = ctor.getParameterTypes();
				Object[] parameterValues = new Object[parameterTypes.length];
				int parametersFound = 0;
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> parameterType = parameterTypes[i];
					for (Class<?> type : singletons.keySet()) {
						if (parameterType.isAssignableFrom(type)) {
							parameterValues[i] = singletons.get(type);
							logger.info(String
									.format("Injecting dependency of type into constructor %s => %s",
											parameterType, moduleType));
							parametersFound++;
							break;
						}
					}
				}
				if (parametersFound < parameterValues.length) {
					for (int i = parametersFound; i < parameterValues.length; i++) {
						Class<?> requiredType = parameterTypes[i];
						Method getter = instanceFinder.get(requiredType);
						for (Dependencies d : rootModules) {
							if (getter.getDeclaringClass().equals(d.getClass())) {
								try {
									parameterValues[i] = getter.invoke(d);
									logger.info(String
											.format("Injecting dependency of type into constructor %s => %s",
													requiredType, moduleType));
									break;
								} catch (Exception e) {
									throw new DependencyException(e);
								}
							}
						}
					}
				}
				try {
					rootModules.add((Dependencies) ctor
							.newInstance(parameterValues));
				} catch (Exception e) {
					throw new DependencyException(e);
				}
			}
		}
	}

	/**
	 * Calls the close() method on any singletons that implement {@link Closeable}
	 * and then on any modules that implement {@link Closeable}.
	 */
	@Override
	public final void close() {
		for (Object singleton : singletons.values()) {
			if (singleton instanceof Closeable) {
				Closeable c = (Closeable) singleton;
				try {
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		for (Object module : rootModules) {
			if (module instanceof Closeable) {
				Closeable c = (Closeable) module;
				try {
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Finds the specified dependency by firstly searching through the
	 * singletons container, then the getter methods of root modules (those
	 * passed into the constructor of {@link Dependencies}), then the getter
	 * methods of any other modules.
	 * 
	 * @param type
	 *            The type of the required dependency.
	 * @return An instance of the required dependency type.
	 * @throws DependencyException
	 *             If the required dependency is not available or if an
	 *             exception is thrown while attempting to obtain an instance of
	 *             it.
	 */
	@SuppressWarnings("unchecked")
	public final <T> T resolveDependency(Class<T> type)
			throws DependencyException {
		T result = null;
		// try the singletons container first
		if (Object.class.equals(getClass().getSuperclass())) {
			result = (T) singletons.get(type);
		}
		// then the module bindings
		if (result == null) {
			if (rootModules != null) {
				for (Dependencies dep : rootModules) {
					result = findDependency(dep.getClass(), dep, type);
					if (result != null) {
						break;
					}
				}
			}
		}
		// finally the subclass getter methods
		if (result == null) {
			result = findDependency(getClass(), this, type);
		}

		if (result == null) {
			throw new DependencyException(String.format(
					"Dependency of type %s not found", type.getName()));
		}

		return result;
	}

	final <T> T findDependency(Class<?> dependenciesClass, Object instance,
			Class<T> dependencyType) throws DependencyException {
		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(dependenciesClass);
			for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
				if (dependencyType.isAssignableFrom(pd.getPropertyType())) {
					Method getter = pd.getReadMethod();
					@SuppressWarnings("unchecked")
					T dependency = (T) getter.invoke(instance);
					return dependency;
				}
			}

			return null;
		} catch (IntrospectionException e) {
			throw new DependencyException(e);
		} catch (IllegalArgumentException e) {
			throw new DependencyException(e);
		} catch (IllegalAccessException e) {
			throw new DependencyException(e);
		} catch (InvocationTargetException e) {
			throw new DependencyException(e);
		}
	}
}

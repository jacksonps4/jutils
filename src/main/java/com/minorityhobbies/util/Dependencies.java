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
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Simple, minimalist dependency injection for Java. Modular design helps you to
 * build testable, extensible and maintainable applications.
 * 
 * An application module represents a subsystem with a defined public API which
 * may be used without any knowledge of the internal implementation of that API.
 * 
 * To create an application module, simply create a POJO with a constructor
 * which takes in all of its required dependencies as parameters. A module can
 * expose dependencies that can be used by other modules simply by implementing
 * a getter method to return that dependency. This gives the module creator
 * complete control over the scope of each dependency.
 * 
 * To bootstrap an application, create an instance of this class passing in any
 * singletons which are required by your modules as well as the module classes.
 * You can then call bootstrap() to instantiate and inject the dependencies into
 * your modules. This will then allow you to retrieve any of your dependencies
 * by calling the resolveDependency() method.
 * 
 * Below is an example of how usage of this class is typically achieved: <code>
 *   // contains application dependencies
 *   public class ApplicationModule {
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
 *   public class DataModule {
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
	private final Class<?>[] moduleTypes;
	private List<Object> rootModules = new LinkedList<Object>();

	public Dependencies(Object[] singletons) {
		this(singletons, new Class<?>[0]);
	}

	public Dependencies(Class<?>... modules) {
		this(new Object[0], modules);
	}

	public Dependencies(Object[] singletons, Class<?>... modules) {
		super();
		this.moduleTypes = modules;
		for (Object o : singletons) {
			this.singletons.put(o.getClass(), o);
		}
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

		Map<Class<?>, List<Method>> dependencyLocator = new HashMap<Class<?>, List<Method>>();

		// first pass: create a map of all dependencies exposed by modules:
		// type -> getter method
		// this obviously doesn't include anything in singletons or declared as
		// services
		for (Class<?> moduleType : moduleTypes) {
			BeanInfo bi = null;
			try {
				bi = Introspector.getBeanInfo(moduleType);
				PropertyDescriptor[] properties = bi.getPropertyDescriptors();
				for (PropertyDescriptor property : properties) {
					Class<?> propertyType = property.getPropertyType();

					Method getter = property.getReadMethod();
					if (Class.class.equals(propertyType)) {
						continue;
					}
					if (getter != null) {
						List<Method> methods = null;
						if ((methods = dependencyLocator.get(propertyType)) == null) {
							methods = new LinkedList<Method>();
							dependencyLocator.put(propertyType, methods);
						}
						methods.add(getter);

						logger.info(String.format(
								"Found dependency of type %s in module %s",
								property.getPropertyType(), moduleType));
					}
				}
			} catch (IntrospectionException e) {
				throw new DependencyException(e);
			}
		}

		List<Class<?>> modulesToBeConstructed = new LinkedList<Class<?>>();
		for (Class<?> moduleClass : moduleTypes) {
			modulesToBeConstructed.add(moduleClass);
		}

		try {
			// sort the modules by the number of constructor arguments
			Collections.sort(modulesToBeConstructed,
					new Comparator<Class<?>>() {
						@Override
						public int compare(Class<?> o1, Class<?> o2) {
							int c1ParamCount = 0;
							Constructor<?>[] c1 = o1.getConstructors();
							if (c1.length > 1) {
								throw new RuntimeException();
							}
							c1ParamCount = c1[0].getParameterTypes().length;

							int c2ParamCount = 0;
							Constructor<?>[] c2 = o2.getConstructors();
							c2ParamCount = c2[0].getParameterTypes().length;

							return new Integer(c1ParamCount)
									.compareTo(c2ParamCount);
						}
					});
		} catch (RuntimeException e) {
			throw new DependencyException(
					"Modules should have only one constructor.");
		}
		for (int count = 0; modulesToBeConstructed.size() > 0 && count < 10; count++) {
			constructModules(dependencyLocator, modulesToBeConstructed);
			for (Iterator<Class<?>> itr = modulesToBeConstructed.iterator(); itr
					.hasNext();) {
				Class<?> moduleType = itr.next();
				for (Object rootModule : rootModules) {
					if (rootModule.getClass().equals(moduleType)) {
						itr.remove();
						logger.info(String.format("Module %s populated",
								moduleType));
						break;
					}
				}
			}
		}

		if (modulesToBeConstructed.size() > 0) {
			throw new DependencyException(String.format(
					"Failed to resolve dependencies for modules: %s",
					modulesToBeConstructed));
		}
	}

	private void constructModules(
			Map<Class<?>, List<Method>> dependencyLocator,
			List<Class<?>> moduleTypes) throws DependencyException {
		// for all modules, try to match the constructor parameters first to a
		// singleton dependency, then a dependency provided by another module
		// and finally, to the ServiceLoader
		logger.info(String.format("Attempting to populate modules: %s",
				moduleTypes));

		modules: for (Class<?> moduleType : moduleTypes) {
			Constructor<?>[] ctors = moduleType.getConstructors();
			for (Constructor<?> ctor : ctors) {
				Class<?>[] parameterTypes = ctor.getParameterTypes();
				Object[] parameterValues = new Object[parameterTypes.length];
				Annotation[][] parameterAnnotations = ctor
						.getParameterAnnotations();

				// look for matching dependencies in singletons
				int parametersFound = 0;
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> parameterType = parameterTypes[i];
					for (Class<?> type : singletons.keySet()) {
						if (parameterType.isAssignableFrom(type)) {
							parameterValues[i] = singletons.get(type);
							logger.info(String
									.format("Injecting singleton instance '%s' of type into constructor %s => %s",
											parameterValues[i].getClass(),
											parameterType, moduleType));
							parametersFound++;
							break;
						}
					}
				}

				if (parametersFound < parameterValues.length) {
					for (int i = 0; i < parameterValues.length; i++) {
						if (parameterValues[i] != null) {
							continue;
						}
						Class<?> requiredType = parameterTypes[i];
						List<Class<? extends Annotation>> requiredAnnotations = new LinkedList<Class<? extends Annotation>>();
						for (Annotation annotation : parameterAnnotations[i]) {
							requiredAnnotations
									.add(annotation.annotationType());
						}

						// for remaining parameters, check modules for matching
						// dependencies
						List<Method> getters = dependencyLocator
								.get(requiredType);
						if (getters == null) {
							for (Object d : rootModules) {
								if (requiredType.isAssignableFrom(d.getClass())) {
									parameterValues[i] = d;
									logger.info(String
											.format("Injecting single dependency of type '%s' into constructor %s => %s",
													parameterValues[i]
															.getClass(),
													requiredType, moduleType));
									break;
								}
							}
						} else {
							Method getter = null;
							if (requiredAnnotations.size() > 0) {
								// must match getter annotations to
								// constructor annotations
								for (Method m : getters) {
									for (Class<? extends Annotation> annotation : requiredAnnotations) {
										if (m.isAnnotationPresent(annotation)) {
											getter = m;
											break;
										}
									}
									if (getter != null) {
										break;
									}
								}
							} else if (getter == null && getters.size() == 1) {
								getter = getters.get(0);
							} else {
								throw new DependencyException(
										"Cannot find unique dependency"
												+ "to inject of type "
												+ requiredType);
							}

							if (getter == null) {
								// could simply have not reached required
								// dependency yet:
								// try again on next pass
								logger.info(String
										.format("Could not find getter for type %s annotated with %s "
												+ "- will try again on next pass",
												requiredType,
												requiredAnnotations));
								continue modules;
							}

							for (Object d : rootModules) {
								if (getter.getDeclaringClass().equals(
										d.getClass())) {
									try {
										parameterValues[i] = getter.invoke(d);
										String msg = "Injecting dependency of type into constructor %s => %s";
										if (requiredAnnotations.size() > 0) {
											msg = String.format(
													"%s - annotated with [%s]",
													msg, requiredAnnotations);
										}
										logger.info(String.format(msg,
												requiredType, moduleType));
										break;
									} catch (Exception e) {
										throw new DependencyException(e);
									}
								}
							}
						}

						// if there are still parameters that are unmatched, try
						// the ServiceLoader.
						if (parameterValues[i] == null) {
							ServiceLoader<?> serviceLoader = ServiceLoader
									.load(requiredType);
							for (Object instance : serviceLoader) {
								parameterValues[i] = instance;
								logger.info(String
										.format("Injecting dependency from service loader of type '%s' into constructor %s => %s",
												parameterValues[i].getClass(),
												requiredType, moduleType));
								break;
							}
						}
					}
				}

				String msg = null;
				for (int i = 0; i < parameterValues.length; i++) {
					Object parameterValue = parameterValues[i];
					if (parameterValue == null) {
						if (parameterAnnotations[i].length > 0) {
							List<Class<? extends Annotation>> annotationTypes = new LinkedList<Class<? extends Annotation>>();
							for (Annotation annotation : parameterAnnotations[i]) {
								annotationTypes
										.add(annotation.annotationType());
							}
							msg = String
									.format("This run didn't resolve parameter of type %s "
											+ "annotated with %s for constructor %s in module %s",
											parameterTypes[i], annotationTypes,
											ctor, moduleType);
						} else {
							msg = String
									.format("This run didn't resolve parameter of type %s "
											+ "for constructor %s in module %s",
											parameterTypes[i], ctor, moduleType);
						}
						logger.info(msg);
					}
				}

				if (msg == null) {
					try {
						rootModules.add(ctor.newInstance(parameterValues));
					} catch (Exception e) {
						throw new DependencyException(e);
					}
				}
			}
		}
	}

	/**
	 * Calls the close() method on any singletons that implement
	 * {@link Closeable} and then on any modules that implement
	 * {@link Closeable}.
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
	 * methods of any other modules and finally, the service loader.
	 * 
	 * @param type
	 *            The type of the required dependency.
	 * @return An instance of the required dependency type.
	 * @throws DependencyException
	 *             If the required dependency is not available or if an
	 *             exception is thrown while attempting to obtain an instance of
	 *             it.
	 */
	public final <T> T resolveDependency(Class<T> type)
			throws DependencyException {
		return resolveDependency(type, null);
	}

	/**
	 * Finds the specified dependency by firstly searching through the
	 * singletons container, then the getter methods of root modules (those
	 * passed into the constructor of {@link Dependencies}), then the getter
	 * methods of any other modules and finally, the service loader.
	 * 
	 * @param type
	 *            The type of the required dependency.
	 * @param annotation
	 *            The annotation present on the getter of the required
	 *            dependency.
	 * @return An instance of the required dependency type.
	 * @throws DependencyException
	 *             If the required dependency is not available or if an
	 *             exception is thrown while attempting to obtain an instance of
	 *             it.
	 */
	@SuppressWarnings("unchecked")
	public final <T> T resolveDependency(Class<T> type,
			Class<? extends Annotation> annotation) throws DependencyException {
		T result = null;
		// try the singletons container first
		if (Object.class.equals(getClass().getSuperclass())) {
			for (Class<?> singletonType : singletons.keySet()) {
				if (type.isAssignableFrom(singletonType)) {
					if (result == null) {
						result = (T) singletons.get(singletonType);
					} else {
						throw new DependencyException(
								"Unable to find unique singleton of type "
										+ type);
					}
				}
			}
		}
		// then the module bindings
		if (result == null) {
			if (rootModules != null) {
				for (Object dep : rootModules) {
					result = findDependency(dep.getClass(), dep, type,
							annotation);
					if (result != null) {
						break;
					}
				}
			}
		}
		// then the subclass getter methods
		if (result == null) {
			result = findDependency(getClass(), this, type, annotation);
		}

		// finally, the ServiceLoader
		if (result == null) {
			ServiceLoader<T> serviceLoader = ServiceLoader.load(type);
			Iterator<T> itr = serviceLoader.iterator();
			if (itr.hasNext()) {
				result = itr.next();
			}
		}

		if (result == null) {
			throw new DependencyException(String.format(
					"Dependency of type %s not found", type.getName()));
		}

		return result;
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
	public final <T> Collection<T> resolveDependencies(Class<T> type)
			throws DependencyException {
		return resolveDependencies(type, null);
	}

	/**
	 * Finds the specified dependency by firstly searching through the
	 * singletons container, then the getter methods of root modules (those
	 * passed into the constructor of {@link Dependencies}), then the getter
	 * methods of any other modules.
	 * 
	 * @param type
	 *            The type of the required dependency.
	 * @param annotation
	 *            The annotation present on the method exposing the required
	 *            dependency.
	 * @return An instance of the required dependency type.
	 * @throws DependencyException
	 *             If the required dependency is not available or if an
	 *             exception is thrown while attempting to obtain an instance of
	 *             it.
	 */
	@SuppressWarnings("unchecked")
	public final <T> Collection<T> resolveDependencies(Class<T> type,
			Class<? extends Annotation> annotation) throws DependencyException {
		List<T> results = new LinkedList<T>();
		// try the singletons container first
		if (Object.class.equals(getClass().getSuperclass())) {
			T result = (T) singletons.get(type);
			if (result != null) {
				results.add(result);
			}
		}

		// then the module bindings
		if (rootModules != null) {
			for (Object dep : rootModules) {
				T result = findDependency(dep.getClass(), dep, type, annotation);
				if (result != null) {
					results.add(result);
				}
			}
		}

		// finally the subclass getter methods
		T result = findDependency(getClass(), this, type, annotation);
		if (result != null) {
			results.add(result);
		}

		return results;
	}

	final <T> T findDependency(Class<?> dependenciesClass, Object instance,
			Class<T> dependencyType, Class<? extends Annotation> annotation)
			throws DependencyException {
		Collection<T> deps = findDependencies(dependenciesClass, instance,
				dependencyType, annotation);
		if (deps.size() > 1) {
			throw new DependencyException(
					String.format("More than one dependency of type %s was found. "
							+ "Please annotate the getter to select the appropriate instance."));
		}
		Iterator<T> itr = deps.iterator();
		if (itr.hasNext()) {
			return itr.next();
		}
		return null;
	}

	final <T> Collection<T> findDependencies(Class<?> dependenciesClass,
			Object instance, Class<T> dependencyType,
			Class<? extends Annotation> annotation) throws DependencyException {
		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(dependenciesClass);
			List<T> deps = new LinkedList<T>();
			for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
				if (dependencyType.isAssignableFrom(pd.getPropertyType())) {
					Method getter = pd.getReadMethod();
					if (annotation != null
							&& getter.isAnnotationPresent(annotation)
							|| annotation == null) {
						@SuppressWarnings("unchecked")
						T dependency = (T) getter.invoke(instance);
						deps.add(dependency);
					}
				}
			}

			return deps;
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

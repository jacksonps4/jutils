/*
Copyright (c) 2014 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * When deploying against an application server or testing code that will run
 * in a different target environment, it is often useful to be able to set up
 * a {@link Context} to test against. This is an in-memory context which 
 * does not support all of the standard {@link Context} methods but does 
 * support Context.bind and Context.lookup without any of the hierarchical
 * searching.
 * 
 * To use this factory, either include a <code>jndi.properties</code> 
 * file on the classpath containing the property 
 * <code>java.naming.factory.initial</code> or set the same as a System 
 * property.
 * 
 * This is not thread-safe.
 */
public class BasicInitialContextFactory implements InitialContextFactory {
	private static final Map<Object, Object> environment = new HashMap<>();
	private static final Context context = new BasicInitialContext(environment);

	@Override
	public Context getInitialContext(Hashtable<?, ?> env)
			throws NamingException {
		synchronized (BasicInitialContextFactory.class) {
			for (Map.Entry<?, ?> entry : env.entrySet()) {
				if (!environment.containsKey(entry.getKey())) {
					environment.put(entry.getKey(), entry.getValue());
				}
			}
			return context;
		}
	}
}

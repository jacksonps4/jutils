/*
Copyright (c) 2014 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class BasicInitialContext implements Context {
	private final Map<Object, Object> env;
	private final Map<String, Object> bindings = new HashMap<>();
	
	public BasicInitialContext(Map<?, ?> env) {
		super();
		this.env = new HashMap<>();
		for (Map.Entry<?, ?> entry : env.entrySet()) {
			this.env.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Object addToEnvironment(String key, Object value)
			throws NamingException {
		return env.put(key, value);
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		bind(name.toString(), obj);
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		bindings.put(name, obj);
	}

	@Override
	public void close() throws NamingException {
	}

	@Override
	public Name composeName(Name arg0, Name arg1) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String composeName(String arg0, String arg1) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context createSubcontext(Name arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context createSubcontext(String arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroySubcontext(Name arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroySubcontext(String arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		return new Hashtable<Object, Object>(env);
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameParser getNameParser(final Name nm) throws NamingException {
		return new NameParser() {
			@Override
			public Name parse(String name) throws NamingException {
				return nm;
			}
		};
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		return new NameParser() {
			@Override
			public Name parse(String name) throws NamingException {
				return new CompositeName(name);
			}
		};
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name arg0)
			throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String arg0)
			throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		return listBindings(name.toString());
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		Collection<Binding> bindingValues = new LinkedList<>();
		Object value = bindings.get(name);
		if (value == null && name.trim().endsWith("/")) {
			for (Map.Entry<String, Object> entry : bindings.entrySet()) {
				if (entry.getKey().startsWith(name)) {
					bindingValues.add(new Binding(name, entry.getValue()));
				}
			}
		}
		bindingValues.add(new Binding(name, value));
		return new CollectionEnumeration<>(bindingValues);
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
	}

	@Override
	public Object lookup(String name) throws NamingException {
		return bindings.get(name);
	}

	@Override
	public Object lookupLink(Name arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object lookupLink(String arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rebind(Name arg0, Object arg1) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rebind(String arg0, Object arg1) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object removeFromEnvironment(String arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(Name arg0, Name arg1) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(String arg0, String arg1) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbind(Name arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbind(String arg0) throws NamingException {
		throw new UnsupportedOperationException();
	}
}

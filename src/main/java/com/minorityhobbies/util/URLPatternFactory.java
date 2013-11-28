package com.minorityhobbies.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple bulk URL generator from a pattern.
 * 
 * This enables a number of URLs to be generated from a basic pattern and
 * the appropriate parameters.
 * 
 * Simple usage example:
 * 
 * <code>
	URLPatternFactory upf = new URLPatternFactory("http://www.java.com/data-%n", Arrays.asList(1, 2, 3));
	for (URL url : upf) {
		....
	}
 * </code>
 */
public class URLPatternFactory implements Iterable<URL>, Iterator<URL> {
	private final String urlPattern;
	private Iterator<Object[]> parameters;

	public URLPatternFactory(String urlPattern, Iterable<Object[]> parameters) {
		super();
		this.urlPattern = urlPattern;
		this.parameters = parameters.iterator();
	}
	
	public URLPatternFactory(String urlPattern, List<?> parameters) {
		super();
		this.urlPattern = urlPattern;
		List<Object[]> params = new LinkedList<Object[]>();
		for (Object parameter : parameters) {
			params.add(new Object[] { parameter });
		}
		this.parameters = params.iterator();
	}

	@Override
	public Iterator<URL> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return parameters.hasNext();
	}

	@Override
	public URL next() {
		try {
			return new URL(String.format(urlPattern, parameters.next()));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}

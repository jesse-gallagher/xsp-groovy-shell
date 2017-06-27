package org.openntf.xsp.groovyshell;

import java.util.Arrays;

import com.ibm.commons.util.StringUtil;

/**
 * This {@link ClassLoader} implementation attempts to load a class from the provided
 * delegate {@link ClassLoader} instances in order, returning the class from the first
 * one that found it. 
 * 
 * @author Jesse Gallagher
 *
 */
public class DelegatingClassLoader extends ClassLoader {
	private final ClassLoader[] delegates;
	
	public DelegatingClassLoader(ClassLoader... delegates) {
		if(delegates == null || delegates.length < 1) {
			throw new IllegalArgumentException("delegates must be an array of non-zero length");
		}
		
		this.delegates = delegates;
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		for(ClassLoader delegate : delegates) {
			try {
				return Class.forName(name, resolve, delegate);
			} catch(ClassNotFoundException e) {
				// Move on
			}
		}
		throw new ClassNotFoundException(StringUtil.format("Unable to locate class '{0}' using class loaders {1}", name, Arrays.asList(delegates)));
	}
}
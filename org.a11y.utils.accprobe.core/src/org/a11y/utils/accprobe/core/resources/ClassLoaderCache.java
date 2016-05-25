/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.resources;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * cache for class loaders used during pre-instrumentation or transformation
 * processes. The A11Y AOP engine uses the bootstrap classpath to perform .class
 * file transformations and entries on this classpath cannot simultaneously
 * occur on the standard classpath. Hence, any need for a <code>Class</code>
 * object fo the former set of classes must be retreaved via this class. This
 * cache should also be used to perform all class-comparison operations such as
 * <code>instanceof</code> and <code>isAssignableFrom</code>.
*
 * @author Mike Squillace
 */
public class ClassLoaderCache
{

	private static ClassLoaderCache _clc;

	private Map<String, ClassLoader> _cache = Collections.synchronizedMap(new HashMap<String, ClassLoader>());

	protected ClassLoaderCache () {
	}

	/**
	 * get the default cache or create one if one has not already been created
	 * 
	 * @return default cache
	 */
	public static ClassLoaderCache getDefault () {
		if (_clc == null) {
			_clc = new ClassLoaderCache();
		}
		return _clc;
	}

	/**
	 * get a <code>java.net.URL</code> object representing the named resource
	 * 
	 * @param name -
	 *            name of desired resource
	 * @return <code>java.net.URL</code> object or <code>null</code> if
	 *         name cannot be resolved
	 */
	public URL getResource (String name) {
		URL result = getClass().getClassLoader().getResource(name);
		
		// if we can't find the resource - loop through the classloaders and try them
		if ( result == null ) {
			synchronized (_cache) {
				Iterator<ClassLoader> iter = _cache.values().iterator();
				while (iter.hasNext() && result == null ) {
					ClassLoader loader = (ClassLoader) iter.next();
					if ( loader != null ) {
						result = loader.getResource( name );
					}
				}	
			}
		}
		
		return result;
	}

	/**
	 * get a <code>java.util.Enumeration</code> representing the named resources
	 * 
	 * @param name -
	 *            name of desired resource
	 * @return <code>java.util.Enumeration</code> object or <code>null</code> if
	 *         name cannot be resolved
	 */
	public Enumeration<URL> getResources (String name) throws IOException {
		Enumeration<URL> result = getClass().getClassLoader().getResources(name);
		
		// if we can't find the resource - loop through the classloaders and try them
		if ( result == null ) {
			synchronized (_cache) {
				Iterator<ClassLoader> iter = _cache.values().iterator();
				while (iter.hasNext() && result == null ) {
					ClassLoader loader = (ClassLoader) iter.next();
					if ( loader != null ) {
						result = loader.getResources( name );
					}
				}	
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * get a <code>java.lang.Class</code> object representing the named class
	 * 
	 * @param className -
	 *            name of desired class
	 * @return <code>java.lang.Class</code> object or <code>null</code> if
	 *         name cannot be resolved
	 */
	public Class<?> classForName (String className) {
		Class<?> cls = null;
		try {
			cls = Class.forName(className, true, getClassLoaderFor(className));
		} catch (ClassNotFoundException e) {
		} catch (NoClassDefFoundError e) {
		}
		return cls;
	}

	/**
	 * get the <code>java.lang.ClassLoader</code> reference for the named
	 * class. The algorithm used by this method is as follows:
	 * 
	 * <p>
	 * <ul>
	 * <li>1. look for the ClassLoader used to load the named class
	 * <li>2. look for the ClassLoader that loaded the package of the named
	 * class
	 * <li>3. repeat step 2 for each parent package until the named class is
	 * found or until the default package is reached
	 * </ul>
	 * 
	 * <p>
	 * Note: This method is used by all other methods in this class to resolve
	 * class names.
	 * 
	 * @param className
	 *            name of class
	 * @return <code>ClassLoader</code> object for the class named or
	 *         <code>null</code> if the name cannot be resolved
	 */
	public ClassLoader getClassLoaderFor (String className) {
		ClassLoader cl = (ClassLoader) _cache.get(className);
		int dot = className.lastIndexOf('.');
		String packPrefix = dot == -1 ? "" : className.substring(0, dot);
		
		while (cl == null & packPrefix.length() > 0) {
			synchronized (_cache) {
				Iterator<String> iter = _cache.keySet().iterator();
				while (iter.hasNext() & cl == null) {
					String key = iter.next();
					if (key.indexOf(packPrefix) >= 0) {
						cl = _cache.get(key);
					}
				}
			}
			
			dot = packPrefix.lastIndexOf('.');
			packPrefix = dot == -1 ? "" : packPrefix.substring(0, dot);
		}
		
		return cl != null ? cl : getClass().getClassLoader();
	}

	/**
	 * add a class name with its corresponding class loader to the cache
	 * 
	 * @param className -
	 *            name of class
	 * @param cl -
	 *            class loader
	 */
	public void put (String className, ClassLoader cl) {
		if (_cache.containsKey(className)) { throw new IllegalArgumentException("Duplicate entries in cache"); }
		_cache.put(className, cl);
	}

	/**
	 * test whether or not the given class is assignable to the specified class
	 * name.
	 * 
	 * @param className
	 *            name of class to which target is to be assigned
	 * @param testCls
	 *            class to be tested
	 * @return <code>true</code> if and only if the test class is equal to or
	 *         a subclass of the class name, <code>false</code> otherwise
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 */
	public boolean isAssignableFrom (String className, Class<?> testCls) {
		Class<?> cls = null;
		boolean result = false;
		try {
			cls = Class.forName(className, true, getClassLoaderFor(className));
			result = cls.isAssignableFrom(testCls);
		}catch (ClassNotFoundException e) {
		}
		return result;
	}

	/**
	 * test whether or not the specified object is an instance of the class with
	 * the given name.
	 * 
	 * @param className -
	 *            name of class
	 * @param o -
	 *            object to be tested
	 * @return <code>true</code> if and only if the object is an instance of
	 *         hte class named by the className, <code>false</code> otherwise
	 */
	public boolean isInstanceOf (String className, Object o) {
		return isAssignableFrom(className, o.getClass());
	}

	/**
	 * returns whether or not this class name names a class that cannot be
	 * instrumented in A11Y. Classes that cannot be instrumented include classes
	 * in the A11Y packages, the AOP engine classes, classes that comprise the
	 * XML parsing engines, etc.
	 * 
	 * @param classname
	 *            name of class to be tested
	 * @return <code>true</code> if class named by this class name can be
	 *         instrumented, <code>false</code> otherwise
	 */
	public boolean isNonAdvisableClassName (String classname) {
		return classname.startsWith("org.aspectj")
		|| classname.startsWith("org.apache.")
		|| classname.startsWith("org.mozilla.javascript.")
		|| classname.startsWith("org.xml.sax.")
				|| classname.startsWith("sunw.")
				|| classname.startsWith("sun.")
				|| (classname.startsWith("java.") && !classname.startsWith("java.awt."))
				|| (classname.startsWith("javax.") && !classname.startsWith("javax.swing."))
				|| classname.startsWith("com.sun.")
				|| classname.startsWith("com.ibm.sns.")
				|| classname.startsWith("org.a11y.");
	}
} // ClassLoaderCache

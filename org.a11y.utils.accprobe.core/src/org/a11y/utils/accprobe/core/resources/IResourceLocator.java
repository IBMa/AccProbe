/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.resources;

import java.io.InputStream;
import java.net.URL;

/**
 * A IResourceLocator is used to retreave streams for named resources. It is meant to enhance
 * the normal functionality of <code>ClassLoader.getResourceAsStream()</code> in contexts in which classpaths can be difficult to resolve
 * or are unknown.
 *
 * @author Barry Feigenbaum
 */
public interface IResourceLocator
{

	
	
	public static final String DEFAULT_A11Y_RESOURCES_DIR = "resources";
	public static final String A11Y_RESOURCES_PATHS_KEY = "org.a11y.utils.accprobe.core.resourcesPaths";

	/**
	 * get the resource with the given id as a stream and using the
	 * class loader that loaded this IResourceLocator instance.
	 *
	 * @param id - id of resource 
	 * @return input stream
	 */
	public InputStream getResourceAsStream (String id);

	/**
	 * get the resource corresponding to the given id as a stream using the specified class loader.
	 * 
	 * @param id - id of resource
	 * @param loader -- class loader to use 
	 * @return input stream
	 */
	public InputStream getResourceAsStream (String id, ClassLoader loader);

	/**
	 * get the resource corresponding to the given id as a stream using the specified class loader. The base path
	 * and extention of the file resource are also specified.
	 * 
	 * @param id - id of resource
	 * @param loader -- class loader to use
	 * @param base - base path
	 * @param ext -extentio
	 * @return input streamn 
	 */
	public InputStream getResourceAsStream (String id, String base,
									 		String ext, ClassLoader loader);

	/**
	 * find the resource with the given name. The name is a /-separated path 
	 * describing the relative path to the resource.
	 * 
	 * @param name - name of resource
	 * @return URL of a resource with the given name or <code>null</code> if 
	 * no resources with the given name could be found
	 * @see java.lang.ClassLoader#getResource(String)
	 */
	public URL getResource (String name);

	
	/**
	 * find the resources with the given name. The name is a /-separated path 
	 * describing the relative path to the resource.
	 * 
	 * @param name - name of resource
	 * @return URL of all resources with the given name or <code>null</code> if 
	 * no resources with the given name could be found
	 * @see java.lang.ClassLoader#getResources(String)
	 */
public URL[] getResources (String name);

/**
 * find the Path with the given name. The name is a /-separated path 
 * describing the relative path to the resource.
*
 * @param name - name of resource
 * @return String file representation of the URL returned from getResources or <code>null</code> if 
 * no resources with the given name could be found
 * @see java.lang.ClassLoader#getResource(String)
 */
public String getPath (String name);


/**
 * find the paths with the given name. The name is a /-separated path 
 * describing the relative path to the resource.
*
 * @param name - name of resource
 * @return String[] file representation of the URL[] returned from getResources or <code>null</code> if 
 * no resources with the given name could be found
 * @see java.lang.ClassLoader#getResources(String)
 */
public String[] getPaths (String name);
}

/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * an implementation for locating resources within the Eclipse framework.
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public class EclipseResourceLocator extends DefaultResourceLocator
{
	private List _bundles = new LinkedList();
	private File metadataDir;
	private Logger logger = Logger.getLogger(LoggingUtil.A11Y_CORE_LOGGER_NAME);
	
	/**
	 * used to identify the bundles in which resources are to be found. Each bundle in 
	 * Eclipse has its own class loader and we leave it to the platform to choose the
	 * class loader to be used. Thus, calling this method means that a <code>ClassLoader</code> 
	 * should not be passed to <code>getResourceAsStream</code>. Also, not all methods for 
	 * finding resources in bundles use <code>ClassLoader</code> objects.
	 * 
	 * @param bundle - the name of the bundle (i.e. plug-in) that is to be searched for a resource
	 */
	public void registerBundleName (String bundle) {
		_bundles.add(bundle);
	}
	
	/**
	 * {@inheritDoc}
	 * Note that resources are located in the context of bundles in Eclipse.
	 */
	public InputStream getResourceAsStream (String id, ClassLoader loader) {
		return getResourceAsStream(id, null, null, loader);
	}

	public InputStream getResourceAsStream (String id, String base,
			String ext, ClassLoader loader) {
		return loader != null
			? super.getResourceAsStream(id, base, ext, loader)
			: getResourceAsStream(id, base, ext);
	}
	
	private InputStream getResourceAsStream (String id, String base, String ext) {
		InputStream stream = null;
		
		for (Iterator iter = _bundles.listIterator(); iter.hasNext() & stream == null; ){
			String bundleName = (String) iter.next();
			stream = getResourceAsStream(id, base, ext, bundleName);
		}
		
		return stream;
	}
	
	public InputStream getResourceAsStream (String id, String base,
											String ext, String bundleName) {
		InputStream stream = null; //Stream to be returned.
		String relativePath = getRelativePath(base, id, ext);
		IPath pathToFile = null;
		
		if (bundleName != null) {
			pathToFile = getPathToFile(bundleName, relativePath); 
		}
		if (pathToFile != null) {
			try {
				stream = new FileInputStream(pathToFile.toFile());
				logger.log(Level.FINE, "Created stream for file - " + relativePath + " using bundle " + bundleName);
			}catch (FileNotFoundException e) {
				//Don't need to report anything in this catch , the result of the call to
				//getPathToFile will have already made sure that this file exists.
			}
		} else {
			logger.log(Level.FINE, "unable to create stream for file - " + relativePath + " using bundle " + bundleName);
		}
		
		return stream;
	}
	
	private String getRelativePath (String base, String fileName, String ext) {
		String relativePath = null;

		//First, build the relative file path that we want to look for.	
		if (base == null) {
			//If we have a base of null, default to using the resources directory   		
			base = IResourceLocator.DEFAULT_A11Y_RESOURCES_DIR;
		}
		
		if (ext != null) {
			//The passed extension is not null, use it.    		
			relativePath = base + File.separator + fileName + "." + ext;
		}else {
			//Null for an extension means we default to using .properties   		
			relativePath = base + File.separator + fileName + ".properties";
		}
		
		return relativePath;
	}

	/**
	 * {@inheritDoc}
	 * <p>Search Algorithm is:</p>
	 * <p><ol> 
	 * <li>Search through the registered ClassLoaders for the resource
	 * <li>Search through the registered Bundles for the resource
	 * <li>Test if the name is actually a Bundle
	 * </ol></p>
	 */	
	public URL getResource (String name) {
		URL result = super.getResource(name);
		
		// If we didn't find the resource in any of the registered ClassLoaders
		// Search through the bundle list to see if the resource is in a bundle
		if ( result == null ) {
			IPath ipath = null;
			
			for (Iterator iter = _bundles.listIterator(); iter.hasNext() & ipath == null; ){
				String bundleName = (String) iter.next();
				if ( bundleName.equals(name)) {
					ipath = getPathToBundle( name );
				} else {
					ipath = getPathToFile(bundleName, name);
				}
			}
			
			// If the resource was not in any of the registered bundles
			// Check to see if the resource is a bundle name
			if ( ipath == null ) {
				ipath = getPathToBundle( name );
			}
		
			// If we found the resource then create a URL to return
			if ( ipath != null ) {
				try {
					result = ipath.toFile().toURL();
				} catch (MalformedURLException e) {
				}
			}
		}
		//if result still null convert name it to URI
		if(result == null) {
			File sourceLoc = new File(name);
			if(name.endsWith(".xml")) {
				metadataDir = sourceLoc.getParentFile();
			} else {
				String path = metadataDir.getAbsolutePath();
				sourceLoc = new File(path+"/"+name);
			}

			try {
				result = sourceLoc.toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} 
		}
		return result;
	}
		
	/**
	 * {@inheritDoc}
	 * Note: You may also pass a bundle name to this method to obtain a bundle path
	 */
	public String getPath ( String name ) {
		String result = null;
		
		URL url = getResource( name );
		
		result = convertToFileURL( url );
				
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getPaths( String name ) {
		String[] result = null;
		
		URL[] urls = getResources( name );
		
		if ( urls != null ) {
			result = new String[urls.length];
			
			for( int i=0; i < urls.length; i++ ) {
				result[i] = convertToFileURL( urls[i]);
			}
		}
		
		return result;
	}
	
	private String convertToFileURL( URL url ) {
		String result = null;
		
		if ( url != null ) {
			try {
				URL fileUrl = FileLocator.toFileURL(url);				
				result = fileUrl.getFile().substring(1); // remove "file:/"
			} catch( IOException e) {
				result = null;
			}
		}
		return result;
	}
	
	public IPath getPathToFile (String bundleName, String relativePath) {
		Path absolutePath = null;
		
		if (bundleName != null && bundleName.length() > 0) {
			Bundle bundle = Platform.getBundle(bundleName);
			if (bundle != null) {
				Path relativeFilePath = new Path(relativePath);
				URL pathUrl = Platform.find(bundle, relativeFilePath);
				
				if (pathUrl == null) {
					// look in resources directory
					relativeFilePath = new Path(DEFAULT_A11Y_RESOURCES_DIR + "/" + relativePath);
					pathUrl = Platform.find(bundle, relativeFilePath);
				}
				
				if (pathUrl != null) {
					try {
						pathUrl = Platform.resolve(pathUrl);
						absolutePath = new Path(new File(pathUrl.getFile()).getAbsolutePath());
						logger.log(Level.FINE, "getPathToFile(), absolute path to " + relativePath + " = " + absolutePath.toString());
					}catch (IOException ioe) {
						logger.log(Level.WARNING, ioe.getMessage(), ioe);
					}
				}
			}
		}
		
		return absolutePath;
	}	
	
	public IPath getPathToBundle (String bundleName) {
		IPath absolutePath = null;
		if (bundleName != null && bundleName.length() > 0) {
			Bundle bundle = Platform.getBundle(bundleName);
			if (bundle != null) {
				try {
					String bundleLoc = FileLocator.resolve(bundle.getEntry("/")).getFile();
					if (bundleLoc.startsWith("file:")) {
						bundleLoc = bundleLoc.substring("file:".length());
					}
					if (bundleLoc.startsWith("/") && Platform.getOS().equals(Platform.OS_WIN32)) {
						// remove forward slash on windows systems
						bundleLoc = bundleLoc.substring(1);
					}
					if (bundleLoc.endsWith("jar!/")) {
						// root of bundle is in jar so just use the absolute path to that jar file
						absolutePath = new Path(bundleLoc.substring(0, bundleLoc.length() - 2));
					} else if (bundleLoc.endsWith("/")) {
						// bundle is unpacked as directory so just remove trailing slash
						absolutePath = new Path(bundleLoc.substring(0, bundleLoc.length() - 1));
					}
					logger.log(Level.FINE, "getPathToBundle(), path to bundle " + bundleName + " = " + absolutePath.toString());
				} catch (IOException e) {
					logger.log(Level.WARNING, "Could not retrieve location for a bundle", e);
				}
		}
		}
		return absolutePath;
	}	
	
}

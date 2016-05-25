/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.runtime;

import org.a11y.utils.accprobe.core.config.ConfigurationException;
import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.logging.IErrorLogger;
import org.a11y.utils.accprobe.core.resources.IResourceLocator;


public interface IRuntimeContext
{

	public IConfiguration getConfiguration () throws ConfigurationException;
	public IResourceLocator getResourceLocator ();
	public IErrorLogger getErrorLogger ();
	
}

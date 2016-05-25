/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.runtime;

import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.logging.IErrorLogger;
import org.a11y.utils.accprobe.core.resources.IResourceLocator;


/**
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public abstract class AbstractRuntimeContext implements IRuntimeContext
{

	protected IErrorLogger errorLogger;
	protected IConfiguration configuration;
	protected IResourceLocator resourceLocator;
	
	/** {@inheritDoc} */
	public IResourceLocator getResourceLocator() {
		return resourceLocator;
	}

	/** {@inheritDoc} */
	public IErrorLogger getErrorLogger() {
		return errorLogger;
	}

}

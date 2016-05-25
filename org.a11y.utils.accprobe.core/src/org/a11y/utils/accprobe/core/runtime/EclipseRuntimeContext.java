/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.runtime;

import org.a11y.utils.accprobe.core.config.ConfigurationException;
import org.a11y.utils.accprobe.core.config.EclipseConfiguration;
import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.logging.EclipseErrorLogger;
import org.a11y.utils.accprobe.core.resources.EclipseResourceLocator;


/**
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public class EclipseRuntimeContext extends AbstractRuntimeContext
{

	public EclipseRuntimeContext() {
		super();
		errorLogger = new EclipseErrorLogger();
		resourceLocator = new EclipseResourceLocator();
	}

	public IConfiguration getConfiguration() throws ConfigurationException {
		if (configuration == null) {
			configuration = new EclipseConfiguration();
		}
		
		return configuration;
	}

}

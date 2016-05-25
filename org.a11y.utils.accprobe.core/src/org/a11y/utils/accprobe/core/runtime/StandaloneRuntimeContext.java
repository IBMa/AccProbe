/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.a11y.utils.accprobe.core.config.ConfigurationException;
import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.config.XmlConfiguration;
import org.a11y.utils.accprobe.core.logging.StreamErrorLogger;
import org.a11y.utils.accprobe.core.resources.DefaultResourceLocator;


public class StandaloneRuntimeContext extends AbstractRuntimeContext
{

	public StandaloneRuntimeContext() {
		super();
		errorLogger = new StreamErrorLogger(System.err);
		resourceLocator = new DefaultResourceLocator();
	}

	public IConfiguration getConfiguration () throws ConfigurationException {
		if (configuration == null) {
			URL[] urls = resourceLocator.getResources(IConfiguration.A11Y_ID + ".xml");
			configuration = new XmlConfiguration();
			for (int p = 0; p < urls.length; ++p) {
				try {
					InputStream configFileStream = urls[p].openStream(); 
					//System.err.println("url="+urls[p]+"; "+urls[p].toExternalForm()+"; "+configFileStream);
					if (configFileStream != null) {
						configuration.addConfigurationData(configFileStream);
					}
					configFileStream.close();
				} catch (IOException e) {
				}
			}
		}
		
	return configuration;
	}

}

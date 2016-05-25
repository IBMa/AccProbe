/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.config;

/**
 * This class is used to report errors with A11Y's configuration
 * @author Randy Horwitz
 *
 */
public class ConfigurationException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigurationException () {
		super();
	}

	public ConfigurationException (String message) {
		super(message);
	}

	public ConfigurationException (Throwable cause) {
		super(cause);
	}

	public ConfigurationException (String message, Throwable cause) {
		super(message, cause);
	}
}

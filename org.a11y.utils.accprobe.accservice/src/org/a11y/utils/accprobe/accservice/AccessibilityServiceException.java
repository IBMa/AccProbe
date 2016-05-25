/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice;

public class AccessibilityServiceException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccessibilityServiceException (final String message) {
		super(message);
	}

	public AccessibilityServiceException (final Throwable cause) {
		this(cause.getMessage() == null ? "" : cause.getMessage(), cause);
	}

	public AccessibilityServiceException (final String message, final Throwable cause) {
		super(message, cause);
	}
	
}

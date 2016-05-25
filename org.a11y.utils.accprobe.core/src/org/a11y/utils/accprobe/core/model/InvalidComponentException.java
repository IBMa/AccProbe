/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model;

public class InvalidComponentException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidComponentException () {
		super();
	}

	public InvalidComponentException (String message) {
		super(message);
	}

	public InvalidComponentException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidComponentException (Throwable cause) {
		super(cause);
	}
}

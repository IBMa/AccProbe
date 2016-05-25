/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.event;

import java.util.EventObject;

public class TopLevelWindowEvent extends EventObject
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//	public ReportElement[] addedElements;
	//	public ValidationReport source;
	/**
	 * create a new event object. This constructor is called when a Window
	 * has happened and the <code>fireWindowCallback</code> method is invoked. 	 
	 */
	public TopLevelWindowEvent (Object source) {
		super(source);
	}
}
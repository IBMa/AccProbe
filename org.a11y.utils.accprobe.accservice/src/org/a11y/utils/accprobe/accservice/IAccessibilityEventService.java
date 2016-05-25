/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice;

import java.io.Serializable;

import org.a11y.utils.accprobe.core.model.events.IModelEventListener;


public interface IAccessibilityEventService extends Serializable
{
	static final long serialVersionUID = -8127562007783656376L;
	/**
	 * add a listener to be notified of accessibility events
	 * 
	 * @param listener
	 * @param eventTypes type of event (which may be <code>null</code> for some 
	 * accessibility event APIs that do not support such functionality)
	 */
	public void addAccessibilityEventListener (IModelEventListener listener, Object[] eventTypes, Object[] params);

	/**
	 * remove a previously added listener
	 * 
	 * @param listener
	 * @param eventTypes
	 */
	public void removeAccessibilityEventListener (IModelEventListener listener, Object[] eventTypes);

}

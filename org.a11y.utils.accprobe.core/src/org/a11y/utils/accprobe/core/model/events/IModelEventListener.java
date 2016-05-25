/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.model.events;

import java.util.EventObject;

/**
 * a listener to be implemented by clients wishing to monitor events within a model
*
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public interface IModelEventListener
{

	/**
	 * handle the dispatched event
	 * 
	 * @param event
	 */
	public void handleEvent (EventObject event);
	
}

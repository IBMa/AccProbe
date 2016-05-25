/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.event;

import java.util.EventListener;

/**
 */
public interface TopLevelWindowEventListener extends EventListener
{

	/**TopLevelWindowEvent 
	 * occurs when a TopLevelWindowEvent has been created
	 * 
	 * @param e - TopLevelWindowEvent
	 */
	public void WindowEventCreated (TopLevelWindowEvent e);
}

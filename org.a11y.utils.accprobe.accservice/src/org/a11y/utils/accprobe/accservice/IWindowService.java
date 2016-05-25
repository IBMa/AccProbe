/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice;

import java.io.Serializable;

import org.a11y.utils.accprobe.accservice.event.TopLevelWindowEventListener;

/**
 * a service for functionality related to whatever objects constitute top-level windows of the 
 * native desktop.
*
 * @author <a href="mailto:masquill@us.ibm.com">Mike Squillace</a>
 *
 */
public interface IWindowService extends Serializable
{


	/**
	 * retreave all top-level windows from whatever object is considered to be the underlying platform's desktop.
	 * 
	 * @return array of top-level windows or an empty array if unavailable
	 */
	public Object[] getTopLevelWindows ();
	
	/**
	 * get the active or current window. This is the window that has keyboard focus andis the 
	 * subject of processes in the framework (e.g. inspection, validation).
	 *  
	 * @return active or current window
	 */
	public Object getActiveWindow ();
	
	/**
	 * get the process id with which the given window is associated
	 * 
	 * @param window window for which process id is desired
	 * @return process id for given window or -1 if no id can be obtained
	 */
	public int getProcessId (Object window);
	
	/**
	 * 
	 * @return process id
	 */
	public int getCurrentProcessId ();
	
	/**
	 * add a listener to be notified of the creation or destruction of top-level windows
	 * 
	 * @param listener
	 */
	public void addTopLevelWindowListener (TopLevelWindowEventListener listener);
	
	/**
	 * remove the listener previously added via <code>addTopLevelWindowListener</code>
	 * 
	 * @param listener
	 * @see #addTopLevelWindowListener(TopLevelWindowEventListener)
	 */
	public void removeTopLevelWindowListener (TopLevelWindowEventListener listener);

	/**
	 * sets the given window as the activeWindow
	 * 
	 * @param window to be set as active 
	 */
	public void setActiveWindow(Object window);
	
}

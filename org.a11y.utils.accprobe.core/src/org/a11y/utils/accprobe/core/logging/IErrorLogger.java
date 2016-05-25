/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.logging;

/**
 * used to log errors to different destinations such as files, streams, or GUI components. The
 * <code>ErrorLoggerFactory</code> interface should be implemented to return instances of this interface appropriate to different
 * contexts.
 *
 * @author Mike Squillace
 */
public interface IErrorLogger
{

	/**
	 * log a Throwable
	 * 
	 * @param t - Throwable to log
	 */
	public void logError (Throwable t);

	/**
	 * log an error message
	 * 
	 * @param msg - error message
	 */
	public void logError (String msg);

	/**
	 * log an error message along with its corresponding Throwable. If no message
	 * is specified, the class and <code>toString()</code> of the
	 * Throwable should be logged.
	 * 
	 * @param msg - error message
	 * @param t - Throwable being logged
	 */
	public void logError (String msg, Throwable t);
}

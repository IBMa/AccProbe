/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.logging;


/**
 * a base from which error loggers can be built. Extending this class, clients need only
 * implement <code>logError(String, Throwable)</code>.
*
 * @author Mike Squillace
 */
public abstract class AbstractErrorLogger implements IErrorLogger
{

	/** {@inheritDoc} */
	public void logError (Throwable t) {
		logError(t.getMessage() != null ? t.getMessage() : "<no message>", t);
	}

	/** {@inheritDoc} */
	public void logError (String msg) {
		logError(msg, null);
	}
}

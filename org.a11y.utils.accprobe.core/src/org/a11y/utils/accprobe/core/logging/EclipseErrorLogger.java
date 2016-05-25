/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.logging;

import org.a11y.utils.accprobe.core.A11yCorePlugin;

/**
 * @author Mike Squillace
 */
public class EclipseErrorLogger extends AbstractErrorLogger
{

	/** {@inheritDoc} */
	public void logError (String msg, Throwable t) {
		A11yCorePlugin.getDefault().logException(msg, t);
	}
	
}

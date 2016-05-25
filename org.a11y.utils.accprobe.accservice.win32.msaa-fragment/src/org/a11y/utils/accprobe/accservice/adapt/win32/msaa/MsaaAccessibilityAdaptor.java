/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.adapt.win32.msaa;

import org.a11y.utils.accprobe.core.adapt.IAdaptor;

/**
 * Implementation for the Msaa Accesibility <code>IAdaptor</code>. 
 *
 * @author Mike Smith
 */
public class MsaaAccessibilityAdaptor implements IAdaptor
{

	private static final Class<?>[] ADAPTABLE_MSAA_TYPES = new Class<?>[] {};

	/** {@inheritDoc} */
	public Class<?>[] getSupportedTypes () {
		return ADAPTABLE_MSAA_TYPES;
	}

	/** {@inheritDoc} */
	public Object adapt (Object o, Class<?> type) throws Exception {
		Object result = null;
		return result;
	}
	/** {@inheritDoc} */
	/*
	 public NodeSynchronizer getNodeSynchronizer ()
	 {
	 return new SwingAccessibilityNodeSynchronizer();
	 }
	 */
}

/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.adapt.win32.ia2;

import org.a11y.utils.accprobe.core.adapt.IAdaptor;

/**
 * Implementation for the IA2 Accesibility <code>IAdaptor</code>. 
 *
 * @author Mike Smith
 */
public class IA2AccessibilityAdaptor implements IAdaptor
{

	private static final Class<?>[] ADAPTABLE_IA2_TYPES = new Class<?>[] {};

	/** {@inheritDoc} */
	public Class<?>[] getSupportedTypes () {
		return ADAPTABLE_IA2_TYPES;
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

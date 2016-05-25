/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice.locate;

import java.util.Locale;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;


public class AccessibleNodePointerFactory implements NodePointerFactory
{

	public static final int ACCNODE_POINTER_FACTORY_ORDER = 4;
	
	public NodePointer createNodePointer(QName name, Object object, Locale locale) {
		return object instanceof IAccessibleElement
			? new AccessibleNodePointer((IAccessibleElement) object, locale) : null;
	}

	public NodePointer createNodePointer(NodePointer parent, QName name, Object object) {
		return object instanceof IAccessibleElement
		? new AccessibleNodePointer((IAccessibleElement) object, parent, null) : null;
	}

	public int getOrder() {
		return ACCNODE_POINTER_FACTORY_ORDER;
	}

}

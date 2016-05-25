/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice.locate;

import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.core.model.locate.BaseNodeIterator;


public class AccessibleNodeIterator extends BaseNodeIterator
{

    private IAccessibleElement accElement;
    
	public AccessibleNodeIterator(NodePointer parent, NodeTest nodeTest,
    						 boolean reverse, NodePointer startWith, IModel model) 
    {
        super(parent, nodeTest, reverse, startWith, model);
    	this.accElement = (IAccessibleElement) parent.getNode();
    }

    public NodePointer getNodePointer() {
        if (position == 0) {
            setPosition(1);
        }
        return child == null ? null : new AccessibleNodePointer((IAccessibleElement) child, parent, null);
    }

}

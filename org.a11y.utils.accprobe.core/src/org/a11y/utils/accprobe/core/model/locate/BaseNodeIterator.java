/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.model.locate;

import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.traverse.ITreeNodeWalker;


public abstract class BaseNodeIterator implements NodeIterator {

	protected NodePointer parent;
	protected IModel model;
    protected NodeTest nodeTest;
    protected Object child;
    protected boolean reverse;
    protected int position = 0;
	
	public BaseNodeIterator(NodePointer parent, NodeTest nodeTest,
			 boolean reverse, NodePointer startWith, IModel model) 
{
this.parent = parent;
this.model = model;
this.nodeTest = nodeTest;
this.reverse = reverse;
if (startWith != null) {
	this.child = startWith.getNode();
	}
}

    public int getPosition() {
        return position;
    }

    public boolean setPosition(int position) {
    	boolean res = true;
    	while (res && this.position < position) {
    		try {
				res = next();
			} catch (InvalidComponentException e) {
			}
        }
        while (res && this.position > position) {
        	try {
				res = previous();
			} catch (InvalidComponentException e) {
			}
        }
        return res;
    }

    protected boolean previous() throws InvalidComponentException {
        --position;
        if (!reverse) {
            if (position == 0) {
                child = null;
            } else if (child == null) {
                child = lastChild();
            } else {
                child = previousSibling(child);
            }
            
            while (child != null && !testChild()) {
                child = previousSibling(child);
            }
        } else {
            child = nextSibling(child);
            while (child != null && !testChild()) {
                child = nextSibling(child);
            }
        }
        
        return child != null;
    }

    protected boolean next()  throws InvalidComponentException {
        ++position;
        if (!reverse) {
            if (position == 1) {
                child = child == null ? firstChild() : nextSibling(child);
            } else {
                child = nextSibling(child);
            }
            
            while (child != null && !testChild()) {
                child = nextSibling(child);
            }
        } else {
            if (position == 1) {
                child = child == null ? lastChild() : previousSibling(child);
            } else {
                child = previousSibling(child);
            }
            
            while (child != null && !testChild()) {
                child = previousSibling(child);
            }
        }
     //if (child != null) System.err.println("Next:"+child);   
        return child != null;
    }

    protected boolean testChild() {
        return ((BaseNodePointer) parent).testNode(nodeTest, child);
    }
    
    protected Object nextSibling (Object child) throws InvalidComponentException {
    	Object elemParent = parent.getNode();
    	Object[] children = ((ITreeNodeWalker) model.getNodeWalker()).getFilteredChildren(elemParent);
    	int index = ((BaseNodePointer) parent).findChildIndex(children, child);
    	return index > -1 && index + 1 < children.length ? children[index + 1] : null;
    }
    
    protected Object previousSibling (Object child)  throws InvalidComponentException {
    	Object elemParent = parent.getNode();
    	Object[] children = ((ITreeNodeWalker) model.getNodeWalker()).getFilteredChildren(elemParent);
    	int index = ((BaseNodePointer) parent).findChildIndex(children, child);
    	return index > 0 ? children[index - 1] : null;
    }
    
    protected Object firstChild () {
        Object[] children = null;
		try {
			children = ((ITreeNodeWalker) model.getNodeWalker()).getFilteredChildren(parent.getNode());
		} catch (InvalidComponentException e) {
		}
    	return children != null && children.length > 0 ? children[0] : null;
    }
    
    protected Object lastChild () {
    	Object[] children = null;
		try {
			children = ((ITreeNodeWalker) model.getNodeWalker()).getFilteredChildren(parent.getNode());
		} catch (InvalidComponentException e) {
		}
    	return children != null && children.length > 0 ? children[children.length - 1] : null;
    }
    
}

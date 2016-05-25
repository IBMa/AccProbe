/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice.locate;

import java.util.Locale;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.locate.BaseNodePointer;


public class AccessibleNodePointer extends BaseNodePointer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IAccessibleElement accElement;
	
	public AccessibleNodePointer(IAccessibleElement acc, Locale locale) {
		this(acc, null, locale);
	}

	public AccessibleNodePointer(IAccessibleElement acc, NodePointer parent, Locale locale) {
		super(parent, locale, "msaa");
		accElement = acc;
		name = new QName(null, model.getNodeName(accElement));
	}

	public Object getBaseValue() {
		return accElement;
	}

	public Object getImmediateNode() {
		return accElement;
	}

    public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith)  {
            return new AccessibleNodeIterator(this, test, reverse, startWith, model);
        }

	public String asPath () {
        StringBuffer buffer = new StringBuffer();
        String accName = null;
		try {
			accName = accElement.getAccessibleName();
		} catch (InvalidComponentException e) {
		}
        
        if (parent != null) {
            buffer.append(parent.asPath());
        }
        if (buffer.length() == 0 || buffer.charAt(buffer.length() - 1) != '/') {
    		buffer.append('/');
    	}
        
        buffer.append(getName().getName());
        buffer.append('[');
        try {
			buffer.append(
					accName != null && accName.length() > 0
						? "@accessibleName=\'" + accName + "\'" : Integer.toString(getRelativePositionByRole()));
		} catch (InvalidComponentException e) {
		}
        buffer.append(']');
        
        return buffer.toString();
    }
	
	protected int getRelativePositionByClassName () {
		return -1;
	}
	
	protected int getRelativePositionByRole () throws InvalidComponentException {
		IAccessibleElement accParent = parent != null
			? (IAccessibleElement) parent.getBaseValue() : accElement.getAccessibleParent();
		int count = 1;
		
		if (accParent != null) {
			IAccessibleElement child = null;
			int childCount = accParent.getAccessibleChildCount();
			String name = getName().getName();

			// count number of occurrences of this accElement's role
			// prior to it in parent container
for (int cc = 0; cc < childCount; ++cc) {
	child = accParent.getAccessibleChild(cc);
	if (accElement.getAccessibleIndexInParent() == cc) {
		break;
	}
	if (child!=null && child.getAccessibleRole().equals(name)) {
					++count;
				}
			}
		}
		
		return count;
	}
	
}

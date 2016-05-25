/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
*
*
*
 *
 *
 * Contributors:
 *  IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.a11y.utils.accprobe.accservice.core;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.traverse.AbstractTreeNodeWalker;


/**
 * implementation of <code>TreeWalker</code> for walking a tree of <code>IAccessibleElement</code objects.
 *
 * @see org.a11y.utils.accprobe.accservice.core.IAccessibleElement
 * @author Mike Squillace
 *
 */
public class AccessibleElementTreeWalker extends AbstractTreeNodeWalker
{

	public AccessibleElementTreeWalker () {
		super();
	}

	public Object[] getChildren (Object element)
		throws InvalidComponentException {
		Object[] children = new Object[0];
		Object bridgedChild = getBridgedChild(element);
		if (bridgedChild != null) {
			children = new Object[] {bridgedChild};
		}else if (element instanceof IAccessibleElement) {
			IAccessibleElement acc = (IAccessibleElement) element;
			children = acc.getAccessibleChildren();
		}
		return children;
	}
	
	/** {@inheritDoc} */
	public Object getChild (Object element, int index) throws InvalidComponentException {
		return element instanceof IAccessibleElement ? ((IAccessibleElement) element).getAccessibleChild(index) : null;
	}

	/** {@inheritDoc} */
	public Object getParent (Object element) throws InvalidComponentException {
		Object parent = null;
		Object bridgedParent = getBridgedParent(element);
		if (bridgedParent != null) {
			parent = bridgedParent;
		}else if (element instanceof IAccessibleElement) {
			parent = ((IAccessibleElement) element).getAccessibleParent();
		}
		return parent;
	}

	/** {@inheritDoc} */
	public boolean hasChildren (Object element)
		throws InvalidComponentException {
		return element instanceof IAccessibleElement
				&& ((IAccessibleElement) element).getAccessibleChildCount() > 0;
	}

	public Object[] getStartNodes () {
		return new Object[0];
	}
}

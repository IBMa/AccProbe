/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.accservice.core;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.traverse.filters.INodeFilter;


/**
 * This filter will exclude items.  
 * If you wish to show all items,
 * remove it, or do not set it in the first place.
 * @author annford
 *
 */
public class AccessibleNodeItemFilter implements INodeFilter
{
		
	public boolean pass (Object node) {
		boolean item = false;
		if (node instanceof IAccessibleElement){
			IAccessibleElement acc = (IAccessibleElement)node;
			try {
				String role = acc.getAccessibleRole();
				if (role != null) {
					item = role.indexOf(AccessibleConstants.ROLE_LIST_ITEM) >= 0
							|| role.indexOf(AccessibleConstants.ROLE_MENU_ITEM) >= 0
							|| role.indexOf(AccessibleConstants.ROLE_TREEITEM) >= 0
							|| role.indexOf(AccessibleConstants.ROLE_TABITEM) >= 0;
				}
			}catch (InvalidComponentException e) {
			}
		}
		return !item;
	}
	
}

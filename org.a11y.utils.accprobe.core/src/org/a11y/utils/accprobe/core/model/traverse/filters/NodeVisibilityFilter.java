/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.core.model.traverse.filters;

import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.core.model.IRenderableModel;

/**
 * This filter will exclude invisible items.  
 * If you wish to show all items, visible or invisible,
 * remove it, or do not set it in the first place.
 * @author annford
 *
 */
public class NodeVisibilityFilter implements INodeFilter
{
	private IModel model = null;
	
	public NodeVisibilityFilter (IModel model) {
		this.model = model;
	}

	public boolean pass (Object node) {
		boolean visible = false;
		if (model != null && model instanceof IRenderableModel) {
			visible = ((IRenderableModel)model).isVisible(node);
		}
		return visible;
	}
}

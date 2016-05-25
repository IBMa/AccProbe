/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
*
*
*
 *
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.a11y.utils.accprobe.core.model.traverse.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.a11y.utils.accprobe.core.model.IModel;


public class NodeNameFilter implements INodeFilter {

	protected IModel model = null;
	protected List<String> ignoreNames = new ArrayList<String>();

	public NodeNameFilter(IModel model, String names) {
		this.model = model;
		if (names != null) {
			this.ignoreNames = Arrays.asList(names.split(",\\s*"));
		}
	}

	public boolean pass(Object node) {
		boolean pass = true;
		if (node != null && model != null) {
			String name = model.getNodeName(node);
			pass = !ignoreNames.contains(name);
		}
		return pass;
	}
	
}

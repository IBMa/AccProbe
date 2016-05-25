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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.a11y.utils.accprobe.core.model.IModel;


public class NodeIdFilter implements INodeFilter {

	protected IModel model = null;
	protected List<Pattern> ignoreIds = new ArrayList<Pattern>();

	public NodeIdFilter(IModel model, String ids) {
		this.model = model;
		StringTokenizer regExpList = ids == null ? null : new StringTokenizer(ids);
		while (regExpList != null && regExpList.hasMoreTokens()) {
			 ignoreIds.add(Pattern.compile(regExpList.nextToken()));
		}
	}

	public boolean pass(Object node) {
		boolean matched = false;
		if (node != null && model != null) {
		   	Iterator<Pattern> iter = ignoreIds.iterator();
		   	String id = model.getNodeId(node);
		   	while (!matched & iter.hasNext()) {
		      		matched = ((Pattern) iter.next()).matcher(id).matches();
		   	}
		 }
		 return !matched;
	}
}

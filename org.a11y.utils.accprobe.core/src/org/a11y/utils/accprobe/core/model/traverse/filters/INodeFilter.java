/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.core.model.traverse.filters;

import org.a11y.utils.accprobe.core.model.traverse.INodeWalker;

/**
 * used as a general-purpose filter for nodes during retrieval of successor nodes in a graph.
 *
 * @see INodeWalker#addNodeFilter(INodeFilter)
 * @see INodeWalker#getFilteredSuccessorNodes(Object)
 * @author <a href="mailto:masquill@us.ibm.com">Mike Squillace</a>
 *
 */
public interface INodeFilter
{
	/**
	 * whether or not to admit this object through the filter
	 * 
	 * @param node
	 * @return <code>true</code> if this object should be included in successor node collections, 
	 * <code>false</code> if it should be filtered
	 */
	public boolean pass (Object node);
	
}

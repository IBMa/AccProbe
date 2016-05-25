/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.core.model.traverse;

import java.util.LinkedList;

import org.a11y.utils.accprobe.core.model.traverse.filters.INodeFilter;


/**
 * base implementation for any node walker. Clients should subclass this class rather than 
 * attempting to implement all of <code>INodeWalker</code>.
*
 * @author <a href="mailto:masquill@us.ibm.com">Mike Squillace</a>
 *
 */
public abstract class AbstractNodeWalker implements INodeWalker
{

	protected LinkedList nodeFilters = new LinkedList();
	
	/** {@inheritDoc} */
	public void addNodeFilter(INodeFilter filter) {
		if (!nodeFilters.contains(filter)) {
			nodeFilters.add(filter);
		}
	}

	/** {@inheritDoc} */
	public INodeFilter[] removeAllFilters() {
		INodeFilter[] filters = (INodeFilter[]) nodeFilters.toArray(new INodeFilter[nodeFilters.size()]);
		nodeFilters.clear();
		return filters;
	}

	public void removeNodeFilter(INodeFilter filter) {
		nodeFilters.remove(filter);
	}

}

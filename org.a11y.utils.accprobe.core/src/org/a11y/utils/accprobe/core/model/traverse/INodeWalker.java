/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.core.model.traverse;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.traverse.filters.INodeFilter;


/**
 * used to traverse the nodes in a model
*
 * @see org.a11y.utils.accprobe.core.model.IModel
 * @author Mike Squillace
 *
 */
public interface INodeWalker {
	
	/**
	 * adds a filter to the list of filters through which nodes will be passed when 
	 * successor nodes are retrieved via the <code>getFilteredSuccessorNodes</code> method. 
	 * If an identical filter is already in the list of filters, this method does nothing.
	 *  
	 * @param filter
	 * @see #getFilteredSuccessorNodes(Object)
	 */
	public void addNodeFilter (INodeFilter filter);
	
	/**
	 * removes the specified filter from the list of filters through which a node is passed when 
	 * <code>getFilteredSuccessorNodes</code> is called.
	 * 
	 * @param filter
	 * @see #getFilteredSuccessorNodes(Object)
	 */
	public void removeNodeFilter (INodeFilter filter);
	
	/**
	 * removes all filters from this walker
	 * 
	 * @return all removed filters
	 */
	public INodeFilter[] removeAllFilters ();
	
	/**
	 * get the starting nodes of the graph.  These are the nodes of the graph that have
	 * no predecessors.
	 *
	 * @return starting nodes of graph
	 */
	public Object[] getStartNodes ();

	/**
	 * retreave the direct predecessors of the given element.  The direct predecessors of the
	 * given element are those elements that are connected to the given element via a single edge and
	 * serve as heads on that edge, permitting the given element to serve as the tail.
	 *
	 * @param node - node for which direct predecessors are desired
	 * @return direct predecessors of given node or empty array if no predecessors are present
	 */
	public Object[] getPredecessorNodes (Object node) throws InvalidComponentException;

	/**
	 * retreave the direct successors of the given element.  The direct successors of the
	 * given element are those elements that are connected to the given element via a single edge and
	 * serve as tails on that edge, permitting the given element to serve as the head.
	 *
	 * @param node - node for which direct successors are desired
	 * @return direct successors of given node or empty array if no successors are present
	 */
	public Object[] getSuccessorNodes (Object node) throws InvalidComponentException;

	/**
	 * retrieve the direct successors of the given element that pass each of the node filters added via the
	 * <code>addNodeFilter</code> method.  The direct successors of the
	 * given element are those elements that are connected to the given element via a single edge and
	 * serve as tails on that edge, permitting the given element to serve as the head.
	 * 
	 * <p>If no filters have been added or if all filters pass all successor nodes, then this method is 
	 * equivalent to <code>getSuccessorNodes</code>.
	 *
	 * @param node - node for which direct successors are desired
	 * @return direct successors of given node (filtered) or empty array if no successors are present
	 * @see #addNodeFilter(INodeFilter)
	 */
	public Object[] getFilteredSuccessorNodes (Object node) throws InvalidComponentException;

}

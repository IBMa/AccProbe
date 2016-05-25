/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model.locate;

import java.util.Properties;

/**
 * an interface to aid processing engines in 
 * locating or describing components of a model during a process.
 * Implementations perform two primary functions:
*
 * <p><ul>
 * <li>locate - constructs an XPath expression to locate an element within a model
 * <li>describe - attempts to distinguish a particular element from others by providing its id, name, textual content, etc.
 * </ul>
*
 * <p>Implementations are model-specific and should be defined within model configuration files. They should also 
 * not implement this interface directly, but extend <code>AbstractNodeLocator</code>.
 *
 * @see AbstractNodeLocator
 * @author Mike Squillace
 */
public interface INodeLocator
{
	
	/**
	 * locate a component within a model from the given root. The returned 
	 * string represents an XPath location path that uniquely determines the component's location within the 
	 * graph starting with the given root. This method can be used in conjunction with <code>describey</code> to provide a 
	 * richer description of the component.
	 * 
	 * @param element element within a graph to be located
	 * @param root - root from which element is to be located or <code>null</code> if
	 * element is to be found from a start node in the graph 
	 * @return string that uniquely locates element within the model
	 * @see <a href="http://www.w3.org/tr/xpath">XPath specification</a>
	 */
	public String locate (Object element, Object root);
	
	/**
	 * finds an object in a model given the specified path. The path must be a 
	 * valid XPath expression according to the XPath 1.0 specification. A path 
	 * that is returned by <code>locate</code> is just such a path.
	 *
	 * <p>If a startNode is specified, the path is assumed to have that startNode 
	 * as its context node. If the startNode is <code>null</code>, the model's start nodes 
	 * will be used as supplying potential context nodes for the path.
	 *
	 *<p>The parameters for searching for a node can be defined in  
	 * the <code>Configuration.MODEL_ID</code> symbol pool using the public symbolic constants of this interface.
	 * 
	 * @param path - path to desired node in the model
	 * @param startNode - context for the path (may be <code>null</code>)
	 * @return desired node or <code>null</code> if no object in the model
	 * matches the given path
	 * @see <a href="http://www.w3.org/tr/xpath">XPath 1.0 Specification</a>
	 */
	public Object find (String path, Object startNode);
	
	/**
	 * finds all objects in a model that match the specified path. The path must be a 
	 * valid XPath expression according to the XPath 1.0 specification. A path 
	 * that is returned by <code>locate</code> is just such a path.
	 *
	 * <p>If a startNode is specified, the path is assumed to have that startNode 
	 * as its context node. If the startNode is <code>null</code>, the model's start nodes 
	 * will be used as supplying potential context nodes for the path.
	 * 
	 *<p>The parameters for searching for a node can be defined in  
	 * the <code>Configuration.MODEL_ID</code> symbol pool using the public symbolic constants of this interface.
	 * 
	 * @param path - path to desired nodes in the model
	 * @param startNode - context for the path (may be <code>null</code>)
	 * @return desired nodes or an empty array if no objects in the model
	 * match the given path
	 * @see <a href="http://www.w3.org/tr/xpath">XPath 1.0 Specification</a>
	 */
	public Object[] findAll (String path, Object startNode);
	
	/**
	 * describe the specified element. Identification may occur by providing
	 * the element's name, id, location on screen, textual content, and the like. The primary
	 * goal of this method is to return a set of properties that will aid developers or testers in
	 * finding the element in the context in which it was originally created, 
	 * defined, or rendered. Thus, the returned set of properties is usually meant to be displayed.
	 * 
	 * @param element element to be described or distinguished
	 * @return set of properties distinguishing the element  
	 */
	public Properties describe (Object element);
	
} // INodeLocator

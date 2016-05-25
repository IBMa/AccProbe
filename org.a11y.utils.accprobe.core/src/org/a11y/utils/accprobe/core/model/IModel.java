/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model;

import org.a11y.utils.accprobe.core.model.events.IModelChangeListener;
import org.a11y.utils.accprobe.core.model.locate.INodeLocator;
import org.a11y.utils.accprobe.core.model.traverse.INodeWalker;


/**
 * a model embodies any object that can be represented as a
 * directed graph. Such a structure possesses the following characteristics:
*
 * <p><ul>
 * <li>can be represented as a set of nodes <b>n</b> and a set of paths between nodes <b>P</b>
 * <li>contains a set of root or start nodes <b>R</b>
 * <li>each node in <b>N</b> provides a means by which to access its successor nodes (i.e. the
 * nodes to which it is connected via a path in <b>P</b>
 * <li>a means by which to add new nodes or components to the set <b>N</b>
 * <li>can be described as either top-down or bottom-up, depending upon the way in which new nodes
 * are added to the structure
 * </ul>
*
 * <p>This abstraction allows A11Y componentry to examine and manipulate structures 
 * composed of a variety of nodes and or created and assembled in a multitude of runtime platforms. Supported 
 * models are declared using the <code>org.a11y.utils.accprobe.core.model.modelTypes</code> extension point. 
*
 * <p>The <code>IRenderableModel</code> sub-interface is used by A11Y to examine and manipulate structures that can be 
 * rendered, like a graphical user interface (GUI) or Document Object Model (DOM) tree in a browser. Besides
 * the properties of a general model structure, it reveals more GUI-specific behaviors
 * and attributes.  
*
 * @see org.a11y.utils.accprobe.core.model.IRenderableModel
 * @author Mike Squillace
 */
public interface IModel
{

	/**
	 * return the type of the model. This may be one of the 
	 * pre-defined constants in <code>org.a11y.utils.accprobe.core.config.Configuration</code>.
	 *
	 * @return name of model implemented by this model class
	 * @see org.a11y.utils.accprobe.core.config.IConfiguration
	 */
	// TODO replaced by ValidatableModel.getName()
	public String getName ();

	/**
	 * get the order of the graph-based model starting at the given head.  The order of a graph is the number of verticies or
	 * nodes that comprise it.
	 *
	 * @return order of the subgraph
	 */
	public int getOrder (Object head);

	/**
	 * retrieves a locator for finding and identifying nodes in the model. The locator 
	 * can provide an XPath expression that describes a node in the model or, given a 
	 * valid XPath expression, find an object in the model.
	 * 
	 * @return node locator
	 */
	public INodeLocator getNodeLocator ();

	/**
	 * retrieve the <code>NodeWalker</code> for traversing elements in this model
	 * 
	 * @return walker for traversing nodes in this model or <code>null</code> if 
	 * no walker is available
	 */
	public INodeWalker getNodeWalker ();

	/**
	 * set the id for a component in the model
	 *
	 * @param comp - component for which id is to be set
	 * @param id - id for component
	 */
	// TODO remove get/setComponentId and rely on set/getLocator on Validator interface
	// TODO move to ValidatableModel.setElementId
	public void setNodeID (Object comp, String id);

	/**
	 * return the id for this component. The id for a component is model-specific. For 
	 * example, it may be the id attribute of an element of a model representing a structure that 
	 * conforms to the W3C Document Object Model (DOM) specification or data unique to that object in some other model.
	 *
	 *<p><b>Note</b>: This method should never return <code>null</code> and should always make an attempt to 
	 *return a unique id that identifies this node from all other nodes in this model.
	 *
	 * @param element - element for which id is desired
	 * @return unique id of element
	 */
	public String getNodeId (Object element);

	/**
	 * return the short name of this element. The id for an element is model-specific. For 
	 * example, it may be the tag name of an element of a model representing a structure that 
	 * conforms to W3the C DOM specification or the simple class name of the object.
	 *
	 * @param element - element for which name is desired
	 * @return name of element or <code>null</code> if not available
	 */
	public String getNodeName (Object element);
	
	/**
	 * returns the type name associated with this nodeName. The nodeName parameter 
	 * is most frequently formed by calling <code>getNodeName</code>. The name returned identifies 
	 * the type that is represented by this node name in the graph.
	 * 
	 * <p>If the specified nodeName was not obtained from <code>getNodeName</code>, then 
	 * implementers should make a rough guess regarding the underlying type on the basis of the package names 
	 * associated with this model via <code>getPackageNames</code>.</p>
	 * 
	 * @param nodeName - as returned by <code>getNodeName</code>
	 * @return type name for the node name specified
	 * @see #getNodeName(Object)Object)
	 * @see #getPackageNames()
	 */
	public String getTypeName (String nodeName);

	/**
	 * returns the default alias prefix.
	 * The default alias prefix (usually a package name)
	 * is used when a fully-qualified name is not used in the 'value' attribute
	 * of an &lt;alias&gt; tag
	 *
	 * @return alias prefix (not including trailing '.')
	 */
	// TODO eliminate
	public String getDefaultAliasPrefix ();

	/**
	 * adds a listener to receive notifications when this model's structure or properties
	 * of its nodes are changed
	 *
	 * @param listener - listener to be notified of updates to the model
	 */
	public void addModelChangeListener (IModelChangeListener listener);

	/**
	 * remove the given listener from the list of listeners to be notified when this model is updated
	 *
	 * @param listener - listener to be removed
	 */
	public void removeModelChangeListener (IModelChangeListener listener);

	/**
	 * returns an array of the names of packages to be imported by CodeProcessors.
	 * Any CodeProcessor used in the context of an <code>IModel</code> will, upon its
	 * instantiation and initialization, import the list of packages and make their traditional short-forms available.
	 * (Of course, short-forms are language-dependent.) Each element of the array is only a name for a
	 * package (e.g. 'my.java.package').
	 *
	 * @return array of names of packages to be imported by a CodeProcessor
	 *         or an array of length 0 for no imports
	 */
	// TODO replace with RuleBase.getRuntime()
	public String[] getPackageNames ();
	
	public String getBaseType();
	
	
} // IModel

/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.model.events;


/**
 * a listener to be implemented by clients who wish to be notified of changes to the model or
 * to its elements.  Structures that are instances of classes that inherit from
 * <code>IModel</code> may wish to extend this interface.
 *
 * @see org.a11y.utils.accprobe.core.model.IModel
 * @author <a href="mailto:masquill@us.ibm.com">Mike Squilace</a>
 */
public interface IModelChangeListener
{

	/**
	 * notifies clients that an element has been inserted into the structure.
	 *
	 * @param event
	 */
	public void nodeInserted (ModelChangeEvent event);

	/**
	 * notifies clients that an element has been removed from the structure.
	 *
	 * @param event
	 */
	public void nodeRemoved (ModelChangeEvent event);

	/**
	 * notifies clients that an element in the structure has been modified.  Different models will want to
	 * determine more precisely which modifications might be of interest to clients.
	 *
	 * @param event
	 */
	public void nodeModified (ModelChangeEvent event);

}

/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core;

import java.util.Map;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * extended interface for exposing more semantically rich properties and relations that might be provided by 
 * some accessibility APIs. The Java Accessibility API is one such API.
 *
 *<p>In most cases, implementations of this interface will be primarily native and platform-dependent.
 *
 * @author Mike Smith
 * @see javax.accessibility.AccessibleContext
 */
public interface IAccessibleElement2 extends IAccessibleElement
{

	/**
	 * get the accessible table element for this object
	 * 
	 * @return table or <code>null</code> if no table is available
	 * @throws InvalidComponentException
	 */
	public IAccessibleTableElement getAccessibleTable ()
		throws InvalidComponentException;

	/**
	 * get the accessible text element for this object
	 * 
	 * @return text object or <code>null</code> if no text is available
	 * @throws InvalidComponentException
	 */
	public IAccessibleTextElement getAccessibleText ()
		throws InvalidComponentException;

	/**
	 * get the accessible image elements for this object
	 * 
	 * @return array of image objects or empty array if no images are available
	 * @throws InvalidComponentException
	 */
	public IAccessibleImageElement[] getAccessibleImage ()
		throws InvalidComponentException;
	
	/**
	 * get the relations for this accessible element. Such objects express this element's 
	 * relation to other accessible elements (e.g. the element is a "member of" or 
	 * "labeled by" some other element). The map 
	 * should have the relation type as key and relation target(s) as value. The value will be an array of 
	 * <code>IAccessibleElement</code> objects.
	 * 
	 * <p><b>Note</b>: All attempts will be made to use keys 
	 * that match one of the pre-defined relation constants in <code>AccessibleConstants</code>. Should the relation type be 
	 * unknown or not match one of the pre-defined constants, the original 
	 * relationship type from the underlying accessibility model will be returned.

	 * @return accessible relations held by this element to other accessible elements
	 * @throws InvalidComponentException
	 */
	public Map getAccessibleRelations () throws InvalidComponentException;
	/**
	 * get the accessible Editable Text for this object
	 * 
	 * @return EditbleText object or <code>null</code> if no EditableText is available
	 * @throws InvalidComponentException
	 */
	public IAccessibleEditableTextElement getAccessibleEditableText ()
		throws InvalidComponentException;
	
}
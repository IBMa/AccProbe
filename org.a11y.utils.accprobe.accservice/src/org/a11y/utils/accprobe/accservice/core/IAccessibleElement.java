/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core;

import java.awt.Rectangle;
import java.util.Set;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 *interface for exposing accessibility-related properties to the validation engine. This 'accessible object' is usually different from the original element and exposes 
 *properties of the original element that aid asisstive technologies in rendering the element in an alternative manner.
 *
 *<p>This interface provides a way for elements to be viewed as accessible objects by exposing the most commonly queried properties. It 
 *permits different structures to present properties to the engine in a consistent manner. 
 *
 *<p>In most cases, implementations of this interface will be primarily native and platform-dependent.
 *
 * @author Mike Squillace
 */
public interface IAccessibleElement
{

	/**
	 * get the element associated with this  accessible element
	 * 
	 * @return original element associated with this accessible or <code>null</code>
	 *         if this object was not initialized in such a way that the original element is available
	 */
	public Object element ();

	/**
	 * checks whether or not this accessible element is in a valid state. Validity
	 * may consist of whether or not the original element has been disposed or destroyed, whether or not 
	 * this accessible was successfully initialized, and so forth.
	 * 
	 * <p>Accessing properties of invalid accessible elements will raise an 
	 * <code>InvalidComponentException</code>.
	 * 
	 * @throws InvalidComponentException
	 */
	public void checkIsValid () throws InvalidComponentException;

	/**
	 * returns the parent accessible object. 
	 * 
	 * @return parent accessible element or <code>null</code>
	 * if no parent for this element exists
	 * @throws InvalidComponentException 
	 */
	public IAccessibleElement getAccessibleParent () throws InvalidComponentException;
	
	/**
	 * retreave the index of this accessible within its parent accessible
	 * 
	 * @return index of this accessible in its parent or -1 if not available
	 * @throws InvalidComponentException
	 */
	public int getAccessibleIndexInParent () throws InvalidComponentException;
	
	/**
	 * return the number of children of this element. 
	 * 
	 * @return number of children of this element
	 * @throws InvalidComponentException
	 */
	public int getAccessibleChildCount () throws InvalidComponentException;

	/**
	 * return all of the children of this accessible element. 
	 * 
	 * @return all children of this element
	 * @throws InvalidComponentException
	 */
	public IAccessibleElement[] getAccessibleChildren () throws InvalidComponentException;

	/**
	 * return the child at the given index of this accessible element. 
	 * 
	 * @return child at the given index of this element
	 * @throws InvalidComponentException
	 */
	public IAccessibleElement getAccessibleChild (int index) throws InvalidComponentException;

	/**
	 * return the name or short description of this element
	 * 
	 * @return name or an empty string if no name is provided
	 * @throws InvalidComponentException
	 */
	public String getAccessibleName () throws InvalidComponentException;

	/**
	 * return the value (e.g. of a scroll bar or combo)
	 * 
	 * @return value or an empty string if no value is provided
	 * @throws InvalidComponentException
	 */
	public Object getAccessibleValue () throws InvalidComponentException;

	/**
	 * return the keyboard shortcut or mnemonic
	 * 
	 * @return keyboard shortcut or an empty string if no shortcut is provided
	 * @throws InvalidComponentException
	 */
	public String getAccessibleKeyboardShortcut ()
		throws InvalidComponentException;

	/**
	 * return the default action
	 * 
	 * @return default action or <code>null</code> if no default action is provided
	 * @throws InvalidComponentException
	 */
	public Object getAccessibleAction () throws InvalidComponentException;

	/**
	 * return description or use of control
	 * 
	 * @return description of control or an empty string if no description is provided
	 * @throws InvalidComponentException
	 */
	public String getAccessibleDescription () throws InvalidComponentException;

	/**
	 * return role or function of control.
	 * 
	 * <p><b>Note</b>: All attempts will be made to 
	 * return one of the pre-defined role constants in <code>AccessibleConstants</code>. Should the role be 
	 * unknown or not match one of the pre-defined constants, the original 
	 * role string from the underlying accessibility model will be returned.
	 * 
	 * @return role of control or an empty string if no role is provided
	 * @see AccessibleConstants
	 * @throws InvalidComponentException
	 */
	public String getAccessibleRole () throws InvalidComponentException;

	/**
	 * return the state of the control. Note that the returned string may contain 
	 * several states separated by commas.
	 * 
	 * <p><b>Note</b>: All attempts will be made to 
	 * return one of the pre-defined state constants in <code>AccessibleConstants</code>. Should the state be 
	 * unknown or not match one of the pre-defined constants, the original 
	 * state from the underlying accessibility model will be returned.
	 * 
	 * @return state of control or an empty set if no state is provided
	 * @see AccessibleConstants
	 * @throws InvalidComponentException
	 */
	public Set getAccessibleState () throws InvalidComponentException;

	/**
	 * get the list of selected accessibles. 
	 * 
	 * @return selections or empty array if no selection
	 * @throws InvalidComponentException
	 */
	public IAccessibleElement[] getAccessibleSelection ()
		throws InvalidComponentException;

    /** 
     * Gets the bounds of this object in the form of a Rectangle object. 
     * The bounds specify this object's width, height, and location
     * relative to its parent. 
     *
     * @return A rectangle indicating this component's bounds; null if 
     * this object is not on the screen.  
     */
    public Rectangle getAccessibleLocation()  throws InvalidComponentException;

}
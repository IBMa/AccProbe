/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice.core;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;

/**
 * represents the value of a control and the upper and lower bounds of the control.
 * Note that the type of the value is implementation-dependent. Not all controls have values.
 *  
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public interface IAccessibleValueElement
{

	/**
	 * get the current value
	 * 
	 * @return current value of the control or <code>null</code> if value is not supported
	 * @throws InvalidComponentException 
	 */
	public Object getCurrentValue () throws InvalidComponentException;
	
	/**
	 * set the current value of the control
	 * 
	 * @param value - new value
	 * @throws InvalidComponentException 
	 */
	public void setCurrentValue ( String type, long value) throws InvalidComponentException;
	
	/**
	 * gets the minimum value supported by the control
	 * 
	 * @return minimum value supported
	 * @throws InvalidComponentException 
	 */
	public Object getMinimumValue () throws InvalidComponentException;
	
	/**
	 * gets the maximum value supported by the control
	 * 
	 * @return maximum value supported
	 * @throws InvalidComponentException 
	 */
	public Object getMaximumValue () throws InvalidComponentException;
	
}

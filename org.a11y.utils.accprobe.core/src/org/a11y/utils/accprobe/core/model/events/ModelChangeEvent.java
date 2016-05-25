/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model.events;

import java.util.EventObject;

/**
 * an event object fired by <code>IModel</code> instances
 *
 * @see org.a11y.utils.accprobe.core.model.IModel
 * @author Mike Squilace
 */
public class ModelChangeEvent extends EventObject
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int NODE_INSERTED = 0;

	public static final int NODE_REMOVED = NODE_INSERTED + 1;

	public static final int NODE_MODIFIED = NODE_REMOVED + 1;

	protected String propName;

	protected int eventType;

	protected Object oldValue, newValue;

	/**
	 * create a new model change event with the targetElement as the event source.
	 * 
	 * @param targetElement
	 * @param eventType one of the constants in this class
	 */
	public ModelChangeEvent (Object targetElement, int eventType) {
		super(targetElement);
		this.eventType = eventType;
	}

	/**
	 * create a model change event. The final three parameters are only valid if the event type 
	 * is <code>NODE_MODIFIED</code>.
	 * 
	 * @param targetElement source of event within the model
	 * @param propName name of property modified
	 * @param oldValue
	 * @param newValue
	 */
	public ModelChangeEvent (Object targetElement, int eventType,
								String propName, Object oldValue,
								Object newValue) {
		super(targetElement);
		this.eventType = eventType;
		this.propName = propName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * get the type of event
	 * 
	 * @return type of event
	 */
	public int getEventType () {
		return eventType;
	}

	/**
	 * get the name of the property that was modified
	 *
	 * @return modified property name
	 */
	public String getModifiedPropertyName () {
		return propName;
	}

	/**
	 * get the former value of the modified property
	 *
	 * @return former value of modified property
	 */
	public Object getOldValue () {
		return oldValue;
	}

	/**
	 * get the new value of the modified property
	 *
	 * @return new value of modified property
	 */
	public Object getNewValue () {
		return newValue;
	}
}

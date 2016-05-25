/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.event;

import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.events.ModelEvent;



public class AccessibilityModelEvent extends ModelEvent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ACCESSIBLE_NAME_PROPERTY = "accessibleName";
	public static final String ACCESSIBLE_ROLE_PROPERTY = "accessibleRole";
	public static final String ACCESSIBLE_STATE_PROPERTY = "accessibleState";
	public static final String MISC_DATA_PROPERTY = "miscData";
	private static final int INCONTEXT_PROPS_LENGTH = 6;

	/**
	 * create an event based on a Windows callback. The source object may be an <code>IAccessibleElement</code> instance
	 * or merely contain information about such an instance.
	 * 
	 * @param source
	 */
	public AccessibilityModelEvent (Object source) {
		super(source);
		
		if (source instanceof IAccessibleElement) {
			IAccessibleElement acc = (IAccessibleElement) source;
			try {
				setAccessibleName(acc.getAccessibleName());
				setAccessibleRole(acc.getAccessibleRole());
				setAccessibleState((acc.getAccessibleState()).toString());
			} catch (InvalidComponentException e) {
			}
		} else if (source instanceof String[]) {
			String props[] = (String[]) source;
			if(props!=null && props.length==INCONTEXT_PROPS_LENGTH){
				long timeMillis = Long.parseLong(props[4]);
				setAccessibleName(props[0]);
				setAccessibleRole(props[1]);
				setAccessibleState(props[2]);
				setEventType(props[3]);
				setTimeMillis(timeMillis);
				setMiscData(props[5]);
			}
		}
	}

	/**
	 * convenience method for getting the accessible name of the accessible that fired this event
	 * 
	 * @return name of accessible that fired this event
	 */
	public String getAccessibleName () {
		return (String) getProperty(ACCESSIBLE_NAME_PROPERTY);
	}
	
	/**
	 * convenience method for setting the accessible name of the accessible that fired this event. This event should nt 
	 * be called by clients. It is made public for use by native code capturing the event.
	 *  
	 * @param accName
	 */
	public void setAccessibleName (String accName) {
		setProperty(ACCESSIBLE_NAME_PROPERTY, accName);
	}
	
	/**
	 * convenience method for getting the accessible role of the accessible that fired this event
	 * 
	 * @return role of accessible that fired this event
	 */
	public String getAccessibleRole () {
		return (String) getProperty(ACCESSIBLE_ROLE_PROPERTY);
	}
	
	/**
	 * convenience method for setting the accessible role of the accessible that fired this event. This event should nt 
	 * be called by clients. It is made public for use by native code capturing the event.
	 *  
	 * @param accRole
	 */
	public void setAccessibleRole (String accRole) {
		setProperty(ACCESSIBLE_ROLE_PROPERTY, accRole);
	}

	/**
	 * convenience method for getting the misc text Data of the accessible that fired this event
	 * 
	 * @return miscData of accessible that fired this event
	 */
	public String getMiscData () {
		return (String) getProperty(MISC_DATA_PROPERTY);
	}
	
	/**
	 * convenience method for setting the misc Text Data of the accessible that fired this event. This event should nt 
	 * be called by clients. It is made public for use by native code capturing the event.
	 *  
	 * @param accRole
	 */
	public void setMiscData (String data) {
		setProperty(MISC_DATA_PROPERTY, data);
	}
	public void setAccessibleState (String accState) {
		setProperty(ACCESSIBLE_STATE_PROPERTY, accState);
	}
	
	public String getAccessibleState () {
	 Object state = getProperty(ACCESSIBLE_STATE_PROPERTY);
	 return state.toString();
	}
	

}
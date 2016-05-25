/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.model.events;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * an event fired within a model
*
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public abstract class ModelEvent extends EventObject
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TIMESTAMP_PROPERTY = "timestamp";
	public static final String EVENT_TYPE_PROPERTY = "eventType";
	
	protected Map<String, Object> properties = new HashMap<String, Object>();
	
	/**
	 * @param source
	 */
	public ModelEvent(Object source) {
		super(source);
	}
	
	public Object getProperty (String name) {
		return properties.get(name);
	}
	
	public void setProperty (String name, Object value) {
		if (name != null && name.trim().length() > 0 && value != null) {
			properties.put(name, value);
		}
	}
		
	/**
	 * convenience method for getting the timestamp of the event
	 * 
	 * @return timestamp of event
	 */
		public long getTimeMillis () {
			Long time = (Long) getProperty(TIMESTAMP_PROPERTY);
			long res = 0;
			if (time != null) {
				res = time.longValue();
			}
			return res;
		}
		
		/**
		 * convenience method for retreaving the timestamp of the event
		 * 
		 * @param time timestamp of event
		 */
		public void setTimeMillis (long time) {
			setProperty(TIMESTAMP_PROPERTY, new Long(time));
		}
		
		/**
		 * convenience method for getting the symbolic name or type of the event. This should be a more informative token than the 
		 * event id used by the underlying model.
		 * 
		 * @return symbolic name or type of event
		 */
		public String getEventType () {
			return (String) getProperty(EVENT_TYPE_PROPERTY);
		}
		
		/**
		 * convenience method for setting the symbolic name or type of the event. This should be a more informative token than the 
		 * event id used by the underlying model. Typically, this will be obtained from the corresponding <code>ModelEventType</code> object.
		 * 
		 * @param eventType symbolic name or type of event
		 * @see ModelEventType
		 */
		public void setEventType (String eventType) {
			if (eventType != null && eventType.trim().length() > 0) {
				setProperty(EVENT_TYPE_PROPERTY, eventType);
			}
		}
		
}

/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.model.events;

/**
 * represents an event type that can be fired within a model
*
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public class ModelEventType
{

	private String _eventName;
	private Object _eventId;
	private Class<?> _sourceType;
	private Class<?> _eventType;
	
	/**
	 * create a model event type to represent an event type for a given model. Event types represent 
	 * events that can be fired within a model and are typically identified by unique ids within the model's API.
	 * 
	 * @param eventName symbolic or printable name of event
	 * @param eventId unique event id or type within the model
	 */
	public ModelEventType (String eventName, Object eventId) {
		this(eventName, eventId, null, null);
	}
	
	/**
	 * create a model event type to represent an event type for a given model. Event types represent 
	 * events that can be fired within a model and are typically identified by unique ids within the model's API.
	 * 
	 * @param eventName symbolic or printable name of event
	 * @param eventId unique event id or type within the model
	 * @param sourceType type of object that can fire this event
	 */
	public ModelEventType (String eventName, Object eventId, Class<?> sourceType) {
		this(eventName, eventId, sourceType, null);
	}
	
	/**
	 * create a model event type to represent an event type for a given model. Event types represent 
	 * events that can be fired within a model and are typically identified by unique ids within the model's API.
	 * 
	 * @param eventName symbolic or printable name of event
	 * @param eventId unique event id or type within the model
	 * @param sourceType type of object that can fire this event
	 * @param eventType type of event fired by the object, which must inherit from <code>java.util.EventObject</code>
	 */
	public ModelEventType (String eventName, Object eventId, Class<?> sourceType, Class<?> eventType) {
		_eventName = eventName;
		_eventId = eventId;
		_sourceType = sourceType;
		_eventType = eventType;
	}
	
	/**
	 * return the printable or symbolic name of the event
	 * 
	 * @return symbolic or printable name of event
	 */
	public String getEventName () {
		return _eventName;
	}

	/**
	 * return unique event id or type within model framework. This is uaually part of the 
	 * published API for the model.
	 * 
	 * @return unique id of event within the model framework
	 */
	public Object getEventId () {
		return _eventId;
	}

	/**
	 * return the type of objects that can fire this event
	 * 
	 * @return type of object that fires this event in the model
	 */
	public Class<?> getSourceType () {
		return _sourceType;
	}

	/**
	 * return the type of the event
	 * 
	 * @return type of the event
	 */
	public Class<?> getEventType () {
		return _eventType;
	}

}

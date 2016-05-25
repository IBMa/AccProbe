/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model;

import java.awt.Rectangle;

import org.a11y.utils.accprobe.core.model.events.IModelEventListener;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;


/**
 * specialization of <code>IModel</code> for renderable structures such as GUIs
 *
 * @author Mike Squillace
 */
public interface IRenderableModel extends IModel
{

	/**
	 * get the model event types for the given source type. A <code>ModelEventType</code> object 
	 * describes the types of events that can be fired within the context of this runtime model. If the sourceType is <code>null</code>, 
	 * then all of the event types of which this model is aware will be returned else only events that can be fired by the given sourceType 
	 * will be returned.
	 *   
	 * @param sourceType type to be queried for events
	 * @return all events that can be fired by instances of the given type or 
	 * all events of which this model is aware
	 * @see org.a11y.utils.accprobe.core.model.events.ModelEventType
	 */
	public ModelEventType[] getModelEventTypes (Class<?> sourceType);
	
	/**
	 * get the <code>ModelEventType</code> instances associated with the given event id. The event id is model-specific 
	 * and will usually be presented in the model's API. What constitutes an event id and how they are determined should be 
	 * specified in clients that implement this model.
	 * 
	 * @param eventId model-specific event id
	 * @return <code>ModelEventType</code> instances associated with this event id or <code>null</code>
	 * if no event types are associated with this id
	 */
	public ModelEventType getModelEventType (Object eventId);
	
	/**
	 * register a listener to receive notification when events with the given eventTypes are fired within this 
	 * model. The eventTypes should be obtained from one of the <code>getModelEventTypes</code> methods. 
	 * 
	 * @param listener listener to be notified when specified events occur
	 * @param eventTypes event types for which listener is being registered
	 * @see #getModelEventType(Object)
	 * @see #getModelEventTypes(Class)
	 */
	public void registerModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes);
	
	/**
	 * register a listener to receive notification when events with the given eventTypes are fired within this 
	 * model. The eventTypes should be obtained from one of the <code>getModelEventTypes</code> methods. 
	 * Optional parameters can also be passed to facilitate additional side effects or for additional information about the context 
	 * in which events are being fired. 
	 * 
	 * @param listener listener to be notified when specified events occur
	 * @param eventTypes event types for which listener is being registered
	 * @param params - additional parameters (may be <code>null</code>)
	 * @see #getModelEventType(Object)
	 * @see #getModelEventTypes(Class)
	 */
	public void registerModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes, Object[] params);
	
	/**
	 * unregister a previously registered listener.  The eventTypes should be obtained from either 
	 * of the <code>getModelEventTypes</code> methods.
	 * 
	 * @param listener listener to be unregistered
	 * @param eventTypes event types for which listener is to be unregistered
	 * @see #registerModelEventListener(IModelEventListener, ModelEventType[])
	 * @see org.a11y.utils.accprobe.core.model.events.ModelEventType
	 */
	public void unregisterModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes);
	
	/**
	 * returns whether or not the currently executing thread is the UI thread.
	 * The UI thread is the thread upon which actions effecting the state or drawing of a component
	 * or the event handlers associated with a component are to be executed. If this method returns
	 * <code>false</code>, the client will typically need to invoke the <code>invokeOnUIThread</code> method in order to effect the GUI.
	 *
	 * @return <code>true</code> if the current thread is the UI thread, <code>false</code> otherwise
	 * @see #invokeOnUIThread(Runnable) 
	 */
	public boolean isUIThread ();

	/**
	 * executes the given Runnable from within the UI thread. This method will typically be called if
	 * <code>isUIThread</code> returns <code>false</code>.
	 * 
	 * @param runnable - Runnable to be invoked in UI thread
	 * @see #isUIThread()
	 */
	public void invokeOnUIThread (Runnable runnable);

	/**
	 * asyncronously executes the given Runnable from within the UI thread. That is, the <code>Runnable</code>
	 * is placed in a queue and control is returned immediately to the calling thread.
	 * 
	 * @param runnable - Runnable to be invoked in UI thread
	 * @see #invokeOnUIThread(Runnable)
	 */
	public void asyncInvokeOnUIThread (Runnable runnable);

	/**
	 * returns whether or not the specified component is currently visible
	 * 
	 * @param component - component to be tested
	 * @return whether or not the component is currently visible or showing on the screen
	 */
	public boolean isVisible (Object component);

	/**
	 * returns whether or not the specified component is valid for access. Components may be 
	 * invalid because their underlying resources have been disposed, because the device that 
	 * displays or renders them is destroyed, or for many other reasons.
	 * 
	 * @param component - component to be tested
	 * @return <code>true</code> if this component is valid, <code>false</code> otherwise
	 * @see org.a11y.utils.accprobe.core.model.InvalidComponentException
	 */
	public boolean isValid (Object component);

	/**
	 * request the focus for the specified component. This method
	 * should be invoked, for example, just prior to validation for a report
	 * that reflects the state of the component while visible.
	 * 
	 * @param comp - component for which focus is desired
	 * @return <code>true</code> if focus is successful, <code>false</code> otherwise
	 */
	public boolean requestFocusFor (Object comp);

	/**
	 * gets the rectangle bounding the given element
	 *
	 * @param element - element for which bounds are desired
	 * @return bounding rectangle of component
	 */
	public Rectangle getBoundingRectangle (Object element);

	/**
	 * highlight or visually indicate the element that is being examined. Highlighting may take place by 
	 * placing a border around the element, flashing the element, or changing its background color.
	 * 
	 * @param element - the element to be highlited
	 */
	public void highlight (Object element);
	
} // IRenderableModel

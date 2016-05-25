/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.a11y.utils.accprobe.core.model.events.IModelEventListener;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;


/**
 * base implementation for <code>IRenderableModel</code>. Clients should extend this class 
 * rather than attempting to implement <code>IRenderableModel</code>.
*
 * @author Mike Squillace
 *
 */
public abstract class AbstractRenderableModel extends AbstractModel
	implements IRenderableModel
{

	/** keyed by event ids with values that are instances of <code>ModelEventType</code> */
	protected Map<Object, ModelEventType> eventIdMap = new HashMap<Object, ModelEventType>();

	/**
	 * create an IRenderableModel implementation
	 * 
	 * @param modelType - name of model
	 */
	public AbstractRenderableModel (String modelType) {
		super(modelType);
		initEventIdMap();
	}
	
	/** {@inheritDoc} */
	public ModelEventType getModelEventType (Object eventId) {
		return (ModelEventType) eventIdMap.get(eventId);
	}
	
	/** {@inheritDoc} */
	public boolean isVisible (Object comp) {
		return false;
	}

	/** {@inheritDoc}
	 *  
	 *  default implementation merely checks that the component is not <code>null</code>.
	 */
	public boolean isValid (Object comp) {
		return comp != null;
	}

	/** {@inheritDoc} */
	public boolean requestFocusFor (Object comp) {
		return false;
	}

	/** {@inheritDoc} */
	public void highlight (Object element) {
	}

	/** {@inheritDoc}
	 *  
	 *  default implementation returns <code>null</code>
	 */
	public Rectangle getBoundingRectangle (Object element) {
		return null;
	}

	/** {@inheritDoc}
	 *  
	 *  default implementation returns <code>null</code>
	 */
	public ModelEventType[] getModelEventTypes (Class<?> c) {
		return null;
	}
	
	/** {@inheritDoc}
	 * 
	 * equivalent to calling <code>registerModelEventListener(listener, eventTypes, null)</code>
	 */
	public void registerModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes) {
		registerModelEventListener(listener, eventTypes, null);
	}
	
	/** {@inheritDoc} */
	public void registerModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes, Object[] params) {
	}
	
	public void unregisterModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes)  {
	}
	
	/**
	 * used to initialize the eventId map, which is keyed by eventId that corresponds to a <code>ModelEventType</code> 
	 * instance. Event ids are model-specific and typically defined as part of that model's definition.
	 * 
	 * @see org.a11y.utils.accprobe.core.model.events.ModelEventType
	 * @see #registerModelEventListener(IModelEventListener, ModelEventType[])
	 */
	protected void initEventIdMap () {
	}
	
	protected void initEventIdMap(Class<?> sourceType, Class<?> eventFieldProviderType, String fieldPrefix) {
		for (Field field : eventFieldProviderType.getFields()) {
			if (field.getName().startsWith(fieldPrefix)) {
				try {
					Object eventId = field.get(null);
					ModelEventType eventType = new ModelEventType(field.getName(), eventId, sourceType);
					eventIdMap.put(eventId, eventType);
				} catch (Exception e) {
					// do nothing - should never happen
				}
			}
		}
	}

} // AbstractRenderableModel

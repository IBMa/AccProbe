/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice;

import java.io.Serializable;

import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * a service for accessing platform-specific accessibility models. An
 * <code>IAccessibilityService</code> object is used to access two sorts of
 * objects:
*
 * <p>
 * <ol>
 * <li>accessible elements corresponding to components of a supported model
 * (e.g. GUI components, document elements)
 * <li>the event service for registering event listeners with the underlying
 * accessibility model
 * </ol>
 * </p>
*
 * <p>
 * An <code>IAccessibilityService</code> should be registered with the
 * <code>AccessibilityServiceManager</code> and must include only the default
 * no-argument constructor.
*
 * @see AccessibilityServiceManager#registerAccessibilityService(String, String)
 * @author Mike Squillace
*
 */
public interface IAccessibilityService extends Serializable
{

	static final long serialVersionUID = -6130675733288283784L;
	/**
	 * create an <code>IAccessibleElement</code> from the given object and
	 * optional parameters. <code>IAccessibleElement</code> instances are used
	 * to access accesibility-related properties of the given object and need
	 * not (and, in most cases, are not) the same object as the given object.
	 * 
	 * @param obj
	 *            object for which an accessible is desired
	 * @param params
	 *            optional parameters for creating the accessible (may be
	 *            <code>null</code>)
	 * @return an <code>IAccessibleElement</code> for the given object
	 * @throws InvalidComponentException
	 *             if the given object is somehow invalid or an accessible
	 *             cannot b created for the given object
	 */
	public IAccessibleElement createAccessibleElement(Object obj, Object[] params) throws InvalidComponentException;

	/**
	 * retreaves the event services for the underlying accessibility
	 * infrastructure. The <code>IAccessibilityEventService</code> allows
	 * clients to register event listeners to listen for low-level,
	 * platform-specific events that are used by accessibility devices (like
	 * assistive technologies).
	 * 
	 * @return the <code>IAccessibilityEventService</code> object for this
	 *         accessibility infrastructure or <code>null</code> if no such
	 *         event service is available
	 */
	public IAccessibilityEventService getAccessibilityEventService();

	/**
	 * returns a service for windowing-related functionality. This includes
	 * getting the top-level windows of whatever the platform considers to be
	 * its desktop, adding listeners to observe creation and destruction of
	 * top-level windows, and getting the active or current window.
	 * 
	 * @return windowing service for this platform or <code>null</code> if no
	 *         such service exists
	 */
	public IWindowService getWindowService();

	/**
	 * initialize the service by loading native libraries, configuring
	 * platform-specific resources, and the like. This method will be called by
	 * the service manager upon instantiating the service.
	 * @throws AccessibilityServiceException 
	 * 
	 */
	public void initialize() throws AccessibilityServiceException;

}

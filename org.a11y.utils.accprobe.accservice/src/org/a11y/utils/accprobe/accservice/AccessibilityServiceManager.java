/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;



/**
 * a manager for coordinating <code>IAccessibilityService</code> objects. <code>IAccessibilityService</code> objects are used to 
 * access native accessibility infrastructures in a variety of runtime platforms and environments.
*
 * @see IAccessibilityService
 * @author Mike Squillace
 *
 */
public final class AccessibilityServiceManager
{

	private static AccessibilityServiceManager _mgrInstance;
	
	private Map _accServicesMap = new HashMap();
	
	private Logger logger = Logger.getLogger(LoggingUtil.ACCSERVICE_LOGGER_NAME);

	protected AccessibilityServiceManager()
			throws AccessibilityServiceException {
	}
	
	/**
	 * get the single instance of this manager
	 * 
	 * @return singleton copy of this manager
	 * @throws AccessibilityServiceException if there is a problem instantiating the manager
	 */
	public static AccessibilityServiceManager getInstance () throws AccessibilityServiceException {
		if (_mgrInstance == null) {
			_mgrInstance = new AccessibilityServiceManager();
		}
		return _mgrInstance;
	}
	
	/**
	 * register the accessibility service with the given name. 
	 * Note that only one service can be registered for a given name.
	 * 
	 * <p>The manager uses lazy instantiation for creating <code>IAccessibilityService</code> objects. If a service 
	 * with the given name is requested, the no-argument constructor of the given class name will be invoked and the <code>initialize</code> method 
	 * will be called on the service.
	 * 
	 * @param name - name of service
	 * @param accServiceClassName - class name of accessibility service to register
	 * @see #getAccessibilityService(String)
	 */
	public void registerAccessibilityService (String name, String accServiceClassName) {
		if (_accServicesMap.containsKey(name)) {
			logger.log(Level.WARNING, "Service already registered for name " + name + " -- ignoring class " + accServiceClassName);
		} else if (accServiceClassName != null && accServiceClassName.length() > 0) {
			_accServicesMap.put(name, accServiceClassName);
		}
	}
	
	/**
	 * return the service with the given name. The service with the given name must have 
	 * been registered using the <code>registerAccessibilityService</code> method.
	 * 
	 * <p>The manager uses lazy instantiation for creating <code>IAccessibilityService</code> objects. If a service 
	 * with the given name is requested, the no-argument constructor of the given class name will be invoked and the <code>initialize</code> method 
	 * will be called on the service.
	 * 
	 * @param name name of desired service
	 * @return accessibility service with the given name or <code>null</code> if
	 *  no service with the given name exists
	 *  @see #registerAccessibilityService(String, String)
	 *  @see IAccessibilityService#initialize()
	 *  @throws AccessibilityServiceException if the service cannot be instantiated or initialized
	 */
	public IAccessibilityService getAccessibilityService (String name) throws AccessibilityServiceException {
		Object ser = _accServicesMap.get(name);
		IAccessibilityService service = null;
		logger.log(Level.FINE, name + " " + ser);
		if (ser != null) {
			if (ser instanceof String) {
				try {
					service = (IAccessibilityService) ClassLoaderCache
							.getDefault().classForName((String) ser)
							.newInstance();
					if (service != null) {
						logger.log(Level.INFO, "Initializing accservice " + ser);
						service.initialize();
						_accServicesMap.put(name, service);
					} else {
						throw new AccessibilityServiceException(
								"Failed to instantiate accservice " + ser);
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "exception in getAccessiilityService", e);
					throw new AccessibilityServiceException(e);
				}
			} else if (ser instanceof IAccessibilityService) {
				service = (IAccessibilityService) ser;
			}
		} else {
			logger.log(Level.INFO, "No accessibility service " + name + " found");
		}
		
		return service;
	}
	
	/**
	 * get all registered services
	 * 
	 * @return array of all registered services of this manager
	 */
	public IAccessibilityService[] getRegisteredAccessibilityServices () {
		return (IAccessibilityService[]) _accServicesMap.values().toArray(new IAccessibilityService[_accServicesMap.size()]);
	}

	/**
	 * get all registered services
	 * 
	 * @return array of all registered services of this manager
	 */
	public Map getRegisteredAccessibilityServicesMap() {
		return _accServicesMap;
	}
	
}

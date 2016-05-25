/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
 *
 * Contributors:
 *  IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.a11y.utils.accprobe.accservice.core;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;

public class AccserviceRegistryChangeListener implements
		IRegistryChangeListener {

	protected AccessibilityServiceManager asManager;
	
	public static final String ACCSERVICE_EXTENSION_POINT = "accservices";
	public static final String namespace = AccessibilityServicePlugin.ACCCORE_PLUGIN_ID;
	

	public AccserviceRegistryChangeListener() {
		super();
	}

	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas(namespace);
		for (int i = 0; i < deltas.length; i++) {
			IExtensionPoint point = deltas[i].getExtensionPoint();
			String id = point.getSimpleIdentifier();
			if (id.equals(ACCSERVICE_EXTENSION_POINT)) {
				try {
					asManager = AccessibilityServiceManager
					.getInstance();
				} catch (AccessibilityServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				IExtension[] extensions = point.getExtensions();
				for (int j = 0; j < extensions.length; j++) {
					IConfigurationElement[] elements = extensions[j]
							.getConfigurationElements();
					for (int k = 0; k < elements.length; k++) {
						String name = elements[k].getAttribute("name");
						String classname = elements[k].getAttribute("class");
						asManager.registerAccessibilityService(name, classname);
					}
				}
			}
		}
	}

}

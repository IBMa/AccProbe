/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
*
*
*
 *
 *
 * Contributors:
 *  IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.a11y.utils.accprobe.accservice.core;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.core.A11yCorePlugin;
import org.a11y.utils.accprobe.core.model.ModelPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;


public class AccessibilityServicePlugin extends ModelPlugin
{

	// The plug-in ID
	public static final String ACCCORE_PLUGIN_ID = "org.a11y.utils.accprobe.accservice";

	public static final String ACCESSIBILITY_JAR =  " a11y-accservice.jar";
	
	public static final String ACCESSIBILITY_IA2_BUNDLE =  "org.a11y.utils.accprobe.accservice.win32.ia2";
	public static final String ACCESSIBILITY_IA2_JAR =  "a11y-accservice-ia2.jar";
	public static final String ACCESSIBILITY_MSAA_BUNDLE =  "org.a11y.utils.accprobe.accservice.win32.msaa";
	public static final String ACCESSIBILITY_MSAA_JAR =  "a11y-accservice-msaa.jar";
	
	// The shared instance
	private static AccessibilityServicePlugin plugin;
	
	//registry things
	private AccserviceRegistryChangeListener registryListener;
	private IExtensionRegistry registry;
	/**
	 * The constructor
	 */
	public AccessibilityServicePlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		//set a registry change listener for any future changes
		registry = Platform.getExtensionRegistry();
		registryListener = new AccserviceRegistryChangeListener();
		if (registry != null && (getPluginId().equals(ACCCORE_PLUGIN_ID))) {
			registry.addRegistryChangeListener(registryListener);
		}
		//now add all the accservices we already know about
		IExtensionPoint[] exps = registry.getExtensionPoints(ACCCORE_PLUGIN_ID);
		AccessibilityServiceManager asManager = AccessibilityServiceManager
				.getInstance();
		for (int i = 0; i < exps.length; i++) {
			IExtension[] extensions = exps[i].getExtensions();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		if (getPluginId().equals(ACCCORE_PLUGIN_ID)) {
			if (registry != null && registryListener != null) {
				registry.removeRegistryChangeListener(registryListener);
			}
		}
			
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static A11yCorePlugin getDefault() {
		return plugin;
	}

	protected String getPluginId () {
		return ACCCORE_PLUGIN_ID;
	}

}

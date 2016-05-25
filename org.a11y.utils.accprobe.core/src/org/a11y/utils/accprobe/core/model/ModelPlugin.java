/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.core.model;

import org.a11y.utils.accprobe.core.A11yCorePlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ModelPlugin extends A11yCorePlugin {

	// The plug-in ID
	public static final String MODEL_PLUGIN_ID = "org.a11y.utils.accprobe.core";
	
	public static final String MODEL_TYPE_EXTENSION = "modelTypes";
	
	//registry things
	private IExtensionRegistry registry;
	private ModelRegistryChangeListener registryListener;
	/**
	 * The constructor
	 */
	public ModelPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		//set a registry change listener for any future changes
		registry = Platform.getExtensionRegistry();
		registryListener = new ModelRegistryChangeListener();
		if (registry != null && (getPluginId().equals(MODEL_PLUGIN_ID))) {
			registry.addRegistryChangeListener(registryListener);
		}
		
		 IConfigurationElement [] elements = registry.getConfigurationElementsFor(MODEL_PLUGIN_ID, MODEL_TYPE_EXTENSION);
		 for (int i=0;i<elements.length;i++){			
			// force these plugins to load so that they will be found by when the RuleViewer tries to load the rulebases
			String plugin = elements[i].getContributor().getName();
			if (plugin!= null){
				try {
				 Platform.getPlugin(plugin);
				} catch (IllegalStateException ise){
					//this seems to be caused by timing issues and only occurs for Accprobe - no harm done
					//LoggingUtil.println(IReporter.SYSTEM_NONFATAL, "Error loading model from plugin "+plugin);
				}
			}
		}		 
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		if (getPluginId().equals(MODEL_PLUGIN_ID)) {
			if (registry != null && registryListener != null) {
				registry.removeRegistryChangeListener(registryListener);
			}
		}
	}

	protected String getPluginId () {
		return MODEL_PLUGIN_ID;
	}

}

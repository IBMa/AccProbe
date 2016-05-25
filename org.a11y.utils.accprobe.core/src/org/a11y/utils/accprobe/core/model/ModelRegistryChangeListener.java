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
package org.a11y.utils.accprobe.core.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.core.config.ConfigurationException;
import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.a11y.utils.accprobe.core.runtime.IRuntimeContext;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;

public class ModelRegistryChangeListener implements IRegistryChangeListener {

	protected IConfiguration configuration;
	protected IRuntimeContext runtimeContext;
	public static final String namespace = ModelPlugin.MODEL_PLUGIN_ID;
	public static final String MODEL_TYPES_EXTENSION_POINT = "modelTypes";
	public static final String NODE_FILTERS_EXTENSION_POINT = "nodeFilters";

	public ModelRegistryChangeListener() {
		super();
	}

	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas(namespace);
		for (int i = 0; i < deltas.length; i++) {
			IExtensionPoint point = deltas[i].getExtensionPoint();
			String id = point.getSimpleIdentifier();
			if (id.equals(MODEL_TYPES_EXTENSION_POINT)
				|| id.equals(NODE_FILTERS_EXTENSION_POINT)) {

				IExtension[] extensions = point.getExtensions();
				for (int j = 0; j < extensions.length; j++) {
					try {
						configuration.addConfigurationData(extensions[j]);
					} catch (ConfigurationException e) {
						Logger.getLogger(LoggingUtil.A11Y_CORE_LOGGER_NAME)
							.log(Level.WARNING, "ConfigurationException" +e.getMessage());
					}
				}
			}
		}
	}

}

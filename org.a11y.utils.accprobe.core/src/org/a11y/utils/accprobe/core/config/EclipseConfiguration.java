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
package org.a11y.utils.accprobe.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

public class EclipseConfiguration extends AbstractConfiguration {

	static final long serialVersionUID = 7981371198710093969L;
	
	public static final String MODEL_NAME_ATTRIBUTE = "name";
	public static final String ALIAS_ID = "alias";

	transient private HashMap modelMap;
	transient private HashMap filterMap;
	transient private HashMap pluginMap;
	transient private HashMap attributeMap;
	private Stack elementStack = new Stack();
	private String pluginId;

	/**
	 * create a Configuration from extension point information
	 * 
	 * 
	 * @throws ConfigurationException
	 */
	public EclipseConfiguration() throws ConfigurationException {
		super();
	}
	
	public String[] getModelTypes () {
		//add all data from declared entry points
		 IExtensionPoint [] extPts = Platform.getExtensionRegistry().getExtensionPoints("org.a11y.utils.accprobe.core");
		 for (IExtensionPoint point : extPts) {
			 for (IExtension extension : point.getExtensions()) {
				 try {
					addConfigurationData(extension);
				} catch (ConfigurationException e) {
					Logger.getLogger(LoggingUtil.A11Y_CORE_LOGGER_NAME).log(Level.WARNING, "Could not configure for " + extension.getNamespaceIdentifier() + ", " + extension.getSimpleIdentifier());
				}
			 }
		  }
		 
		 return super.getModelTypes();
	}

	/**
	 * treats data object as an
	 * <code>IConfigurationElement (or IExtension??)</code>.
	 * 
	 * @param data
	 *            configuration data in the form of a
	 *            <code>IConfigurationElement (or IExtension??)</code>
	 * @throws ConfigurationException
	 */
	public void addConfigurationData(Object data) throws ConfigurationException {
		if (data instanceof IConfigurationElement) {
			IConfigurationElement element = (IConfigurationElement) data;
			pluginId = element.getNamespaceIdentifier();
			addElement(element);
		} else if (data instanceof IExtension) {
			IExtension extension = (IExtension) data;
			pluginId = extension.getNamespaceIdentifier();
			for (IConfigurationElement configElement : extension.getConfigurationElements()) {
				addElement(configElement);
			}
		}
	}

	private void addElement(IConfigurationElement element) throws ConfigurationException {
		/*
		 * our extension points are currently either model types, or nodeFilters
		 * This version is based  on just to get it working for now, then make it more generic to
		 * accommodate later additions and be more efficient
		 */
		String elementName = element.getName();
		if (elementName.equals(MODEL_ID)) {
			// create the model symbol pool if it doesn't exist and then select it
			if (_configMap.containsKey(elementName)) {
				setSymbolPool(elementName);
				modelMap = (HashMap) _configMap.get(elementName);
			} else {
				createSymbolPool(elementName);
				modelMap = new HashMap();
				_configMap.put(elementName, modelMap);
			}
			// get model attributes and add the model to the model pool
			attributeMap = new HashMap();
			for (String attrName : element.getAttributeNames()) {
				String attrValue = element.getAttribute(attrName);
				if (attrName.equals("class")) {
					try {
						Object model = element.createExecutableExtension("class");
						if (model != null) {
							attributeMap.put("modelInstance", model);
						}
					} catch (Exception e) {
					}
				}
				attributeMap.put(attrName, attrValue);
			}
			String modelName = (String) attributeMap.get(MODEL_NAME_ATTRIBUTE);
			modelMap.put(modelName, attributeMap);

			// then get all of its children and process them too
			IConfigurationElement[] children = element.getChildren();
			if (children.length > 0) {
				elementStack.push(modelName);
				for (int i = 0; i < children.length; i++) {
					addElement(children[i]);
				}
				elementStack.pop();
			}
		} else if (elementName.equals(IConfiguration.FILTER_ID)) {
			// create the filter symbol pool if it doesn't exist and then select it
			if (_configMap.containsKey(elementName)) {
				setSymbolPool(elementName);
				filterMap = (HashMap) _configMap.get(elementName);
			} else {
				createSymbolPool(elementName);
				filterMap = new HashMap();
				_configMap.put(elementName, filterMap);
			}
			// get filter attributes and add the filter to the filter pool
			attributeMap = new HashMap();
			for (String attrName : element.getAttributeNames()) {
				String attrValue = element.getAttribute(attrName);
				attributeMap.put(attrName, attrValue);
			}
			String filterModel = (String) attributeMap
					.get(IConfiguration.FILTER_MODEL_ATTRIBUTE);
			filterMap.put(filterModel, attributeMap);
		} else if (elementName.equals(ALIAS_ID)) {
			// get the attributes and add this element to
			// the current model alias pool - <model_name>_aliases)
			String key = element.getAttribute("name");
			String value = element.getAttribute("value");
			setParameter(key, value);
		} else {
			throw new ConfigurationException("addElement - unknown element " + elementName);
		}
	}

	/*
	 * Adaptor method to handle the fact that we have two different
	 * Configurations adding data to the configMap. This ensures that they both
	 * add it to the same map
	 */
	 //TODO AKF - remove this if u can get class var solution working
	protected Map addConfigurationData(Object data, Map configMap)
			throws ConfigurationException {
		_configMap = configMap;
		addConfigurationData(data);
		return _configMap;
	}

}

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.core.config.ConfigurationException;
import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;
import org.a11y.utils.accprobe.core.runtime.IRuntimeContext;
import org.a11y.utils.accprobe.core.runtime.RuntimeContextFactory;


/**
 * retrieves the desired <code>IModel</code> based upon the raven:model
 * attribute value of the &lt;rulebase&gt; element.
*
 * @see "resources/a11y.xml"
 * @author Mike Squillace
 */
public class DefaultModelFactory implements IModelFactory
{

	private static DefaultModelFactory factoryInstance;

	private Map<String, IModel> _modelCache = new HashMap<String, IModel>();
	private IConfiguration _config;

	private Logger logger = Logger.getLogger(LoggingUtil.A11Y_CORE_LOGGER_NAME);
	
	protected DefaultModelFactory () {
		try {
			IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
			_config = context.getConfiguration();
		}catch (ConfigurationException e) {
			throw new RuntimeException("Cannot initialize DefaultModelFactory - no Configuration object");
		}
	}

	/**
	 * retrieve the instance of this default factory implementation.
	 * 
	 * @return factory instance
	 */
	public static DefaultModelFactory getInstance () {
		if (factoryInstance == null) {
			factoryInstance = new DefaultModelFactory();
		}
		return factoryInstance;
	}

	/**
	 * retrieve an <code>IModel</code> object for the given model type. If 
	 * a call to this method with the specified type has previously resulted in an <code>IModel</code> 
	 * object being created, that previously created instance will be returned.
	 * 
	 * <p>The IBM Rule-based Accessibility Validation Environment currently supports the following
	 * models:
	 *
	 * <p><ul>
	 * <li>Java AWT
	 * <li>Java Swing
	 * <li>Eclipse SWT
	 * <li>Mozilla/web
	 * </ul>
	 *
	 * @param model -- model name as defined in the model extension
	 * @see #resolveModel(String, boolean)
	 */
	public IModel resolveModel (String model) {
		return resolveModel(model, true);
	}

	/**
	 * retrieve an <code>IModel</code> object for the given model type. If 
	 * a call to this method with the specified type has previously resulted in an <code>IModel</code> 
	 * object being created and the useCache parameter is <code>true</code>, 
	 * that previously created instance will be returned.
	 * 
	 * <p>The IBM Rule-based Accessibility Validation Environment currently supports the following
	 * models:
	 *
	 * <p><ul>
	 * <li>Java AWT
	 * <li>Java Swing
	 * <li>Eclipse SWT
	 * <li>Mozilla/web
	 * </ul>
	 *
	 * @param modelName -- model name as defined in the model extension
	 * @param useCache -- if <code>true</code> any previously instantiated model of the specified 
	 * type will be used else a new <code>IModel</code> object will be created
	 */
	public IModel resolveModel (String modelName, boolean useCache) {
		IModel model = null;
		Class<?> modelCls = null;
		
		if (useCache && modelName != null) {
			model = (IModel) _modelCache.get(modelName);
		}
		
		if (model == null && modelName != null && _config != null) {
			List<String> modelTypes = Arrays.asList(_config.getModelTypes());
			if (modelTypes.contains(modelName)) {
				try {
					_config.setSymbolPool(IConfiguration.MODEL_ID);
					HashMap modelMap = (HashMap) _config.getParameter(modelName);
					String classname = (String) modelMap.get("class");
					model = (IModel) modelMap.get("modelInstance");
					if (model == null) {
						try {
							modelCls = ClassLoaderCache.getDefault().classForName(classname);
						} catch (Exception e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
							throw new IllegalArgumentException("Cannot load model class for model named: " + modelName);
						}
					}
					if (modelCls != null){
						model = (IModel) modelCls.newInstance();
					} else if (model == null) {
						throw new IllegalArgumentException("Cannot load model class for model named: " + modelName);
					}
					if (model != null) {
						_modelCache.put(modelName, model);
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					throw new IllegalArgumentException("Cannot find model for model named: " + modelName);
				}
			}
		}
		
		return model;
	} // resolveModel

	/**
	 * find a model instance that supports the given type. The configuration of each model includes its 
	 * 'basetype' attribute, which specifies the superclass of all objects in the hierarchical structure embodied by 
	 * a model. This basetype is given by the <code>Configuration.MODEL_BASE_TYPE</code> key. This attribute 
	 * wil be examined for each model type that has been placed into the <code>Configuration.A11Y_ID</code> pool of the active 
	 * configuration instance.
	 * 
	 * <p>If more than one model supports the given type, which model 
	 * type is returned is indeterminant. A cached <code>IModel</code> instance is always used by this method when available.
	 * 
	 * @param type - type for which an model instance is desired
	 * @return model instance that supports the given type or <code>null</code> if no 
	 * model can be found that supports the given type
	 * @see org.a11y.utils.accprobe.core.config.IConfiguration#MODEL_BASE_TYPE
	 * @see org.a11y.utils.accprobe.core.config.IConfiguration#getModelTypes()
	 */
	public IModel resolveModel (Class<?> type) {
		String[] modelTypes = _config.getModelTypes();
		IModel model = null;
		List<Class<?>> classes = new LinkedList<Class<?>>();
		
		classes.add(type);
		classes.addAll(Arrays.asList(type.getInterfaces()));
		_config.setSymbolPool(IConfiguration.MODEL_ID);
		OUTER: for (int a = 0; a < modelTypes.length; ++a) {
			Map modelMap = (HashMap) _config.getParameter(modelTypes[a]);
			String baseType = (String) modelMap.get(IConfiguration.MODEL_BASE_TYPE);

			// MAS: May want to support multiple base types
			if (baseType != null && baseType.length() > 0) {
				try {
					Class<?> baseCls = Class.forName(baseType, false, type.getClassLoader());
					for (Iterator<Class<?>> iter = classes.iterator(); iter.hasNext();) {
						if (baseCls.isAssignableFrom(iter.next())) {
							model = resolveModel(modelTypes[a]);
							break OUTER;
						}
					}
				}catch (Exception e) {
					if (e instanceof ClassNotFoundException){
						continue;
					} else {
						logger.log(Level.WARNING, e.getMessage(), e);
					}
				}
			}
		}
		return model;
	}
} // DefaultModelFactory

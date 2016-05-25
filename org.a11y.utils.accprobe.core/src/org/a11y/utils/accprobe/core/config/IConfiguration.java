/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.config;

import java.io.Serializable;
import java.util.Map;

/**
 * interface to embody overall settings and parameters for the validation process. Implementations of this interface will 
 * build configuration objects from a variety of sources such as files on the file system, serialized data, database queries, and the like.
*
 * <p>A11Y configuration instances consist of symbol pools, each pool containing a set of symbols and their 
 * corresponding values. There are two types of symbol pools:
*
 * <p><ul>
 * <li>general symbol pools that specify parameters to the validation engine or UI componentry or other 
 * componentry built upon the core rules engine
 * <li>model-specific pools that contain symbols and values for specific model 
 * contributed to the engine. These pools are noted below as 'model pools' and are 
 * specified with the noted 'model pool ids'.
 * </ul>
*
 * <p><b>Note</b>: Clients who wish to form configuration instances from other sources than the default source (i.e. xml files) should extend 
 * <code>AbstractConfiguration</code> rather than implementing this interface. Also, the <code>RuntimeContextFactory</code> 
 * should always be used to instantiate configuration objects.
*
 *  <p><b>Important</b>: The current implementations packaged with A11Y are <i>not</i> thread-safe.
 *
 *  @see AbstractConfiguration
 *  @see XmlConfiguration
 *  @see org.a11y.utils.accprobe.core.runtime.RuntimeContextFactory
 * @author Mike Squillace
 */
public interface IConfiguration extends Serializable
{

	static final long serialVersionUID = -3661241965923991487L;

	/**
	 * model pool ids are denoted with the model name followed by this 
	 * delimiter followed by the actual pool id
	 */
	public static final char MODEL_POOL_ID_DELIMITER = '_';

	/**
	 * pool id and model pool id for models - value is 'model'
	 */
	public static final String MODEL_ID = "model";

	/**
	 * pool id for main a11y pool - value is 'a11y'
	 */
	public static final String A11Y_ID = "a11y";

	public static final String WORKING_DIR_KEY = "a11y.working.dir";

	public static final String MODEL_LOCATORIDS_POOL = "locator";
	public static final String MODEL_ADAPTOR_FACTORY = "adaptor.factory";
	public static final String MODEL_IGNORENODENAMES = "ignore.names";
	public static final String MODEL_IGNOREIDS = "ignore.ids";
	public static final String MODEL_LOCATOR = "locator";
	public static final String MODEL_BASE_TYPE = "basetype";

	public static final String SHOW_INVISIBLE_KEY = "show.hidden";
	public static final String SHOW_ITEMS_KEY = "show.items";

	public static final String FILTER_ID = "filter";
	public static final String FILTER_MODEL_ATTRIBUTE = "model";
	public static final String FILTER_CLASSNAME_ATTRIBUTE ="class";
	public static final String FILTER_NODENAMES_ATTRIBUTE ="nodenames";
	public static final String FILTER_NODEIDS_ATTRIBUTE ="nodeids";
	public static final String FILTER_NODETYPES_ATTRIBUTE = "nodetypes";

	public static final String ALIASES_ID = "aliases";
	
	
	
	/**
	 * set the symbol pool to be used in subsequent set/getParameter calls. A
	 * symbol pool embodies a purpose or role in the A11Y engine. Instances of
	 * this interface might represent a particular symbol pool by a .properties
	 * file, data base table, or XML document.
	 * 
	 * <p>Symbol pool ids can have two forms:
	 * 
	 * <p><ul>
	 * <li>generic pool ids which are alphanumeric in nature
	 * <li>model pool ids, which have the form:
	 * 
	 * <p>
	 * &lt;modelName&gt;<code>MODEL_POOL_ID_DELIMITER</code>&lt;poolId&gt;
	 * 
	 * <p>The symbol pool is the pool that is queried for all get/setXXXParameter methods. Thus, 
	 * this method should always be called prior to a set/getXXXParameter method to insure that the correct 
	 * pool is being accessed.
	 * 
	 * @param id -
	 *            id of symbol pool
	 *            
	 */
	public void setSymbolPool (String id);

	/**
	 * get the current symbol pool. The returned value is the id of the 
	 * pool that is currently be accessed by any get/setXXXParameter methods.
	 * 
	 * @return current symbol pool id
	 * @see #setSymbolPool(String)
	 */
	public String getSymbolPool ();

	/**
	 * get all available symbol pools.
	 * 
	 * @return array of ids for all available symbol pools or empty array if no
	 *         pools have been created other than the default pool
	 *         @see #setSymbolPool(String)
	 */
	public String[] getSymbolPools ();

	/**
	 * get the contents of the specified symbol pool
	 * 
	 * @param poolID -
	 *            id of symbol pool
	 * @return map containing contents of pool or <code>null</code> if pool
	 *         does not exist
	 *         @see #setSymbolPool(String)
	 */
	public Map getSymbolPoolContents (String poolID);

	/**
	 * retrieve a string parameter from the current symbol pool
	 * 
	 * @param key -
	 *            name or key of parameter
	 * @return string parameter with the given name or <code>null</code> if no
	 *         such key exists
	 *         @see #setSymbolPool(String)
	 */
	public String getStringParameter (String key);

	/**
	 * set a string parameter in the current symbol pool
	 * 
	 * @param key -
	 *            name of parameter
	 * @param val -
	 *            string value corresponding to this key
	 *            @see #setSymbolPool(String)
	 */
	public void setParameter (String key, String val);

	/**
	 * retrieve an int parameter from the current symbol pool
	 * 
	 * @param key -
	 *            name or key of parameter
	 * @return int parameter with the given name or <code>null</code> if no
	 *         such key exists
	 *         @see #setSymbolPool(String)
	 */
	public int getIntParameter (String key);

	/**
	 * set an int parameter in the current symbol pool
	 * 
	 * @param key -
	 *            name of parameter
	 * @param val -
	 *            int value corresponding to this key
	 *            @see #setSymbolPool(String)
	 */
	public void setParameter (String key, int val);

	/**
	 * retrieve a double parameter from the current symbol pool
	 * 
	 * @param key -
	 *            name or key of parameter
	 * @return double parameter with the given name or <code>null</code> if no
	 *         such key exists
	 *         @see #setSymbolPool(String)
	 */
	public double getDoubleParameter (String key);

	/**
	 * set a double parameter in the current symbol pool
	 * 
	 * @param key -
	 *            name of parameter
	 * @param val -
	 *            double value corresponding to this key
	 *            @see #setSymbolPool(String)
	 */
	public void setParameter (String key, double val);

	/**
	 * retrieve a boolean parameter from the current symbol pool
	 * 
	 * @param key -
	 *            name or key of parameter
	 * @return boolean parameter with the given name or <code>null</code> if
	 *         no such key exists
	 *         @see #setSymbolPool(String)
	 */
	public boolean getBooleanParameter (String key);

	/**
	 * set a boolean parameter in the current symbol pool
	 * 
	 * @param key -
	 *            name of parameter
	 * @param val -
	 *            boolean value corresponding to this key
	 *            @see #setSymbolPool(String)
	 */
	public void setParameter (String key, boolean val);

	/**
	 * retrieve a class parameter from the current symbol pool
	 * 
	 * @param key -
	 *            name or key of parameter
	 * @return class parameter with the given name or <code>null</code> if no
	 *         such key exists
	 *         @see #setSymbolPool(String)
	 */
	public Class<?> getClassParameter (String key);

	/**
	 * set a class parameter in the current symbol pool
	 * 
	 * @param key -
	 *            name of parameter
	 * @param val -
	 *            Class<?> object corresponding to this key
	 *            @see #setSymbolPool(String)
	 */
	public void setParameter (String key, Class<?> val);

	/**
	 * set a parameter of an undetermined type in the current symbol pool
	 * 
	 * @param key -
	 *            name of parameter
	 * @param val -
	 *            object corresponding to this key
	 *            @see #setSymbolPool(String)
	 */
	public void setParameter (String key, Object val);

	/**
	 * get a parameter of an undetermined type in the current symbol pool
	 * 
	 * @param key -
	 *            name of parameter to retrieve
	 * @return desired parameter
	 * @see #setSymbolPool(String)
	 */
	public Object getParameter (String key);

	/**
	 * retrieve list of parameters from the current symbol pool 
	 * 
	 * @return list of parameters or empty array if no
	 *         parameters exist
	 *         @see #setSymbolPool(String)
	 */
	public String[] getParameterNames ();

	/**
	 * get the parameter with the given key as a string
	 * 
	 * @param key -
	 *            key of desired parameter
	 * @return string representation of parameter or <code>null</code> if
	 *         parameter is not found
	 *         @see #setSymbolPool(String)
	 */
	public String getParameterAsString (String key);

	/**
	 * returns the model supported within this configuration object. A11Y is packaged with 
	 * support for the following model:
	 * 
	 * <p><ul>
	 * <li>Java Swing
	 * <li>Eclipse SWT
	 * <li>W3C Document Object Model (DOM)
	 * <li>Mozilla browser
	 * </ul>
	 * 
	 * <p>Models are always introduced in the main symbol pool, <code>A11Y_ID</code> and 
	 * should be retrieved via the <code>MODEL_ID</code> symbol pool. Indeed, this method will usually be short-hand for:
	 * 
	 * <p><pre>
	 * setSymbolPool(MODEL_ID);
	 * return getParameterNames();
	 * </pre></p>
	 * 
	 * @return names of supported model
	 * @see #getParameterNames()
	 * @see #setSymbolPool(String)
	 */
	public String[] getModelTypes ();

	/**
	 * add data to this configuration object
	 * 
	 * @param data the data to be added
	 * @throws ConfigurationException
	 */
	public void addConfigurationData (Object data)
		throws ConfigurationException;
} // Configuration

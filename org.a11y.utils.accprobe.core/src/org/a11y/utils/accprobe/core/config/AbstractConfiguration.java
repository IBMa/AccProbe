/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;


/**
 * serves as a general-purpose implementation of the
 * Configuration interface. In particular, this class manages the overall
 * pool of ids and the key-value pairs (parameters) to which each id
 * corresponds.
*
 * <p><b>Note</b>: Clients should extend this class rather than implementing <code>Configuration</code> 
 * to avoid API modifications.
*
 * @author Randy Horwitz
 */
public class AbstractConfiguration implements IConfiguration
{

	static final long serialVersionUID = -1301837737929422833L;

	protected Map _configMap;
	protected String _curPool;

	/**
	 * create a Configuration. Configuration objects are managed by the engine and should not 
	 * be created by clients. Use a RuntimeContextFactory instance to create and access 
	 * active configurations.
	 * 
	 * @see org.a11y.utils.accprobe.core.runtime.RuntimeContextFactory
	 */
	public AbstractConfiguration () {
		_configMap = new HashMap();
		createSymbolPool(A11Y_ID);
	}

	/** {@inheritDoc} */
	public Map getSymbolPoolContents (String poolID) {
		return (Map) _configMap.get(poolID);
	}


	/** {@inheritDoc} */
	public void setSymbolPool (String id) {
		if (id == null || !_configMap.containsKey(id)) {
			throw new IllegalArgumentException("No pool with id "+ id); 
		}
		_curPool = id;
	}

	public void createSymbolPool (String id) {
		if (id == null) {
			throw new IllegalArgumentException("Pool name cannot be null");
		}
		_configMap.put(id, new HashMap());
		setSymbolPool(id);
	}


	/** {@inheritDoc} */
	public String getSymbolPool () {
		return _curPool;
	}

	/** {@inheritDoc} */
	public String[] getSymbolPools () {
		return (String[]) _configMap.keySet().toArray(new String[0]);
	}

	/** {@inheritDoc} */
	public String[] getModelTypes () {
		setSymbolPool(IConfiguration.MODEL_ID);
		return getParameterNames();
	}

	/** {@inheritDoc} */
	public void setParameter (String key, Object value) {
		if (value != null) {
			getSymbolPoolContents(_curPool).put(key, value);
		}
	}

	/** {@inheritDoc} */
	public void setParameter (String key, String value) {
		getSymbolPoolContents(_curPool).put(key, value);
	}

	/** {@inheritDoc} */
	public void setParameter (String key, int value) {
		getSymbolPoolContents(_curPool).put(key, new Integer(value));
	}

	/** {@inheritDoc} */
	public void setParameter (String key, double value) {
		getSymbolPoolContents(_curPool).put(key, new Double(value));
	}

	/** {@inheritDoc} */
	public void setParameter (String key, boolean value) {
		getSymbolPoolContents(_curPool).put(key, Boolean.valueOf(value));
	}

	/** {@inheritDoc} */
	public void setParameter (String key, Class<?> value) {
		getSymbolPoolContents(_curPool).put(key, value);
	}

	/**
	 * convenience method to set any parameter in the configuration. The current
	 * symbol pool id is saved and then restored after the set operation is
	 * complete.
	 * 
	 * @param poolID -
	 *            id of symbol pool in which set is to occur
	 * @param name -
	 *            name or id of parameter to set
	 * @param val -
	 *            value of parameter
	 */
	public void setParameter (String poolID, String name, Object val) {
		String curID = getSymbolPool();
		setSymbolPool(poolID);
		getSymbolPoolContents(_curPool).put(name, val);
		setSymbolPool(curID);
	}

	/** {@inheritDoc} */
	public Object getParameter (String key) {
		return getSymbolPoolContents(_curPool).get(key);
	}

	/** {@inheritDoc} */
	public String getStringParameter (String key) {
		String res = (String) getSymbolPoolContents(_curPool).get(key);
		return res == null ? "" : res;
	}

	/** {@inheritDoc} */
	public int getIntParameter (String key) {
		Object obj = getSymbolPoolContents(_curPool).get(key);
		int res = Integer.MIN_VALUE;
		if (obj instanceof Integer) {
			res = ((Integer) obj).intValue();
		}else if (obj instanceof String) {
			try {
				res = Integer.parseInt((String) obj);
			}catch (NumberFormatException e) {
			}
		}
		return res;
	}

	/** {@inheritDoc} */
	public double getDoubleParameter (String key) {
		Object obj = getSymbolPoolContents(_curPool).get(key);
		double res = Double.MIN_VALUE;
		if (obj instanceof Double) {
			res = ((Double) obj).doubleValue();
		}else if (obj instanceof String) {
			try {
				res = Double.parseDouble((String) obj);
			}catch (NumberFormatException e) {
			}
		}
		return res;
	}

	/** {@inheritDoc} */
	public boolean getBooleanParameter (String key) {
		Object obj = getSymbolPoolContents(_curPool).get(key);
		boolean res = false;
		if (obj instanceof Boolean) {
			res = ((Boolean) obj).booleanValue();
		}else if (obj instanceof String) {
			try {
				res = Boolean.valueOf((String) obj).booleanValue();
			}catch (NumberFormatException e) {
			}
		}
		return res;
	}

	/** {@inheritDoc} */
	public Class<?> getClassParameter (String key) {
		Object o = getSymbolPoolContents(_curPool).get(key);
		Class<?> res = null;
		
		if (o instanceof Class) {
			res = (Class<?>) o;
		}else if (o instanceof String) {
			try {
				res = ClassLoaderCache.getDefault().classForName((String) o);
			}catch (Exception e) {
				Logger.getLogger(LoggingUtil.A11Y_CORE_LOGGER_NAME).log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		return res;
	}

	/** {@inheritDoc} */
	public String getParameterAsString (String key) {
		return getSymbolPoolContents(_curPool).get(key).toString();
	}

	/**
	 * default implementation for this method attempts to treat the data object as a map 
	 * and simply adds it to the existing configuration map.
	 *
	 * @param data configuration data
	 * @throws ConfigurationException
	 */
	public void addConfigurationData (Object data)
		throws ConfigurationException {
		if (data != null && data instanceof Map) {
			_configMap.put(getSymbolPool(), (Map) data);
		}
	}

	/** {@inheritDoc} */
	public String[] getParameterNames () {
		return (String[]) getSymbolPoolContents(_curPool).keySet().toArray(
			new String[0]);
	}

	public String toString () {
		StringBuffer sb = new StringBuffer();
		String[] pools = getSymbolPools();
		for (int p = 0; p < pools.length; ++p) {
			sb.append("Pool: " + pools[p]);
			sb.append('\n');
			Map symbols = getSymbolPoolContents(pools[p]);
			if (symbols != null && !symbols.isEmpty()) {
				for (Iterator iter = symbols.keySet().iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					sb.append(key);
					sb.append('=');
					sb.append(symbols.get(key).toString());
					sb.append('\n');
				}
			}
		}
		return sb.toString();
	}
} // AbstractConfiguration

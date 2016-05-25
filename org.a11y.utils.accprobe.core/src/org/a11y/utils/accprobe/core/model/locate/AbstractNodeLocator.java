/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model.locate;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
//import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.model.DefaultModelFactory;
import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.core.model.IRenderableModel;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.traverse.ITreeNodeWalker;
import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;


/**
 * default base class for element locators. Clients should extend this class rather than attempting to 
 * implement <code>INodeLocator</code> directly.
*
 * @author Mike Squillace
 */
public abstract class AbstractNodeLocator implements INodeLocator
{
	
	public static final String NODENAME_KEY = "com.ibm.haac.properties.nodeName";
	public static final String NODELOCATOR_VARIABLE = "nodeLocator";
	
	public static final String EXACTMATCH_SEARCH = "search.exactMatch";
	public static final String IGNORECASE_SEARCH = "search.ignoreCase";
	public static final String CONTAINSSUBSTR_SEARCH = "search.containsSubstring";
	public static final String REGEXP_SEARCH = "search.regExp";
	
	public static final String NODENAME_CHARCLASS = "[\\w\\s!#\\$%\\&\\(\\)\\*\\+\\,\\-\\./:;<=>\\?\\\\^_`\\{\\|\\}\\~]";
	public static final String PREDICATEVAL_CHARCLASS = "[^']"; // "[\\w\\s!#$%&()*+,./:;<=>?_`{|}~\\\\^\\[\\]\\-]";
	public static final Pattern SEGMENT_PATTERN = Pattern.compile(
			"/" + NODENAME_CHARCLASS +
			"+\\[(\\d+|@[\\w\\s]+\\=\\'" + PREDICATEVAL_CHARCLASS +
			"+\\')\\]"
	);
	
	protected static final JXPathContextReferenceImpl ROOT_JXPATH_CONTEXT = (JXPathContextReferenceImpl) JXPathContext.newContext(null);
	static {
		// configure the context if ne
	}
	
	protected String defaultIdPropName = "id";
	protected IModel model;
	
	protected Map<String, String> propertiesMap = new HashMap<String, String>(); 
	
	/**
	 * create a new node locator 
	 * 
	 * @param defaultIdProperty - name of property to serve as default identification of node
	 * @param modelName - name of model for which this locator is being registered
	 * @param factory
	 */
	public AbstractNodeLocator (String defaultIdProperty, String modelName, NodePointerFactory factory) {
		defaultIdPropName = defaultIdProperty;
		model = DefaultModelFactory.getInstance().resolveModel(modelName);
		if (factory != null) {
			addNodePointerFactory(factory);
		}
		/*
		IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
		try {
			IConfiguration config = context.getConfiguration();
			String baseType = model.getBaseType();
			if (baseType != null) {
				Class<?> cls = ClassLoaderCache.getDefault().classForName(baseType);
				JXPathIntrospector.registerAtomicClass(cls);
			}
		} catch (ConfigurationException e) {
		}
		*/
	}
	
	protected IModel getModel () {
		return model;
	}
	
	/**
	 * used by the JXPath evaluator to find the node ID of the context node by 
	 * querying the underlying model. Every context includes the variable <code>$&lt;NODELOCATOR_VARIABLE&gt;</code>, which 
	 * can be used to invoke this method.
	 * 
	 * @param context supplied by the JXPath runtime
	 * @return node id as determined by underlying model or an empty string if no id is available
	 */
	public String getNodeId (ExpressionContext context) {
		String id = model == null
			? null : model.getNodeId(context.getContextNodePointer().getValue());
		return id == null ? "" : id;
	}

	/**
	 * used by the JXPath evaluator to perform various comparisons during a find operation. 
	 * Every context includes the variable <code>$&lt;NODELOCATOR_VARIABLE&gt;</code>, which 
	 * can be used to invoke this method.
	 * 
	 * @param context supplied by the JXPath runtime
	 * @param property property whose value is to be tested
	 * @param input against which property value is to be tested
	 * @param searchType one of <code>EXACTMATCH_SEARCH</code>, <code>IGNORECASE_SEARCH</code>, <code>CONTAINSSUBSTR_SEARCH</code>, <code>REGEXP_SEARCH</code>
	 * @return <code>true</code> if the given input matches the value of the property for the given 
	 * node according to the search type, <code>false</code> otherwise
	 */
	public boolean matches (ExpressionContext context, String property, 
						    String input, String searchType) {
		Object node = context.getContextNodePointer().getValue();
		boolean result = false;
		String value = null;
		
		if (property.equals(NODENAME_KEY)) {
			value = model == null ? (String) null : model.getNodeName(node);
		} else {
			try {
				value = (String) PropertyUtils.getSimpleProperty(node, property);
			} catch (Exception e) {
			}
		}
		
		if (value != null) {
			if (searchType.equals(EXACTMATCH_SEARCH)) {
				result = value.equals(input);
			} else if (searchType.equals(IGNORECASE_SEARCH)) {
				result = value.equalsIgnoreCase(input);
			} else if (searchType.equals(CONTAINSSUBSTR_SEARCH)) {
				result = value.indexOf(input) >= 0;
			} else if (searchType.equals(REGEXP_SEARCH)) {
				result = Pattern.matches(input, value);
			}
		}
		
		return result;
	}

	/**
	 * identify or distinguish the given element in the hierarchy. This default 
	 * implementation first uses the model-specific locator properties. These properties 
	 * are defined in the <code>IConfiguration.MODEL_LOCATORIDS_POOL</code> symbol pool of a given model type. The returned <code>Properties</code> 
	 * object contains the names of these properties as keys and their values. 
	 * 
	 * <p>If no getter was defined for the type of the given element, this implementation then uses the property name 
	 * returned by <code>getDefaultIdPropertyName</code>.
	 * 
	 * <p><b>Note</b>: If the model associated 
	 * with this locator is an instance of <code>IRenderableModel</code>, this locator will invoke 
	 * this getter on the UI thread.
	 *
	 *@param element element to be identified
	 *@return set of properties describing or identifying element
	 * @see org.a11y.utils.accprobe.core.model.IRenderableModel
	 */
	public Properties describe (Object element) {
		Properties properties = new Properties();
		List<String> propNames = new LinkedList<String>();
		//IConfiguration config = null;
		Map<String, String> getterMap = propertiesMap;
		
		if (getterMap != null) {
			for (Iterator<String> iter = getterMap.keySet().iterator(); iter.hasNext();) {
					String clsName = iter.next();
					Class<?> cls = ClassLoaderCache.getDefault().classForName(clsName);
					if (cls!=null && cls.isAssignableFrom(element.getClass())) {
						// add the properties to the list for this class;
						// note that multiple properties can be listed for each type
						String[] props = ((String) getterMap.get(clsName)).split("\\,\\s*");
						for (int s = 0; s < props.length; ++s) {
							if (props[s].trim().length() > 0) {
								propNames.add(props[s]);
							}
						}
					}
			}
		}
		
		if (propNames.isEmpty()) {
			propNames.add(getDefaultIdPropertyName());
		}
		for (Iterator<String> iter = propNames.iterator(); iter.hasNext(); ) {
			String propName = (String) iter.next();
			String value = (String) invokeGetter(element, propName);
			if (value != null && value.trim().length() > 0) {
				properties.setProperty(propName, value);
			}
		}
		
		return properties;
	}
	
	/**
	 * utility method for converting the set of properties returned by <code>describe</code> to a string. The 
	 * returned string will have the form:
	 * 
	 * <p><code>key1=val1,key2=val2,...</code>
	 * 
	 * @param properties as returned by <code>describe</code>
	 * @return string of above form representing set of properties of description
	 * @see #describe(Object)
	 */
	public static String descriptionAsString (Properties properties) {
		StringBuffer sb = new StringBuffer();
		if (properties != null) {
			Enumeration<?> enm = properties.propertyNames();
			while (enm.hasMoreElements()) {
				String propName = (String) enm.nextElement();
				sb.append(propName);
				sb.append('=');
				sb.append(properties.getProperty(propName));
				if (enm.hasMoreElements()) {
					sb.append(',');
				}
			}
		}

		return sb.toString();
	}

	/**
	 * invoke the getter with the specified method name on the given element. If this locator 
	 * defines a model and it is an instance of <code>IRenderableModel</code>, the getter will 
	 * be invoked on the UI thread.
	 *  
	 * @param element
	 * @param methName
	 * @return result of getter invocation
	 */
	protected Object invokeGetter (final Object element, final String propName) {
		final Object[] result = new Object[1];
		Runnable runnable = new Runnable() {
			public void run () {
				try {
					result[0] = PropertyUtils.getSimpleProperty(element, propName);
				}catch (Throwable e) {
				}
			}
		};
		
		if (model != null && model instanceof IRenderableModel) {
			((IRenderableModel) model).invokeOnUIThread(runnable);
		}else {
			runnable.run();
		}
		
		return result[0];
	}

	@SuppressWarnings("static-access")
	protected void addNodePointerFactory (NodePointerFactory factory) {
		NodePointerFactory[] factories = ROOT_JXPATH_CONTEXT.getNodePointerFactories();
		boolean found = false;
		if(factories!=null){
			for (int f = 0; !found & f < factories.length; ++f) {
				found = factories[f].getClass().equals(factory.getClass());
			}
		}
		if (!found) {
			ROOT_JXPATH_CONTEXT.addNodePointerFactory(factory);
		}
	}
	
	/**
	 * return the property used by default for discovering the id string of a 
	 * component. For this abstract implementation, the default property name is 'id'.
	 * 
	 * @return default property name used to fetch id string of a component
	 */
	protected String getDefaultIdPropertyName () {
		return defaultIdPropName;
	}

	/** {@inheritDoc} */
	public String locate (Object element, Object root) {
		StringBuffer sb = new StringBuffer();
		
		try {
			while (element != null && !element.equals(root)) {
				JXPathContext  ctx = JXPathContext.newContext(ROOT_JXPATH_CONTEXT, element);
				sb.insert(0, ctx.getContextPointer().asPath());
				
				// TODO needs to be more generic
			element = ((ITreeNodeWalker) model.getNodeWalker()).getParent(element);
			}
			if (element != null && element.equals(root)) {
				JXPathContext  ctx = JXPathContext.newContext(ROOT_JXPATH_CONTEXT, element);
				sb.insert(0, ctx.getContextPointer().asPath());
			}
		} catch (InvalidComponentException e) {
			// best we can do is hope for a successful descendent search
			sb.insert(0, '/');
		}
		
		return sb.toString();
	}

	/** {@inheritDoc} */
	public Object find (String path, Object startNode) {
		Object node = null;
		Object contextNode = startNode;
		
		if (contextNode != null) {
				JXPathContext ctx = JXPathContext.newContext(ROOT_JXPATH_CONTEXT, startNode);
				ctx.setLenient(true);
			ctx.getVariables().declareVariable(NODELOCATOR_VARIABLE, this);
			node = ctx.selectSingleNode(path);
		} else {
			Object[] startNodes = model.getNodeWalker().getStartNodes();
			for (int i = 0; node == null && i < startNodes.length; ++i) {
				JXPathContext ctx = JXPathContext.newContext(ROOT_JXPATH_CONTEXT, startNodes[i]);
				ctx.getVariables().declareVariable(NODELOCATOR_VARIABLE, this);
				node = ctx.selectSingleNode(path);
			}
		}
		
		return node; 
	}

	/** {@inheritDoc} */
	public Object[] findAll (String path, Object startNode) {
		List<?> nodeList = null;
		Object contextNode = startNode;
		
		if (contextNode != null) {
			JXPathContext ctx = JXPathContext.newContext(ROOT_JXPATH_CONTEXT, startNode);
			ctx.getVariables().declareVariable(NODELOCATOR_VARIABLE, this);
			nodeList = ctx.selectNodes(path);
		} else {
			Object[] startNodes = model.getNodeWalker().getStartNodes();
			for (int i = 0; nodeList == null && i < startNodes.length; ++i) {
				JXPathContext ctx = JXPathContext.newContext(ROOT_JXPATH_CONTEXT, startNodes[i]);
				ctx.getVariables().declareVariable(NODELOCATOR_VARIABLE, this);
				nodeList = ctx.selectNodes(path);
			}
		}
		
		return nodeList != null ? nodeList.toArray() : new Object[0]; 
	}

} // AbstractNodeLocator

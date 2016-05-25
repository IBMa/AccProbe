/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.config;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;
import org.a11y.utils.accprobe.core.runtime.IRuntimeContext;
import org.a11y.utils.accprobe.core.runtime.RuntimeContextFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * a Configuration based on XML files which contain A11Y's default
 * values.
*
 * @author Mike Squillace, Randy Horwitz
 */
public class XmlConfiguration extends AbstractConfiguration
{

	static final long serialVersionUID = -6983580695767275023L;

	private static final char PATH_SEPARATOR = '.';

	private static final String ROOT_ELEMENT_NAME = "configuration";

	private static final String POOL_ELEMENT = "pool"; // Title of pool element

	private static final String ATTR_PROPERTY_NAME = "name";

	private static final String ATTR_CONTENT_TYPE = "type";

	private static final String TEXT_CONTENT = ".textContent.";

	private static final String PROPERTY_ELEMENT = "property";

	private static final String POOL_ID = "id";

	private static final String A11Y_XML_PREFIX = "a11y";

	transient private String _currentXmlFilePrefix;

	transient private HashMap _attributeMap = new HashMap();
	
	transient private Logger logger = Logger.getLogger(LoggingUtil.A11Y_CORE_LOGGER_NAME);

	/**
	 * create a Configuration from the .xml files found in the resources
	 * directory or, if specified, using System Properties.
	 * 
	 * @throws ConfigurationException
	 */
	public XmlConfiguration () throws ConfigurationException {
		super();
	} // main ctor

	/**
	 * treats data object as an <code>InputStream</code>. The contents of the
	 * stream are assumed to be syntactically correct XML and valid markup
	 * within a a11y.xml file for the A11Y engine. All model-specific
	 * configuration .xml files will be processed based on the contents of the
	 * 'model' pool.
	 *
	 *@param data configuration data in the form of an <code>InputStream</code>
	 * @throws ConfigurationException
	 */
	public void addConfigurationData (Object data)
		throws ConfigurationException {
		if (data instanceof InputStream) {
			InputStream dataStream = (InputStream) data;
			Set currentModels = new HashSet();
			if (!_configMap.isEmpty()) {
				currentModels.addAll(Arrays.asList(getModelTypes()));
			}
			parseXml(dataStream);
			
			String[] models = getModelTypes();
			// Go through all of the model specific XML files, and parse them
			for (int i = 0; i < models.length; i++) {
				if (!currentModels.contains(models[i])) {
					_currentXmlFilePrefix = models[i];
					parseXml(models[i]);
				}
			}
			_currentXmlFilePrefix = null;
		} else {
			super.addConfigurationData(data);
			}
	}
	
	protected Map addConfigurationData(Object data, Map configMap)
		throws ConfigurationException {
		_configMap=configMap;
		addConfigurationData(data);
		return _configMap;
		
	}
	/**
	 * This method returns an attributes map, keyed by all of the elements in
	 * the current pool. The values are maps, holding the attributes and their
	 * values in key value form.
	 * 
	 * @return the attribute map
	 */
	public Map getAttributeMap () {
		return (Map) _attributeMap.get(getSymbolPool());
	}

	/**
	 * parse the xml file represented by the stream
	 * 
	 * @param stream -
	 *            InputStream representing the XML file to be parsed.
	 */
	private void parseXml (InputStream stream)
		throws ConfigurationException {
		// If we actually have an input stream, parse the file.
		if (stream != null) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				parser.parse(stream, new ParserHandler());
			}catch (Exception e) {
				logger.log(Level.SEVERE,
						"SAXParseException caught while parsing file."
						+ e.getMessage(), e);
				throw new ConfigurationException("Exception caught while trying to parse file."
						+ e.getMessage());
			}
		}
	}

	/**
	 * parse the xml file denoted by the prefix passed in
	 * 
	 * @param xmlFilePrefix -
	 *            prefix of the XML file passed in. I.E. awt is the prefix for
	 *            awt.xml.
	 */
	private void parseXml (String xmlFilePrefix) throws ConfigurationException {
		IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
		InputStream inputStream = context.getResourceLocator().getResourceAsStream(xmlFilePrefix, null, "xml", null);
		if (inputStream != null) {
			parseXml(inputStream);
		}
	}
	
	protected class ParserHandler extends DefaultHandler
	{
		private Locator _locator;
		private Stack _elementStack = new Stack();
		private boolean _readChars;
		private String _key;
		private String _poolName;
		private HashMap _elemAttributeMap;

		public void setDocumentLocator (Locator locator) {
			_locator = locator;
		}

		public void characters (char[] ch, int start, int length)
			throws SAXException {
			// The only time we care about characters is if we are within an
			// element,
			// nested within a pool element
			// Any other characters will be things such as white space,
			// which we don't care about
			if (_readChars) {
				// We are not guaranteed that the data for this element will be
				// read in
				// in one chunk. So, append the latest set of characters to our
				// element data.
				HashMap elemAttrs = (HashMap) _elemAttributeMap.get(computeCurrentPath());
				StringBuffer elementData = (StringBuffer) elemAttrs.get(TEXT_CONTENT);
				elementData.append(new String(ch, start, length).trim());
			}
		}

		public void startElement (String namespaceURI, String localName,
								  String qName, Attributes atts)
			throws SAXException {
			// The first thing to do is figure out whether we have encountered
			// a pool element.
			// MAS: want to expand this to accept any child element of
			// <configuration> as a pool
			if (qName.equals(POOL_ELEMENT)) {
				// OK, we have encountered a pool element. Initialize
				// necessary variables, and then set the pool id for use
				// in setting the map for this pool into the config map.
				_poolName = getPoolName(atts.getValue(POOL_ID));
				if (_configMap.containsKey(_poolName)) {
					setSymbolPool(_poolName);
					_elemAttributeMap = (HashMap) _attributeMap.get(_poolName);
				}else {
					createSymbolPool(_poolName);
					_elemAttributeMap = new HashMap();
					_attributeMap.put(_poolName, _elemAttributeMap);
				}
			}else if (!qName.equals(ROOT_ELEMENT_NAME)) {
				// start definition of symbol in pool
				_readChars = true;
				// Check the elementname. If this is a property element, the key
				// is going
				// to be the name attribute of that property.
				if (qName.equals(PROPERTY_ELEMENT)) {
					_key = atts.getValue(ATTR_PROPERTY_NAME);
					if (_key == null || _key.length() == 0) {
						throw new SAXException("No name defined for property in pool " + _poolName);
					}
					_elementStack.push(PATH_SEPARATOR + PROPERTY_ELEMENT);
					HashMap attributeMap = new HashMap();
					// holds the text content for the current element
					attributeMap.put(TEXT_CONTENT, new StringBuffer());
					_elemAttributeMap.put(computeCurrentPath(), attributeMap);
				}else {
					// If this element has any of them, build a map of the
					// attributes,
					// and place it into the XmlConfiguration attribute
					// map,
					// keyed by poolName
					_elementStack.push(qName);
					String path = computeCurrentPath();
					HashMap attributeMap = (HashMap) _elemAttributeMap.get(path);
					if (attributeMap == null) {
						attributeMap = new HashMap();
						// holds the text content for the current element
						attributeMap.put(TEXT_CONTENT, new StringBuffer());
						_elemAttributeMap.put(path, attributeMap);
					}
					if (atts.getLength() > 0) {
						for (int i = 0; i < atts.getLength(); i++) {
							attributeMap.put(atts.getQName(i), atts.getValue(i));
						}
					} // element has attributes
				} // is not property element
			} // is not a pool element
		}

		public void endElement (String namespaceURI, String localName,
								String qName) throws SAXException {
			// Are we closing a pool element?
			if (qName.equals(POOL_ELEMENT)) {
				_attributeMap.put(_poolName, _elemAttributeMap);
				_readChars = false;
			}else if (!qName.equals(ROOT_ELEMENT_NAME)) {
				HashMap elemAttrs = (HashMap) _elemAttributeMap.get(computeCurrentPath());
				String val = ((StringBuffer) elemAttrs.get(TEXT_CONTENT)).toString();
				String type = (String) elemAttrs.get(ATTR_CONTENT_TYPE);
				mapAndSetParameter(val, type);
				// clear the text content since its a StringBuffer
				// and we do not want to append to old data if the element has the same name
				((StringBuffer) elemAttrs.get(TEXT_CONTENT)).setLength(0);
				_elementStack.pop();
				_readChars = !_elementStack.empty();
			}
		}

		public void error (SAXParseException e) throws SAXException {
			logger.log(Level.SEVERE,
					"SAX parse exception -- line: "
					+ _locator.getLineNumber() + ", column: "
					+ _locator.getColumnNumber(), e);
			throw e;
		}

		public void warning (SAXParseException e) throws SAXException {
			logger.log(Level.WARNING,
					"SAX parse exception -- line: "
					+ _locator.getLineNumber() + ", column: "
					+ _locator.getColumnNumber(), e );
		}

		public void fatalError (SAXParseException e) throws SAXException {			
					logger.log(Level.SEVERE,
						"SAX parse exception -- line: "
						+ _locator.getLineNumber() + ", column: "
						+ _locator.getColumnNumber(), e );
			throw e;
		}

		private String computeCurrentPath () {
			StringBuffer sb = new StringBuffer();
			for (Iterator iter = _elementStack.iterator(); iter.hasNext();) {
				sb.append((String) iter.next());
				if (iter.hasNext()) {
					sb.append(PATH_SEPARATOR);
				}
			}
			return sb.toString();
		}

		/**
		 * This method builds the name of the pool, based on the kind of XML
		 * file we are reading.
		 * 
		 * @param xmlId -
		 *            id attribute of pool passed in
		 * @return the name of the pool
		 */
		private String getPoolName (String xmlId) throws SAXException {
			String returnValue; // value to be returned
			if (xmlId == null || xmlId.length() == 0) { 
				throw new SAXException("No id defined for pool"); 
			}
			
			// This test is pretty simple. One of the model based pools,
			// such as one residing in swt.xml, needs to have swt prepended to
			// the pool name in order
			// to stay with our current convention. I.E. swt.xml contains a pool
			// with an id of aliases,
			// but our conventions expect swt_aliases for the pool name.
			// MAS: note that the test assumes that all pools are defined in
			// either a11y.xml or in
			// a model configuration file
			if (_currentXmlFilePrefix == null
					|| _currentXmlFilePrefix.equals(A11Y_XML_PREFIX)) {
				returnValue = xmlId;
			}else {
				returnValue = _currentXmlFilePrefix
						+ MODEL_POOL_ID_DELIMITER + xmlId;
			}
			return returnValue;
		}

		private void mapAndSetParameter (String val, String type) {
			String path = computeCurrentPath();
			if (val == null) {
				val = "";
			}
			if (path.equals(PATH_SEPARATOR + PROPERTY_ELEMENT)) {
				path = _key;
			}
			if (type == null || type.equals(String.class.getName())) {
				setParameter(path, val);
			}else if (type.equals(Class.class.getName())) {
				Class<?> cls = ClassLoaderCache.getDefault().classForName(val);
				if (cls != null) {
					setParameter(path, cls.getName());
				} else {
					logger.log(Level.WARNING, "Could not find class for value " + val);
				}
			}else if (type.equals(Integer.TYPE.getName())
					|| type.equals(Integer.class.getName())) {
				try {
					setParameter(path, Integer.parseInt(val));
				}catch (NumberFormatException e) {
					logger.log(Level.WARNING, "Error parsing value for symbol " + path);
				}
			}else if (type.equals(Double.TYPE.getName())
					|| type.equals(Double.class.getName())) {
				try {
					setParameter(path, Double.parseDouble(val));
				}catch (NumberFormatException e) {
					logger.log(Level.WARNING, "Error parsing value for symbol " + path);
				}
			}else if (type.equals(Long.TYPE.getName())
					|| type.equals(Long.class.getName())) {
				try {
					setParameter(path, Long.parseLong(val));
				}catch (NumberFormatException e) {
					logger.log(Level.WARNING, "Error parsing value for symbol " + path);
				}
			}else if (type.equals(Float.TYPE.getName())
					|| type.equals(Float.class.getName())) {
				try {
					setParameter(path, Float.parseFloat(val));
				}catch (NumberFormatException e) {
					logger.log(Level.WARNING, "Error parsing value for symbol " + path);
				}
			}else if (type.equals(Boolean.TYPE.getName())
					|| type.equals(Boolean.class.getName())) {
				try {
					setParameter(path, Boolean.valueOf(val).booleanValue());
				}catch (NumberFormatException e) {
					logger.log(Level.WARNING, "Error parsing value for symbol " + path);
				}
			}else {
				setParameter(path, val.toString());
			}
		}
	} // end inner class ParserHandler
} // XmlConfiguration

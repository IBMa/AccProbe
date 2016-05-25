/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.core.model.locate;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.BeanPropertyPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;
import org.a11y.utils.accprobe.core.config.ConfigurationException;
import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.model.DefaultModelFactory;
import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.traverse.ITreeNodeWalker;
import org.a11y.utils.accprobe.core.runtime.IRuntimeContext;
import org.a11y.utils.accprobe.core.runtime.RuntimeContextFactory;


public abstract class BaseNodePointer extends PropertyOwnerPointer
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IModel model;
	protected IConfiguration config;
	protected QName name;
	
	public BaseNodePointer(NodePointer parent, Locale locale, String modelName) {
		super(parent, locale);
		model = DefaultModelFactory.getInstance().resolveModel(modelName);

		IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
		try {
			config = context.getConfiguration();
		} catch (ConfigurationException e) {
		}
}

	public int getLength() {
		return 1;
	}

	public QName getName() {
		return name == null ? new QName(null, model.getNodeName(getBaseValue())) : name;
	}

	public boolean isCollection() {
		return false;
	}
	
	public boolean isActual () {
		return getBaseValue() != null;
	}
	
	public boolean isLeaf() {
		boolean res = true;
		try {
			res = !((ITreeNodeWalker) model.getNodeWalker()).hasChildren(getBaseValue());
		} catch (InvalidComponentException e) {
		}
		return res;
	}

	public PropertyPointer getPropertyPointer () {
		return new BeanPropertyPointer(
				this, JXPathIntrospector.getBeanInfo(getBaseValue().getClass()));
	}

	public void setValue(Object value) {
	}
	
	public int hashCode () {
		return System.identityHashCode(getBaseValue());
	}
	
	public boolean equals (Object o) {
		return o == this
			|| (o != null && o instanceof NodePointer && ((NodePointer) o).getBaseValue().equals(getBaseValue()));
	}
	
	public String asPath () {
        StringBuffer buffer = new StringBuffer();
        INodeLocator locator = model.getNodeLocator();
        Properties identification = locator != null ? locator.describe(getBaseValue()) : null;
        
        if (parent != null) {
            buffer.append(parent.asPath());
        }
        if (buffer.length() == 0 || buffer.charAt(buffer.length() - 1) != '/') {
    		buffer.append('/');
    	}
        
        buffer.append(getName().getName());
        if (identification == null || !identification.propertyNames().hasMoreElements()) {
            buffer.append('[');
            buffer.append(getRelativePositionByClassName());
            buffer.append(']');
        } else {
        	buffer.append(formPropertiesPredicates(identification));
        }
        
        return buffer.toString();
    }
	
	public boolean testNode (NodeTest test) {
		return testNode(test, getBaseValue());
	}
	
	public boolean testNode (NodeTest test, Object node) {
		boolean result = false;
		if (test instanceof NodeTypeTest && ((NodeTypeTest) test).getNodeType() == org.apache.commons.jxpath.ri.Compiler.NODE_TYPE_NODE) {
			result = true;
		} else if (test instanceof NodeNameTest) {
			NodeNameTest nameTest = (NodeNameTest) test;
			if (nameTest.isWildcard()) {
				result = true;
			} else {
				String elemNodeName = model.getNodeName(node);
				String testName = nameTest.getNodeName().getName();
				result = elemNodeName.equals(testName);
			}
		}
		
		return result;
	}
	
	protected int getRelativePositionByClassName ()  {
		ITreeNodeWalker walker = (ITreeNodeWalker) model.getNodeWalker();
		int count = 1;
		
		try {
			Object parent = walker.getParent(getBaseValue());
			if (parent != null) {
				Object[] children = walker.getChildren(parent);
				int index = findChildIndex(children, getBaseValue());
				// count number of occurrences of this component's class
				// prior to it in parent container
				String name = getName().getName();
				for (int cc = 0; cc < index; ++cc) {
					if (model.getNodeName(children[cc]).equals(name)) {
						++count;
					}
				}
			}
		} catch (InvalidComponentException e) {
			count = 0;
		}
		
		return count;
	}
	
	protected String formPropertiesPredicates (Properties identification) {
		StringBuffer sb = new StringBuffer();
		Enumeration<?> enm = identification.propertyNames();
		
		while (enm.hasMoreElements()) {
			String propName = (String) enm.nextElement();
			sb.append('[');
			if (propName.equals("name")) {
				sb.append("getNodeId($" + AbstractNodeLocator.NODELOCATOR_VARIABLE + ")");
			} else {
				sb.append('@');
				sb.append(propName);
			}
			sb.append('=');
			sb.append('\'');
			sb.append(identification.getProperty(propName));
			sb.append('\'');
			sb.append(']');
		}

		return sb.toString();
	}
	
	int findChildIndex (Object[] children, Object child)  {
		int index = -1;
		
		for (int c = 0; index == -1 && c < children.length; ++c) {
			if (children[c] == child || children[c].equals(child)) {
				index = c;
			}
		}
		
		return index;
	}
	
}

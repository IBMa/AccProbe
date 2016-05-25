/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.adapt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;


/**
 * default implementation for creating adaptors in A11Y. Clients should subclass this implementation rather than 
 * implementing <code>IAdaptorFactory</code>.
*
 * @author Mike Squillace
 *
 */
public class DefaultAdaptorFactory implements IAdaptorFactory
{

	protected static final ClassLoaderCache _clCache = ClassLoaderCache.getDefault();

	private static DefaultAdaptorFactory factoryInstance;

	private Map<String, List<IAdaptor>> _adaptorMap = new HashMap<String, List<IAdaptor>>();

	/**
	 * create a default adaptor factory
	 *
	 */
	protected DefaultAdaptorFactory () {
	}

	public static DefaultAdaptorFactory getInstance() {
		if (factoryInstance == null) {
			factoryInstance = new DefaultAdaptorFactory();
		}
		return factoryInstance;
	}
	
	/** {@inheritDoc} */
	public void registerAdaptor (Class<?> type, IAdaptor adaptor) {
		String typeName = type.getName();
		List<IAdaptor> adaptors = _adaptorMap.get(typeName);
		if (adaptors == null) {
			adaptors = new LinkedList<IAdaptor>();
			_adaptorMap.put(typeName, adaptors);
		}
		adaptors.add(adaptor);
	}

	/** {@inheritDoc} */
	public IAdaptor[] getAdaptors (Class<?> type) {
		return getAdaptors(type.getName());
	}

	/** {@inheritDoc} */
	public IAdaptor[] getAdaptors (String className) {
		List<IAdaptor> l = _adaptorMap.get(className);
		return l == null ? new IAdaptor[0]
				: (IAdaptor[]) l.toArray(new IAdaptor[l.size()]);
	}

	/** {@inheritDoc} */
	public IAdaptor[] getAllAdaptors (Class<?> targetType) {
		Class<?>[] ints = targetType.getInterfaces();
		List<IAdaptor> adaptorList = new ArrayList<IAdaptor>();
		// check original target type and all of its supertypes
		do {
			List<IAdaptor> adaptors = _adaptorMap.get(targetType.getName());
			if (adaptors != null && !adaptors.isEmpty()) {
				adaptorList.addAll(adaptors);
			}
		}while ((targetType = targetType.getSuperclass()) != null);
		// now check all of the target type's interfaces, checking each interface's
		// supertypes along the way
		for (int i = 0; i < ints.length; ++i) {
			List<IAdaptor> adaptors = _adaptorMap.get(ints[i].getName());
			if (adaptors != null && !adaptors.isEmpty()) {
				adaptorList.addAll(adaptors);
			}
			List<IAdaptor> adaptors2 = Arrays.asList(getAllAdaptors(ints[i]));
			if (adaptors2 != null && !adaptors2.isEmpty()) {
				adaptorList.addAll(adaptors2);
			}
		}
		return adaptorList.isEmpty() ? new IAdaptor[0]
				: (IAdaptor[]) adaptorList.toArray(new IAdaptor[adaptorList.size()]);
	}
}

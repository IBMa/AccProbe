/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.adapt;

/**
 * a factory for creating adaptors.  Factories are typically used for specific models using the
 * <code>Configuration.MODEL_ADAPTOR_FACTORY</code> key. A factory instance, once obtained, can be used
 * to find adaptors for a given type.
*
 * @author Mike Squillace
 *
 */
public interface IAdaptorFactory
{

	/**
	 * registers an adaptor for the given type. This type should be returned 
	 * by the adaptor's <code>getSupportedTypes</code> method.
	 * 
	 * @param type - type for which adaptor is being registered
	 * @param adaptor
	 * @see IAdaptor#getSupportedTypes()
	 */
	public void registerAdaptor (Class<?> type, IAdaptor adaptor);

	/**
	 * retrieves the adaptors for the given type. The adaptors are those that were
	 * registered via the <code>registerAdaptor</code> method.
	 * 
	 * @param type - type for which adaptors are desired
	 * @return registered adaptors for the given type or an empty array if
	 * no adaptors were registered for the given type
	 */
	public IAdaptor[] getAdaptors (Class<?> type);

	/**
	 * retrieves the adaptors for the class name. The adaptors are those that were
	 * registered via the <code>registerAdaptor</code> method.
	 * 
	 * @param className - class name for which adaptors are desired
	 * @return registered adaptors for the class name or an empty array if
	 * no adaptors were registered for the given name
	 */
	public IAdaptor[] getAdaptors (String className);

	/**
	 * retrieves the adaptors not only for the given type but for all types of
	 * which this class is assignable. This method differs from <code>getAdaptors</code> 
	 * in that <code>getAdaptors</code> returns those Adaptors only for that type
	 * while this method also tries to determine whether the
	 * class type argument is a subclass of a class type registered. For instance,
	 * a class type of <code>org.eclipse.swt.widgets.Composite</code> may not have
	 * any adaptors registered for it but it is a subclass of <code>org.eclipse.swt.widgets.Control</code>,
	 * which may have adaptors registered for it. In any case, 
	 * all of the adaptors returned are those that were registered via the <code>registerAdaptor</code> 
	 * method.
	 * 
	 * @param type - type for which adaptors are desired
	 * @return registered adaptors for the given type or any of it's super types, or
	 * an empty array if no adaptors were registered for the given type or any of its super types
	 */
	public IAdaptor[] getAllAdaptors (Class<?> type);
}

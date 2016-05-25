/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.adapt;

/**
 * provides a way to transform or "morph" objects of one type to another type. The simplest form of
 * adaptation is a simple cast, which, of course, could be done without this interface. More interesting uses
 * of this interface provide a way to capture a subset of properties of the adaptable object or to convertthe an adaptable object to a class
 * that is similar to the original class of the object but has a different public interface (e.g. the <code>java.io.File</code> class
 * and <code>org.eclipse.core.runtime.IPath</code> interface).
 *  
 * @author Mike Squillace
 *
 */
public interface IAdaptor
{

	/**
	 * returns the types to which given objects may be adapted by this adaptor. One of these types
	 * should be used when calling the <code>adapt</code> method.
	 * 
	 * @return list of types to which objects can be adapted by this adaptor
	 * @see #adapt(Object, Class)
	 */
	public Class<?>[] getSupportedTypes ();

	/**
	 * adapts the object to the given type. This method is responsible for casting or transforming
	 * or somehow producing an object of the specified type that represents a set of properties of or directly corresponds to
	 * the specified object. The type to which the object is to be adapted should be one
	 * of those returned by <code>getSupportedTypes</code>.
	 * 
	 * <p>All attempts should be made to insure that this method is symmetric. That is, if 
	 * object <code>o</code> is of type <code>B</code>, then:
	 * 
	 * <p><pre>
	 * adapt(adapt(o, A.class), B.class) = o
	 * </pre>
	 *
	 * <p>Also note that both types <code>A</code> and <code>B</code> should be returned 
	 * by <code>getSupportedTypes</code>.
	 * 
	 * @param o - object to be adapted
	 * @param newType - the type to which the object is to be adapted
	 * @return an instance of the specified type or <code>null</code> if the
	 * object could not be adapted to the given type
	 * @throws Exception
	 * @see #getSupportedTypes()
	 */
	public Object adapt (Object o, Class<?> newType) throws Exception;
	/**
	 * Retrieves the appropriate NodeSynchronizer for the given adaptor
	 * 
	 * @return node synchronizer for the given adapter or <code>null</code> if not implemented
	 */
	//public NodeSynchronizer getNodeSynchronizer ();
}

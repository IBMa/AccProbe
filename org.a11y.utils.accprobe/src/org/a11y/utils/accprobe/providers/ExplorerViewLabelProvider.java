/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.providers;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.model.DefaultModelFactory;
import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.views.ExplorerView;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class ExplorerViewLabelProvider extends LabelProvider
	implements IBaseLabelProvider
{

	/**
	 * 
	 */
	protected final ExplorerView view;

	public static final String accessibleElementIcon = "/icons/methpro_obj.gif";

	public String getText(Object comp) {
		String result = null;
		if (comp instanceof ExplorerViewNode) {
			try {
				ExplorerViewNode compNode = (ExplorerViewNode) comp;
				Object obj = compNode.getUnderlyingComponent();
				String modelType = compNode.getModelType();
				IModel model = DefaultModelFactory.getInstance().resolveModel(modelType);
				result = model.getNodeLocator().locate(obj, obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (result != null && result.startsWith("/")) {
			result = result.substring(1);
		}
		return result == null ? super.getText(comp) : result;
	}

	public Image getImage(Object comp) {
		Object element = ((ExplorerViewNode) comp).getUnderlyingComponent();
		ImageDescriptor id = null;
		Image icon = null;
		if (element instanceof IAccessibleElement) {
			id = Activator.getImageDescriptor(accessibleElementIcon);
		}
		if (id == null) {
			id = Activator.getImageDescriptor(Activator.DEFAULT_IMAGE_NAME);
		}	
		icon = id.createImage();
		return icon;
	}

	public ExplorerViewLabelProvider(ExplorerView part) {
		super();
		view = part;
	}

}

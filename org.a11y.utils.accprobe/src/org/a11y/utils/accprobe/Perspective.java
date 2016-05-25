/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe;

import org.a11y.utils.accprobe.views.EventMonitorView;
import org.a11y.utils.accprobe.views.ExplorerView;
import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class Perspective implements IPerspectiveFactory {

	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.addStandaloneView(ExplorerView.ID,  true, IPageLayout.LEFT, 0.5f, editorArea);		
		layout.addView(EventMonitorView.ID, IPageLayout.BOTTOM, 0.8f, ExplorerView.ID);
		layout.addView(PropertiesView.ID, IPageLayout.RIGHT, 0.5f, ExplorerView.ID);
		
		layout.getViewLayout(ExplorerView.ID).setCloseable(false);
		layout.getViewLayout(PropertiesView.ID).setCloseable(false);
		layout.getViewLayout(EventMonitorView.ID).setCloseable(false);
	}
}

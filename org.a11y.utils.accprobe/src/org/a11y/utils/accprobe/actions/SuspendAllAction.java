package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.IAccessibilityService;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessibilityService;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaWindowService;
import org.a11y.utils.accprobe.views.AbstractViewPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;

public class SuspendAllAction extends Action{
	public static final String ID = "org.a11y.utils.accprobe.actions.SuspendAllAction";
	private IAccessibilityService accService = null;
	private boolean alwaysOnTop = false;
	private final IWorkbenchWindow window;


	public SuspendAllAction (IWorkbenchWindow window, String label)  {
		super(label, IAction.AS_CHECK_BOX);
		setId(ID);
		setText("Suspend all actions");
		this.window = window;
	}

	public void run () {
		try {
			accService = AccessibilityServiceManager.getInstance().getAccessibilityService(
					MsaaAccessibilityService.MSAA_ACCSERVICE_NAME);
		} catch (AccessibilityServiceException e) {
			e.printStackTrace();
		}
		if (isChecked()) {
			disableAllActions();
			setText("Resume all actions");
		}else {
			resumeAllActions();
			setText("Suspend all actions");
		}
	}

	private void resumeAllActions() {
		//resumes tracking, event monitoring, highlighting, properties view
		AbstractViewPart.setActionSuspended(false);

		//Always on Top

		if (accService != null && accService instanceof MsaaAccessibilityService) {
			MsaaWindowService msaaService = (MsaaWindowService) accService.getWindowService();
			if(alwaysOnTop == true){
				msaaService.setWindowOnTop(window.getShell().handle);
			}
		}
	}

	private void disableAllActions() {
		//suspends tracking, event monitoring, highlighting, properties view
		AbstractViewPart.setActionSuspended(true);
		MsaaAccessible.eraseDesktop();
		
		//Always on top
		if (accService != null && accService instanceof MsaaAccessibilityService) {
			MsaaWindowService msaaService = (MsaaWindowService) accService.getWindowService();
			if(msaaService.isAlwaysonTop()){
				alwaysOnTop = true;
				msaaService.setWindowNoTop(window.getShell().handle);
			}
		}
	}
}

/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	// The plug-in ID
	public static final String ACCPROBE_WINDOW_CLASSNAME = "ACCPROBE_WINDOW";
	public static final String PLUGIN_ID = "org.a11y.utils.accprobe";

	// The shared instance
	private static Activator plugin;

	protected ImageRegistry imageRegistry;

	public static final String DEFAULT_IMAGE_NAME = "accprobe.GIF";

	private final static int SPI_GETSCREENREADER = 70;
	private final static int SPI_SETSCREENREADER = 71;
	private final static int SPIF_UPDATEINIFILE = 0x01;
	private final static int SPIF_SENDCHANGE = 0x02;
	private final static int HWND_BROADCAST = 0xffff;
	private final static int WM_WININICHANGE = 0x001A;
	private static boolean bSPI_SCREENREADER_SET = false;
	 
	public static void setScreenReader(boolean set) {
		if( OS.SystemParametersInfo( SPI_SETSCREENREADER, set ? 1:0, (int[])null, SPIF_UPDATEINIFILE | SPIF_SENDCHANGE ) ) {
			OS.PostMessage( HWND_BROADCAST, WM_WININICHANGE, SPI_SETSCREENREADER, 0 );
		}
	}
	 
	public static boolean getScreenReader() {
		int[] pResult = new int[1];
		if( OS.SystemParametersInfo( SPI_GETSCREENREADER, 0, pResult, 0 ) ) {
			return 0!=pResult[0];
		}
		return false;
	}
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		TCHAR windowClass = new TCHAR (0, "SWT_WINDOW0", true);
		TCHAR windowName = new TCHAR (0, "Accessibility Probe", true);
		if (OS.FindWindow(windowClass, windowName) != 0) {
			throw new RuntimeException("AccProbe already started");
		}
		
		bSPI_SCREENREADER_SET = getScreenReader();
		if (!bSPI_SCREENREADER_SET) {
			setScreenReader(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (!bSPI_SCREENREADER_SET) {
			setScreenReader(false);
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public String getPluginId () {
		return PLUGIN_ID;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}


}

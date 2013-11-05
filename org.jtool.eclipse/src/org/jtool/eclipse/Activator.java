/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

/**
 * The activator class controlling the plug-in life cycle.
 * @author Katsuhisa Maruyama
 */
public class Activator extends AbstractUIPlugin {
    
    /**
     * The plug-in identification.
     */
    public static final String PLUGIN_ID = "org.jtool.eclipse";
    
    /**
     * A shared plug-in object.
     */
    private static Activator plugin;
    
    /**
     * Creates a plug-in runtime object.
     */
    public Activator() {
        super();
    }
    
    /**
     * Refreshes this plug-in's actions when the plug-in is activated.
     * @param context the bundle context for this plug-in
     * @throws Exception if this plug-in did not start up properly
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }
    
    /**
     * Saves this plug-in's preference when the plug-in is stopped.
     * @param context the bundle context for this plug-in
     * @throws Exception if this method fails to shutdown this plug-in
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
     
    /**
     * Returns the shared instance.
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    /**
     * Obtains the workbench window.
     * @return the workbench window
     */
    public static IWorkbenchWindow getWorkbenchWindow() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }
}

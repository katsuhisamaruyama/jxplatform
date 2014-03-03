/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.event;

/**
 * A listener interface for receiving alert events.
 * @author Katsuhisa Maruyama
 */
public interface JtoolAlertListener extends JtoolEventListener {
    
    /**
     * Displays information about a received alert event.
     * @param evt the received event
     */
    public void notifyAlert(JtoolAlertEvent evt);
}

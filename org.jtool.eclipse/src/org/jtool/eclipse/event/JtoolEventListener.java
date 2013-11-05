/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.event;

import java.util.EventListener;

/**
 * A listener interface for receiving alert events.
 * @author Katsuhisa Maruyama
 */
public interface JtoolEventListener extends EventListener {
    
    /**
     * Displays information about a received alert event.
     * @param evt the received event
     */
    public void notifyAlert(JtoolEvent evt);
}

/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.event;

/**
 * A listener interface for receiving log events.
 * @author Katsuhisa Maruyama
 */
public interface JtoolLogListener extends JtoolEventListener {
    
    /**
     * Displays information about s received log event.
     * @param evt the received event.
     */
    public void notifyLog(JtoolLogEvent evt);
}

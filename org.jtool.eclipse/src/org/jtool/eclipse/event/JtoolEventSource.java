/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.event;

import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * An object on which the event initially occurred.
 * @author Katsuhisa Maruyama
 */
public class JtoolEventSource {
    
    static Logger logger = Logger.getLogger(JtoolEventSource.class.getName());
    
    /**
     * A collection for storing listeners for alerts.
     */
    protected HashSet<JtoolEventListener> alertListeners = new HashSet<JtoolEventListener>();
    
    /**
     * A collection for storing listeners for logs.
     */
    protected HashSet<JtoolEventListener> logListeners = new HashSet<JtoolEventListener>();
    
    /**
     * Create an empty object.
     */
    protected JtoolEventSource() {
    }
    
    /**
     * Adds a listener in order to receive alert events from this source.
     * @param listener the warning listener
     */
    public void addEventListener(JtoolAlertListener listener) {
        alertListeners.add(listener);
    }
    
    /**
     * Removes a listener which no longer receives alert events from this source.
     * @param listener the warning listener
     */
    public void removeEventListener(JtoolAlertListener listener) {
        alertListeners.remove(listener);
    }
    
    /**
     * Sends a alert event to all the alert listeners.
     * @param evt the alert event
     */
    public void fire(JtoolAlertEvent evt) {
        fire(evt, alertListeners);
    }
    
    /**
     * Adds a listener in order to receive log events from this source.
     * @param listener the log listener
     */
    public void addEventListener(JtoolLogListener listener) {
        logListeners.add(listener);
    }
    
    /**
     * Removes a listener which no longer receives log events from this source.
     * @param listener the log listener
     */
    public void removeEventListener(JtoolLogListener listener) {
        logListeners.remove(listener);
    }
    
    /**
     * Sends a log event to all the log listeners.
     * @param evt the log event
     */
    public void fire(JtoolLogEvent evt) {
        fire(evt, logListeners);
    }
    
    
    /**
     * Actually sends a event to all the listeners.
     * @param evt the event
     * @param listeners the collection of the listeners that should receive the event
     */
    private void fire(JtoolEvent evt, HashSet<JtoolEventListener> listeners) {
        if (listeners.size() == 0) {
            String mesg = evt.getMessage();
            if (mesg.length() != 0) {
                logger.info(mesg);
            }
            return;
        }
        
        for (JtoolEventListener listener : listeners) {          
            if (listener instanceof JtoolAlertListener) {
                ((JtoolAlertListener)listener).notifyAlert((JtoolAlertEvent)evt);
                
            } else if (listener instanceof JtoolLogListener) {
                ((JtoolLogListener)listener).notifyLog((JtoolLogEvent)evt);
            }
        }
    }
}

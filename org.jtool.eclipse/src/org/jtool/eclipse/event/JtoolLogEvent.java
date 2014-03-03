/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.event;

import java.sql.Timestamp;

/**
 * An event for logging.
 * @author Katsuhisa Maruyama
 */
public class JtoolLogEvent extends JtoolEvent {
    private static final long serialVersionUID = 8701638485146710754L;
    
    /**
     * A time-stamp of a log event.
     */
    private String timestamp;
    
    /**
     * Creates an event object containing log information.
     * @param source the object on which the event initially occurred
     * @param msg a message about this event
     */
    public JtoolLogEvent(Object source, String msg) {
        super(source, msg);
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.timestamp = timestamp.toString();
    }
    
    /**
     * Returns the time when the event occurred.
     * @return the string indicating the time
     */
    public String getTimestamp() {
        return timestamp;
    }
}

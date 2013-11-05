/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.event;

import java.util.EventObject;

/**
 * An event that contains information.
 * @author Katsuhisa Maruyama
 */
public class JtoolEvent extends EventObject {
    private static final long serialVersionUID = -180027864682178231L;
    
    /**
     * A message related to an event.
     */
    protected String message;
    
    /**
     * Creates an event object containing event's information.
     * @param source the object on which the event initially occurred
     */
    protected JtoolEvent(Object source) {
        super(source);
    }
    
    /**
     * Creates an event object containing event's information.
     * @param source the object on which the event initially occurred
     * @param msg a message about this event
     */
    protected JtoolEvent(Object source, String msg) {
        super(source);
        message = msg;
    }
    
    /**
     * Returns a message sent from the event source.
     * @return the message string
     */
    public String getMessage() {
        return message;
    }
}

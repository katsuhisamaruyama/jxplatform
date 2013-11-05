/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.event;

/**
 * An event indicating that an alert has occurred. The lowest level is 0.
 * @author Katsuhisa Maruyama
 */
public class JtoolAlertEvent extends JtoolEvent {
    private static final long serialVersionUID = 3347104852835663618L;
    
    /**
     * The level of an alert.
     */
    private int level;
    
    /**
     * Creates an event object containing alert information.
     * @param source the object on which the event initially occurred
     * @param msg a message about this event
     * @param level the level of this event
     */
    public JtoolAlertEvent(Object source, String msg, int level) {
        super(source, msg);
        this.level = level;
    }
    
    /**
     * Creates an event object containing alert information.
     * @param source the object on which the event initially occurred
     * @param msg a message about this event
     */
    public JtoolAlertEvent(Object source, String msg) {
        this(source, msg, 0);
    }
    
    /**
     * Returns the level of an occurring alert. 
     * @return the level
     */
    public int getLevel() {
        return level;
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

/**
 * An object encapsulating a fatal error about the construction of graphs.
 * @author Katsuhisa Maruyama
 */
public class GraphException extends Exception {
    
    private static final long serialVersionUID = 6850935421431463968L;
    
    /**
     * Creates a new exception with null as its detail message.
     */
    protected GraphException() {
        super();
    }
    
    /**
     * Creates a new exception with a specified message.
     * @param mesg A detail message.
     */
    public GraphException(String mesg) {
        super(mesg);
    }
}

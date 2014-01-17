/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

/**
 * An element of a graph.
 * @author Katsuhsa Maruyama
 */
public abstract class GraphElement {
    
    /**
     * The identification number.
     */
    protected long id = 0;
    
    /**
     * Creates a new, empty element.
     */
    protected GraphElement() {
    }
    
    /**
     * Creates a new, empty element with its new identification number.
     */
    protected GraphElement(long id) {
        this.id = id;
    }
    
    /**
     * Sets the identification number for this graph element.
     * @param id identification number
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Returns the identification number for this graph element.
     * @return the identification number
     */
    public long getId() {
        return id;
    }
    
    /**
     * Displays information about this graph element.
     */
    public abstract void print();
}

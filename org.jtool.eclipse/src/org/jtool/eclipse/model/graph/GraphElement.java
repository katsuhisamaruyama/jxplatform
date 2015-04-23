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
     * Checks if a given graph element is equal to this.
     * @param elem the reference graph element with which to compare
     * @return <code>true</code> if this element is the same as the given element, otherwise <code>false</code>
     */
    public boolean equals(GraphElement elem) {
        return getId() == elem.getId();
    }
    
    /**
     * Displays information about this graph element.
     */
    public abstract void print();
}

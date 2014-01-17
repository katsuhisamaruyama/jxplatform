/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaElement;

/**
 * The entry node of a CFG for a method or a constructor.
 * @author Katsuhisa Maruyama
 */
abstract public class CFGEntry extends CFGNode {
    
    /**
     * A CFG for this entry node. 
     */
    private CFG cfg = null;
    
    /**
     * Creates a new, empty object.
     */
    protected CFGEntry() {
        super();
    }
    
    /**
     * Creates a new node. This node does not correspond to any Java element.
     * @param sort the sort of this node
     */
    protected CFGEntry(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java element.
     * @param jelem the Java element starting from this entry node
     * @param sort the sort of this node
     */
    protected CFGEntry(JavaElement jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Sets the CFG for this entry node.
     * @param g the CFG
     */
    public void setCFG(CFG g) {
        cfg = g;
    }
    
    /**
     * Returns the CFG for this entry node.
     * @return the CFG
     */
    public CFG getCFG() {
        return cfg;
    }
    
    /**
     * Returns the name of of this entry node.
     * @return the name string
     */
    abstract public String getName();
    
    /**
     * Returns the fully-qualified name of the type of this entry node.
     * @return the fully-qualified name string
     */
    abstract public String getType();
    
    /**
     * Tests if this entry node corresponds to a method or a constructor. 
     * @return <code>true</code> if this is the entry node for a method or constructor entry, otherwise <code>false</code>
     */
    public boolean isMethodEntry() {
        return sort == GraphNodeSort.methodEntry || sort == GraphNodeSort.constructorEntry;
    }
    
    /**
     * Tests if this entry node corresponds to a field. 
     * @return <code>true</code> if this is the entry node for a field entry, otherwise <code>false</code>
     */
    public boolean isFieldEntry() {
        return sort == GraphNodeSort.fieldEntry;
    }
    
    /**
     * Tests if this entry node corresponds to an initializer. 
     * @return <code>true</code> if this is the entry node for an initializer, otherwise <code>false</code>
     */
    public boolean isInitializerEntry() {
        return sort == GraphNodeSort.initializerEntry;
    }
    
    /**
     * Tests if this entry node corresponds to an initializer. 
     * @return <code>true</code> if this is the entry node for an initializer, otherwise <code>false</code>
     */
    public boolean isEnumConstantEntry() {
        return sort == GraphNodeSort.enumConstantEntry;
    }
    
    /**
     * Collects information about this node for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + getId() + "] ");
        switch (sort) {
            case classEntry: buf.append("class entry"); break;
            case interfaceEntry: buf.append("interface entry"); break;
            case enumEntry: buf.append("enum entry"); break;
            case methodEntry: buf.append("method entry"); break;
            case constructorEntry: buf.append("constructor entry"); break;
            case initializerEntry: buf.append("static initializer entry"); break;
            case fieldEntry: buf.append("field entry"); break;
            case enumConstantEntry: buf.append("enum constant entry"); break;
            default: break;
        }
        buf.append(": ");
        buf.append(getName());
        return buf.toString();
    }
}

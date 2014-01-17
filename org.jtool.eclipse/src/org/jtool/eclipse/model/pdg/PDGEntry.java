/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFGEntry;

/**
 * The entry node of PDGs for a method or a constructor.
 * @author Katsuhisa Maruyama
 */
public class PDGEntry extends PDGNode {
    
    /**
     * A PDG for this entry node. 
     */
    private PDG pdg = null;
    
    /**
     * Creates a new, empty object.
     */
    protected PDGEntry() {
        super();
    }
    
    /**
     * Creates a new node.
     * @param node the entry node of a CFG corresponding to the entry node of a PDG.
     */
    public PDGEntry(CFGEntry node) {
        super(node);
    }
    
    /**
     * Sets the CFG for this entry node.
     * @param g the CFG
     */
    public void setPDG(PDG g) {
        pdg = g;
    }
    
    /**
     * Returns the CFG for this entry node.
     * @return the CFG
     */
    public PDG getPDG() {
        return pdg;
    }
    
    /**
     * Returns the CFG node for this entry node.
     * @return the CFG node for this entry node
     */
    public CFGEntry getCFGEntry() {
        return (CFGEntry)getCFGNode();
    }
    
    /**
     * Returns the name of of this method or field.
     * @return the name of this method or field
     */
    public String getName() {
        return getCFGEntry().getName();
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public PDGEntry clone() {
        PDGEntry cloneNode = new PDGEntry(getCFGEntry());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone. The PDG for this node is not copied.
     * @param cloneNode the clone of this node
     */
    protected void clone(PDGEntry cloneNode) {
        super.clone(cloneNode);
    }
}

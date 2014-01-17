/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFGEntry;

/**
 * The entry node of PDGs for a class or an interface.
 * @author Katsuhisa Maruyama
 */
public class PDGClassEntry extends PDGEntry {
    
    /**
     * Creates a new, empty object.
     */
    protected PDGClassEntry() {
        super();
    }
    
    /**
     * Creates a new node.
     * @param node the entry node of a CCFG corresponding to the entry node of a SDG (ClDG).
     */
    public PDGClassEntry(CFGEntry node) {
        super(node);
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public PDGClassEntry clone() {
        PDGClassEntry cloneNode = new PDGClassEntry((CFGEntry)getCFGNode());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone. The ClDG for this node is not copied.
     * @param cloneNode the clone of this node
     */
    protected void clone(PDGClassEntry cloneNode) {
        super.clone(cloneNode);
    }
}

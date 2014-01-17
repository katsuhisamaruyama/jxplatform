/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg.internal;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.cfg.CFGParameter;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.java.JavaStatement;

/**
 * A node for a <code>catch</code> clause of CFGs.
 * @author Katsuhisa Maruyama
 */
public class CFGCatch extends CFGStatement {
    
    /**
     * The formal-in node for an object this clause catches. 
     */
    private CFGParameter formalIn;
    
    /**
     * Creates a new, empty object.
     */
    CFGCatch() {
        super();
    }
    
    /**
     * Creates a new node corresponding to a given Java statement.
     * @param jelem the Java element
     * @param sort the sort of this node
     */
    CFGCatch(JavaStatement jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Sets the formal-in node for an object this clause catches.
     * @param node the formal-in node for an object this clause catches
     */
    void setFormalIn(CFGParameter node) {
        formalIn = node;
    }
    
    /**
     * Returns the formal-in node for an object this clause catches.
     * @return node the formal-in node for an object this clause catches
     */
    CFGParameter getFormalIn() {
        return formalIn;
    }
}

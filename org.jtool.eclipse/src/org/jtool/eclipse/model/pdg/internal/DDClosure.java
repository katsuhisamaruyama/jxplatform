/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import org.jtool.eclipse.model.pdg.DD;
import org.jtool.eclipse.model.pdg.PDG;
import org.jtool.eclipse.model.pdg.PDGStatement;

/**
 * An object storing information about a closure created by traversing only the data dependence.
 * @author Katsuhisa Maruyama
 */
public class DDClosure extends PDG {
    
    /**
     * Creates a new, empty object.
     */
    protected DDClosure() {
        super();
    }
    
    /**
     * Creates a new data dependence closure object.
     * @param node the criterion node for this closure
     * @param jv the criterion variable for this closure
     */
    public DDClosure(PDGStatement node) {
        super();
        
        traverseBackward(node);
    }
    
    /**
     * Backward traverses data dependence edges and nodes of the PDG nodes and collects them.
     * @param anchor the anchor node
     */
    private void traverseBackward(PDGStatement anchor) {
        add(anchor);
        
        for (DD edge : anchor.getIncomingDDEdges()) {
            add(edge);
            PDGStatement node = (PDGStatement)edge.getSrcNode();
            
            if (!getNodes().contains(node)) {
                traverseBackward(node);
            }
        }
    }
    
    /**
     * Collects information about this slice for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- DD Closure (from here) -----\n");
        buf.append("\n");
        buf.append(getNodeInfo()); 
        buf.append(getEdgeInfo());
        buf.append("----- DD Closure (to here) -----\n");
        
        return buf.toString();
    }
}

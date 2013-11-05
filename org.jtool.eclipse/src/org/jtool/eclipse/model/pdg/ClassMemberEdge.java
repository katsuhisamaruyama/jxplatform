/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.GraphEdge;

/**
 * Constructs call edges in a class dependence graph (ClDG).
 * @author Katsuhisa Maruyama
 */
public class ClassMemberEdge extends GraphEdge {
    
    /**
     * Creates a new, empty object.
     */
    protected ClassMemberEdge() {
        super();
    }
    
    /**
     * Creates a new edge between two nodes.
     * @param src the source node of this edge
     * @param dst the destination node of this edge
     */
    public ClassMemberEdge(PDGNode src, PDGNode dst) {
        super(src, dst);
    } 
    
    /**
     * Tests if this edge equals to a given edge.
     * @param edge the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(GraphEdge edge) {
        if (edge == null || !(edge instanceof ClassMemberEdge)) {
            return false;
        }
        
        return super.equals((ClassMemberEdge)edge);
    }
    
    /**
     * Returns a hash code value for this edge.
     * @return the hash code value for the edge
     */
    public int hashCode() {
        return super.hashCode();
    }
    
    /**
     * Collects information about this edge for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        switch (sort) {
            case methodCall: buf.append("M: "); break;
            default: break;
        }
        
        buf.append(super.toString());
        return buf.toString();
    }
}

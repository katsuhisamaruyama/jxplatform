/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.GraphEdge;
import org.apache.log4j.Logger;

/**
 * An edge of PDGs, which represents dependence between PDG nodes.
 * @author Katsuhisa Maruyama
 */
public class Dependence extends GraphEdge {
    
    static Logger logger = Logger.getLogger(Dependence.class.getName());
    
    /**
     * Creates a new, empty object.
     */
    protected Dependence() {
        super();
    }
    
    /**
     * Creates a new edge between two PDG nodes.
     * @param src the source node of this edge
     * @param dst the destination node of this edge
     */
    protected Dependence(PDGNode src, PDGNode dst) {
        super(src, dst);
    }
    
    /**
     * Tests if this edge represents a control dependence. 
     * @return <code>true</code> if this edge is a control dependence, otherwise <code>false</code>
     */
    public boolean isCD() {
        return sort.isCD();
    }
    
    /**
     * Tests if this edge represents a data dependence. 
     * @return <code>true</code> if this edge is a data dependence, otherwise <code>false</code>
     */
    public boolean isDD() {
        return sort.isDD();
    }
    
    /**
     * Returns the source node for this edge.
     * @return the source PDG node
     */
    public PDGNode getSrcNode() {
        return (PDGNode)src;
    }
    
    /**
     * Returns the destination node for this edge.
     * @return the destination PDG node
     */
    public PDGNode getDstNode() {
        return (PDGNode)dst;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param obj the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(Object obj) {
        if (obj instanceof Dependence) {
            Dependence edge = (Dependence)obj;
            return equals(edge);
        }
        return false;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param edge the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(Dependence edge) {
        if (edge == null) {
            return false;
        }
        
        return this == edge || (getSrcNode().equals(edge.getSrcNode()) &&
                                getDstNode().equals(edge.getDstNode()) &&
                                getSort() == edge.getSort());
    }
    
    /**
     * Returns a hash code value for this edge.
     * @return the hash code value for the edge
     */
    public int hashCode() {
        return super.hashCode();
    }
    
    /**
     * Creates a clone of this edge.
     * @return the clone of this edge
     */
    public Dependence clone() {
        Dependence cloneEdge = new Dependence(getSrcNode(), getDstNode());
        clone(cloneEdge);
        return cloneEdge;
    }
    
    /**
     * Copies all the attributes of this edge into a given clone.
     * @param cloneEdge the clone of this edge
     */
    protected void clone(Dependence cloneEdge) {
        super.clone(cloneEdge);
    }
    
    /**
     * Displays information about this edge.
     */
    public void print() {
        logger.info(toString());
    }
    
    /**
     * Collects information about this edge for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(src.getId());
        buf.append(" -> ");
        buf.append(dst.getId());
        
        return buf.toString();
    }
}

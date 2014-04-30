/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.GraphEdgeSort;

/**
 * An edge of PDGs, which represents control dependence between PDG nodes.
 * @author Katsuhisa Maruyama
 */
public class CD extends Dependence {
    
    /**
     * Creates a new, empty object.
     */
    protected CD() {
        super();
    }
    
    /**
     * Creates a new edge between two PDG nodes.
     * @param src the source node of this edge
     * @param dst the destination node of this edge
     */
    public CD(PDGNode src, PDGNode dst) {
        super(src, dst);
    }
    
    /**
     * Sets this edge as a true control dependence.
     */
    public void setTrue() {
        sort = GraphEdgeSort.trueControlDependence;
    }
    
    /**
     * Tests if this edge is a true control dependence.
     * @return <code>true</code> if this edge is a true control dependence, otherwise <code>false</code>
     */
    public boolean isTrue() {
        return sort == GraphEdgeSort.trueControlDependence;
    }
    
    /**
     * Sets this edge as a false control dependence.
     */
    public void setFalse() {
        sort = GraphEdgeSort.falseControlDependence;
    }
    
    /**
     * Tests if this edge is a false control dependence.
     * @return <code>true</code> if this edge is a false control dependence, otherwise <code>false</code>
     */
    public boolean isFalse() {
        return sort == GraphEdgeSort.falseControlDependence;
    }
    
    /**
     * Sets this edge as a fall through dependence.
     */
    public void setFall() {
        sort = GraphEdgeSort.fallControlDependence;
    }
    
    /**
     * Tests if this edge is a fall through dependence.
     * @return <code>true</code> if this edge is a fall through dependence, otherwise <code>false</code>
     */
    public boolean isFall() {
        return sort == GraphEdgeSort.fallControlDependence;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param obj the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(Object obj) {
        if (obj instanceof CD) {
            CD edge = (CD)obj;
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
        if (edge == null || !(edge instanceof CD)) {
            return false;
        }
        
        return super.equals((CD)edge);
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
    public CD clone() {
        CD cloneEdge = new CD(getSrcNode(), getDstNode());
        clone(cloneEdge);
        return cloneEdge;
    }
    
    /**
     * Copies all the attributes of this edge into a given clone.
     * @param cloneEdge the clone of this edge
     */
    protected void clone(CD cloneEdge) {
        super.clone(cloneEdge);
    }
    
    /**
     * Collects information about this edge for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        switch (sort) {
            case trueControlDependence: buf.append("T: "); break;
            case falseControlDependence: buf.append("F: "); break;
            case fallControlDependence: buf.append("Fall:"); break;
            default: break;
        }
        
        buf.append(src.getId());
        buf.append(" -> ");
        buf.append(dst.getId());
        
        return buf.toString();
    }
}

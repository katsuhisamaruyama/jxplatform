/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.GraphEdgeSort;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.java.JavaVariableAccess;

/**
 * Constructs dependences related to parameters in a class dependence graph (ClDG).
 * @author Katsuhisa Maruyama
 */
public class ParameterEdge extends DD {
    
    /**
     * Creates a new, empty object.
     */
    protected ParameterEdge() {
        super();
    }
    
    /**
     * Creates a new edge between two nodes.
     * @param src the source node of this edge
     * @param dst the destination node of this edge
     */
    public ParameterEdge(PDGNode src, PDGNode dst) {
        super(src, dst);
    }
    
    /**
     * Creates a new edge between two nodes, which carries a variable.
     * @param src the source node of this edge
     * @param dst the destination node of this edge
     * @param jv the variable carried by this edge
     */
    public ParameterEdge(PDGNode src, PDGNode dst, JavaVariableAccess jv) {
        super(src, dst);
        jvar = jv;
    }
    
    /**
     * Returns the variable carried by this edge.
     * @return the variable carried by this edge
     */
    public JavaVariableAccess getVariable() {
        return jvar;
    }
    
    /**
     * Sets this edge as a parameter-in dependence. 
     */
    public void setParameterIn() {
        sort = GraphEdgeSort.parameterIn;
    }
    
    /**
     * Tests if this edge is a parameter-in dependence.
     * @return <code>true</code> if this edge is a parameter-in dependence, otherwise <code>false</code>
     */
    public boolean isParameterIn() {
        return sort == GraphEdgeSort.parameterIn;
    }
    
    /**
     * Sets this edge as a parameter-out dependence. 
     */
    public void setParameterOut() {
        sort = GraphEdgeSort.parameterOut;
    }
    
    /**
     * Tests if this edge is a parameter-out dependence.
     * @return <code>true</code> if this edge is a parameter-out dependence, otherwise <code>false</code>
     */
    public boolean isParameterOut() {
        return sort == GraphEdgeSort.parameterOut;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param obj the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(Object obj) {
        if (obj instanceof ParameterEdge) {
            ParameterEdge edge = (ParameterEdge)obj;
            return equals(edge);
        }
        return false;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param edge the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(GraphEdge edge) {
        if (edge == null || !(edge instanceof ParameterEdge)) {
            return false;
        }
        
        return super.equals((ParameterEdge)edge);
    }
    
    /**
     * Returns a hash code value for this edge.
     * @return the hash code value for the edge
     */
    public int hashCode() {
        return super.hashCode();
    }
}

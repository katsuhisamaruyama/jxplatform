/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.GraphEdgeSort;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.java.JavaVariableAccess;

/**
 * Constructs dependences related to parameters in a class dependence graph (ClDG).
 * @author Katsuhisa Maruyama
 */
public class ParameterEdge extends Dependence {
    
    /**
     * A variable that this edge carries.
     */
    private JavaVariableAccess jvar;
    
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
    protected ParameterEdge(PDGNode src, PDGNode dst, JavaVariableAccess jv) {
        super(src, dst);
        jvar = jv;
    }
    
    /**
     * Sets a variable carried by this edge.
     * @param jv the variable
     */
    protected void setVariable(JavaVariableAccess jv) {
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
    
    /**
     * Collects information about this edge for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + getId() + "] ");
        switch (sort) {
            case parameterIn: buf.append("pin: "); break;
            case parameterOut: buf.append("pout: "); break;
            default: break;
        }
        
        buf.append(super.toString());
        
        buf.append(" [ ");
        buf.append(jvar.getName());
        buf.append(" ]");
        
        return buf.toString();
    }
}

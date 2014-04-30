/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.GraphEdgeSort;
import org.jtool.eclipse.model.java.JavaVariableAccess;

/**
 * An edge of PDGs, which represents data dependence between PDG nodes.
 * @author Katsuhisa Maruyama
 */
public class DD extends Dependence {
    
    /**
     * A variable that this edge carries.
     */
    protected JavaVariableAccess jvar;
    
    /**
     * A loop carried node if this edge carries a variable for loop.
     * The default value is <code>null</code>, which means that this edge has no loop carreied node.
     */
    private PDGNode loopCarriedNode = null;
    
    /**
     * Creates a new, empty object.
     */
    protected DD() {
        super();
    }
    
    /**
     * Creates a new edge between two PDG nodes.
     * @param src the source node of this edge
     * @param dst the destination node of this edge
     */
    public DD(PDGNode src, PDGNode dst) {
        super(src, dst);
    }
    
    /**
     * Creates a new edge with a variable between two PDG nodes.
     * @param src the source node of this edge
     * @param dst the destination node of this edge
     * @param jv the variable carried by this edge
     */
    public DD(PDGNode src, PDGNode dst, JavaVariableAccess jv) {
        super(src, dst);
        setVariable(jv);
    }
    
    /**
     * Sets a variable carried by this edge.
     * @param jv the variable for this edge
     */
    public void setVariable(JavaVariableAccess jv) {
        jvar = jv;
    }
    
    /**
     * Returns the variable carried by this edge.
     * @return the variable for this edge
     */
    public JavaVariableAccess getVariable() {
        return jvar;
    }
    
    /**
     * Sets a PDG node which carries any variable for loop.
     * @param node the loop carried node for this edge
     */
    public void setLoopCarriedNode(PDGNode node) {
        loopCarriedNode = node;
    }
    
    /**
     * Returns the PDG node which carries any variable for loop.
     * @return the loop carried node for this edge
     */
    public PDGNode getLoopCarriedNode() {
        return loopCarriedNode;
    }
    
    /**
     * Tests if this edge has a loop carried node.
     * @return <code>true</code> if this edge has a loop carried node, or <code>false</code> for a loop-indent edge
     */
    public boolean isLoopCarried() {
        return loopCarriedNode != null;
    }
    
    /**
     * Tests if this edge is independent from loop.
     * @return <code>true</code> if this edge is independent from loop, or <code>false</code> for a loop-carried edge
     */
    public boolean isLoopIndependent() {
        return loopCarriedNode == null;
    }
    
    /**
     * Tests if this edge is a def-use dependence.
     * @return <code>true</code> if this edge is a def-use dependence, otherwise <code>false</code>
     */
    public boolean isDefUse() {
        return isLIDD() || isLCDD();
    }
    
    /**
     * Sets this edge as a loop-independent def-use dependence.
     */
    public void setLIDD() {
        sort = GraphEdgeSort.loopIndependentDefUseDependence;
    }
    
    /**
     * Tests if this edge as a loop-independent def-use dependence.
     * @return <code>true</code> if this edge is a loop-independent def-use dependence, otherwise <code>false</code>
     */
    public boolean isLIDD() {
        return sort == GraphEdgeSort.loopIndependentDefUseDependence;
    }
    
    /**
     * Sets this edge as a loop-carried def-use dependence.
     */
    public void setLCDD() {
        sort = GraphEdgeSort.loopCarriedDefUseDependence;
    }
    
    /**
     * Tests if this edge as a loop-carried def-use dependence.
     * @return <code>true</code> if this edge is a loop-carried def-use dependence, otherwise <code>false</code>
     */
    public boolean isLCDD() {
        return sort == GraphEdgeSort.loopCarriedDefUseDependence;
    }
    
    /**
     * Sets this edge as a def-order dependence.
     */
    public void setDefOrder() {
        sort = GraphEdgeSort.defOrderDependence;
    }
    
    /**
     * Tests if this edge as a def-order dependence.
     * @return <code>true</code> if this edge is a def-order dependence, otherwise <code>false</code>
     */
    public boolean isDefOrder() {
        return sort == GraphEdgeSort.defOrderDependence;
    }
    
    /**
     * Sets this edge as an output dependence. 
     */
    public void setOutput() {
        sort = GraphEdgeSort.outputDependence;
    }
    
    /**
     * Tests if this edge as an output dependence.
     * @return <code>true</code> if this edge is an output dependence, otherwise <code>false</code>
     */
    public boolean isOutput() {
        return sort == GraphEdgeSort.outputDependence;
    }
    
    /**
     * Sets this edge as an anti dependence. 
     */
    public void setAnti() {
        sort = GraphEdgeSort.antiDependence;
    }
    
    /**
     * Tests if this edge as an anti dependence.
     * @return <code>true</code> if this edge is an anti dependence, otherwise <code>false</code>
     */
    public boolean isAnti() {
        return sort == GraphEdgeSort.antiDependence;
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
     * Sets this edge as a parameter-out dependence.
     */
    public void setSummary() {
        sort = GraphEdgeSort.summary;
    }
    
    /**
     * Tests if this edge is a parameter-out dependence.
     * @return <code>true</code> if this edge is a parameter-out dependence, otherwise <code>false</code>
     */
    public boolean isSummary() {
        return sort == GraphEdgeSort.summary;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param obj the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(Object obj) {
        if (obj instanceof DD) {
            DD edge = (DD)obj;
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
        if (edge == null || !(edge instanceof DD)) {
            return false;
        }
        
        DD dd = (DD)edge;
        return super.equals(dd) && getVariable().equals(dd.getVariable());
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
    public DD clone() {
        DD cloneEdge = new DD(getSrcNode(), getDstNode());
        clone(cloneEdge);
        return cloneEdge;
    }
    
    /**
     * Copies all the attributes of this edge into a given clone.
     * @param cloneEdge the clone of this edge
     */
    protected void clone(DD cloneEdge) {
        super.clone(cloneEdge);
        cloneEdge.setVariable(getVariable());
        cloneEdge.setLoopCarriedNode(getLoopCarriedNode());
    }
    
    /**
     * Collects information about this edge for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        switch (sort) {
            case loopIndependentDefUseDependence: buf.append("LIDD: "); break;
            case loopCarriedDefUseDependence: buf.append( "LCDD: "); break;
            case defOrderDependence: buf.append("DO: "); break;
            case outputDependence: buf.append("OD: "); break;
            case antiDependence: buf.append("AD: "); break;
            case parameterIn: buf.append("PIN: "); break;
            case parameterOut: buf.append("POUT: "); break;
            case summary: buf.append("SUMM: "); break;
            default: break;
        }
        
        buf.append(src.getId());
        buf.append(" -> ");
        buf.append(dst.getId());
        
        buf.append(" [ ");
        buf.append(jvar.getName());
        buf.append(" ]");
        if (isLoopCarried()) {
            buf.append(" (LC = ");
            buf.append(getLoopCarriedNode().getId());
            buf.append(")");
        }
        
        return buf.toString();
    }
}

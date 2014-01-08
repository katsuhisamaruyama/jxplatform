/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.graph.GraphEdgeSort;
import org.apache.log4j.Logger;

/**
 * An edge of CFGs, which represents control flow between CFG nodes.
 * @author Katsuhisa Maruyama
 */
public class ControlFlow extends GraphEdge {
    
    static Logger logger = Logger.getLogger(ControlFlow.class.getName());
    
    /**
     * A CFG node that carries a loop-back edge if it exists.
     */
    private CFGNode loopback = null;
    
    /**
     * Creates a new, empty object.
     *
     */
    protected ControlFlow() {
        super();
    }
    
    /**
     * Creates a new edge between two CFG nodes.
     * @param src the source node of this edge
     * @param dst the destination node of this edge
     */
    public ControlFlow(CFGNode src, CFGNode dst) {
        super(src, dst);
    }
    
    /**
     * Sets as this edge is the true control flow.
     * @see org.jtool.eclipse.model.graph.GraphEdgeSort
     */
    public void setTrue() {
        sort = GraphEdgeSort.trueControlFlow;
    }
    
    /**
     * Tests if this edge represents the true control flow.
     * @return <code>true</code> if this edge is the true control flow, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphEdgeSort
     */
    public boolean isTrue() {
        return sort == GraphEdgeSort.trueControlFlow;
    }
    
    /**
     * Sets as this edge is the false control flow.
     * @see org.jtool.eclipse.model.graph.GraphEdgeSort
     */
    public void setFalse() {
        sort = GraphEdgeSort.falseControlFlow;
    }
    
    /**
     * Tests if this edge represents the false control flow.
     * @return <code>true</code> if this edge is the false control flow, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphEdgeSort
     */
    public boolean isFalse() {
        return sort == GraphEdgeSort.falseControlFlow;
    }
    
    /**
     * Sets as this edge is the fall-through control flow.
     * @see org.jtool.eclipse.model.graph.GraphEdgeSort
     */
    public void setFallThrough() {
        sort = GraphEdgeSort.fallThroughFlow;
    }
    
    /**
     * Tests if this edge represents the fall-through control flow.
     * @return <code>true</code> if this edge is the fall-through control flow, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphEdgeSort
     */
    public boolean isFallThrough() {
        return sort == GraphEdgeSort.fallThroughFlow;
    }
    
    /**
     * Returns the source node for this edge.
     * @return the source CFG node
     */
    public CFGNode getSrcNode() {
        return (CFGNode)src;
    }
    
    /**
     * Returns the destination node for this edge.
     * @return the destination CFG node
     */
    public CFGNode getDstNode() {
        return (CFGNode)dst;
    }
    
    /**
     * Sets as this edge is the parameter passing flow.
     * @see org.jtool.eclipse.model.graph.GraphEdgeSort
     */
    public void setParameter() {
        sort = GraphEdgeSort.parameterFlow;
    }
    
    /**
     * Tests if this edge represents the parameter passing flow.
     * @return <code>true</code> if this edge is the parameter passing flow, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphEdgeSort
     */
    public boolean isParameter() {
        return sort == GraphEdgeSort.parameterFlow;
    }
    
    /**
     * Sets the node for the loop-back flow.
     * @param node the CFG node that carries a loop-back edge if it exists
     */
    public void setLoopBack(CFGNode node) {
        loopback = node;
    }
    
    /**
     * Returns the node of loop-back flow.
     * @return node the CFG node that carries a loop-back edge, or <code>null</code> if this edge is not a loop-back flow.
     */
    public CFGNode getLoopBack() {
        return loopback;
    }
    
    /**
     * Tests if this edge represents a loop-back flow.
     * @return <code>true</code> if this edge is a loop-back flow, otherwise <code>false</code>
     */
    public boolean isLoopBack() {
        return loopback != null;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param edge the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(ControlFlow edge) {
        if (this == edge) {
            return true;
        }
        return src.equals(edge.getSrcNode()) && dst.equals(edge.getDstNode()) && sort == edge.getSort();
    }
    
    /**
     * Creates a clone of this edge.
     * @return the clone of this edge
     */
    public ControlFlow clone() {
        ControlFlow cloneEdge = new ControlFlow(getSrcNode(), getDstNode());
        clone(cloneEdge);
        return cloneEdge;
    }
    
    /**
     * Copies all the attributes of this edge into a given clone.
     * @param cloneEdge the clone of this edge
     */
    protected void clone(ControlFlow cloneEdge) {
        super.clone(cloneEdge);
        cloneEdge.setSort(getSort());
        cloneEdge.setLoopBack(getLoopBack());
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param edge the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>
     */
    public boolean equals(GraphEdge edge) {
        if (edge == null || !(edge instanceof ControlFlow)) {
            return false;
        }
        
        ControlFlow flow = (ControlFlow)edge;
        return this == flow || (getSrcNode().equals(flow.getSrcNode()) &&
                                getDstNode().equals(flow.getDstNode()) && 
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
     * Displays information about this edge.
     */
    public void print() {
        logger.info(toString());
    }
    
    /**
     * Collects information about this edge.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        switch (sort) {
            case trueControlFlow: buf.append("true: "); break;
            case falseControlFlow: buf.append("false: "); break;
            case fallThroughFlow: buf.append("fall through: "); break;
            case parameterFlow: buf.append("param: "); break;
            default: break;
        }
        buf.append(src.getId());
        buf.append(" -> ");
        buf.append(dst.getId());
        if (loopback != null) {
            buf.append("(L = " + getLoopBack().getId() + ")");
        }
        return buf.toString();
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

import org.apache.log4j.Logger;

/**
 * An edge object for graph.
 * @author Katsuhsa Maruyama
 */
public class GraphEdge extends GraphElement {
    
    static Logger logger = Logger.getLogger(GraphEdge.class.getName());
    
    /**
     * The source node of this edge.
     */
    protected GraphNode src;
    
    /**
     * The destination node of this edge.
     */
    protected GraphNode dst;
    
    /**
     * The sort of this edge which is selected from among <code>GraphEdgeSort</code>.
     */
    protected GraphEdgeSort sort;
    
    /**
     * Creates a new, empty edge.
     */
    protected GraphEdge() {
        super(GraphEdgeIdFactory.getId());
    }
    
    /**
     * Creates a new edge.
     * @param src A source node of this edge
     * @param dst A destination node of this edge
     */
    protected GraphEdge(GraphNode src, GraphNode dst) {
        this();
        this.src = src;
        this.dst = dst;
        src.addOutgoingEdge(this);
        dst.addIncomingEdge(this);
    }
    
    /**
     * Returns the identification number for this graph element.
     * @return the identification number
     */
    public long getId() {
        return id;
    }
    
    /**
     * Sets the sort of this edge.
     * @param sort the sort of this edge
     */
    public void setSort(GraphEdgeSort s) {
        sort = s;
    }
    
    /**
     * Returns the sort of this edge.
     * @return the sort of this edge
     */
    public GraphEdgeSort getSort() {
        return sort;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param obj the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>.
     */
    public boolean equals(Object obj) {
        if (obj instanceof GraphEdge) {
            GraphEdge edge = (GraphEdge)obj;
            return equals(edge);
        }
        return false;
    }
    
    /**
     * Tests if this edge equals to a given edge.
     * @param edge the edge to be checked
     * @return <code>true</code> if the edges are equal, otherwise <code>false</code>.
     */
    public boolean equals(GraphEdge edge) {
        if (this == edge) {
            return true;
        }
        return src.equals(edge.getSrcNode()) && dst.equals(edge.getDstNode());
    }
    
    /**
     * Returns a hash code value for this edge.
     * @return the hash code value for the edge
     */
    public int hashCode() {
        return (src.hashCode() + dst.hashCode()) / 2;
    }
    
    /**
     * Sets a source node for this edge.
     * @param node the source node
     */
    public void setSrcNode(GraphNode node) {
        src.removeOutgoingEdge(this);
        dst.removeIncomingEdge(this);
        src = node;
        src.addOutgoingEdge(this);
        dst.addIncomingEdge(this);
    }
    
    /**
     * Sets a destination node for this edge.
     * @param node the destination node
     */
    public void setDstNode(GraphNode node) {
        src.removeOutgoingEdge(this);
        dst.removeIncomingEdge(this);
        dst = node;
        src.addOutgoingEdge(this);
        dst.addIncomingEdge(this);
    }
    
    /**
     * Returns the source node for this edge.
     * @return the source node
     */
    public GraphNode getSrcNode() {
        return src;
    }
    
    /**
     * Returns the destination node for this edge.
     * @return the destination node
     */
    public GraphNode getDstNode() {
        return dst;
    }
    
    /**
     * Creates a clone of this edge.
     * @return the clone of this edge
     */
    public GraphEdge clone() {
        GraphEdge cloneEdge = new GraphEdge(getSrcNode(), getDstNode());
        clone(cloneEdge);
        return cloneEdge;
    }
    
    /**
     * Copies all the attributes of this edge into a given clone.
     * @param cloneNode the clone of this edge
     */
    protected void clone(GraphEdge cloneEdge) {
        cloneEdge.setSort(getSort());
    }
    
    /**
     * Displays information about this node.
     */
    public void print() {
        logger.info(toString());
    }
    
    /**
     * Collects information about this node for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Edge: " + src.getId() + " -> " + dst.getId());
        return buf.toString();
    }
}

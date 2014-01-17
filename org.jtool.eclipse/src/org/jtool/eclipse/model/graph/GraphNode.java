/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

import org.apache.log4j.Logger;

/**
 * A node object for graph.
 * @author Katsuhsa Maruyama
 */
public class GraphNode extends GraphElement {
    
    static Logger logger = Logger.getLogger(GraphNode.class.getName());
    
    /**
     * The sort of this node which is selected from among <code>GraphNodeSort</code>.
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    protected GraphNodeSort sort;
    
    /**
     * The collection of edges incoming to this node. 
     */
    private GraphElementSet<GraphEdge> incomingEdges = new GraphElementSet<GraphEdge>();
    
    /**
     * The collection of edges outgoing from this node. 
     */
    private GraphElementSet<GraphEdge> outgoingEdges = new GraphElementSet<GraphEdge>();
    
    /**
     * The collection of source nodes of this node. 
     */
    private GraphElementSet<GraphNode> srcNodes = new GraphElementSet<GraphNode>();
    
    /**
     * The collection of destination nodes of this node. 
     */
    private GraphElementSet<GraphNode> dstNodes = new GraphElementSet<GraphNode>();
    
    /**
     * Creates a new, empty node.
     */
    protected GraphNode() {
        super(GraphNodeIdPublisher.getId());
    }
    
    /**
     * Creates a new node.
     * @param s the sort of this node
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    protected GraphNode(GraphNodeSort s) {
        super();
        sort = s;
        if (s != GraphNodeSort.dummy) {
            super.setId(GraphNodeIdPublisher.getId());
        } else {
            super.setId(0);
        }
    }
    
    /**
     * Creates a new node.
     * @param s the sort of this node
     * @param id identification number
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    protected GraphNode(GraphNodeSort s, long id) {
        super(id);
        sort = s;
    }
    
    /**
     * Creates a new node.
     * @param bool <code>false</code> if the identification number number is not needed
     */
    protected GraphNode(boolean bool) {
        super();
        if (bool) {
            super.setId(GraphNodeIdPublisher.getId());
        } else {
            super.setId(-1);
        }
    }
    
    /**
     * Sets the sort of this node.
     * @param sort the sort of this node
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public void setSort(GraphNodeSort s) {
        sort = s;
    }
    
    /**
     * Returns the sort of this node.
     * @return the sort of this node
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public GraphNodeSort getSort() {
        return sort;
    }
    
    /**
     * Clears the contents of caches related to this node. 
     */
    public void clear() {
        incomingEdges.clear();
        outgoingEdges.clear();
        srcNodes.clear();
        dstNodes.clear();
    }
    
    /**
     * Tests if this node equals to a given node.
     * @param node the node to be checked
     * @return <code>true</code> if the nodes are equal, otherwise <code>false</code>
     */
    public boolean equals(GraphNode node) {
        return this == node || getId() == node.getId();
    }
    
    /**
     * Returns a hash code value for this node.
     * @return the hash code value for the node
     */
    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }
    
    /**
     * Adds an edge incoming to this node. 
     * @param edge the incoming edge to be added
     */
    public void addIncomingEdge(GraphEdge edge) {
        if (incomingEdges.add(edge)) {
            srcNodes.add(edge.getSrcNode());
        }
    }
    
    /**
     * Adds an edge outgoing from this node.
     * @param edge the outgoing edge to be added
     */
    public void addOutgoingEdge(GraphEdge edge) {
        if (outgoingEdges.add(edge)) {
            dstNodes.add(edge.getDstNode());
        }
    }
    
    /**
     * Adds edges incoming to this node. 
     * @param edges the collection of incoming edges to be added
     */
    public void addIncomingEdges(GraphElementSet<GraphEdge> edges) {
        for (GraphEdge edge : edges) {
            addIncomingEdge(edge);
        }
    }
    
    /**
     * Adds edges outgoing to this node. 
     * @param edges the collection of outgoing edges to be added
     */
    public void addOutgoingEdges(GraphElementSet<GraphEdge> edges) {
        for (GraphEdge edge : edges) {
            addOutgoingEdge(edge);
        }
    }
    
    /**
     * Removes an edge incoming to this node.
     * @param edge the incoming edge to be removed
     */
    public void removeIncomingEdge(GraphEdge edge) {
        incomingEdges.remove(edge);
        srcNodes.remove(edge.getSrcNode());
    }
    
    /**
     * Removes an edge outgoing from this node. 
     * @param edge the outgoing edge to be removed
     */
    public void removeOutgoingEdge(GraphEdge edge) {
        outgoingEdges.remove(edge);
        dstNodes.remove(edge.getDstNode());
    }
    
    /**
     * Clears the cache storing incoming edges.
     */
    public void clearIncomingEdges() {
        incomingEdges.clear();
    }
    
    /**
     * Clears the cache storing outgoing edges. 
     */
    public void clearOutgoingEdges() {
        outgoingEdges.clear();
    }
    
    /**
     * Sets edges incoming to this node.
     * @param edges the collection of incoming edges
     */
    public void setIncomingEdges(GraphElementSet<GraphEdge> edges) {
        incomingEdges = edges;
    }
    
    /**
     * Sets edges outgoing from this node.
     * @param edges the collection of outgoing edges
     */
    public void setOutgoingEdges(GraphElementSet<GraphEdge> edges) {
        outgoingEdges = edges;
    }
    
    /**
     * Returns edges incoming to this node.
     * @return the collection of the incoming edges
     */
    public GraphElementSet<GraphEdge> getIncomingEdges() {
        return incomingEdges;
    }
    
    /**
     * Returns edges outgoing from this node.
     * @return the collection of the outgoing edges
     */
    public GraphElementSet<GraphEdge> getOutgoingEdges() {
        return outgoingEdges;
    }
    
    /**
     * Returns source nodes for this node.
     * @return the collection of the source nodes
     */
    public GraphElementSet<GraphNode> getSrcNodes() {
        return srcNodes;
    }
    
    /**
     * Returns destination nodes for this node.
     * @return The collection of destination nodes
     */
    public GraphElementSet<GraphNode> getDstNodes() {
        return dstNodes;
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public GraphNode clone() {
        GraphNode cloneNode = new GraphNode(getSort());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(GraphNode cloneNode) {
        cloneNode.addIncomingEdges(getIncomingEdges());
        cloneNode.addOutgoingEdges(getOutgoingEdges());
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
        buf.append("Node: " + getId() + ": sort = " + getSort() + "\n");
        
        GraphElementSet<GraphEdge> outgoing = getOutgoingEdges();
        buf.append("  OutgoingEdge :");
        for (GraphEdge edge : outgoing) {
            buf.append("  " + edge.getDstNode().getId());
        }
        buf.append("\n");
        
        GraphElementSet<GraphEdge> incoming = getIncomingEdges();
        buf.append("  IncomingEdge :");
        for(GraphEdge edge : incoming) {
            buf.append("  " + edge.getSrcNode().getId());
        }
        buf.append("\n");
        
        buf.append("\n");
        return buf.toString();
    }
    
    /**
     * Collects information about the identification number of this node.
     * @return the string that represents the identification number
     */
    protected String getIdNum() {
        StringBuffer buf = new StringBuffer();
        long id = getId();
        if (id < 10) {
            buf.append("   ");
        } else if (id < 100) {
            buf.append("  ");
        } else if (id < 1000) {
            buf.append(" ");
        }
        return buf.toString();
    }
}

/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

/**
 * A graph object which is either a CFG or PDG.
 * @author Katsuhsa Maruyama
 */
public class Graph<N extends GraphNode, E extends GraphEdge> {
    
    /**
     * Nodes of this graph.
     */
    private GraphElementSet<N> nodes = new GraphElementSet<N>();
    
    /**
     * Edges of this graph.
     */
    private GraphElementSet<E> edges = new GraphElementSet<E>();
    
    /**
     * Creates a new, empty object.
     */
    public Graph() {
        super();
    }
    
    /**
     * Sets nodes of this graph.
     * @param set a collection of nodes
     */
    public void setNodes(GraphElementSet<N> set) {
        nodes = set;
    }
    
    /**
     * Returns all nodes of this graph.
     * @return the nodes of this graph
     */
    public GraphElementSet<N> getNodes() {
        return nodes;
    }
    
    /**
     * Sets edges of this graph.
     * @param set a collection of edges
     */
    public void setEdges(GraphElementSet<E> set) {
        edges = set;
    }
    
    /**
     * Returns all edges of this graph.
     * @return the edges of this graph
     */
    public GraphElementSet<E> getEdges() {
        return edges;
    }
    
    /**
     * Removes all nodes and all edges of this graph.
     */
    public void clear() {
        nodes.clear();
        edges.clear();
    }
    
    /**
     * Adds a given node to this graph.
     * @param node the node to be added
     */
    public void add(N node) {
        nodes.add(node);
    }
    
    /**
     * Adds a given edge to this graph.
     * @param edge the edge to be added
     */
    public void add(E edge) {
        edges.add(edge);
    }
    
    /**
     * Removes a given node from this graph.
     * @param node the node to be removed
     */
    public void remove(N node) {
        nodes.remove(node);
        for (E edge : new GraphElementSet<E>(getEdges())) {
            if (edge.getSrcNode().equals(node) || edge.getDstNode().equals(node)) {
                remove(edge);
            }
        } 
    }
    
    /**
     * Removes a given edge from this graph.
     * @param edge the edge to be removed
     */
    public void remove(E edge) {
        edges.remove(edge);
        edge.getSrcNode().removeOutgoingEdge(edge);
        edge.getDstNode().removeIncomingEdge(edge);
    }
    
    /**
     * Tests if this graph contains a given node.
     * @param node the node to be checked
     * @return <code>true</code> if this graph contains the node, otherwise <code>false</code>
     */
    public boolean contains(N node) {
        return nodes.contains(node);
    }
    
    /**
     * Tests if this graph contains a given edge.
     * @param edge the edge to be checked
     * @return <code>true</code> if this graph contains the edge, otherwise <code>false</code>
     */
    public boolean contains(E edge) {
        return edges.contains(edge);
    }
    
    /**
     * Tests if this graph equals to a given graph.
     * @param graph the graph to be checked.
     * @return <code>true</code> if the graphs are equal, otherwise <code>false</code>
     */
    public boolean equals(Graph<N, E> graph) {
        if (this == graph) {;
            return true;
        }
        return getNodes().equals(graph.getNodes()) && getEdges().equals(graph.getEdges());
    }
    
    /**
     * Returns a hash code value for this graph.
     * @return the hash code value for the graph
     */
    public int hashCode() {
        return (getNodes().size() + getEdges().size()) / 2;
    }
    
    /**
     * Displays information about this graph.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- Graph (from here) -----\n");
        buf.append(getNodeInfo());
        buf.append(getEdgeInfo());
        buf.append("----- Graph (to here) -----\n");
        
        return buf.toString();
    }
    
    /**
     * Collects information about nodes of this graph for printing.
     * @return the string for printing
     */
    protected String getNodeInfo() {
        StringBuffer buf = new StringBuffer();
        for (N node : getNodes()) {
            buf.append(node.toString());
            buf.append("\n");
        }
        
        return buf.toString();
    }
    
    /**
     * Collects information about edges of this graph for printing.
     * @return the string for printing 
     */
    protected String getEdgeInfo() {
        StringBuffer buf = new StringBuffer();
        for (E edge : getEdges()) {
            buf.append(edge.toString());
            buf.append("\n");
        }
        
        return buf.toString();
    }
}

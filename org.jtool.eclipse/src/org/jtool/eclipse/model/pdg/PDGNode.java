/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.graph.GraphNode;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.apache.log4j.Logger;

/**
 * A node of PDGs.
 * @author Katsuhisa Maruyama
 */
public class PDGNode extends GraphNode {
    
    static Logger logger = Logger.getLogger(PDGNode.class.getName());
    
    /**
     * A CFG node corresponding to this node.
     */
    protected CFGNode cfgnode;
    
    /**
     * Creates a new, empty object.
     */
    protected PDGNode() {
        super();
    }
    
    /**
     * Creates a new node.
     * @param node a CFG node corresponding to this node
     */
    protected PDGNode(CFGNode node) {
        super(node.getSort(), node.getId());
        cfgnode = node;
    }
    
    /**
     * Returns a CFG node corresponding to this node.
     * @return the CFG node corresponding to this node.
     */
    public CFGNode getCFGNode() {
        return cfgnode;
    }
    
    /**
     * Tests if this node indicates a statement node.
     * @return always <code>false</code>
     */
    public boolean isStatement() {
        return false;
    }
    
    /**
     * Tests if this node indicates a parameter node.
     * @return always <code>false</code>
     */
    public boolean isParameter() {
        return false;
    }
    
    /**
     * Tests if this node is classified into the loop (<code>while</code>, <code>do</code>, and <code>for</code>).
     * @return <code>true</code> if this node is classified into the loop, otherwise <code>false</code>
     */
    public boolean isLoop() {
        return cfgnode.isLoop();
    }
    
    /**
     * Tests if this node corresponds to the branch statement.
     * @return <code>true</code> if this node corresponds to the branch statement, otherwise <code>false</code>
     */
    public boolean isBranch() {
        return cfgnode.isBranch();
    }
    
    /**
     * Returns control dependence edges incoming to this node.
     * @return a collection of the control dependence incoming edges
     */
    public GraphElementSet<CD> getIncomingCDEdges() {
        GraphElementSet<CD> edges = new GraphElementSet<CD>();
        for (GraphEdge edge : getIncomingEdges()) {
            Dependence dependence = (Dependence)edge;
            if (dependence.isCD()) {
                edges.add((CD)dependence);
            }
        }
        return edges;
    }
    
    /**
     * Returns control dependence edges outgoing from this node.
     * @return a collection of the control dependence outgoing edges
     */
    public GraphElementSet<CD> getOutgoingCDEdges() {
        GraphElementSet<CD> edges = new GraphElementSet<CD>();
        for (GraphEdge edge : getOutgoingEdges()) {
            Dependence dependence = (Dependence)edge;
            if (dependence.isCD()) {
                edges.add((CD)dependence);
            }
        }
        return edges;
    }
    
    /**
     * Returns data dependence edges incoming to this node.
     * @return a collection of the data dependence incoming edges
     */
    public GraphElementSet<DD> getIncomingDDEdges() {
        GraphElementSet<DD> edges = new GraphElementSet<DD>();
        for (GraphEdge edge : getIncomingEdges()) {
            Dependence dependence = (Dependence)edge;
            if (dependence.isDD()) {
                edges.add((DD)dependence);
            }
        }
        return edges;
    }
    
    /**
     * Returns data dependence edges outgoing from this node.
     * @return a collection of the data dependence outgoing edges
     */
    public GraphElementSet<DD> getOutgoingDDEdges() {
        GraphElementSet<DD> edges = new GraphElementSet<DD>();
        for (GraphEdge edge : getOutgoingEdges()) {
            Dependence dependence = (Dependence)edge;
            if (dependence.isDD()) {
                edges.add((DD)dependence);
            }
        }
        return edges;
    }
    
    /**
     * Tests if this node is dominated by another node.
     * @return <code>true</code> if this node is dominated by another node, otherwise <code>false</code>
     */
    public boolean isDominated() {
        return !getIncomingCDEdges().isEmpty();
    }
    
    /**
     * Tests if this node is dominated by another node with respect to true dependence.
     * @return <code>true</code> if this node is dominated by another node, otherwise <code>false</code>
     */
    public boolean isTrueDominated() {
        for (CD cd : getIncomingCDEdges()) {
            if (cd.isTrue()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Tests if this node is dominated by another node with respect to false dependence.
     * @return <code>true</code> if this node is dominated by another node, otherwise <code>false</code>
     */
    public boolean isFalseDominated() {
        for (CD cd : getIncomingCDEdges()) {
            if (cd.isFalse()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the number of true and false control dependences incoming to this node. 
     * @return The number of the incoming true and false control dependences
     */
    public int getNumOfIncomingTrueFalseCDs() {
        int num = 0;
        for (CD cd : getIncomingCDEdges()) {
            if (cd.isTrue() || cd.isFalse()) {
                num++;
            }
        }
        return num;
    }
    
    /**
     * Tests if this node equals to a given node.
     * @param node the node to be checked
     * @return <code>true</code> if the nodes are equal, otherwise <code>false</code>
     */
    public boolean equals(PDGNode node) {
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
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public PDGNode clone() {
        PDGNode cloneNode = new PDGNode(getCFGNode());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(PDGNode cloneNode) {
        super.clone(cloneNode);
    }
    
    /**
     * Displays information about this node.
     */
    public void print() {
        cfgnode.print();
    }
    
    /**
     * Collects information about this node for printing.
     * @return the string for printing
     */
    public String toString() {
        return getCFGNode().toString();
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.graph.GraphElementSet;
import java.util.Iterator;

/**
 * Calculates constrained reachable nodes on a CFG.
 * @author Katsuhisa Maruyama
 */
public class ConstrainedReachableNodes implements Iterable<CFGNode> {
    
    /**
     * The collection of constrained reachable nodes between two nodes on a CFG.
     */
    private GraphElementSet<CFGNode> reachableNodes;
    
    /**
     * Prohibits creating this object without specifying two CFG nodes.
     */
    @SuppressWarnings("unused")
    private ConstrainedReachableNodes() {
    }
    
    /**
     * Calculates constrained reachable path between two nodes of a CFG and records nodes in the path.
     * @param cfg the CFG.
     * @param from the start node of the CFG for the reachable path to be calculated
     * @param to the end node of the CFG for the reachable path to be calculated
     */
    public ConstrainedReachableNodes(CFG cfg, CFGNode from, CFGNode to) {
        GraphElementSet<CFGNode> W;
        
        W = cfg.getBackwardReachableNodes(to, from);
        GraphElementSet<CFGNode> forwardCRP = new GraphElementSet<CFGNode>(W);
        W = cfg.getForwardReachableNodes(from, cfg.getEndNode());
        forwardCRP.intersection(W);
        
        W = cfg.getForwardReachableNodes(from, to);
        GraphElementSet<CFGNode> backwardCRP = new GraphElementSet<CFGNode>(W);
        W = cfg.getBackwardReachableNodes(to, cfg.getStartNode());
        backwardCRP.intersection(W);
        
        reachableNodes = new GraphElementSet<CFGNode>(forwardCRP.union(backwardCRP));
    }
    
    /**
     * Tests if the collection of the reachable nodes contains a given node.
     * @param node the CFG node to be checked
     * @return <code>true</code> if the reachable nodes contains the node, otherwise <code>false</code>
     */
    public boolean contains(CFGNode node) {
        return reachableNodes.contains(node);
    }
    
    /**
     * Tests if there is no reachable node.
     * @return <code>true</code> if no reachable node was found, otherwise <code>false</code>
     */
    public boolean isEmpty() {
        return reachableNodes.isEmpty();
    }
    
    /**
     * Returns an iterator of the collection of the reachable nodes in proper sequence.
     * @return the iterator of the collection of the reachable nodes
     */
    public Iterator<CFGNode> iterator() {
        return reachableNodes.iterator();
    }
    
    /**
     * Displays information about the collected nodes.
     */
    public void print() {
        for (CFGNode node : reachableNodes) {
            node.print();
        }
    }
}

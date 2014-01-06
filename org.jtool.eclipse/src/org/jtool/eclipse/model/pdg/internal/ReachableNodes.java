/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.graph.GraphElementSet;
import java.util.Iterator;

/**
 * Calculates reachable nodes on a CFG.
 * @author Katsuhisa Maruyama
 */
public class ReachableNodes implements Iterable<CFGNode> {
    
    /**
     * The collection of reachable nodes between two nodes on a CFG.
     */
    private GraphElementSet<CFGNode> reachableNodes;
    
    /**
     * A cache for collecting nodes passed forward on a CFG.
     */
    private GraphElementSet<CFGNode> ftrack;
    
    /**
     * A cache for collecting nodes passed backward on a CFG.
     */
    private GraphElementSet<CFGNode> btrack;
    
    /**
     * Prohibits creating this object without specifying two CFG nodes.
     */
    @SuppressWarnings("unused")
    private ReachableNodes() {
    }
    
    /**
     * Calculates reachable path between two nodes of a CFG and records nodes in the path.
     * @param cfg the CFG.
     * @param from the start node of the CFG for the reachable path to be calculated
     * @param to the end node of the CFG for the reachable path to be calculated
     */
    public ReachableNodes(CFG cfg, CFGNode from, CFGNode to) {
        ftrack = new GraphElementSet<CFGNode>(cfg.getForwardReachableNodes(from, to));
        btrack = new GraphElementSet<CFGNode>(cfg.getBackwardReachableNodes(to, from));
        reachableNodes = new GraphElementSet<CFGNode>(ftrack.intersection(btrack));
    }
    
    /**
     * Returns the collection of nodes passed forward on a CFG.
     * @return the collection of nodes on the forward reachable path
     */
    public GraphElementSet<CFGNode> getForwardReachableNodes() {
        return ftrack;
    }
    
    /**
     * Returns the collection of nodes passed backward on a CFG.
     * @return the collection of nodes on the backward reachable path
     */
    public GraphElementSet<CFGNode> getBackwardReachableNodes() {
        return btrack;
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

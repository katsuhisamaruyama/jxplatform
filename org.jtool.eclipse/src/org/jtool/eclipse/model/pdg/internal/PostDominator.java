/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.graph.GraphElementSet;
import java.util.Iterator;

/**
 * Calculates post-dominator nodes on a CFG.
 * @author Katsuhisa Maruyama
 */
public class PostDominator implements Iterable<CFGNode> {
    
    /**
     * The collection of post-dominator nodes on a CFG. 
     */
    private GraphElementSet<CFGNode> postDominator = new GraphElementSet<CFGNode>();
    
    /**
     * A cache for collecting nodes passed on a CFG.
     */
    private GraphElementSet<CFGNode> track = new GraphElementSet<CFGNode>();
    
    /**
     * Prohibits creating this object without specifying a CFG node.
     */
    @SuppressWarnings("unused")
    private PostDominator() {
    }
    
    /**
     * Calculates post-dominator nodes for a given node on a CFG.
     * @param cfg the CFG.
     * @param anchor the anchor node of the CFG
     */
    public PostDominator(CFG cfg, CFGNode anchor) {
        for (CFGNode node : cfg.getNodes()) {
            if (!anchor.equals(node)) {
                track.clear();
                track = cfg.getForwardReachableNodes(anchor, node);
                if (track.contains(node) && !track.contains(cfg.getEndNode())) {
                    add(node);
                }
            }
        }
    }
    
    /**
     * Adds a CFG node to the post-dominator nodes.
     * @param node the CFG node to be added
     * @return <code>true</code> if the post-dominator nodes changed, otherwise <code>false</code>
     */
    public boolean add(CFGNode node) {
        return postDominator.add(node);
    }
    
    /**
     * Removes a CFG node from the post-dominator nodes.
     * @param node the CFG node to be removed
     * @return <code>true</code> if the post-dominator nodes changed, otherwise <code>false</code>
     */
    public boolean remove(CFGNode node) {
        return postDominator.remove(node);
    }
    
    /**
     * Tests if the collection of the post-dominator nodes contains a given node.
     * @param node the CFG node to be checked
     * @return <code>true</code> if the post-dominator nodes contains the node, otherwise <code>false</code>
     */
    public boolean contains(CFGNode node) {
        return postDominator.contains(node);
    }
    
    /**
     * Tests if there is no post-dominator node.
     * @return <code>true</code> if no post-dominator node was found, otherwise <code>false</code>
     */
    public boolean isEmpty() {
        return postDominator.isEmpty();
    }
    
    /**
     * Returns an iterator of the collection of the post-dominator nodes in proper sequence.
     * @return The iterator of the collection of the post-dominator nodes
     */
    public Iterator<CFGNode> iterator() {
        return postDominator.iterator();
    }
    
    /**
     * Displays information about the collected nodes.
     */
    public void printNodes() {
        for (CFGNode node : postDominator) {
            node.print();
        }
    }
    
    /**
     * Collects information about this node list for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (CFGNode node : postDominator) {
            buf.append(node.getId());
            buf.append(", ");
        }
        
        if (buf.length() != 0) {
            return buf.substring(0, buf.length() - 2);
        } else {
            return "";
        }
    }
}

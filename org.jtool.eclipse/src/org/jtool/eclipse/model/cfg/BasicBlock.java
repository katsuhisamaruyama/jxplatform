/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphElementSet;
import org.apache.log4j.Logger;

/**
 * An object storing information about a basic block of a CFG.
 * @author Katsuhisa Maruyama
 */
public class BasicBlock {
    
    static Logger logger = Logger.getLogger(BasicBlock.class.getName());
    
    /**
     * The number prepared for generating the identification numbers of newly created basic blocks.
     */
    private static int blockNum = 0;
    
    /**
     * The identification number for this basic block.
     */
    private int id;
    
    /**
     * The leader for this basic block.
     */
    private CFGNode leader;
    
    /**
     * the collection of CFG nodes contained in this basic block.
     */
    protected GraphElementSet<CFGNode> nodes = new GraphElementSet<CFGNode>();
    
    /**
     * Creates a new, empty object.
     */
    protected BasicBlock() {
    }
    
    /**
     * Creates a new, empty object for storing the basic block having a given leader.
     * @param node CFG node that represents the leader
     */
    public BasicBlock(CFGNode node) {
        blockNum++;
        id = blockNum;
        leader = node;
    }
    
    /**
     * Returns the identification number for this basic block.
     * @return the identification number  for this basic block
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns the leader for this basic block.
     * @return the CFG node that represents the leader
     */
    public CFGNode getLeader() {
        return leader;
    }
    
    /**
     * Adds a CFG node to this basic block.
     * @param node the CFG node to be added
     */
    public void add(CFGNode node) {
        nodes.add(node);
        node.setBasicBlock(this);
    }
    
    /**
     * Returns the nodes contained in this basic block.
     * @return the collection of the contained CFG nodes
     */
    public GraphElementSet<CFGNode> getNodes() {
        return nodes;
    }
    
    /**
     * Tests if this basic block contains a given CFG node.
     * @param node the CFG node to be checked
     * @return <code>true</code> if this basic block contains the node, otherwise <code>false</code>
     */
    public boolean contains(CFGNode node) {
        return nodes.contains(node);
    }
    
    /**
     * Tests if this basic block has any CFG node.
     * @return <code>true</code> if this basic block has a CFG node, otherwise <code>false</code>
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }
    
    /**
     * Displays information about this node.
     */
    public void print() {
        logger.info(toString());
    }
    
    /**
     * Displays information about this graph.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- Basic Block (from here) -----\n");
        buf.append(printNodes());
        buf.append("----- Basic Block (to here) -----\n");
        return buf.toString();
    }
    
    /**
     * Collects information about nodes of this graph for printing.
     * @return the string for printing
     */
    private String printNodes() {
        StringBuffer buf = new StringBuffer();
        for (CFGNode node : getNodes()) {
            buf.append(node.toString());
            buf.append("\n");
        }
        return buf.toString();
    }
}

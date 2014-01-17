/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

/**
 * Calculates and stores basic blocks of a CFG.
 * @author Katsuhisa Maruyama
 */
public class BasicBlockFactory {
    
    /**
     * Calculates and stores basic blocks of a CFG.
     * @param cfg the target CFG
     */
    public static void create(CFG cfg) {
        CFGNode start = cfg.getStartNode();
        CFGNode first = start.getSuccessors().getFirst();
        
        for (CFGNode node : cfg.getNodes()) {
            if (node.equals(first) || node.isJoin() || (node.isNextToBranch() && !node.equals(start))) {
                BasicBlock block = new BasicBlock(node);
                cfg.add(block);
                block.add(node);
            }
        }
        
        for (BasicBlock block : cfg.getBasicBlocks()) {
            collectNodesInBlock(block, cfg);
        }
    }
    
    /**
     * Collects nodes contained in a given basic block on a CFG.
     * @param block the basic block
     * @param cfg the CFG that contains the basic block
     */
    private static void collectNodesInBlock(BasicBlock block, CFG cfg) {
        CFGNode node = getTrueSucc(block.getLeader());
        while (node != null && !node.isLeader() && !node.equals(cfg.getEndNode())) {
            block.add(node);   
            node = getTrueSucc(node);
        }
    }
    
    /**
     * Returns the node next to a given node. 
     * @param node the CFG node
     * @return the next CFG node
     */
    private static CFGNode getTrueSucc(CFGNode node) {
        for (ControlFlow edge : node.getOutgoingFlows()) {
            if (edge.isTrue()) {
                return edge.getDstNode();
            }
        }
        return null;
    }
}

/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.Graph;
import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaMethod;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * An object storing information about a control flow graph (CFG).
 * @author Katsuhisa Maruyama
 */
public class CFG extends Graph<CFGNode, ControlFlow> {
    
    static Logger logger = Logger.getLogger(CFG.class.getName());
    
    /**
     * The start node of this CFG.
     */
    private CFGEntry start;
    
    /**
     * The end node of this CFG.
     */
    protected CFGNode end;
    
    /**
     * The collection of basic blocks of this CFG.
     */
    private List<BasicBlock> blocks = new ArrayList<BasicBlock>();
    
    /**
     * Creates a new, empty object for storing the CFG of a method.
     */
    public CFG() {
        super();
    }
    
    /**
     * Sets the start node of this CFG.
     * @param node the start node of this CFG
     */
    public void setStartNode(CFGEntry node) {
        start = node;
        start.setCFG(this);
    }
    
    /**
     * Returns the start node of this CFG.
     * @return the start node of this CFG
     */
    public CFGEntry getStartNode() {
        return start;
    }
    
    /**
     * Sets the end node of this CFG.
     * @param node the end node of this CFG
     */
    public void setEndNode(CFGNode node) {
        end = node;
    }
    
    /**
     * Returns the end node of this CFG.
     * @return the end node of this CFG
     */
    public CFGNode getEndNode() {
        return end;
    }
    
    /**
     * Returns the field corresponding to this CFG.
     * @return the corresponding Java field, or <code>null</code> if this node does not represent a field. 
     */
    public JavaField getJavaField() {
        if (start.isFieldEntry()) {
            return (JavaField)start.getJavaElement();
        }
        return null;
    }
    
    /**
     * Returns the method corresponding to this CFG.
     * @return the corresponding Java method, or <code>null</code> if this node does not represent a method. 
     */
    public JavaMethod getJavaMethod() {
        if (start.isMethodEntry() || start.isInitializerEntry()) {
            return (JavaMethod)start.getJavaElement();
        }
        return null;
    }
            
    /**
     * Returns the identification number of this CFG.
     * @return the identification number that equals to that of the start node of this CFG.
     */
    public long getId() {
        return start.getId();
    }
    
    /**
     * Returns the name of this CFG.
     * @return the name of the method corresponding to this CFG
     */
    public String getName() {
        return start.getName();
    }
    
    /**
     * Returns all the nodes contained in this CFG.
     * @return the collection of the contained nodes.
     */
    public GraphElementSet<CFGNode> getNodes() {
        return super.getNodes();
    }
    
    /**
     * Appends a new CFG to this existing CFG. The nodes or edges of the both CFGs are merged respectively.
     * @param cfg the CFG to be appended
     */
    public void append(CFG cfg) {
        for (CFGNode node : cfg.getNodes()) {
            add(node);
        }
        for (ControlFlow edge : cfg.getEdges()) {
            add(edge);
        }
    }
    
    /**
     * Adds a node to this CFG.
     * @param node the node to be added 
     */
    public void add(CFGNode node) {
        super.add(node);
    }
    
    /**
     * Adds a control flow to this CFG.
     * @param edge the control flow to be added
     */
    public void add(ControlFlow edge) {
        super.add(edge);
    }
    
    /**
     * Creates and stores basic blocks of this CFG.
     */
    public void createBasicBlock() {
        BasicBlockFactory.create(this);
    }
    
    /**
     * Adds a basic block
     * @param block the basic block to be added
     */
    public void add(BasicBlock block) {
        blocks.add(block);
    }
    
    /**
     * Returns the basic blocks contained in this CFG.
     * @return the collection of the contained basic block
     */
    public List<BasicBlock> getBasicBlocks() {
        return blocks;
    }
    
    /**
     * Tests if a given node represents branch in this CFG.
     * @param node the node to be checked
     * @return <code>true</code> if the node represents branch, otherwise <code>false</code>
     */
    public boolean isBranch(CFGNode node) {
        return node.isBranch();
    }
    
    /**
     * Tests if a given node represents loop in this CFG.
     * @param node the node to be checked
     * @return <code>true</code> if the node represents loop, otherwise <code>false</code>
     */
    public boolean isLoop(CFGNode node) {
        return node.isLoop();
    }
    
    /**
     * Tests if a given node represents join in this CFG.
     * @param node the node to be checked
     * @return <code>true</code> if the node represents join, otherwise <code>false</code>
     */
    public boolean isJoinNode(CFGNode node) {
        return node.isJoin();
    }
    
    /**
     * Returns the edge specified by given source and destination nodes.
     * @param src the source CFG node for the edge to be retrieved
     * @param dst the destination CFG node for the edge to be retrieved
     * @return the found edge of this CFG, or <code>null</code> if none.
     */
    public ControlFlow getFlow(CFGNode src, CFGNode dst) {
        if (src != null && dst != null) {
            for (ControlFlow edge : getEdges()) {
                if (src.equals(edge.getSrcNode()) && dst.equals(edge.getDstNode())) {
                    return edge;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns the CFG node with a given identification number.
     * @param id the identification number of the node to be retrieved
     * @return the found node of this CFG, or <code>null</code> if none
     */
    public CFGNode getNode(long id) {
        for (CFGNode node : getNodes()) {
            if (id == node.getId()) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * Returns the true control flow outgoing from a given node.
     * @param node the CFG node having the outgoing control flow
     * @return the true control flow of this CFG, or <code>null</code> if none
     */
    public ControlFlow getTrueFlowFrom(CFGNode node) {
        for (ControlFlow edge : getEdges()) {
            if (edge.getSrcNode().equals(node) && edge.isTrue()) {
                return edge;
            }
        }
        return null;
    }
    
    /**
     * Returns the false control flow outgoing from a given node.
     * @param node the CFG node having the outgoing control flow
     * @return the false control flow of this CFG, or <code>null</code> if none
     */
    public ControlFlow getFalseFlowFrom(CFGNode node) {
        for (ControlFlow edge : getEdges()) {
            if (edge.getSrcNode().equals(node) && edge.isFalse()) {
                return edge;
            }
        }
        return null;
    }
    
    /**
     * Returns the successor node of a given node with respect to the true control flow.
     * @param node the CFG node having the control flow
     * @return the found successor, or <code>null</code> if none
     */
    public CFGNode getTrueSuccessor(CFGNode node) {
        ControlFlow flow = getTrueFlowFrom(node);
        if (flow != null) {
            return flow.getDstNode();
        }
        return null;
    }
    
    /**
     * Returns the successor node of a given node with respect to the false control flow.
     * @param node the CFG node having the control flow
     * @return the found successor, or <code>null</code> if none
     */
    public CFGNode getFalseSuccessor(CFGNode node) {
        ControlFlow flow = getFalseFlowFrom(node);
        if (flow != null) {
            return flow.getDstNode();
        }
        return null;
    }
    
    /**
     * Tests if this method contains a try statement.
     * @return <code>true</code> if this method contains a try statement, otherwise <code>false</code>
     */
    public boolean hasTryStatement(){
        for (CFGNode node : getNodes()) {
            if (node.isTry()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calculates and returns the nodes for method calls.
     * @return the nodes for the method calls
     */     
    public GraphElementSet<CFGNode> getCallNodes() {
        GraphElementSet<CFGNode> set = new GraphElementSet<CFGNode>();
        for (CFGNode node : getNodes()) {
            if (node.isMethodCall()) {
                set.add(node);
            }
        }
        return set;
    }
    
    /**
     * Calculates and returns the nodes passed forward between two nodes CFG.
     * @param from the start node of the CFG for the reachable path to be calculated
     * @param to the end node of the CFG for the reachable path to be calculated
     * @return the nodes on the forward reachable path
     */
    public GraphElementSet<CFGNode> getForwardReachableNodes(CFGNode from, CFGNode to) {
        GraphElementSet<CFGNode> track = new GraphElementSet<CFGNode>();
        walkForward(from, to, true, track);
        return track;
    }
    
    /**
     * Calculates and returns the nodes passed forward between two nodes CFG without traversing loopback edges.
     * @param from the start node of the CFG for the reachable path to be calculated
     * @param to the end node of the CFG for the reachable path to be calculated
     * @return the nodes on the forward reachable path
     */
    public GraphElementSet<CFGNode> getForwardReachableNodesWithoutLoopback(CFGNode from, CFGNode to) {
        GraphElementSet<CFGNode> track = new GraphElementSet<CFGNode>();
        walkForward(from, to, false, track);
        return track;
    }
    
    /**
     * Calculates and returns the nodes passed backward between two nodes CFG.
     * @param from the start node of the CFG for the reachable path to be calculated
     * @param to the end node of the CFG for the reachable path to be calculated
     * @return the nodes on the backward reachable path
     */
    public GraphElementSet<CFGNode> getBackwardReachableNodes(CFGNode from, CFGNode to) {
        GraphElementSet<CFGNode> track = new GraphElementSet<CFGNode>();
        walkBackward(from, to, true, track);
        return track;
    }
    
    /**
     * Calculates and returns the nodes passed backward between two nodes CFG without traversing loopback edges.
     * @param from the start node of the CFG for the reachable path to be calculated
     * @param to the end node of the CFG for the reachable path to be calculated
     * @return the nodes on the backward reachable path
     */
    public GraphElementSet<CFGNode> getBackwardReachableNodesWithoutLoopback(CFGNode from, CFGNode to) {
        GraphElementSet<CFGNode> track = new GraphElementSet<CFGNode>();
        walkBackward(from, to, false, track);
        return track;
    }
    
    /**
     * Walks forward and records the passed nodes.
     * @param from the start node
     * @param to the end node
     * @param loopbackOk <code>true</code> if loop-back edges can be passed, otherwise <code>false</code>
     * track the collection of nodes traversed on this CFG
     */
    private void walkForward(CFGNode from, CFGNode to, boolean loopbackOk, GraphElementSet<CFGNode> track) {
        if (from == null) {
            return;
        }
        if (from.equals(to) && !track.isEmpty()) {
            track.add(from);
            return;
        }
        track.add(from);
        
        for (ControlFlow flow : from.getOutgoingFlows()) {
            if (loopbackOk || !flow.isLoopBack()) {
                CFGNode succ = flow.getDstNode();
                if (!track.contains(succ)) {
                    walkForward(succ, to, loopbackOk, track);
                }
            }
        }
    }
    
    /**
     * Walks backward and records the passed nodes.
     * @param from the start node
     * @param to the end node
     * @param loopbackOk <code>true</code> if loop-back edges can be passed, otherwise <code>false</code>
     * @param track the collection of nodes traversed on this CFG
     */
    private void walkBackward(CFGNode to, CFGNode from, boolean loopbackOk, GraphElementSet<CFGNode> track) {
        if (to == null) {
            return;
        }
        if (to.equals(from) && !track.isEmpty()) {
            track.add(to);
            return;
        }
        track.add(to);
        
        for (ControlFlow flow : to.getIncomingFlows()) {
            if (loopbackOk || !flow.isLoopBack()) {
                CFGNode pred = flow.getSrcNode();
                if (!track.contains(pred)) {
                    walkBackward(pred, from, loopbackOk, track);
                }
            }
        }
    }
    
    /**
     * Creates a clone of this CFG.
     * @return the clone of this CFG
     */
    public CFG clone() {
        CFG cloneCFG = new CFG();
        HashMap<Long, Long> idmap = new HashMap<Long, Long>();
        
        for (CFGNode node : getNodes()) {
            CFGNode cloneNode = node.clone();
            cloneCFG.add(cloneNode);
            idmap.put(node.getId(), cloneNode.getId());
            
            if (node.isEntry()) {
                cloneCFG.setStartNode((CFGEntry)cloneNode);
            } else if (node.isExit()) {
                cloneCFG.setEndNode((CFGExit)cloneNode);
            }
        }
        
        for (ControlFlow edge : getEdges()) {
            CFGNode src = edge.getSrcNode();
            CFGNode dst = edge.getDstNode();
            long srcId = idmap.get(src.getId());
            long dstId = idmap.get(dst.getId());
            CFGNode cloneSrc = cloneCFG.getNode(srcId);
            CFGNode cloneDst = cloneCFG.getNode(dstId);
            
            ControlFlow cloneEdge = edge.clone();
            cloneEdge.setSrcNode(cloneSrc);
            cloneEdge.setDstNode(cloneDst);
            cloneCFG.add(edge);
        }
        
        cloneCFG.createBasicBlock();
        
        return cloneCFG;
    }
    
    /**
     * Displays information about this graph.
     */
    public void print() {
        logger.info(toString());
    }
    
    /**
     * Collects information about this graph.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- CFG (from here) -----\n");
        buf.append("Name = " + getName());
        buf.append("\n");
        buf.append(getNodeInfo()); 
        buf.append(getEdgeInfo());
        buf.append("----- CFG (to here) -----\n");
        
        return buf.toString();
    }
    
    /**
     * Collects information about edges of this CFG for printing.
     * @return the string for printing 
     */
    protected String getEdgeInfo() {
        StringBuffer buf = new StringBuffer();
        int index = 1;
        for (ControlFlow edge : getEdges()) {
            buf.append(String.valueOf(index));
            buf.append(": ");
            
            buf.append(edge.toString());
            buf.append("\n");
            index++;
        }
        
        return buf.toString();
    }
}

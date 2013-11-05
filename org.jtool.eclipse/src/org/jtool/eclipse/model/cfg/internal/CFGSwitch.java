/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg.internal;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.cfg.ControlFlow;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.java.JavaStatement;

/**
 * A node for a <code>switch</code> statement of CFGs.
 * @author Katsuhisa Maruyama
 */
public class CFGSwitch extends CFGStatement {
    
    /**
     * A node corresponding to the start of the default block.
     */
    private CFGNode defaultStartNode = null;
    
    /**
     * A node corresponding to the end of the default block.
     */
    private CFGNode defaultEndNode = null;
    
    /**
     * Creates a new, empty object.
     */
    CFGSwitch() {
        super();
    }
    
    /**
     * Creates a new node corresponding to a given Java statement.
     * @param jelem the Java element
     * @param sort the sort of this node
     */
    CFGSwitch(JavaStatement jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Returns the start node of the default block in this switch statement.
     * @param the start node of the default block
     */
    void setDefaultStartNode(CFGNode node) {
        defaultStartNode = node;
    }
    
    /**
     * Returns the start node of the default block in this switch statement.
     * @return the start node of the default block
     */
    CFGNode getDefaultStartNode() {
        return defaultStartNode;
    }
    
    /**
     * Returns the end node of the default block in this switch statement.
     * @param the end node of the default block
     */
    void setDefaultEndNode(CFGNode node) {
        defaultEndNode = node;
    }
    
    /**
     * Returns the end node of the default block in this switch statement.
     * @return the end node of the default block
     */
    CFGNode getDefaultEndNode() {
        return defaultEndNode;
    }
    
    /**
     * Returns the predecessor node of the default block in this switch statement
     * @return the predecessor node of the default block
     */
    CFGNode getPredecessorOfDefault() {
        for (GraphEdge edge : defaultStartNode.getIncomingEdges()) {
            ControlFlow flow = (ControlFlow)edge;
            
            if (flow.isFalse()) {
                return (CFGNode)flow.getSrcNode();        
            }
        }
        return null;
    }
    
    /**
     * Returns the successor node of the default block in this switch statement
     * @return the successor node of the default block
     */
    CFGNode getSuccessorOfDefault() {
        for (GraphEdge edge : defaultStartNode.getOutgoingEdges()) {
            ControlFlow flow = (ControlFlow)edge;
            
            if (flow.isFalse()) {
                return (CFGNode)flow.getDstNode();
            }
        }
        return null;
    }
    
    boolean hasDefault() {
        return defaultStartNode != null;
    }
}

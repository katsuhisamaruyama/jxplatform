/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg.internal;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.java.JavaStatement;
import java.util.List;
import java.util.ArrayList;

/**
 * A node for a <code>try</code> statement of CFGs.
 * @author Katsuhisa Maruyama
 */
public class CFGTry extends CFGStatement {
    
    /**
     * The collection of nodes for the catch clauses corresponding to this try statement. 
     */
    private List<CFGCatch> catchClauses = new ArrayList<CFGCatch>();
    
    /**
     * A node for the finally clause corresponding to this try statement.
     */
    private CFGStatement finallyBlock;
    
    /**
     * A node for the end of the finally clause corresponding to this try statement.
     */
    private CFGNode finallyEnd;
    
    /**
     * A node for the end of the block starting from this try statement.
     */
    private CFGNode tryEnd;
    
    /**
     * Creates a new, empty object.
     */
    CFGTry() {
        super();
    }
    
    /**
     * Creates a new node corresponding to a given Java statement.
     * @param jelem the Java element
     * @param sort the sort of this node
     */
    CFGTry(JavaStatement jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Adds a catch clause corresponding to this try statement.
     * @param node the node for the catch block
     */
    void addCatchClause(CFGCatch node) {
        catchClauses.add(node);
    }
    
    /**
     * Sets the catch clauses corresponding to this try statement.
     * @param clauses the collection of the catch clauses
     */
    void setCatchClauses(List<CFGCatch> clauses) {
        for (CFGCatch node : clauses) {
            addCatchClause(node);
        }
    }
    
    /**
     * Returns the catch clauses corresponding to this try statement.
     * @return the collection of the catch clauses
     */
    List<CFGCatch> getCatchClauses() {
        return catchClauses;
    }
    
    /**
     * Sets a finally clause corresponding to this try statement.
     * @param node
     */
    void setFinallyBlock(CFGStatement node) {
        finallyBlock = node;
    }
    
    /**
     * Returns the finally clause corresponding to this try statement.
     * @return the finally clause corresponding to this try statement
     */
    CFGStatement getFinallyBlock() {
        return finallyBlock;
    }
    
    /**
     * Sets the node for the end of the finally clause corresponding to this try statement.
     * @param node the end node for the finally clause corresponding to this try statement
     */
    void setFinallyBlockEnd(CFGNode node) {
        finallyEnd = node;
    }
    
    /**
     * Returns the node for the end of the finally clause corresponding to this try statement.
     * @return the end node for the finally clause corresponding to this try statement
     */
    CFGNode getFinallyBlockEnd() {
        return finallyEnd;
    }
    
    /**
     * Sets the node for the end of the block starting from this try statement.
     * @param node the end node for the try block
     */
    void setTryEnd(CFGNode node) {
        tryEnd = node;
    }
    
    /**
     * Returns the node for the end of the block starting from this try statement.
     * @param the end node for the try block
     */
    CFGNode getTryEnd() {
        return tryEnd;
    }
}

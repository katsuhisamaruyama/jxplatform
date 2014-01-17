/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import java.util.List;

/**
 * A statement node of a PDG.
 * @author Katsuhisa Maruyama
 */
public class PDGStatement extends PDGNode {
    
    /**
     * Creates a new, empty object.
     */
    protected PDGStatement() {
        super();
    }
    
    /**
     * Creates a new node.
     * @param node the statement node of a CFG corresponding to the statement node of a PDG.
     */
    public PDGStatement(CFGStatement node) {
        super(node);
    }
    
    /**
     * Tests if this node indicates a statement.
     * @return always <code>true</code>
     */
    public boolean isStatement() {
        return true;
    }
    
    /**
     * Returns the CFG node for this statement.
     * @return the CFG node for this statement
     */
    public CFGStatement getCFGStatement() {
        return (CFGStatement)getCFGNode();
    }
    
    /**
     * Returns all the variables defined in this statement.
     * @return the collection of the defined variables in this statement
     */
    public List<JavaVariableAccess> getDefVariables() {
        return getCFGStatement().getDefVariables();
    }
    
    /**
     * Returns all the variables used in this statement.
     * @return the collection of the used variables in this statement
     */
    public List<JavaVariableAccess> getUseVariables() {
        return getCFGStatement().getUseVariables();
    }
    
    /**
     * Tests if this statement defines a given variable.
     * @param jv the variable to be checked
     * @return <code>true</code> if this statement defines the variable, otherwise <code>false</code>
     */
    public boolean definesVariable(JavaVariableAccess jv) {
        return getCFGStatement().getDefVariables().contains(jv);
    }
    
    /**
     * Tests if this statement uses a given variable.
     * @param jv the variable to be checked
     * @return <code>true</code> if this statement uses the variable, otherwise <code>false</code>
     */
    public boolean usesVariable(JavaVariableAccess jv) {
        return getCFGStatement().getUseVariables().contains(jv);
    }
    
    /**
     * Collects information about this statement.
     * @return the string for printing
     */
    public String toString() {
        return getCFGStatement().toString();
    }
}

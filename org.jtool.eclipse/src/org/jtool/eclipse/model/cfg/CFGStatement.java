/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import java.util.List;

import org.jtool.eclipse.model.cfg.internal.CFGDefUseNode;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaExpression;
import org.jtool.eclipse.model.java.JavaStatement;
import org.jtool.eclipse.model.java.JavaVariableAccess;

/**
 * A statement node of a CFG.
 * @author Katsuhisa Maruyama
 */
public class CFGStatement extends CFGDefUseNode {
    
    /**
     * Creates a new, empty object.
     */
    protected CFGStatement() {
        super();
    }
    
    /**
     * Creates a new node when the corresponding Java element does not exist.
     * @param sort the sort of this node
     */
    protected CFGStatement(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java expression.
     * @param jelem the Java element
     * @param sort the sort of this node
     */
    public CFGStatement(JavaExpression jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java statement.
     * @param jelem the Java element
     * @param sort the sort of this node
     */
    public CFGStatement(JavaStatement jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Returns the variable declared at this statement.
     * @return the declared variable
     */
    public JavaVariableAccess getDeclaration() {
        return getDefVariables().get(0);
    }
    
    /**
     * Returns the information of this statement.
     * @return the information of this statement
     */
    public JavaStatement getJavaStatement() {
        return (JavaStatement)getJavaElement();
    }
    
    /**
     * Returns the defined variables of this node.
     * @return the list of the defined variables
     */
    public List<JavaVariableAccess> getDefVariables() {
        return super.getDefVariables();
    }
    
    /**
     * Returns the used variables of this node.
     * @return the list of the used variables
     */
    public List<JavaVariableAccess> getUseVariables() {
        return super.getUseVariables();
    }
    
    /**
     * Tests if the defined variables contains a variable
     * @param jv the variable to be checked
     * @return <code>true</code> if the variable is contained, otherwise <code>false</code>
     */
    public boolean defineVariable(JavaVariableAccess jv) {
        return super.defineVariable(jv);
    }
    
    /**
     * Tests if the used variables contains a given variable
     * @param jv the variable to be checked
     * @return <code>true</code> if the variable is contained, otherwise <code>false</code>
     */
    public boolean useVariable(JavaVariableAccess jv) {
        return super.useVariable(jv);
    }
    
    /**
     * Tests if the defined variables contains any variable
     * @return <code>true</code> if the list of the defined variables is not empty, otherwise <code>false</code>
     */
    public boolean hasDefVariable() {
        return super.hasDefVariable();
    }
    
    /**
     * Tests if the used variables contains any variable
     * @return <code>true</code> if the list of the used variables is not empty, otherwise <code>false</code>
     */
    public boolean hasUseVariable() {
        return super.hasUseVariable();
    }
    
    /**
     * Returns the defined variable with a given name.
     * @param name the name of the variable to be retrieved
     * @return The found variable, or <code>null</code> if none
     */
    public JavaVariableAccess getDefVariable(String name) {
        return super.getDefVariable(name);
    }
    
    /**
     * Returns the used variable with a given name.
     * @param name the name of a variable to be retrieved
     * @return the found variable, or <code>null</code> if none
     */
    public JavaVariableAccess getUseVariable(String name) {
        return super.getUseVariable(name);
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGStatement clone() {
        CFGStatement cloneNode = new CFGStatement(getJavaStatement(), getSort());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGStatement cloneNode) {
        super.clone(cloneNode);
    }
    
    /**
     * Collects information about this statement.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + getId() + "] ");
        switch (sort) {
            case assignment: buf.append("assignment"); break;
            case methodCall: buf.append("method call"); break;
            case instanceCreation: buf.append("instance creatation"); break;
            case fieldDeclaration: buf.append("field declaration"); break;
            case localDeclaration: buf.append("local declaration"); break;
            
            case assertSt: buf.append("assert"); break;
            case breakSt: buf.append("break"); break;    
            case constructorCall: buf.append("constructor call"); break;
            case continueSt: buf.append("continue"); break;
            case doSt: buf.append("do"); break;
            case forSt: buf.append("for"); break;
            case ifSt: buf.append("if"); break;
            case returnSt: buf.append("return"); break;
            case switchCaseSt: buf.append("switch case"); break;
            case switchDefaultSt: buf.append("switch default"); break;
            case whileSt: buf.append("while"); break;
            
            case labelSt: buf.append("label"); break;
            case switchSt: buf.append("switch"); break;
            case synchronizedSt: buf.append("synchronized"); break;
            case throwSt: buf.append("throw"); break;
            case trySt: buf.append("try"); break;
            case catchSt: buf.append("catch"); break;
            case finallySt: buf.append("finally"); break;
            default: break;
        }
        buf.append(": ");
        buf.append("{ " + toStringDefVariables() + " }");
        buf.append(" = ");
        buf.append("{ " + toStringUseVariables() + " }");
        return buf.toString();
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGExit;
import org.jtool.eclipse.model.cfg.CFGFieldEntry;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.cfg.ControlFlow;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Visits a field within a Java program and creates its CFG. 
 * @author Katsuhisa Maruyama
 */
public class CFGFieldFactory {
    
    /**
     * Creates a CFG by visiting a field element.
     * @param jf information on the field
     * @return the created CFG
     */
    public static CFG getCFG(JavaField jf) {
        CFG cfg = new CFG();
        
        CFGFieldEntry entry = new CFGFieldEntry(jf, GraphNodeSort.fieldEntry);
        cfg.setStartNode(entry);
        cfg.add(entry);
        
        CFGStatement fieldNode = new CFGStatement(jf, GraphNodeSort.fieldDeclaration);
        JavaVariableAccess jv = jf.convertJavavariableAccess();
        fieldNode.addDefVariable(jv);
        fieldNode.addUseVariable(jv);
        cfg.add(fieldNode);
        
        ControlFlow edge = new ControlFlow(entry, fieldNode);
        edge.setTrue();
        cfg.add(edge);
        
        CFGNode curNode = fieldNode;
        VariableDeclarationFragment frag = (VariableDeclarationFragment)jf.getASTNode();
        Expression initializer = frag.getInitializer();
        if (initializer != null) {
            ExpressionVisitor visitor = new ExpressionVisitor(cfg, fieldNode);
            initializer.accept(visitor);
            
            curNode = visitor.getExitNode();
        }
        
        CFGExit exit = new CFGExit(GraphNodeSort.fieldExit);
        cfg.setEndNode(exit);
        cfg.add(exit);
        
        edge = new ControlFlow(curNode, exit);
        edge.setTrue();
        cfg.add(edge);
        
        return cfg;
    }
}

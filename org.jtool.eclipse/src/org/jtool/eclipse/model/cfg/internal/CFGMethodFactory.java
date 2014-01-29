/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg.internal;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGExit;
import org.jtool.eclipse.model.cfg.CFGMethodEntry;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGParameter;
import org.jtool.eclipse.model.cfg.ControlFlow;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.java.JavaExpression;
import org.jtool.eclipse.model.java.internal.JavaSpecialVariable;
import org.jtool.eclipse.model.java.JavaLocal;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaVariableAccess;

/**
 * Visits a method within a Java program and creates its CFG. 
 * @author Katsuhisa Maruyama
 */
public class CFGMethodFactory {
    
    /**
     * Creates a CFG by visiting a method element.
     * @param jm information on the method
     */
    public static CFG getCFG(JavaMethod jm) {
        CFG cfg = new CFG();
        ExpressionVisitor.paramNumber = 1;
        
        CFGMethodEntry entry = new CFGMethodEntry(jm, GraphNodeSort.methodEntry);
        if (jm.isConstructor()) {
            entry.setSort(GraphNodeSort.constructorEntry);
        } else if (jm.isInitializer()) {
            entry.setSort(GraphNodeSort.initializerEntry);
        }
        cfg.setStartNode(entry);
        cfg.add(entry);
        
        CFGNode tmpExit = new CFGNode();
        cfg.setEndNode(tmpExit);
        
        CFGNode formalInNode = createFormalIn(jm, cfg, entry, entry);
        CFGNode nextNode = new CFGNode();
        
        ControlFlow entryEdge = new ControlFlow(formalInNode, nextNode);
        entryEdge.setTrue();
        cfg.add(entryEdge);
        
        if (jm.isMethod() && !jm.isInitializer()) {
            MethodDeclaration method = (MethodDeclaration)jm.getASTNode();
            
            StatementVisitor visitor = new StatementVisitor(cfg, nextNode);
            method.accept(visitor);
            
            nextNode = visitor.getNextCFGNode();
            
        } else if (jm.isInitializer()) {
            Initializer initializer = (Initializer)jm.getASTNode();
            
            StatementVisitor visitor = new StatementVisitor(cfg, nextNode);
            initializer.accept(visitor);
            
            nextNode = visitor.getNextCFGNode();
        }
        
        CFGNode formalOutNode = createFormalOut(jm, cfg, entry, nextNode);
        
        CFGExit exit = new CFGExit(GraphNodeSort.methodExit);
        if (jm.isConstructor()) {
            exit.setSort(GraphNodeSort.constructorExit);
        } else if (jm.isInitializer()) {
            exit.setSort(GraphNodeSort.initializerExit);
        }
        cfg.setEndNode(exit);
        
        if (formalOutNode != null) {
            replace(cfg, nextNode, formalOutNode);
            replace(cfg, tmpExit, formalOutNode);
            cfg.add(exit);
            
            ControlFlow exitEdge = new ControlFlow(formalOutNode, exit);
            exitEdge.setTrue();
            cfg.add(exitEdge);
            
        } else {
            replace(cfg, nextNode, exit);
            replace(cfg, tmpExit, exit);
            cfg.add(exit);
        }
        
        return cfg;
    }
    
    /**
     * Reconnects a control flow by replacing a temporary node with a current node.
     * @param the CFG containing the nodes
     * @param tmpNode the temporary node to be replaced
     * @param node the node which is actually contained in the created CFG
     */
    private static void replace(CFG cfg, CFGNode tmpNode, CFGNode node) {
        GraphElementSet<GraphEdge> edges = new GraphElementSet<GraphEdge>(tmpNode.getIncomingEdges());
        for (GraphEdge edge : edges) {
            edge.setDstNode(node);
        }
    }
    
    /**
     * Creates a CFG node for a formal-in parameter.
     * @param jm the method for the CFG 
     * @param cfg the CFG to be created
     * @param entry the entry node of the CFG
     * @param prevNode the first node of the created parameter nodes
     * @return the last node of the created parameter nodes
     */
    private static CFGNode createFormalIn(JavaMethod jm, CFG cfg, CFGMethodEntry entry, CFGNode prevNode) {
        int ordinal = 0;
        for (JavaLocal param : jm.getParameters()) {
            CFGParameter finNode = new CFGParameter(param, GraphNodeSort.formalIn, ordinal);
            finNode.setBelongNode(entry);
            entry.addFormalIn(finNode);
            cfg.add(finNode);
            ordinal++;
            
            JavaVariableAccess jvout = param.convertJavaVariableAccess();
            finNode.setDefVariable(jvout);
            
            JavaVariableAccess jvin = new JavaSpecialVariable("$" + String.valueOf(ExpressionVisitor.paramNumber), jvout.getType(), jm);
            finNode.setUseVariable(jvin);
            ExpressionVisitor.paramNumber++;
            
            ControlFlow edge = new ControlFlow(prevNode, finNode);
            edge.setTrue();
            cfg.add(edge);
            
            prevNode = finNode;
        }
        
        return prevNode;
    }
    
    /**
     * Creates a CFG node for a formal-out parameter.
     * @param jm the method for the CFG 
     * @param cfg the CFG to be created
     * @param entry the entry node of the CFG
     * @param prevNode the first node of the created parameter nodes
     * @return the last node of the created parameter nodes
     */
    private static CFGNode createFormalOut(JavaMethod jm, CFG cfg, CFGMethodEntry entry, CFGNode prevNode) {
        if (!jm.isVoid()) {
            JavaExpression jexpr = new JavaExpression(jm.getASTNode());
            CFGParameter foutNode = new CFGParameter(jexpr, GraphNodeSort.formalOut, 0);
            foutNode.setBelongNode(entry);
            entry.addFormalOut(foutNode);
            
            cfg.add(foutNode);
            
            JavaVariableAccess jvout = new JavaSpecialVariable("$" + String.valueOf(ExpressionVisitor.paramNumber), jm.getReturnType(), jm);
            foutNode.addDefVariable(jvout); 
            ExpressionVisitor.paramNumber++;
            
            JavaLocal returnValue = jm.getReturnValueVariable();
            JavaVariableAccess jvin = returnValue.convertJavaVariableAccess();
            foutNode.addUseVariable(jvin);
            
            return foutNode;
        }
        
        return null;
    }
}

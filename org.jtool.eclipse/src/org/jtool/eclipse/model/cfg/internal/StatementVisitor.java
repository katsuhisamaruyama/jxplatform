/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGMerge;
import org.jtool.eclipse.model.cfg.CFGMethodEntry;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGParameter;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.cfg.ControlFlow;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.java.JavaStatement;
import org.jtool.eclipse.model.java.internal.JavaSpecialVariable;
import org.jtool.eclipse.model.java.JavaLocal;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * Visits expressions within a Java program and creates its CFG.
 * 
 * Statement:
 *   Block
 *   EmptyStatement
 *   TypeDeclarationStatement (not needed to visit because it was already visited under model creation)
 *   ExpressionStatement
 *   VariableDeclarationStatement
 *   ConstructorInvocation
 *   SuperConstructorInvocation
 *   IfStatement
 *   SwitchStatement
 *   SwitchCase
 *   WhileStatement
 *   DoStatement,
 *   ForStatement
 *   EnhancedForStatement
 *   BreakStatement
 *   ContinueStatement
 *   ReturnStatement
 *   AssertStatement
 *   LabeledStatement  
 *   SynchronizedStatement
 *   ThrowStatement
 *   TryStatement
 */
public class StatementVisitor extends ASTVisitor {
    
    static Logger logger = Logger.getLogger(StatementVisitor.class.getName());
    
    /**
     * A CFG created after visiting.
     */
    protected CFG cfg;
    
    /**
     * The current node under visiting. This equals to the end node after visiting.
     */
    protected CFGNode prevNode;
    
    /**
     * A CFG node which is a proxy of a node immediately manipulated.
     */
    protected CFGNode nextNode;
    
    /**
     * A stack that stores a loop entry node for a node currently visited.
     */
    private Stack<CFGNode> blockEntries = new Stack<CFGNode>();
    
    /**
     * A stack that stores a loop exit node for a node currently visited.
     */
    private Stack<CFGNode> blockExits = new Stack<CFGNode>();
    
    /**
     * A stack which stores a labeled node enclosing a node currently manipulated.
     */
    private Set<Label> labels = new HashSet<Label>();
    
    /**
     * Creates a new object for visiting a Java program.
     */
    public StatementVisitor() {
        super();
    }
    
    /**
     * Creates a new object for visiting an expression.
     * @param cfg CFG to be created
     * @param node the CFG node currently visited
     */
    protected StatementVisitor(CFG cfg, CFGNode node) {
         super();
         
         this.cfg = cfg;
         nextNode = node;
    }
    
    /**
     * Returns the last next node of the CFG, which will be replaced with a successor.
     * @return the next node
     */
    public CFGNode getNextCFGNode() {
        return nextNode;
    }
    
    /**
     * Creates a control flow between two nodes.
     * @param src the source node of a control flow to be created
     * @param dst the destination node of a control flow to be created
     * @return the created control flow
     */
    protected ControlFlow createFlow(CFGNode src, CFGNode dst) {
        ControlFlow edge = new ControlFlow(src, dst);
        cfg.add(edge);
        return edge;
    }
    
    /**
     * Reconnects a control flow by replacing a proxy node (<code>nextNode</code>) with a node.
     * @param node the node which is actually contained in the created CFG.
     */
    protected void reconnect(CFGNode node) {
        GraphElementSet<GraphEdge> edges = new GraphElementSet<GraphEdge>(nextNode.getIncomingEdges());
        for (GraphEdge edge : edges) {
            edge.setDstNode(node);
        }
        
        cfg.add(node);
        
        nextNode.clear();
        prevNode = node;
    }
    
    /**
     * Visits a block statement node and stores its information.
     * @param node the block statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(Block node) {
        return true;
    }
    
    /**
     * Visits an empty statement node and stores its information.
     * @param node the empty statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(EmptyStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement emptyNode = new CFGStatement(jst, GraphNodeSort.emptySt);
        
        reconnect(emptyNode);
        
        ControlFlow edge = createFlow(emptyNode, nextNode);
        edge.setTrue();
        
        return false;
    }
    
    /**
     * Visits a type declaration statement node and stores its information.
     * @param node the type declaration statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(TypeDeclarationStatement node) {
        return false;
    }
    
    /**
     * Visits an expression statement node and stores its information.
     * @param node the expression statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ExpressionStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement expressionStNode = new CFGStatement(jst, GraphNodeSort.assignment);
        
        reconnect(expressionStNode);
        
        Expression expression = node.getExpression();
        ExpressionVisitor visitor = new ExpressionVisitor(cfg, expressionStNode);
        expression.accept(visitor);
        
        CFGNode curNode = visitor.getExitNode();
        
        ControlFlow edge = createFlow(curNode, nextNode);
        edge.setTrue();
        
        return false;
    }
    
    /**
     * Visits an variable declaration statement node and stores its information.
     * @param node the variable declaration statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(VariableDeclarationStatement node) {
        
        for (VariableDeclarationFragment frag : (List<VariableDeclarationFragment>)node.fragments()) {
            JavaStatement jst = new JavaStatement(node);
            CFGStatement stNode = new CFGStatement(jst, GraphNodeSort.assignment);
            
            reconnect(stNode);
            
            ExpressionVisitor visitor = new ExpressionVisitor(cfg, stNode);
            frag.accept(visitor);
            
            CFGNode curNode = visitor.getExitNode();
            
            ControlFlow edge = createFlow(curNode, nextNode);
            edge.setTrue();
        }
        
        return false;
    }
    
    /**
     * Visits an constructor invocation node and stores its information.
     * @param node the constructor invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ConstructorInvocation node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement expressionStNode = new CFGStatement(jst, GraphNodeSort.assignment);
        
        reconnect(expressionStNode);
        
        ExpressionVisitor visitor = new ExpressionVisitor(cfg, expressionStNode);
        node.accept(visitor);
        
        CFGNode curNode = visitor.getExitNode();
        
        ControlFlow edge = createFlow(curNode, nextNode);
        edge.setTrue();
        
        return false;
    }
    
    /**
     * Visits a super-constructor invocation node and stores its information.
     * @param node the super-constructor invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SuperConstructorInvocation node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement expressionStNode = new CFGStatement(jst, GraphNodeSort.assignment);
        
        reconnect(expressionStNode);
        
        ExpressionVisitor visitor = new ExpressionVisitor(cfg, expressionStNode);
        node.accept(visitor);
        
        CFGNode curNode = visitor.getExitNode();
        
        ControlFlow edge = createFlow(curNode, nextNode);
        edge.setTrue();
        
        return false;
    }
    
    /**
     * Visits a <code>if</code> statement node and stores its information.
     * @param node the <code>if</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(IfStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement ifNode = new CFGStatement(jst, GraphNodeSort.ifSt);
        
        reconnect(ifNode);
        
        Expression condition = node.getExpression();
        ExpressionVisitor condVisitor = new ExpressionVisitor(cfg, ifNode);
        condition.accept(condVisitor);
            
        CFGNode curNode = condVisitor.getExitNode();
        
        ControlFlow trueEdge = createFlow(curNode, nextNode);
        trueEdge.setTrue();
        
        Statement thenSt = node.getThenStatement();
        thenSt.accept(this);
        
        ControlFlow trueMergeEdge = cfg.getFlow(prevNode, nextNode);
        
        ControlFlow falseEdge = createFlow(curNode, nextNode);
        falseEdge.setFalse();
        
        Statement elseSt = node.getElseStatement();
        if (elseSt != null) {
            elseSt.accept(this);
            
            if (trueMergeEdge != null) {
                trueMergeEdge.setDstNode(nextNode);
            }
        }
        
        CFGMerge mergeNode = new CFGMerge(ifNode);
        
        reconnect(mergeNode);
        
        ControlFlow edge = createFlow(mergeNode, nextNode);
        edge.setTrue();
        
        return false;
    }
    
    /**
     * Visits a <code>switch</code> statement node and stores its information.
     * @param node the <code>switch</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(SwitchStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGSwitch switchNode = new CFGSwitch(jst, GraphNodeSort.switchSt);
        
        reconnect(switchNode);
        
        Expression condition = node.getExpression();
        ExpressionVisitor condVisitor = new ExpressionVisitor(cfg, switchNode);
        condition.accept(condVisitor);
        
        CFGNode curNode = condVisitor.getExitNode();
        
        ControlFlow caseEdge = createFlow(curNode, nextNode);
        caseEdge.setTrue();
       
        CFGNode exitNode = new CFGNode();
        blockEntries.push(switchNode);
        blockExits.push(exitNode);
        
        List<Statement> remaining = new ArrayList<Statement>();
        for (Statement statement : (List<Statement>)node.statements()) {
            remaining.add(statement);
        }
        for (Statement statement : (List<Statement>)node.statements()) {
            remaining.remove(0);
            
            if (statement instanceof SwitchCase) {  
                visitSwitchCase((SwitchCase)statement, switchNode, remaining);
            }
        }
        
        if (switchNode.hasDefault()) {
            CFGNode successor = switchNode.getSuccessorOfDefault();
            
            List<GraphEdge> nextEdges = new ArrayList<GraphEdge>();
            for (GraphEdge edge : nextNode.getIncomingEdges()) {
                nextEdges.add(edge);
            }
            
            List<GraphEdge> incomingEdges = new ArrayList<GraphEdge>();
            for (GraphEdge edge : switchNode.getDefaultStartNode().getIncomingEdges()) {
                incomingEdges.add(edge);
            }
            
            List<GraphEdge> outgoingEdges = new ArrayList<GraphEdge>();
            for (GraphEdge edge : successor.getIncomingEdges()) {
                outgoingEdges.add(edge);
            }
            
            for (GraphEdge edge : nextEdges) {
                ControlFlow flow = (ControlFlow)edge;
                flow.setDstNode(switchNode.getDefaultStartNode());
            }
            
            for (GraphEdge edge : incomingEdges) {
                ControlFlow flow = (ControlFlow)edge;
                flow.setDstNode(successor);
            }
            
            for (GraphEdge edge : outgoingEdges) {
                ControlFlow flow = (ControlFlow)edge;
                if (flow.isFalse()) {
                    cfg.remove(flow);
                } else {
                    flow.setDstNode(nextNode);
                } 
            }
        }
        
        nextNode.addIncomingEdges(exitNode.getIncomingEdges());
     
        CFGMerge mergeNode = new CFGMerge(switchNode);
        
        reconnect(mergeNode);
        
        ControlFlow falseEdge = createFlow(switchNode, mergeNode);
        falseEdge.setFalse();
        
        blockEntries.pop();
        blockExits.pop();
        
        ControlFlow edge = createFlow(mergeNode, nextNode);
        edge.setTrue();
        
        return false;
    }
    
    /**
     * Visits a switch-case statement node and stores its information.
     * @param node the switch-case statement node
     * @param swichNode the <code>switch</code> statement node that contains switch-case
     * @param remaining the set of the remaining <Statement> nodes
     * @return the flow for the <code>default</code>, <code>null</code> if there is no <code>default</code>
     */
    private void visitSwitchCase(SwitchCase node, CFGSwitch switchNode, List<Statement> remaining)  {
        JavaStatement jst = new JavaStatement(node);
        
        CFGStatement caseNode;
        if (!node.isDefault()) {
            caseNode = new CFGStatement(jst, GraphNodeSort.switchCaseSt);
            
            reconnect(caseNode);
            
            Expression condition = node.getExpression();
            ExpressionVisitor condVisitor = new ExpressionVisitor(cfg, caseNode);
            condition.accept(condVisitor);
            
            caseNode.addDefVariables(switchNode.getDefVariables());
            caseNode.addUseVariables(switchNode.getUseVariables());
            
            CFGNode curNode = condVisitor.getExitNode();
            
            ControlFlow edge = createFlow(curNode, nextNode);
            edge.setTrue();
            
        } else {
            caseNode = new CFGStatement(jst, GraphNodeSort.switchDefaultSt);
            
            reconnect(caseNode);
            
            ControlFlow edge = createFlow(caseNode, nextNode);
            edge.setTrue();
            
            switchNode.setDefaultStartNode(caseNode);
        }
            
        for (Statement statement : remaining) {
            if (statement instanceof SwitchCase) {
                break;
            }
            
            statement.accept(this); 
        }
        
        ControlFlow edge = createFlow(caseNode, nextNode);
        edge.setFalse();
       
        if (node.isDefault()) {
            switchNode.setDefaultEndNode(prevNode);
        }
    }
    
    /**
     * Visits a <code>while</code> statement node and stores its information.
     * @param node the <code>while</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(WhileStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement whileNode = new CFGStatement(jst, GraphNodeSort.whileSt);
        
        reconnect(whileNode);
        
        Expression condition = node.getExpression();
        ExpressionVisitor condVisitor = new ExpressionVisitor(cfg, whileNode);
        condition.accept(condVisitor);
        
        CFGNode curNode = condVisitor.getExitNode();
        
        ControlFlow trueEdge = createFlow(curNode, nextNode);
        trueEdge.setTrue();
       
        CFGNode entryNode = condVisitor.getEntryNode();
        CFGNode exitNode = new CFGNode();
        blockEntries.push(entryNode);
        blockExits.push(exitNode);
        
        Statement body = node.getBody();
        body.accept(this);
        
        ControlFlow loopbackEdge = cfg.getFlow(prevNode, nextNode);
        if (loopbackEdge != null) {
            loopbackEdge.setDstNode(entryNode);
            loopbackEdge.setLoopBack(whileNode);
        }
         
        ControlFlow falseEdge = createFlow(whileNode, nextNode);
        falseEdge.setFalse();
        prevNode = whileNode;
        nextNode.addIncomingEdges(exitNode.getIncomingEdges());
        
        blockEntries.pop();
        blockExits.pop();
         
        return false;
    }
    
    /**
     * Visits a <code>do</code> statement node and stores its information.
     * @param node the <code>do</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(DoStatement node) {
        CFGNode entryNode = new CFGNode();
        CFGNode exitNode = new CFGNode();
        blockEntries.push(entryNode);
        blockExits.push(exitNode);
        
        ControlFlow entryEdge = cfg.getFlow(prevNode, nextNode);
        
        Statement body = node.getBody();
        body.accept(this);
        
        nextNode.addIncomingEdges(entryNode.getIncomingEdges());
        
        JavaStatement jst = new JavaStatement(node);
        CFGStatement doNode = new CFGStatement(jst, GraphNodeSort.doSt);
        
        reconnect(doNode);
        
        Expression condition = node.getExpression();
        ExpressionVisitor condVisitor = new ExpressionVisitor(cfg, doNode);
        condition.accept(condVisitor);
        
        CFGNode curNode = condVisitor.getExitNode();
        
        ControlFlow loopbackEdge = createFlow(curNode, entryEdge.getDstNode());
        loopbackEdge.setTrue();
        loopbackEdge.setLoopBack(doNode);
        
        ControlFlow falseEdge = createFlow(doNode, nextNode);
        falseEdge.setFalse();
        nextNode.addIncomingEdges(exitNode.getIncomingEdges());
        
        blockEntries.pop();
        blockExits.pop();
        
        return false;
    }
    
    /**
     * Visits a <code>for</code> statement node and stores its information.
     * @param node the <code>for</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(ForStatement node) {
        for (Expression initializer : (List<Expression>)node.initializers()) {
            JavaStatement jinit = new JavaStatement(initializer);
            CFGStatement initNode = new CFGStatement(jinit, GraphNodeSort.assignment);
            
            ExpressionVisitor initVisitor = new ExpressionVisitor(cfg, initNode);
            initializer.accept(initVisitor);
            
            CFGNode curNode = initVisitor.getExitNode();
            
            reconnect(initNode);
            
            ControlFlow edge = createFlow(curNode, nextNode);
            edge.setTrue();
        }
        
        JavaStatement jcond = new JavaStatement(node);
        CFGStatement forNode = new CFGStatement(jcond, GraphNodeSort.forSt);
        
        CFGNode entryNode;
        Expression condition = node.getExpression();
        if (condition != null) {
            ExpressionVisitor condVisitor = new ExpressionVisitor(cfg, forNode);
            condition.accept(condVisitor);
            
            CFGNode curNode = condVisitor.getExitNode();
            
            reconnect(forNode);
            
            ControlFlow edge = createFlow(curNode, nextNode);
            edge.setTrue();
            
            entryNode = condVisitor.getEntryNode();
            
        } else {
            ControlFlow edge = createFlow(forNode, nextNode);
            edge.setTrue();
            
            entryNode = forNode;
        }
        
        CFGNode exitNode = new CFGNode();
        blockEntries.push(entryNode);
        blockExits.push(exitNode);
        
        Statement body = node.getBody();
        body.accept(this);
        
        for (Expression update : (List<Expression>)node.updaters()) {
            JavaStatement jupdate = new JavaStatement(update);
            CFGStatement updateNode = new CFGStatement(jupdate, GraphNodeSort.assignment);
            
            ExpressionVisitor updateVisitor = new ExpressionVisitor(cfg, updateNode);
            update.accept(updateVisitor);
            
            CFGNode curNode = updateVisitor.getExitNode();
            
            reconnect(updateNode);
            
            ControlFlow edge = createFlow(curNode, nextNode);
            edge.setTrue();
        }
        
        ControlFlow loopbackEdge = cfg.getFlow(prevNode, nextNode);
        if (loopbackEdge != null) {
            loopbackEdge.setDstNode(entryNode);
            loopbackEdge.setLoopBack(forNode);
        }
        
        ControlFlow falseEdge = createFlow(forNode, nextNode);
        falseEdge.setFalse();
        prevNode = forNode;
        nextNode.addIncomingEdges(exitNode.getIncomingEdges());
        
        blockEntries.pop();
        blockExits.pop();
        
        return false;
    }
    
    /**
     * Visits a <code>break</code> statement node and stores its information.
     * @param node the <code>break</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(EnhancedForStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement forNode = new CFGStatement(jst, GraphNodeSort.assignment);
        
        reconnect(forNode);
        
        SingleVariableDeclaration parameter = node.getParameter();
        ExpressionVisitor paramVisitor = new ExpressionVisitor(cfg, forNode);
        parameter.accept(paramVisitor);
        
        Expression expression = node.getExpression();
        ExpressionVisitor exprVisitor = new ExpressionVisitor(cfg, forNode);
        expression.accept(exprVisitor);
        
        CFGNode curNode = exprVisitor.getExitNode();
        
        ControlFlow edge = createFlow(curNode, nextNode);
        edge.setTrue();
        
        CFGNode exitNode = new CFGNode();
        blockEntries.push(curNode);
        blockExits.push(exitNode);
        
        Statement body = node.getBody();
        body.accept(this);
        
        ControlFlow loopbackEdge = cfg.getFlow(prevNode, nextNode);
        if (loopbackEdge != null) {
            loopbackEdge.setDstNode(curNode);
            loopbackEdge.setLoopBack(forNode);
        }
        
        ControlFlow falseEdge = createFlow(forNode, nextNode);
        falseEdge.setFalse();
        prevNode = forNode;
        nextNode.addIncomingEdges(exitNode.getIncomingEdges());
        
        blockEntries.pop();
        blockExits.pop();
        
        return false;
    }
    
    /**
     * Visits a <code>break</code> statement node and stores its information.
     * @param node the <code>break</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(BreakStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement breakNode = new CFGStatement(jst, GraphNodeSort.breakSt);
        
        reconnect(breakNode);
        
        CFGNode jumpNode;
        if (node.getLabel() != null) {
            String name = node.getLabel().getFullyQualifiedName();
            jumpNode = getLabel(name).getNode();
        
        } else {
            
            jumpNode = (CFGNode)blockEntries.peek();
            // Goes to the entry point and moves its false-successor immediately.
            // Not go to the exit point directly according to the Java specification.
        }
        
        if (jumpNode != null) {
            ControlFlow edge = createFlow(breakNode, jumpNode);
            edge.setTrue();
            
            edge = createFlow(breakNode, nextNode);
            edge.setFallThrough();
        }
        
        return false;
    }
    
    /**
     * Visits a <code>for</code> statement node and stores its information.
     * @param node the <code>for</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ContinueStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement continueNode = new CFGStatement(jst, GraphNodeSort.continueSt);
        
        reconnect(continueNode);
        
        CFGNode jumpNode;
        if (node.getLabel() != null) {
            String name = node.getLabel().getFullyQualifiedName();
            jumpNode = getLabel(name).getNode();
        
        } else {
            jumpNode = (CFGNode)blockEntries.peek();
        }
        
        if (jumpNode != null) {
            ControlFlow edge = createFlow(continueNode, jumpNode);
            edge.setTrue();
            
            edge = createFlow(continueNode, nextNode);
            edge.setFallThrough();
        }
        
        return false;
    }
    
    /**
     * Visits a <code>for</code> statement node and stores its information.
     * @param node the <code>for</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ReturnStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement returnNode = new CFGStatement(jst, GraphNodeSort.returnSt);
        
        reconnect(returnNode);
        
        CFGNode curNode = returnNode;
        Expression expression = node.getExpression();
        if (expression != null) {
            ExpressionVisitor exprVisitor = new ExpressionVisitor(cfg, returnNode);
            expression.accept(exprVisitor);
            
            CFGMethodEntry methodNode = (CFGMethodEntry)cfg.getStartNode();
            JavaMethod jm = methodNode.getJavaMethod();
            JavaVariableAccess jv = new JavaSpecialVariable("$" + jm.getName(), jm.getReturnType(), jm);
            returnNode.addDefVariable(jv);
            
            curNode = exprVisitor.getExitNode();
        }
        
        ControlFlow trueEdge = createFlow(curNode, cfg.getEndNode());
        trueEdge.setTrue();
        
        ControlFlow fallEdge = createFlow(curNode, nextNode);
        fallEdge.setFallThrough();
        
        return false;
    }
    
    /**
     * Visits a <code>assert</code> statement node and stores its information.
     * @param node the <code>assert</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(AssertStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement assertNode = new CFGStatement(jst, GraphNodeSort.assignment);
        
        reconnect(assertNode);
        
        Expression expression = node.getExpression();
        ExpressionVisitor exprVisitor = new ExpressionVisitor(cfg, assertNode);
        expression.accept(exprVisitor);
        
        CFGNode curNode = exprVisitor.getExitNode();
        
        Expression message = node.getMessage();
        if (message != null) {
            ExpressionVisitor mesgVisitor = new ExpressionVisitor(cfg, assertNode);
            message.accept(mesgVisitor);
        
            curNode = mesgVisitor.getExitNode();
        }
        
        ControlFlow edge = createFlow(curNode, nextNode);
        edge.setTrue();
        
        return false;
    }
    
    /**
     * Visits a labeled statement node and stores its information.
     * @param node the labeled statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(LabeledStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement labelNode = new CFGStatement(jst, GraphNodeSort.labelSt);
        
        reconnect(labelNode);
        
        String name = node.getLabel().getFullyQualifiedName();
        labels.add(new Label(name, labelNode));
        
        Statement body = node.getBody();
        body.accept(this);
        
        ControlFlow trueEdge = createFlow(labelNode, cfg.getEndNode());
        trueEdge.setTrue(); 
        
        return false;
    }
    
    /**
     * Visits a <code>throw</code> statement node and stores its information.
     * @param node the <code>throw</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ThrowStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement throwNode = new CFGStatement(jst, GraphNodeSort.throwSt);
        
        reconnect(throwNode);
        
        Expression expression = node.getExpression();
        ExpressionVisitor exprVisitor = new ExpressionVisitor(cfg, throwNode);
        expression.accept(exprVisitor);
        
        CFGNode curNode = exprVisitor.getExitNode();
        
        ControlFlow trueEdge = createFlow(curNode, cfg.getEndNode());
        trueEdge.setTrue();
        
        ControlFlow fallEdge = createFlow(curNode, nextNode);
        fallEdge.setFalse();
        
        return false;
    }
    
    /**
     * Visits a <code>synchronized</code> statement node and stores its information.
     * @param node the <code>synchronized</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SynchronizedStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGStatement syncNode = new CFGStatement(jst, GraphNodeSort.synchronizedSt);
        
        reconnect(syncNode);
        
        Expression expression = node.getExpression();
        ExpressionVisitor exprVisitor = new ExpressionVisitor(cfg, syncNode);
        expression.accept(exprVisitor);
        
        CFGNode curNode = exprVisitor.getExitNode();
        
        ControlFlow trueEdge = createFlow(curNode, nextNode);
        trueEdge.setTrue();
        
        Statement body = node.getBody();
        body.accept(this);
        
        return false;
    }
    
    /**
     * Visits a <code>try</code> statement node and stores its information.
     * @param node the <code>try</code> statement node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(TryStatement node) {
        JavaStatement jst = new JavaStatement(node);
        CFGTry tryNode = new CFGTry(jst, GraphNodeSort.trySt);
        
        reconnect(tryNode);
        
        ControlFlow edge = createFlow(tryNode, nextNode);
        edge.setTrue();
        
        Statement body = node.getBody();
        body.accept(this);
        
        CFGMerge mergeNode = new CFGMerge(tryNode);
        
        reconnect(mergeNode);
        
        for (CatchClause clause : (List<CatchClause>)node.catchClauses()) { 
            visitCatchClause(tryNode, clause, mergeNode);
        }
        
        Block finallyBlock = node.getFinally();
        if (finallyBlock != null) {
            visitFinallyBlock(tryNode, finallyBlock, mergeNode);
        }
        
        ControlFlow endEdge = createFlow(mergeNode, cfg.getEndNode());
        endEdge.setTrue();
        
        return false;
    }
    
    /**
     * Visits a <code>catch</code> clause node of a <code>try</code> statement and stores its information.
     * @param tryNode the <code>try</code> statement node
     * @param node the <code>catch</code> clause node
     * @param mergeNode the merge node for the <code>try</code> statement
     */
    public void visitCatchClause(CFGTry tryNode, CatchClause node, CFGMerge mergeNode) {
        JavaStatement jst = new JavaStatement(node);
        CFGCatch catchNode = new CFGCatch(jst, GraphNodeSort.catchSt);
        tryNode.addCatchClause(catchNode);
        
        reconnect(catchNode);
        
        JavaLocal param = new JavaLocal(node.getException(), jst.getDeclaringJavaMethod());
        CFGParameter paramNode = new CFGParameter(param, GraphNodeSort.formalIn, 0);
        
        reconnect(paramNode);
        
        JavaVariableAccess jv = param.convertJavaVariableAccess();
        paramNode.addDefVariable(jv);
        CFGMethodEntry methodNode = (CFGMethodEntry)cfg.getStartNode();
        JavaMethod jm = methodNode.getJavaMethod();
        JavaVariableAccess jvin = new JavaSpecialVariable("$" + jv.getName(), jv.getType(), jm);
        paramNode.addUseVariable(jvin);
        
        ControlFlow trueEdge = createFlow(paramNode, nextNode);
        trueEdge.setTrue();
        
        Statement body = node.getBody();
        body.accept(this);
        
        reconnect(mergeNode);
    }
    
    /**
     * Visits a <code>finally</code> block node of a <code>try</code> statement and stores its information.
     * @param tryNode tryNode the <code>try</code> statement node
     * @param block the <code>finally</code> block node 
     * @param mergeNode the merge node for the <code>try</code> statement
     */
    private void visitFinallyBlock(CFGTry tryNode, Block block, CFGMerge mergeNode) {
        JavaStatement fst = new JavaStatement(block);
        CFGStatement finallyNode = new CFGStatement(fst, GraphNodeSort.finallySt);
        tryNode.setFinallyBlock(finallyNode);
        
        reconnect(finallyNode);
            
        ControlFlow truEdge = createFlow(finallyNode, nextNode);
        truEdge.setTrue();
        
        block.accept(this);
        
        reconnect(mergeNode);
    }
    
    /**
     * A label attached to a labeled statement.
     * @author Katsuhisa Maruyama
     */
    class Label {
        
        /**
         * The name of this label. 
         */
        String name = "";
        
        /**
         * The entry node for a statement with this label.
         */
        CFGNode node;
        
        /**
         * Creates a new, empty object.
         */
        Label() {
        }
        
        /**
         * Creates a new object.
         * @param name the name of this label
         * @param node the node for this label 
         */
        Label(String name, CFGNode node) {
            this.name = name;
            this.node = node;
        }
        
        /**
         * Returns the name for this label.
         * @return the name for this label
         */
        String getName() {
            return name;
        }
        
        /**
         * Returns the node for this label.
         * @return the node for this label
         */
        CFGNode getNode() {
            return node;
        }
    }
    
    /**
     * Returns the label with a name.
     * @param name A label name to be compared.
     * @return The found label, <code>null</code> if there is no label found.
     */
    private Label getLabel(String name) {
        for (Label label : labels) {
            if (label.getName().compareTo(name) == 0) {
                return label;
            }
        }
        return null;
    }
}

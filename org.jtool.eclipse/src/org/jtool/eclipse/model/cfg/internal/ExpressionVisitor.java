/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGEntry;
import org.jtool.eclipse.model.cfg.CFGFactory;
import org.jtool.eclipse.model.cfg.CFGFieldEntry;
import org.jtool.eclipse.model.cfg.CFGMethodEntry;
import org.jtool.eclipse.model.cfg.CFGMethodCall;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGParameter;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.cfg.ControlFlow;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaExpression;
import org.jtool.eclipse.model.java.internal.JavaSpecialVariable;
import org.jtool.eclipse.model.java.JavaElement;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaLocal;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaMethodCall;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import org.apache.log4j.Logger;

/**
 * Visits expressions within a Java program and creates its CFG.
 * 
 * Expression:
 *   ArrayAccess
 *   ArrayCreation
 *   ArrayInitializer
 *   Assignment
 *   PrefixExpression
 *   PostfixExpression
 *   InfixExpression
 *   FieldAccess
 *   SuperFieldAccess
 *   ThisExpression
 *   VariableDeclarationExpression (VariableDeclarationFragment)
 *   MethodInvocation
 *   SuperMethodInvocation
 *   ClassInstanceCreation
 *     ConstructorInvocation (this originally belongs to Statement)
 *     SuperConstructorInvocation (this originally belongs to Statement)
 *   
 * Nothing to do for the following AST nodes:
 *   CastExpression
 *   ConditionalExpression
 *   InstanceofExpression
 *   ParenthesizedExpression
 *   Annotation
 *   BooleanLiteral
 *   CharacterLiteral
 *   NullLiteral
 *   NumberLiteral
 *   StringLiteral
 *   TypeLiteral
 *   Name (SimpleName/QualifiedName)
 * 
 * @see org.eclipse.jdt.core.dom.Expression
 * @author Katsuhisa Maruyama
 */
public class ExpressionVisitor extends ASTVisitor {
    
    protected static Logger logger = Logger.getLogger(ExpressionVisitor.class.getName());
    
    /**
     * A CFG created after visiting.
     */
    protected CFG cfg;
    
    /**
     * A CFG node currently visited.
     */
    protected CFGStatement curNode;
    
    /**
     * A CFG node that indicates the entry point of the created CFG.
     */
    protected CFGStatement entryNode;
    
    /**
     * A stack storing flags that indicate if the current visit collects defined variables or not.
     * The top of this stack equals to <code>true</code> if the collection needed, otherwise <code>false</code>.
     */
    protected Stack<Boolean> analysingDefinedVariables = new Stack<Boolean>();
    
    /**
     * The status that indicates if the currently visited node is right-hand expression of an assignment.
     */
    @SuppressWarnings("unused")
    private boolean inAssignment;
    
    /**
     * The unique number for a parameter in a method declaration.
     */
    protected static int paramNumber = 1;
    
    /**
     * The flag that requests the creation of actual nodes for method invocation.
     */
    private boolean createActualNodes = false;
    
    /**
     * Creates a new object for visiting an expression.
     * @param cfg CFG to be created
     * @param node the CFG statement node currently visited
     */
    protected ExpressionVisitor(CFG cfg, CFGStatement node) {
        super();
        
        this.cfg = cfg;
        curNode = node;
        entryNode = node;
        inAssignment = false;
        
        createActualNodes = CFGFactory.getActualNodeCreation();
        
        analysingDefinedVariables.push(false);
    }
    
    /**
     * Returns node that indicates the entry point of the created CFG after visiting.
     * @return the entry node
     */
    public CFGNode getEntryNode() {
        return entryNode;
    }
    
    /**
     * Returns node that indicates the exit point of the created CFG after visiting.
     * @return the exit node
     */
    public CFGNode getExitNode() {
        return curNode;
    }
    
    /**
     * Inserts a specified node before the current node (<code>curNode</code>) and reconnects its control flows.
     * @param node the node to be inserted
     */
    protected void insertBeforeCurrentNode(CFGStatement node) {
        GraphElementSet<GraphEdge> edges = new GraphElementSet<GraphEdge>(curNode.getIncomingEdges());
        for (GraphEdge edge : edges) {
            ControlFlow flow = (ControlFlow)edge;
            flow.setDstNode(node);
        }
        cfg.add(node);
        
        ControlFlow flow = createFlow(node, curNode);
        flow.setTrue();
        cfg.add(flow);
        
        entryNode = node;
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
     * Visits an array access node and stores its information.
     * @param node the array access node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ArrayAccess node) {
        Expression array = node.getArray();
        array.accept(this);
        
        Expression index = node.getIndex();
        
        analysingDefinedVariables.push(false);
        index.accept(this);
        analysingDefinedVariables.pop();
        
        return false;
    }
    
    /**
     * Visits an array creation node and stores its information.
     * @param node the array creation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ArrayCreation node) {
        return true;
    }
    
    /**
     * Visits an array initializer node and stores its information.
     * @param node the array initializer node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ArrayInitializer node) {
        return true;
    }
    
    /**
     * Visits an assignment node and stores its information.
     * @param node the assignment node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(Assignment node) {
        JavaExpression assignment = new JavaExpression(node);
        curNode.setSort(GraphNodeSort.assignment);
        curNode.setJavaElement(assignment);
        
        Expression lefthand = node.getLeftHandSide();
        
        analysingDefinedVariables.push(true);
        lefthand.accept(this);
        analysingDefinedVariables.pop();
        
        if (node.getOperator() != Assignment.Operator.ASSIGN) {
            analysingDefinedVariables.push(false);
            lefthand.accept(this);
            analysingDefinedVariables.pop();
        }
        
        Expression righthand = node.getRightHandSide();
        
        analysingDefinedVariables.push(false);
        inAssignment = true;
        righthand.accept(this);
        analysingDefinedVariables.pop();
        inAssignment = false;
        
        return false;
    }
    
    /**
     * Visits a prefix expression and stores its information.
     * @param node the prefix expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(PrefixExpression node) {
        JavaExpression assignment = new JavaExpression(node);
        curNode.setSort(GraphNodeSort.assignment);
        curNode.setJavaElement(assignment);
        
        Expression expr = node.getOperand();
        
        analysingDefinedVariables.push(false);
        expr.accept(this);
        analysingDefinedVariables.pop();
        
        PrefixExpression.Operator operator = node.getOperator();
        if (operator == PrefixExpression.Operator.INCREMENT || operator == PrefixExpression.Operator.DECREMENT) {
            analysingDefinedVariables.push(true);
            expr.accept(this);
            analysingDefinedVariables.pop();
        }
        
        return false;
    }
    
    /**
     * Visits a postfix expression and stores its information.
     * @param node the postfix expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(PostfixExpression node) {
        JavaExpression assignment = new JavaExpression(node);
        curNode.setSort(GraphNodeSort.assignment);
        curNode.setJavaElement(assignment);
        
        Expression expr = node.getOperand();
        
        analysingDefinedVariables.push(false);
        expr.accept(this);
        analysingDefinedVariables.pop();
        
        PostfixExpression.Operator operator = node.getOperator();
        if (operator == PostfixExpression.Operator.INCREMENT || operator == PostfixExpression.Operator.DECREMENT) {
            analysingDefinedVariables.push(true);
            expr.accept(this);
            analysingDefinedVariables.pop();
        }
        
        return false;
    }
    
    /**
     * Visits an infix expression node and stores its information.
     * @param node the infix expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(InfixExpression node) {
        Expression lefthand = node.getLeftOperand();
        
        analysingDefinedVariables.push(false);
        lefthand.accept(this);
        analysingDefinedVariables.pop();
        
        Expression righthand = node.getRightOperand();
        
        analysingDefinedVariables.push(false);
        righthand.accept(this);
        analysingDefinedVariables.pop();
        
        return false;
    }
    
    /**
     * Visits a field access node and stores its information.
     * @param node the field access node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(FieldAccess node) {
        Expression expr = node.getExpression();
        
        analysingDefinedVariables.push(false);
        expr.accept(this);
        analysingDefinedVariables.pop();
        
        SimpleName name = node.getName();
        name.accept(this);
        
        return false;
    }
    
    /**
     * Visits a super field access node and stores its information.
     * @param node the super field access node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SuperFieldAccess node) {
        SimpleName name = node.getName();
        name.accept(this);
        
        return false;
    }
    
    /**
     * Visits a <code>this</code> expression and stores its information.
     * @param node the <code>this</code> expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ThisExpression node) {
        Name name = node.getQualifier();
        JavaClass jc;
        if (name != null) {
            jc = JavaClass.getJavaClass(name.getFullyQualifiedName());
        } else {
            jc = JavaElement.getDeclaringJavaClass(node);
        }
        
        if (jc != null) {
            JavaVariableAccess jv = new JavaSpecialVariable("$this", jc.getQualifiedName(), jc);
            curNode.addUseVariable(jv);
        }
        
        return false;
    }
    
    /**
     * Visits a cast expression and stores its information.
     * @param node the cast expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(CastExpression node) {
        return true;
    }
    
    /**
     * Visits a conditional expression and stores its information.
     * @param node the conditional expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ConditionalExpression node) {
        return true;
    }
    
    /**
     * Visits a <code>instanceof</code> expression and stores its information.
     * @param node the <code>instanceof</code> expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(InstanceofExpression node) {
        return true;
    }
    
    /**
     * Visits a parenthesized expression and stores its information.
     * @param node the parenthesized expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ParenthesizedExpression node) {
        return true;
    }
    
    /**
     * Visits a variable declaration expression node and stores its information.
     * @param node the variable declaration expression node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(VariableDeclarationExpression node) {
        return true;
    }
    
    /**
     * Visits a single variable declaration node and stores its information.
     * @param node the single variable declaration node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SingleVariableDeclaration node) {
        visitVariableDeclaration(node);
        
        return false;
    }
    
    /**
     * Visits a variable declaration fragment node and stores its information.
     * @param node the variable declaration fragment node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(VariableDeclarationFragment node) {
        visitVariableDeclaration(node);
        
        return false;
    }
    
    /**
     * Visits a variable declaration fragment node and stores its information.
     * @param node the variable declaration node
     */
    private void visitVariableDeclaration(VariableDeclaration node) {
        if (isField(node.getName())) {
            JavaField jfield = new JavaField(node, JavaElement.getDeclaringJavaClass(node));
            curNode.setSort(GraphNodeSort.fieldDeclaration);
            curNode.setJavaElement(jfield);
            
        } else if (isLocal(node.getName())) {
            JavaLocal jlocal = new JavaLocal(node, JavaElement.getDeclaringJavaMethod(node));
            curNode.setSort(GraphNodeSort.localDeclaration);
            curNode.setJavaElement(jlocal);
        }
        
        SimpleName name = node.getName();
        analysingDefinedVariables.push(true);
        name.accept(this);
        analysingDefinedVariables.pop();
        
        Expression initializer = node.getInitializer();
        if (initializer != null) {
            analysingDefinedVariables.push(false);
            inAssignment = true;
            initializer.accept(this);
            inAssignment = false;
            analysingDefinedVariables.pop();
        }
    }
    
    /**
     * Visits a method invocation node and stores its information.
     * @param node the method invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(MethodInvocation node) {
        IMethodBinding binding = node.resolveMethodBinding();
        if (binding == null) {
            return false;
        }
        
        JavaMethodCall jmc = new JavaMethodCall(node, binding, JavaElement.getDeclaringJavaMethod(node));
        CFGMethodCall callNode = new CFGMethodCall(jmc, GraphNodeSort.methodCall);
        
        boolean createActual = (jmc.getJavaMethod() != null && createActualNodes && callNode.callMethodInProject() && !callNode.callSelf());
        
        if (createActual) {
            createActualIns(jmc, callNode, node.arguments());
        } else {
            mergeActualIn(callNode, node.arguments());
        }
        
        insertBeforeCurrentNode(callNode);
        
        if (createActual) {
            createActualOuts(jmc, callNode, node.arguments());
            JavaLocal ret = new JavaLocal(node);
            createActualOutForReturnValue(jmc, callNode, ret);
        } else {
            mergeActualOut(callNode);
            curNode.addUseVariable(callNode.getDefVariables().get(0));
        }
        
        Expression primary = node.getExpression();
        if (primary != null) {
            analysingDefinedVariables.push(false);
            primary.accept(this);
            analysingDefinedVariables.pop();
        }
        
        return false;
    }
    
    /**
     * Visits a super-method invocation node and stores its information.
     * @param node the super-method invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(SuperMethodInvocation node) {
        IMethodBinding binding = node.resolveMethodBinding();
        if (binding == null) {
            return false;
        }
        
        JavaMethodCall jmc = new JavaMethodCall(node, binding, JavaElement.getDeclaringJavaMethod(node));
        CFGMethodCall callNode = new CFGMethodCall(jmc, GraphNodeSort.methodCall);
        
        boolean createActual = (jmc.getJavaMethod() != null && createActualNodes && callNode.callMethodInProject() && !callNode.callSelf());
        
        if (createActual) {
            createActualIns(jmc, callNode, node.arguments());
        } else {
            mergeActualIn(callNode, node.arguments());
        }
        
        insertBeforeCurrentNode(callNode);
        
        if (createActual) {
            createActualOuts(jmc, callNode, node.arguments());
            JavaLocal ret = new JavaLocal(node);
            createActualOutForReturnValue(jmc, callNode, ret);
        } else {
            mergeActualOut(callNode);
            curNode.addUseVariable(callNode.getDefVariables().get(0));
        }
        
        return false;
    }
    
    /**
     * Visits a constructor invocation node and stores its information.
     * @param node the constructor invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(ConstructorInvocation node) {  
        IMethodBinding binding = node.resolveConstructorBinding();
        if (binding == null) {
            return false;
        }
        
        JavaMethodCall jmc = new JavaMethodCall(node, binding, JavaElement.getDeclaringJavaMethod(node));
        CFGMethodCall callNode = new CFGMethodCall(jmc, GraphNodeSort.constructorCall);
        
        boolean createActual = (jmc.getJavaMethod() != null && createActualNodes && callNode.callMethodInProject() && !callNode.callSelf());
        
        if (createActual) {
            createActualIns(jmc, callNode, node.arguments());
        } else {
            mergeActualIn(callNode, node.arguments());
        }
        
        insertBeforeCurrentNode(callNode);
        
        if (createActual) {
            createActualOuts(jmc, callNode, node.arguments());
            JavaLocal ret = new JavaLocal(node);
            createActualOutForReturnValue(jmc, callNode, ret);
        } else {
            mergeActualOut(callNode);
            curNode.addUseVariable(callNode.getDefVariables().get(0));
        }
        
        return false;
    }
    
    /**
     * Visits a super-constructor invocation node and stores its information.
     * @param node the super-constructor invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(SuperConstructorInvocation node) {
        IMethodBinding binding = node.resolveConstructorBinding();
        if (binding == null) {
            return false;
        }
        
        JavaMethodCall jmc = new JavaMethodCall(node, binding, JavaElement.getDeclaringJavaMethod(node));
        CFGMethodCall callNode = new CFGMethodCall(jmc, GraphNodeSort.constructorCall);
        
        boolean createActual = (jmc.getJavaMethod() != null && createActualNodes && callNode.callMethodInProject() && !callNode.callSelf());
        
        if (createActual) {
            createActualIns(jmc, callNode, node.arguments());
        } else {
            mergeActualIn(callNode, node.arguments());
        }
        
        insertBeforeCurrentNode(callNode);
        
        if (createActual) {
            createActualOuts(jmc, callNode, node.arguments());
            JavaLocal ret = new JavaLocal(node);
            createActualOutForReturnValue(jmc, callNode, ret);
        } else {
            mergeActualOut(callNode);
            curNode.addUseVariable(callNode.getDefVariables().get(0));
        }
        
        return false;
    }
    
    /**
     * Visits a method invocation node and stores its information.
     * @param node the method invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean visit(ClassInstanceCreation node) {
        IMethodBinding binding = node.resolveConstructorBinding();
        if (binding == null) {
            return false;
        }
        
        JavaMethodCall jmc = new JavaMethodCall(node, binding, JavaElement.getDeclaringJavaMethod(node));
        CFGMethodCall callNode = new CFGMethodCall(jmc, GraphNodeSort.instanceCreation);
        
        boolean createActual = (jmc.getJavaMethod() != null && createActualNodes && callNode.callMethodInProject() && !callNode.callSelf());
        
        if (createActual) {
            createActualIns(jmc, callNode, node.arguments());
        } else {
            mergeActualIn(callNode, node.arguments());
        }
        
        insertBeforeCurrentNode(callNode);
        
        if (createActual) {
            createActualOuts(jmc, callNode, node.arguments());
            JavaLocal ret = new JavaLocal(node);
            createActualOutForReturnValue(jmc, callNode, ret);
        } else {
            mergeActualOut(callNode);
            curNode.addUseVariable(callNode.getDefVariables().get(0));
        }
        
        Expression primary = node.getExpression();
        if (primary != null) {
            analysingDefinedVariables.push(false);
            primary.accept(this);
            analysingDefinedVariables.pop();
        }
        
        return false;
    }
    
    /**
     * Creates a CFG node for actual-in parameters.
     * @param jmc the calling method 
     * @param callNode the CFG node for the method call 
     * @param arguments the arguments of the method call
     */
    private void createActualIns(JavaMethodCall jmc, CFGMethodCall callNode, List<Expression> arguments) {
        int ordinal = 0;
        for (Expression argument : arguments) {
            createActualIn(jmc, callNode, argument, ordinal);
            ordinal++;
        }
    }
    
    /**
     * Creates a CFG node for an actual-in parameter.
     * @param jmc the calling method
     * @param callNode the CFG node for the method call 
     * @param argument the argument in the calling method
     * @param ordinal the ordinal number indicating where a specified parameter is located in a parameter list containing it
     */
    private void createActualIn(JavaMethodCall jmc, CFGMethodCall callNode, Expression argument, int ordinal) {
        JavaLocal jl = new JavaLocal(argument);
        CFGParameter ainNode = new CFGParameter(jl, GraphNodeSort.actualIn, ordinal);
        ainNode.setBelongNode(callNode);
        callNode.addActualIn(ainNode);
        
        CFGMethodEntry methodNode = (CFGMethodEntry)cfg.getStartNode();
        JavaMethod jm = methodNode.getJavaMethod();
        JavaVariableAccess jvin = new JavaSpecialVariable("$" + String.valueOf(paramNumber), jmc.getArgumentType(ordinal), jm);
        ainNode.addDefVariable(jvin);
        paramNumber++;
        
        insertBeforeCurrentNode(ainNode);
        
        CFGStatement tmpNode = curNode;
        curNode = ainNode;
        analysingDefinedVariables.push(false);
        argument.accept(this);
        analysingDefinedVariables.pop();
        curNode = tmpNode;
    }
    
    /**
     * Creates a CFG node for actual-out parameters.
     * @param jmc the calling method 
     * @param callNode the CFG node for the method call 
     * @param arguments the arguments of the method call
     */
    private void createActualOuts(JavaMethodCall jmc, CFGMethodCall callNode, List<Expression> arguments) {
        for (int ordinal = 0; ordinal < arguments.size(); ordinal++) {
            CFGParameter ain = callNode.getActualIn(ordinal);
            
            if (ain.getDefVariables().size() == 1) {
                JavaVariableAccess jacc = ain.getUseVariable();
                if (!jacc.isPrimitiveType()) {
                    createActualOut(jmc, callNode, ain);
                }
            }
        }
    }
    
    /**
     * Creates a CFG node for an actual-out parameter.
     * @param jmc the calling method
     * @param callNode the CFG node for the method call 
     * @param ain the actual-in parameter corresponding to an actual-out parameter to be created
     */
    private void createActualOut(JavaMethodCall jmc, CFGMethodCall callNode, CFGParameter ain) {
        JavaLocal jl = (JavaLocal)ain.getJavaElement();
        CFGParameter aoutNode = new CFGParameter(jl, GraphNodeSort.actualOut, ain.getOrdinal());
        aoutNode.setBelongNode(callNode);
        callNode.addActualOut(aoutNode);
        
        aoutNode.addDefVariable(ain.getUseVariable());
        aoutNode.addUseVariable(ain.getDefVariable());
        
        insertBeforeCurrentNode(aoutNode);
    }
    
    /**
     * Creates a CFG node for an actual-out parameter.
     * @param jmc the calling method
     * @param callNode the CFG node for the method call
     * @param jl the virtual local variable storing the return value of the method call
     */
    private void createActualOutForReturnValue(JavaMethodCall jmc, CFGMethodCall callNode, JavaLocal jl) {
        if (jmc.isVoid()) {
            return;
        }
        
        CFGParameter aoutNode = new CFGParameter(jl, GraphNodeSort.actualOut, 0);
        aoutNode.setBelongNode(callNode);
        callNode.addActualOut(aoutNode);
        
        CFGMethodEntry methodNode = (CFGMethodEntry)cfg.getStartNode();
        JavaMethod jm = methodNode.getJavaMethod();
        JavaVariableAccess jvin = new JavaSpecialVariable("$" + String.valueOf(paramNumber), jmc.getReturnType(), jm);
        aoutNode.addDefVariable(jvin);
        paramNumber++;
        
        JavaVariableAccess jvout = new JavaSpecialVariable("$" + String.valueOf(paramNumber) + "!" + jmc.getName(), jmc.getReturnType(), jm);
        aoutNode.addUseVariable(jvout);
        paramNumber++;
        
        insertBeforeCurrentNode(aoutNode);
        
        curNode.addUseVariable(aoutNode.getDefVariable());
    }
    
    /**
     * Merges information on actual-in parameters into the method call node.
     * @param callNode the CFG node for the method call
     * @param arguments the arguments of the method call
     */
    private void mergeActualIn(CFGMethodCall callNode, List<Expression> arguments) {
        for (Expression argument : arguments) {
            analysingDefinedVariables.push(false);
            argument.accept(this);
            analysingDefinedVariables.pop();
            
            List<JavaVariableAccess> uses = new ArrayList<JavaVariableAccess>(curNode.getUseVariables());
            for (JavaVariableAccess jv : uses) {
                callNode.addUseVariable(jv);
                curNode.removeUseVariable(jv);
            }
        }
    }
    
    /**
     * Merges information on an actual-out parameter into the method call node.
     * @param callNode the CFG node for the method call
     */
    private void mergeActualOut(CFGMethodCall callNode) {
        CFGEntry entry = cfg.getStartNode();
        if (entry.isMethodEntry()) {
            CFGMethodEntry mentry = (CFGMethodEntry)entry;
            JavaMethod jm = mentry.getJavaMethod();
            JavaVariableAccess jvout = new JavaSpecialVariable("$" + String.valueOf(paramNumber) + "!" + callNode.getName(), callNode.getReturnType(), jm);
            callNode.addDefVariable(jvout);
            paramNumber++;
            
        } else if (entry.isFieldEntry()) {
            CFGFieldEntry fentry = (CFGFieldEntry)entry;
            JavaField jf = fentry.getJavaField();
            JavaVariableAccess jvout = new JavaSpecialVariable("$" + String.valueOf(paramNumber) + "!" + callNode.getName(), callNode.getReturnType(), jf);
            callNode.addDefVariable(jvout);
            paramNumber++;
        }
    }
    
    /**
     * Visits a name node and stores its information.
     * @param node the name node representing the variable access
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(Name node) {
        return true;
    }
    
    /**
     * Visits a name node and stores its information.
     * @param node the name node representing the variable access
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SimpleName node) {
        registJavaVariable(node);
        
        return false;
    }
    
    /**
     * Visits a name node and stores its information.
     * @param node the name node representing the variable access
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(QualifiedName node) {
        registJavaVariable(node);
        
        return false;
    }
    
    /**
     * Registers the information on the field access or the local variable access.
     * @param node node the name node representing the variable access
     */
    private void registJavaVariable(Name node) {
        if (node.resolveBinding() != null) {
            if (isField(node) || isLocal(node)) {
                JavaVariableAccess jacc = new JavaVariableAccess(node, JavaElement.getDeclaringJavaMethod(node));
                registJavaVariable(jacc);
            }
        }
    }
    
    /**
     * Registers a field or a local variable in the current CFG node.
     * @param jv the variable to be registered
     */
    private void registJavaVariable(JavaVariableAccess jv) {
        if (analysingDefinedVariables.peek()) {
            addDefVariable(curNode, jv);
        } else {
            addUseVariable(curNode, jv);
        }
    }
    
    /**
     * Stores a field or a local variable into the defined variable set of a CFG node.
     * @param node the CFG node that stores the defined variable  
     * @param jv the variable to be registered
     */
    private void addDefVariable(CFGNode node, JavaVariableAccess jv) {
        if (node.isStatement()) {
            curNode.addDefVariable(jv);
        } else {
            logger.info("!!!! DEF VARIABLE ERROR");
        }
    }
    
    /**
     * Stores a field or a local variable into the used variable set of a CFG node.
     * @param node the CFG node that stores the used variable  
     * @param jv the variable to be registered
     */
    private void addUseVariable(CFGNode node, JavaVariableAccess jv) {
        if (curNode.isStatement()) {
            curNode.addUseVariable(jv);
        } else {
            logger.info("!!!! USE VARIABLE ERROR");
        }
    }
    
    /**
     * Tests if a given name represents a field.
     * @param node an AST node for the name
     * @return <code>true</code> if the name represents a field, otherwise <code>false</code>
     */
    public boolean isField(Name node) {
        IVariableBinding vbinding = getIVariableBinding(node);
        return vbinding != null && vbinding.isField() && !vbinding.isEnumConstant();
    }
    
    /**
     * Tests if a given name represents a local variable.
     * @param node an AST node for the name
     * @return <code>true</code> if the name represents a local variable, otherwise <code>false</code>
     */
    private boolean isLocal(Name node) {
        IVariableBinding vbinding = getIVariableBinding(node);
        return vbinding != null && !vbinding.isField();
    }
    
    /**
     * Returns the variable binding for a name.
     * @param node an AST node for the name
     * @return the variable binding for the name
     */
    private IVariableBinding getIVariableBinding(Name node) {
        IBinding bind = node.resolveBinding();
        if (bind != null && bind.getKind() == IBinding.VARIABLE) {
            return (IVariableBinding)bind;
        }
        return null;
    }
}

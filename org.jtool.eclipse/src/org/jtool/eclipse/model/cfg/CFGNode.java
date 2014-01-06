/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.graph.GraphNode;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaElement;
import org.jtool.eclipse.model.pdg.PDGNode;
import org.apache.log4j.Logger;

/**
 * A node of CFGs.
 * @author Katsuhisa Maruyama
 */
public class CFGNode extends GraphNode {
    
    protected static Logger logger = Logger.getLogger(CFGNode.class.getName());
    
    /**
     * A Java element corresponding to this node.
     */
    protected JavaElement jelem = null;
    
    /**
     * A basic block containing this CFG node.
     */
    private BasicBlock basicBlock = null;
    
    /**
     * The PDG corresponding to this CFG node.
     */
    private PDGNode pdgnode = null;
    
    /**
     * Creates a new, empty object.
     */
    public CFGNode() {
        super(false);
    }
    
    /**
     * Creates a new node when the corresponding Java element does not exist.
     * @param sort the sort of this node
     */
    public CFGNode(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node.
     * @param elem a Java element corresponding to this node
     * @param sort the sort of this node
     */
    public CFGNode(JavaElement elem, GraphNodeSort sort) {
        super(sort);
        jelem = elem;
    }
    
    /**
     * Sets a Java element corresponding to this node
     * @param elem the Java element corresponding to this node
     */
    public void setJavaElement(JavaElement elem) {
        jelem = elem;
    }
    
    /**
     * Returns the Java element corresponding to this node
     * @return the corresponding Java element
     */
    public JavaElement getJavaElement() {
        return jelem;
    }
    
    /**
     * Sets a basic block containing this node.
     * @param node the basic block
     */
    public void setBasicBlock(BasicBlock block) {
        basicBlock = block;
    }
    
    /**
     * Returns a PDG node containing this node.
     * @return the containing basic block
     */
    public BasicBlock getBasicBlock() {
        return basicBlock;
    }
    
    /**
     * Sets the PDG node corresponding to this CFG node.
     * @param node the corresponding PDG node
     */
    public void setPDGNode(PDGNode node) {
        pdgnode = node;
    }
    
    /**
     * Returns the PDG node corresponding to this CFG node.
     * @return the corresponding PDG node, or <code>null</code> if a PDG has not created yet
     */
    public PDGNode getPDGNode() {
        return pdgnode;
    }
    
    /**
     * Returns predecessors of this node.
     * @return the collection of the CFG predecessors
     */
    public GraphElementSet<CFGNode> getPredecessors() {
        return convertNodes(getSrcNodes());
    }
    
    /**
     * Returns successors of this node.
     * @return the collection of the CFG successors
     */
    public GraphElementSet<CFGNode> getSuccessors() {
        return convertNodes(getDstNodes());
    }
    
    /**
     * Returns the number of predecessors of this node.
     * @return the number of the predecessors
     */
    public int getNumOfPredecessors() {
        return getSrcNodes().size();
    }
    
    /**
     * Returns the number of successors of this node.
     * @return the number of the successors
     */
    public int getNumOfSuccessors() {
        return getDstNodes().size();
    }
    
    /**
     * Returns all the edges incoming to this node.
     * @return the collection of the incoming CFG flows
     */
    public GraphElementSet<ControlFlow> getIncomingFlows() {
        return convertEdges(getIncomingEdges());
    }
    
    /**
     * Returns all the edges outgoing from this node.
     * @return the collection of the outgoing CFG flows
     */
    public GraphElementSet<ControlFlow> getOutgoingFlows() {
        return convertEdges(getOutgoingEdges());
    }
    
    /**
     * Converts nodes of a general graph into nodes of a CFG.
     * @param nodes the collection of the graph nodes
     * @return the collection of the CFG nodes
     */
    private GraphElementSet<CFGNode> convertNodes(GraphElementSet<GraphNode> nodes) {
        GraphElementSet<CFGNode> set = new GraphElementSet<CFGNode>();
        for (GraphNode node : nodes) {
            set.add((CFGNode)node);
        }
        return set;
    }
    
    /**
     * Converts edges of a general graph into edges of a CFG. 
     * @param edges the collection of the graph edges
     * @return the collection of the CFG edges
     */
    private GraphElementSet<ControlFlow> convertEdges(GraphElementSet<GraphEdge> edges) {
        GraphElementSet<ControlFlow> set = new GraphElementSet<ControlFlow>();
        for (GraphEdge edge : edges) {
            set.add((ControlFlow)edge);
        }
        return set;
    }
    
    /**
     * Tests if this is a branch node which has multiple outgoing edges.
     * @return <code>true</code> if this is a branch node, otherwise <code>false</code>
     */
    public boolean isBranch() { 
        return getOutgoingEdges().size() > 1;
    }
    
    /**
     * Tests if this node is classified into the loop (<code>while</code>, <code>do</code>, and <code>for</code>).
     * @return <code>true</code> if this is a loop node, otherwise <code>false</code>
     */
    public boolean isLoop() {
        return sort == GraphNodeSort.whileSt || sort == GraphNodeSort.doSt || sort == GraphNodeSort.forSt;
    }
    
    /**
     * Tests if this is a join node which has multiple incoming edges.
     * @return <code>true</code> if this is a join node, otherwise <code>false</code>
     */
    public boolean isJoin() {
        return getIncomingEdges().size() > 1;
    }
   
    /**
     * Tests if this node represents the entry of a class, an interface, a method, a constructor, or an advice.
     * @return <code>true</code> if this node represents an entry, otherwise <code>false</code>.
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isEntry() {
        return sort == GraphNodeSort.classEntry ||
               sort == GraphNodeSort.interfaceEntry ||
               sort == GraphNodeSort.enumEntry ||
               sort == GraphNodeSort.methodEntry ||
               sort == GraphNodeSort.constructorEntry ||
               sort == GraphNodeSort.fieldEntry ||
               sort == GraphNodeSort.initializerEntry ||
               sort == GraphNodeSort.enumConstantEntry;
    }
    
    /**
     * Tests if this represents an entry node for a class or an interface.
     * @return <code>true</code> if this node represents a class or interface entry, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isClassEntry() {
        return sort == GraphNodeSort.classEntry || sort == GraphNodeSort.interfaceEntry;
    }
    
    /**
     * Tests if this represents an entry node for an enum.
     * @return <code>true</code> if this node represents an enum entry, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isEnumEntry() {
        return sort == GraphNodeSort.enumEntry;
    }
    
    /**
     * Tests if this represents an entry node for a method or a constructor.
     * @return <code>true</code> if this node represents a method or constructor entry, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isMethodEntry() {
        return sort == GraphNodeSort.methodEntry || sort == GraphNodeSort.constructorEntry;
    }
    
    /**
     * Tests if this represents an entry node for an initializer.
     * @return <code>true</code> if this node represents an initializer entry, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isInitializerEntry() {
        return sort == GraphNodeSort.initializerEntry;
    }
    
    /**
     * Tests if this represents an entry node for a field.
     * @return <code>true</code> if this node represents a field entry, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isFieldEntry() {
        return sort == GraphNodeSort.fieldEntry;
    }
    
    /**
     * Tests if this represents an entry node for an enum constant.
     * @return <code>true</code> if this node represents an enum constant entry, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isEnumConstantEntry() {
        return sort == GraphNodeSort.enumConstantEntry;
    }
    
    /**
     * Tests if this represents an exit node for a class, an interface, a method, a constructor, or an advice.
     * @return <code>true</code> if this node corresponds to an exit statement, otherwise <code>false</code>.
     * @see org.jtool.jxplatform.graph.GraphNodeSort
     */
    public boolean isExit() {
        return sort == GraphNodeSort.classExit ||
               sort == GraphNodeSort.interfaceExit ||
               sort == GraphNodeSort.enumExit ||
               sort == GraphNodeSort.methodExit ||
               sort == GraphNodeSort.constructorExit ||
               sort == GraphNodeSort.fieldExit ||
               sort == GraphNodeSort.initializerExit ||
               sort == GraphNodeSort.enumConstantExit;
    }
    
    /**
     * Tests if this node corresponds to an assignment expression.
     * @return <code>true</code> if this node represents an assignment expression, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isAssignment() {
        return sort == GraphNodeSort.assignment;
    }
    
    /**
     * Tests if this node requires a method call.
     * @return <code>true</code> if this node represents a method call, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isMethodCall() {
        return sort == GraphNodeSort.methodCall ||
               sort == GraphNodeSort.constructorCall ||
               sort == GraphNodeSort.instanceCreation;
    }
    
    /**
     * Tests if this node corresponds to a field declaration.
     * @return <code>true</code> if this node represents the declaration, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isFieldDeclaration() {
        return sort == GraphNodeSort.fieldDeclaration;
    }
    
    /**
     * Tests if this node corresponds to a local variable declaration.
     * @return <code>true</code> if this node represents the declaration, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isLocalDeclaration() {
        return sort == GraphNodeSort.localDeclaration;
    }
    
    /**
     * Tests if this node represents an <code>assert</code> statement.
     * @return <code>true</code> if this node represents an <code>assert</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isAssert() {
        return sort == GraphNodeSort.assertSt;
    }
    
    /**
     * Tests if this node represents a <code>break</code> statement.
     * @return <code>true</code> if this node represents a <code>break</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isBreak() {
        return sort == GraphNodeSort.breakSt;
    }
    
    /**
     * Tests if this node represents a <code>continue</code> statement.
     * @return <code>true</code> if this node represents a <code>continue</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isContinue() {
        return sort == GraphNodeSort.continueSt;
    }
    
    /**
     * Tests if this node represents a <code>do</code> statement.
     * @return <code>true</code> if this node represents a <code>do</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isDo() {
        return sort == GraphNodeSort.doSt;
    }
    
    /**
     * Tests if this node represents a <code>for</code> statement.
     * @return <code>true</code> if this node represents a <code>for</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isFor() {
        return sort == GraphNodeSort.forSt;
    }
    
    /**
     * Tests if this node represents a<code>if</code> statement.
     * @return <code>true</code> if this node represents a <code>if</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isIf() {
        return sort == GraphNodeSort.ifSt;
    }
    
    /**
     * Tests if this node represents a <code>return</code> statement.
     * @return <code>true</code> if this node represents a <code>return</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isReturn() {
        return sort == GraphNodeSort.returnSt;
    }
    
    /**
     * Tests if this node represents a switch-case label.
     * @return <code>true</code> if this node represents a switch-case label, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isSwitchCase() {
        return sort == GraphNodeSort.switchCaseSt;
    }
    
    /**
     * Tests if this node represents a switch-default label.
     * @return <code>true</code> if this node represents a switch-default label, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isSwitchDefault() {
        return sort == GraphNodeSort.switchDefaultSt;
    }
    
    /**
     * Tests if this node represents the <code>while</code> statement.
     * @return <code>true</code> if this node represents the <code>while</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isWhile() {
        return sort == GraphNodeSort.whileSt;
    }
    
    /**
     * Tests if this node represents a labeled statement.
     * @return <code>true</code> if this node represents a labeled statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isLabel() {
        return sort == GraphNodeSort.labelSt;
    }
    
    /**
     * Tests if this node represents a <code>switch</code> statement.
     * @return <code>true</code> if this node represents a <code>switch</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isSwitch() {
        return sort == GraphNodeSort.switchSt;
    }
    
    /**
     * Tests if this node represents a <code>synchronized</code> statement.
     * @return <code>true</code> if this node represents a <code>synchronized</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isSynchronized() {
        return sort == GraphNodeSort.throwSt;
    }
    
    /**
     * Tests if this node represents a <code>throw</code> statement.
     * @return <code>true</code> if this node represents a <code>throw</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isThrow() {
        return sort == GraphNodeSort.throwSt;
    }
    
    /**
     * Tests if this node represents a <code>try</code> statement.
     * @return <code>true</code> if this node represents a <code>try</code> statement, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isTry() {
        return sort == GraphNodeSort.trySt;
    }
    
    /**
     * Tests if this node represents a <code>catch</code> clause.
     * @return <code>true</code> if this node represents a <code>catch</code> clause, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isCatch() {
        return sort == GraphNodeSort.catchSt;       
    }
    
    /**
     * Tests if this node represents a <code>finally</code> block.
     * @return <code>true</code> if this node represents a <code>finally</code> block, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isFinally() {
        return sort == GraphNodeSort.finallySt;       
    }
    
    /**
     * Tests if this node corresponds to a parameter or an argument.
     * @return <code>true</code> if this node represents a parameter or an argument, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isParameter() {
        return sort == GraphNodeSort.formalIn ||
               sort == GraphNodeSort.formalOut ||
               sort == GraphNodeSort.actualIn ||
               sort == GraphNodeSort.actualOut;
    }
    
    /**
     * Tests if this node corresponds to a formal parameter.
     * @return <code>true</code> if this node represents a formal parameter, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isFormal() {
        return isFormalIn() || isFormalOut();
    }
    
    /**
     * Tests if this node corresponds to a formal-in parameter.
     * @return <code>true</code> if this node represents a formal-in parameter, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isFormalIn() {
        return sort == GraphNodeSort.formalIn;
    }
    
    /**
     * Tests if this node corresponds to a formal-out parameter.
     * @return <code>true</code> if this node represents a formal-out parameter, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isFormalOut() {
        return sort == GraphNodeSort.formalOut;
    }
    
    /**
     * Tests if this node corresponds to an actual argument.
     * @return <code>true</code> if this node represents an actual argument, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isActual() {
        return isActualIn() || isActualOut();
    }
    
    /**
     * Tests if this node corresponds to an actual-in argument.
     * @return <code>true</code> if this node represents an actual-in argument, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isActualIn() {
        return sort == GraphNodeSort.actualIn;
    }
    
    /**
     * Tests if this node corresponds to an actual-out argument.
     * @return <code>true</code> if this node represents an actual-out argument, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isActualOut() {
        return sort == GraphNodeSort.actualOut;
    }
    
    /**
     * Tests if this node belongs to the group of normal statements.
     * @return <code>true</code> if this node represents a normal statement, otherwise <code>false</code>
     */
    public boolean isNormalStatement() {
        return this instanceof CFGStatement ||
               this instanceof CFGMethodInvocation;
    }
    
    /**
     * Tests if this node belongs to the group of normal statements.
     * @return <code>true</code> if this node represents a normal statement, otherwise <code>false</code>
     */
    public boolean isStatement() {
        return this instanceof CFGStatement ||
               this instanceof CFGMethodInvocation ||
               this instanceof CFGParameter;
    }
    
    /**
     * Tests if this represents a merge node.
     * @return <code>true</code> if this node represents the merge, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isMerge() {
        return sort == GraphNodeSort.merge;
    }
    
    /**
     * Tests if this represents a dummy node.
     * @return <code>true</code> if this node represents the dummy, otherwise <code>false</code>
     * @see org.jtool.eclipse.model.graph.GraphNodeSort
     */
    public boolean isDummy() {
        return sort == GraphNodeSort.dummy;
    }
    
    /**
     * Checks if this node is next to a branch node. 
     * @return <code>true</code> if this is the next node, otherwise <code>false</code>
     */
    public boolean isNextToBranch() {
        for (CFGNode node : getPredecessors()) {
            if (node.isBranch()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Tests if the defined variables contains any variable
     * @return always <code>false</code>
     */
    public boolean hasDefVariable() {
        return false;
    }
    
    /**
     * Tests if the used variables contains any variable
     * @return always <code>false</code>
     */
    public boolean hasUseVariable() {
        return false;
    }
    
    /**
     * Checks if this node is a leader.
     * @return <code>true</code> if this is a leader node, otherwise <code>false</code>
     */
    public boolean isLeader() {
        return basicBlock != null && equals(basicBlock.getLeader());
    }
    
    /**
     * Tests if this node equals to a given node.
     * @param node the node to be checked
     * @return <code>true</code> if the nodes are equal, otherwise <code>false</code>
     */
    public boolean equals(CFGNode node) {
        return this == node || getId() == node.getId();
    }
    
    /**
     * Returns a hash code value for this node.
     * @return the hash code value for the node
     */
    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGNode clone() {
        CFGNode cloneNode = new CFGNode(getJavaElement(), getSort());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGNode cloneNode) {
        super.clone(cloneNode);
        cloneNode.setBasicBlock(getBasicBlock());
    }
    
    /**
     * Displays information about this node.
     */
    public void print() {
        logger.info(toString());
    }
    
    /**
     * Collects information about this node.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        
        if (jelem != null) {
            buf.append(jelem.toString());
        }
        
        return buf.toString();
    }
}

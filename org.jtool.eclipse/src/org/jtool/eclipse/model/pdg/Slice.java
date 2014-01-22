/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.apache.log4j.Logger;

/**
 * An object storing information about program slice.
 * @author Katsuhisa Maruyama
 */
public class Slice extends PDG {
    
    static Logger logger = Logger.getLogger(Slice.class.getName());
    
    /**
     * The criterion node for the construction of this slice.
     */
    private PDGStatement criterionNode;
    
    /**
     * The criterion variable for the construction of this slice.
     */
    private JavaVariableAccess criterionVariable;
    
    /**
     * Creates a new, empty object.
     */
    protected Slice() {
        super();
    }
    
    /**
     * Creates a new slice object.
     * @param node the criterion node for this slice
     * @param jv the criterion variable for this slice
     */
    public Slice(PDGStatement node, JavaVariableAccess jv) {
        super();
        
        criterionNode = node;
        criterionVariable = jv;
        
        create();
    }
    
    /**
     * Returns the criterion node for this slice.
     * @return the criterion node
     */
    public PDGStatement getCriterionNode() {
        return criterionNode;
    }
    
    /**
     * Returns the criterion variable for this slice.
     * @return the criterion variable
     */
    public JavaVariableAccess getCriterionVariable() {
        return criterionVariable;
    }
    
    /**
     * Creates a new slice.
     */
    private void create() {
        if (criterionNode.definesVariable(criterionVariable)) {
            traverseBackward(criterionNode);
            
        } else if (criterionNode.usesVariable(criterionVariable)) {
            add(criterionNode);
            
            for (PDGStatement defnode : findDefNode(criterionNode, criterionVariable)) {
                traverseBackward(defnode);
            }
        }
    }
    
    /**
     * Obtains the PDG nodes that define a given variable.
     * @param anchor the criterion node
     * @param jv the criterion variable
     * @return the collection of PDG nodes
     */
    private GraphElementSet<PDGStatement> findDefNode(PDGStatement anchor, JavaVariableAccess jv) {
        GraphElementSet<PDGStatement> defs = new GraphElementSet<PDGStatement>();
        
        for (DD edge : anchor.getIncomingDDEdges()) {
            if (jv.equals(edge.getVariable())) {
                PDGStatement node = (PDGStatement)edge.getSrcNode();
                defs.add(node);
            }
        }
        
        return defs;
    }
    
    /**
     * Backward traverses edges and nodes of the PDG, and collects them.
     * @param anchor the anchor node
     */
    private void traverseBackward(PDGStatement anchor) {
        add(anchor);
        
        for (Dependence edge : anchor.getIncomingDependeceEdges()) {
            add(edge);
            PDGStatement node = (PDGStatement)edge.getSrcNode();
            
            if (!getNodes().contains(node)) {
                traverseBackward(node);
            }
        }
    }
    
    /**
     * Collects information about this slice for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- Slice (from here) -----\n");
        buf.append("Node = " + criterionNode.getId() + "; Variable = " + criterionVariable.getName());
        buf.append("\n");
        buf.append(getNodeInfo()); 
        buf.append(getEdgeInfo());
        buf.append("----- Slice (to here) -----\n");
        
        return buf.toString();
    }
}

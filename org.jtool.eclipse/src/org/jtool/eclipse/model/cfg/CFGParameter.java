/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaLocal;
import org.jtool.eclipse.model.java.JavaVariableAccess;

/**
 * A node for a parameter of a method declaration.
 * @author Katsuhisa Maruyama
 */
public class CFGParameter extends CFGStatement {
    
    /**
     * The ordinal number indicating where this parameter is located in the parameter list.
     */
    private int ordinal;
    
    /**
     * A parent node which this node belongs to.
     */
    private CFGNode parent;
    
    /**
     * Creates a new, empty object.
     */
    protected CFGParameter() {
        super();
    }
    
    /**
     * Creates a new node when the corresponding Java element does not exist.
     * @param sort the sort of this node
     */
    protected CFGParameter(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java element.
     * @param jelem the Java statement
     * @param sort the sort of this node
     * @param num the ordinal number of this parameter
     */
    public CFGParameter(JavaLocal jelem, GraphNodeSort sort, int num) {
        super(jelem, sort);
        ordinal = num;
    }
    
    /**
     * Set the ordinal number of this parameter.
     * @param num the ordinal number of this parameter
     */
    public void setOrdinal(int num) {
        ordinal = num;
    }
    
    /**
     * Returns the ordinal number of this parameter.
     * @return the ordinal number of this parameter
     */
    public int getOrdinal() {
        return ordinal;
    }
    
    /**
     * Returns a variable defined in this parameter node.
     * @return the defined variable in this parameter node
     */
    public JavaVariableAccess getDefVariable() {
        return getDefVariables().get(0);
    }
    
    /**
     * Returns a variable used in this parameter node.
     * @return The used variable in this parameter node
     */
    public JavaVariableAccess getUseVariable() {
        return getUseVariables().get(0);
    }
    
    /**
     * Sets a parent node which this node belongs to.
     * @param n the parent node
     */
    public void setBelongNode(CFGNode n) {
        parent = n;
    }
    
    /**
     * Returns the parent node which this node belongs to.
     * @return the parent node
     */
    public CFGNode getBelongNode() {
        return parent;
    }
    
    /**
     * Returns the information of this parameter.
     * @return the information of this parameter
     */
    public JavaLocal getJavaExpression() {
        return (JavaLocal)getJavaElement();
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGParameter clone() {
        CFGParameter cloneNode = new CFGParameter(getJavaExpression(), getSort(), ordinal);
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGParameter cloneNode) {
        super.clone(cloneNode);
        cloneNode.setOrdinal(getOrdinal());
        cloneNode.setBelongNode(getBelongNode());
    }
    
    /**
     * Collects information about this node.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + getId() + "] ");
        switch (sort) {
            case formalIn: buf.append("formal_in"); break;
            case formalOut: buf.append("formal_out"); break;
            case actualIn: buf.append("actual_in"); break;
            case actualOut: buf.append("actual_out"); break;
            default: break;
        }
        buf.append(": ");
        buf.append("{ " + toStringDefVariables() + " }");
        buf.append(" = ");
        buf.append("{ " + toStringUseVariables() + " }");
        return buf.toString();
    }
}

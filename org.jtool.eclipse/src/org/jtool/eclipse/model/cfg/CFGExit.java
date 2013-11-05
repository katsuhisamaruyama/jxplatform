/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphNodeSort;

/**
 * The exit node of CFGs.
 * @author Katsuhisa Maruyama
 */
public class CFGExit extends CFGNode {
    
    /**
     * Creates a new, empty object.
     */
    protected CFGExit() {
        super();
    }
    
    /**
     * Creates a new node. This node does not correspond to any Java element.
     * @param sort The sort of this node.
     */
    public CFGExit(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGExit clone() {
        CFGExit cloneNode = new CFGExit(getSort());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGExit cloneNode) {
        super.clone(cloneNode);
    }
    
    /**
     * Collects information about this node.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + getId() + "] ");
        switch (sort) {
            case classExit: buf.append("class exit"); break;
            case interfaceExit: buf.append("interface exit"); break;
            case enumExit: buf.append("enum exit"); break;
            case methodExit: buf.append("method exit"); break;
            case constructorExit: buf.append("constructor exit"); break;
            case initializerExit: buf.append("initializer exit"); break;
            case fieldExit: buf.append("field exit"); break;
            case enumConstantExit: buf.append("enum constant exit"); break;
            default: break;
        }
        return buf.toString();
    }
}

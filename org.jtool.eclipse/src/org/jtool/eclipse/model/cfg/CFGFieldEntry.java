/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaField;

/**
 * The entry node of a CFG for a field declaration, or an enum constant.
 * @author Katsuhisa Maruyama
 */
public class CFGFieldEntry extends CFGEntry {
    
    /**
     * Creates a new, empty object.
     */
    protected CFGFieldEntry() {
        super();
    }
    
    /**
     * Creates a new node. This node does not correspond to any Java element.
     * @param sort the sort of this node
     */
    protected CFGFieldEntry(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java element.
     * @param jm the Java field starting from this entry node
     * @param sort the sort of this node
     */
    public CFGFieldEntry(JavaField jf, GraphNodeSort sort) {
        super(jf, sort);
    }
    
    /**
     * Returns the information of this field.
     * @return the information of this field
     */
    public JavaField getJavaField() {
        return (JavaField)getJavaElement();
    }
    
    
    /**
     * Returns the qualified name of of this field.
     * @return the qualified name of this field
     */
    public String getName() {
        return getJavaField().getQualifiedName();
    }
    
    /**
     * Returns the fully-qualified name of the type of this field.
     * @return the fully-qualified name string
     */
    public String getType() {
        return getJavaField().getType();
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGFieldEntry clone() {
        CFGFieldEntry cloneNode = new CFGFieldEntry(getJavaField(), getSort());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGFieldEntry cloneNode) {
        super.clone(cloneNode);
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
        return super.toString();
    }
}

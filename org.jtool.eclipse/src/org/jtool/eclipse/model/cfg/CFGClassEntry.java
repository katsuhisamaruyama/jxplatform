/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.graph.GraphNodeSort;

/**
 * The entry node of a CFG for a class or an interface.
 * @author Katsuhisa Maruyama
 */
public class CFGClassEntry extends CFGEntry {
    
    /**
     * Creates a new, empty object.
     */
    protected CFGClassEntry() {
        super();
    }
    
    /**
     * Creates a new node. This node does not correspond to any Java element.
     * @param sort the sort of this node
     */
    protected CFGClassEntry(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java element.
     * @param jc the Java class starting from this entry node
     * @param sort the sort of this node
     */
    public CFGClassEntry(JavaClass jc, GraphNodeSort sort) {
        super(jc, sort);
    }
    
    /**
     * Returns the information of this class.
     * @return the information of this class
     */
    public JavaClass getJavaClass() {
        return (JavaClass)getJavaElement();
    }
    
    /**
     * Returns the qualified name of this class.
     * @return the qualified name of this class
     */
    public String getName() {
        return getJavaClass().getQualifiedName();
    }
    
    /**
     * Returns the fully-qualified name of type of the return value of this method.
     * @return the fully-qualified name string
     */
    public String getType() {
        return getJavaClass().getQualifiedName();
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGClassEntry clone() {
        CFGClassEntry cloneNode = new CFGClassEntry(getJavaClass(), getSort());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGMethodEntry cloneNode) {
        super.clone(cloneNode);
    }
}

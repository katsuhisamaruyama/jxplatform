/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaMethod;
import java.util.ArrayList;
import java.util.List;

/**
 * The entry node of a CFG for a method or a constructor.
 * @author Katsuhisa Maruyama
 */
public class CFGMethodEntry extends CFGEntry {
    
     /**
     * The collection of formal-in nodes of this method.
     */
    private List<CFGParameter> formalIns = new ArrayList<CFGParameter>();
    
    /**
     * The collection of formal-out nodes of this method.
     */
    private List<CFGParameter> formalOuts = new ArrayList<CFGParameter>();
    
    /**
     * Creates a new, empty object.
     */
    protected CFGMethodEntry() {
        super();
    }
    
    /**
     * Creates a new node. This node does not correspond to any Java element.
     * @param sort the sort of this node
     */
    protected CFGMethodEntry(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java element.
     * @param jm the Java method starting from this entry node
     * @param sort the sort of this node
     */
    public CFGMethodEntry(JavaMethod jm, GraphNodeSort sort) {
        super(jm, sort);
    }
    
    /**
     * Returns the information of this method.
     * @return the information of this method
     */
    public JavaMethod getJavaMethod() {
        return (JavaMethod)getJavaElement();
    }
    
    /**
     * Returns the qualified name of this method.
     * @return the qualified name of this method
     */
    public String getName() {
        return getJavaMethod().getQualifiedName();
    }
    
    /**
     * Returns the fully-qualified name of type of the return value of this method.
     * @return the fully-qualified name string
     */
    public String getType() {
        return getJavaMethod().getReturnType();
    }
    
    /**
     * Tests if this method has no return value. 
     * @return <code>true</code> if there is no return value of this method, otherwise <code>false</code>
     */
    public boolean isVoid() {
        return getJavaMethod().isVoid();
    }
    
    /**
     * Adds an formal-in node as a member of the parameter of this method.
     * @param node the formal-in node
     */
    public void addFormalIn(CFGParameter node) {
        formalIns.add(node);
    }
    
    /**
     * Adds an formal-out node as a member of the parameter of this method.
     * @param node the formal-out node
     */
    public void addFormalOut(CFGParameter node) {
        formalOuts.add(node);
    }
    
    /**
     * Sets formal-in nodes of this method.
     * @param params the collection of the formal-in nodes
     */
    public void setFormalIns(List<CFGParameter> params) {
        for (CFGParameter param : params) {
            addFormalIn(param);
        }
    }
    
    /**
     * Sets formal-out nodes of this method.
     * @param params the collection of the formal-out nodes
     */
    public void setFormalOuts(List<CFGParameter> params) {
        for (CFGParameter param : params) {
            addFormalOut(param);
        }
    }
    
    /**
     * Returns all the formal-in nodes of this method.
     * @return the collection of the formal-in nodes
     */
    public List<CFGParameter> getFormalIns() {
        return formalIns;
    }
    
    /**
     * Returns all the formal-out nodes of this method.
     * @return the collection of the formal-out nodes
     */
    public List<CFGParameter> getFormalOuts() {
        return formalOuts;
    }
    
    /**
     * Returns the number of the parameters of this method.
     * @return the number of the parameters
     */
    public int getParameterSize() {
        return formalIns.size();
    }
    
    /**
     * Returns the formal-in node of this method at a specified position.
     * @param pos the ordinal number of the formal-in node to be retrieved
     * @return the found formal-in node, <code>null</code> if no parameter was found
     */
    public CFGParameter getFormalIn(int pos) {
        return formalIns.get(pos);
    }
    
    /**
     * Returns the formal-out node of this method at a specified position.
     * @param pos the ordinal number of the formal-out node to be retrieved
     * @return the found formal-out node, <code>null</code> if no parameter was found
     */
    public CFGParameter getFormalOut(int pos) {
        return formalOuts.get(pos);
    }
    
    /**
     * Tests if this method has a parameter. 
     * @return <code>true</code> if this method has a parameter, otherwise <code>false</code>
     */
    public boolean hasParameters() {
        return formalIns.size() != 0;
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGMethodEntry clone() {
        CFGMethodEntry cloneNode = new CFGMethodEntry(getJavaMethod(), getSort());
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

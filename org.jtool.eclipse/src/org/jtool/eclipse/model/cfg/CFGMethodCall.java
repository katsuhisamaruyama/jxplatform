/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaMethodInvocation;
import org.jtool.eclipse.model.java.JavaVariableAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * A node that represents a method call node within CFGs.
 * @author Katsuhisa Maruyama
 */
public class CFGMethodCall extends CFGStatement {
    
    /**
     * The collection of actual-in nodes of this method call.
     */
    private List<CFGParameter> actualIns = new ArrayList<CFGParameter>();
    
    /**
     * The collection of actual-out nodes of this method call.
     */
    private List<CFGParameter> actualOuts = new ArrayList<CFGParameter>();
    
    /**
     * The primary variable for this method call.
     */
    private JavaVariableAccess primary = null;
    
    /**
     * Creates a new, empty object.
     */
    protected CFGMethodCall() {
        super();
    }
    
    /**
     * Creates a new node when the corresponding Java element does not exist.
     * @param sort the sort of this node
     */
    protected CFGMethodCall(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java element.
     * @param jmc the Java statement for the method call
     * @param sort the sort of this node
     */
    public CFGMethodCall(JavaMethodInvocation jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Returns the name of the called method.
     * @return the name of the called method
     */
    public String getName() {
        return getJavaMethodInvocation().getName();
    }
    
    /**
     * Returns the return type of the called method.
     * @return the return type of the called method
     */
    public String getType() {
        return getJavaMethodInvocation().getType();
    }
    
    /**
     * Adds an actual-in node as a member of the parameter of the called method.
     * @param node the actual-in node
     */
    public void addActualIn(CFGParameter node) {
        actualIns.add(node);
    }
    
    /**
     * Adds an actual-out node as a member of the parameter of the called method.
     * @param node the actual-out node
     */
    public void addActualOut(CFGParameter node) {
        actualOuts.add(node);
    }
    
    /**
     * Sets actual-in nodes of the called method.
     * @param params the collection of the actual-in nodes
     */
    void setActualIns(List<CFGParameter> params) {
        for (CFGParameter param : params) {
            addActualIn(param);
        }
    }
    
    /**
     * Sets actual-out nodes of the called method.
     * @param params the collection of the actual-out nodes
     */
    void setActualOuts(List<CFGParameter> params) {
        for (CFGParameter param : params) {
            addActualOut(param);
        }
    }
    
    /**
     * Returns all the actual-in nodes of the called method.
     * @return the collection of the actual-in nodes
     */
    public List<CFGParameter> getActualIns() {
        return actualIns;
    }
    
    /**
     * Returns all the actual-out nodes of the called method.
     * @return the collection of the actual-out nodes
     */
    public List<CFGParameter> getActualOuts() {
        return actualOuts;
    }
    
    /**
     * Returns the number of the parameters of the called method.
     * @return the number of the parameters
     */
    public int getParameterSize() {
        return actualIns.size();
    }
    
    /**
     * Returns the actual-in node of the called method at a specified position.
     * @param pos the ordinal number of the actual-in node to be retrieved
     * @return the found actual-in node, <code>null</code> if no parameter was found
     */
    public CFGParameter getActualIn(int pos) {
        return actualIns.get(pos);
    }
    
    /**
     * Returns the actual-out node of the called method at a specified position.
     * @param pos the ordinal number of the actual-out node to be retrieved
     * @return the found actual-out node, <code>null</code> if no parameter was found
     */
    public CFGParameter getActualOut(int pos) {
        return actualOuts.get(pos);
    }
    
    /**
     * Tests if the called method has a parameter. 
     * @return <code>true</code> if this method has a parameter, otherwise <code>false</code>
     */
    public boolean hasParameters() {
        return actualIns.size() != 0;
    }
    
    /**
     * Tests if the called method has a return value. 
     * @return <code>true</code> if this method has a return value, otherwise <code>false</code>
     */
    public boolean hasReturnValue() {
        return actualOuts.size() != 0;
    }
    
    /**
     * Sets the primary variable for this method call.
     * @param name the Java variable
     */
    public void setPrimarye(JavaVariableAccess jv) {
        primary = jv;
    }
    
    /**
     * Returns the primary variable for this method call.
     * @return the Java variable, or <code>null</code> if there is no primary variable
     */
    public JavaVariableAccess getPrimary() {
        return primary;
    }
    
    /**
     * Returns the fully-qualified type of the primary variable for this method call.
     * @return the fully-qualified type of the primary variable, or an empty string if there is no primary variable
     */
    public String getPrimaryType() {
        if (primary != null) {
            primary.getType();
        }
        return "";
    }
    
    /**
     * Tests if there is a primary variable for this method call.
     * @return <code>true</code> if this method call has a primary variable, otherwise <code>false</code>
     */
    public boolean hasPrimary() {
        return primary != null;
    }
    
    /**
     * Returns the information of this method call.
     * @return the information of this method call
     */
    public JavaMethodInvocation getJavaMethodInvocation() {
        return (JavaMethodInvocation)getJavaElement();
    }
    
    /**
     * Returns the CFG entry node corresponding to the called method.
     * @return the entry node for the called method
     */
    public CFGMethodEntry getCalledMethodEntry() {
        JavaMethod jm = getJavaMethodInvocation().getJavaMethod();
        CFG cfg = CFGFactory.create(jm);
        return (CFGMethodEntry)cfg.getStartNode();
    }
    
    /**
     * Tests if this method call directly invokes the method itself.
     * @return <code>true</code> if this method call directly invokes the method itself, otherwise <code>false</code>
     */
    public boolean callSelfDirectly() {
        return getJavaMethodInvocation().callSelfDirectly();
    }
    
    /**
     * Tests if this method call recursively invokes the method itself.
     * @return <code>true</code> if this method call recursively invokes the method itself, otherwise <code>false</code>
     */
    public boolean callSelfRecursively() {
        return getJavaMethodInvocation().callSelfRecursively();
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGMethodCall clone() {
        CFGMethodCall cloneNode = new CFGMethodCall(getJavaMethodInvocation(), getSort());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGMethodCall cloneNode) {
        super.clone(cloneNode);
        cloneNode.setActualIns(getActualIns());
        cloneNode.setActualOuts(getActualOuts());
        cloneNode.setPrimarye(getPrimary());
    }
}

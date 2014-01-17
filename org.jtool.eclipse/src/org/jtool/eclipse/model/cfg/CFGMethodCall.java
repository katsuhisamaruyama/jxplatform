/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaMethodCall;
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
    public CFGMethodCall(JavaMethodCall jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Returns the name of the called method.
     * @return the name of the called method
     */
    public String getName() {
        return getJavaMethodCall().getName();
    }
    
    /**
     * Returns the return type of the called method.
     * @return the return type of the called method
     */
    public String getType() {
        return getJavaMethodCall().getType();
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
    public void setPrimary(JavaVariableAccess jv) {
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
    public JavaMethodCall getJavaMethodCall() {
        return (JavaMethodCall)getJavaElement();
    }
    
    /**
     * Returns the CFG entry node corresponding to the called method.
     * @return the entry node for the called method
     */
    public CFGMethodEntry getCalledMethodEntry() {
        JavaMethod jm = getJavaMethodCall().getJavaMethod();
        CFG cfg = CFGFactory.create(jm);
        return (CFGMethodEntry)cfg.getStartNode();
    }
    
    /**
     * Tests if this method call directly invokes the method itself.
     * @return <code>true</code> if this method call directly invokes the method itself, otherwise <code>false</code>
     */
    public boolean callSelfDirectly() {
        return getJavaMethodCall().callSelfDirectly();
    }
    
    /**
     * Tests if this method call recursively invokes the method itself.
     * @return <code>true</code> if this method call recursively invokes the method itself, otherwise <code>false</code>
     */
    public boolean callSelfRecursively() {
        return getJavaMethodCall().callSelfRecursively();
    }
    
    /**
     * Tests if this method call directly or recursively invokes the method itself.
     * @return <code>true</code> if this method call directly or recursively invokes the method itself, otherwise <code>false</code>
     */
    public boolean callSelf() {
        return callSelfDirectly() || callSelfRecursively();
    }
    
    /**
     * Tests if the called method exists in the project.
     * @return <code>true</code> if the called method exists in the project, otherwise <code>false</code>
     */
    public boolean callMethodInProject() {
        JavaMethod jm = getJavaMethodCall().getJavaMethod();
        return jm != null && jm.isInProject();
    }
    
    /*
    public void makeConservativeDefUseChains() {
        CFGMethodCall
        JavaStatement jst = (JavaStatement)callNode.getJavaComponent();
        jst.addDefVariable(callNode.getPrimary());
        jst.addUseVariable(callNode.getPrimary());
        
        HashMap<Integer, CFGParameterNode> actualIns = callNode.getActualIns();
        for (Iterator<Integer> it = actualIns.keySet().iterator(); it.hasNext(); ) {
            Integer number = it.next();
            JavaVariable jv = actualIns.get(number).getUseVariable();
            if (!jv.isPrimitive()) {
                jst.addDefVariable(jv);
            }
        }
    }
    */
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGMethodCall clone() {
        CFGMethodCall cloneNode = new CFGMethodCall(getJavaMethodCall(), getSort());
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
        cloneNode.setPrimary(getPrimary());
    }
    
    /**
     * Collects information about this statement.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("[" + getId() + "] ");
        buf.append("method call ");
        buf.append(": ");
        buf.append(getName());
        buf.append(" { " + toStringDefVariables() + " }");
        buf.append(" = ");
        buf.append("{ " + toStringUseVariables() + " }");
        
        return buf.toString();
    }
}

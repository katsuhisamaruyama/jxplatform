/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg.internal;

import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaElement;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import java.util.List;
import java.util.ArrayList;

/**
 * An CFG node that might have defined and/or used variables. 
 * @author Katsuhisa Maruyama
 */
public class CFGDefUseNode extends CFGNode {
    
    /**
     * The list of defined variables stored in this node.
     */
    protected List<JavaVariableAccess> defs = new ArrayList<JavaVariableAccess>();
    
    /**
     * The list of used variables stored in this node.
     */
    protected List<JavaVariableAccess> uses = new ArrayList<JavaVariableAccess>();
    
    /**
     * Creates a new, empty object.
     */
    protected CFGDefUseNode() {
        super();
    }
    
    /**
     * Creates a new node when the corresponding Java element does not exist.
     * @param sort the sort of this node
     */
    protected CFGDefUseNode(GraphNodeSort sort) {
        super(sort);
    }
    
    /**
     * Creates a new node corresponding to a given Java element.
     * @param jelem the Java element
     * @param sort the sort of this node
     */
    protected CFGDefUseNode(JavaElement jelem, GraphNodeSort sort) {
        super(jelem, sort);
    }
    
    /**
     * Adds a variable to the defined variables of this node.
     * @param jv the variable to be added
     * @return <code>true</code> if this variable list changed, otherwise <code>false</code>
     */
    public boolean addDefVariable(JavaVariableAccess jv) {
        if (jv != null && !defineVariable(jv)) {
            return defs.add(jv);
        }
        return false;
    }
    
    /**
     * Adds a variable to the used variables of this node.
     * @param jv the variable to be added
     * @return <code>true</code> if this variable list changed, otherwise <code>false</code>
     */
    public boolean addUseVariable(JavaVariableAccess jv) {
        if (jv != null && !useVariable(jv)) {
            return uses.add(jv);
        }
        return false;
    }
    
    /**
     * Adds a variables to the defined variables of this node.
     * @param jvl the list of variables to be added
     * @return <code>true</code> if this variable list changed, otherwise <code>false</code>
     */
    public boolean addDefVariables(List<JavaVariableAccess> jvl) {
        if (jvl == null) {
            return false;
        }
        for (JavaVariableAccess jv : jvl) {
            addDefVariable(jv);
        }
        return true;
    }
    
    /**
     * Adds a variables to the used variables of this node.
     * @param jvl the list of variables to be added
     * @return <code>true</code> if this variable list changed, otherwise <code>false</code>
     */
    public boolean addUseVariables(List<JavaVariableAccess> jvl) {
        if (jvl == null) {
            return false;
        }
        for (JavaVariableAccess jv : jvl) {
            addUseVariable(jv);
        }
        return true;
    }
    
    /**
     * Removes a variable from the defined variables of this node.
     * @param jv the variable to be removed
     * @return <code>true</code> if this variable list contained the removed variable, otherwise <code>false</code>
     */
    public boolean removeDefVariable(JavaVariableAccess jv) {
        if (jv != null) {
            return defs.remove(jv);
        }
        return false;
    }
    
    /**
     * Removes a given variable from the used variables of this node.
     * @param jv the variable to be removed
     * @return <code>true</code> if this variable list contained the removed variable, otherwise <code>false</code>
     */
    public boolean removeUseVariable(JavaVariableAccess jv) {
        if (jv != null) {
            return uses.remove(jv);
        }
        return false;
    }
    
    /**
     * Removes all variables in the defined variables of this node.
     */
    public void clearDefVariables() {
        defs.clear();
    }
    
    /**
     * Removes all variables in the used variables of this node.
     */
    public void clearUseVariables() {
        uses.clear();
    }
    
    /**
     * Stores a variable list as the defined variables of this node.
     * @param jvl the list of the variables
     */
    public void setDefVariables(List<JavaVariableAccess> jvl) {
        defs = jvl;
    }
    
    /**
     * Stores a variable list as the used variables of this node.
     * @param jvl the list of the variables
     */
    public void setUseVariables(List<JavaVariableAccess> jvl) {
        uses = jvl;
    }
    
    /**
     * Stores a single variable as the defined variables of this node.
     * @param jv the variable to be stored
     */
    public void setDefVariable(JavaVariableAccess jv) {
        clearDefVariables();
        addDefVariable(jv);
    }
    
    /**
     * Stores a single variable as the used variables of this node.
     * @param jv the variable to be stored
     */
    public void setUseVariable(JavaVariableAccess jv) {
        clearUseVariables();
        addUseVariable(jv);
    }
    
    /**
     * Returns the defined variables of this node.
     * @return the list of the defined variables
     */
    public List<JavaVariableAccess> getDefVariables() {
        return defs;
    }
    
    /**
     * Returns the used variables of this node.
     * @return the list of the used variables
     */
    public List<JavaVariableAccess> getUseVariables() {
        return uses;
    }
    
    /**
     * Tests if the defined variables contains a variable
     * @param jv the variable to be checked
     * @return <code>true</code> if the variable is contained, otherwise <code>false</code>
     */
    public boolean defineVariable(JavaVariableAccess jv) {
        for (JavaVariableAccess v : defs) {
            if (jv.equals(v)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Tests if the used variables contains a given variable
     * @param jv the variable to be checked
     * @return <code>true</code> if the variable is contained, otherwise <code>false</code>
     */
    public boolean useVariable(JavaVariableAccess jv) {
        for (JavaVariableAccess v : uses) {
            if (jv.equals(v)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Tests if the defined variables contains any variable
     * @return <code>true</code> if the list of the defined variables is not empty, otherwise <code>false</code>
     */
    public boolean hasDefVariable() {
        return defs.size() != 0;
    }
    
    /**
     * Tests if the used variables contains any variable
     * @return <code>true</code> if the list of the used variables is not empty, otherwise <code>false</code>
     */
    public boolean hasUseVariable() {
        return uses.size() != 0;
    }
    
    /**
     * Returns the defined variable with a given name.
     * @param name the name of the variable to be retrieved
     * @return The found variable, or <code>null</code> if none
     */
    public JavaVariableAccess getDefVariable(String name) {
        for (JavaVariableAccess jv : defs) {
            if (jv.getName().equals(name)) {
                return jv;
            }
        }
        return null;
    }
    
    /**
     * Returns the used variable with a given name.
     * @param name the name of a variable to be retrieved
     * @return the found variable, or <code>null</code> if none
     */
    public JavaVariableAccess getUseVariable(String name) {
        for (JavaVariableAccess jv : uses) {
            if (jv.getName().equals(name)) {
                return jv;
            }
        }
        return null;
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGDefUseNode clone() {
        CFGDefUseNode cloneNode = new CFGDefUseNode(getJavaElement(), getSort());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGDefUseNode cloneNode) {
        super.clone(cloneNode);
    }
    
    /**
     * Collects information about this node.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();   
        buf.append(" DEFS: ");
        buf.append(toStringDefVariables());
        buf.append("\n");
        
        buf.append(" USES: ");
        buf.append(toStringUseVariables());
        buf.append("\n");
        
        return buf.toString();
    }
    
    /**
     * Collects information about the defined variables of this node.
     * @return the string for printing
     */
    public String toStringDefVariables() {
        return toString(getDefVariables());
    }
    
    /**
     * Collects information about the used variables of this node.
     * @return the string for printing
     */
    public String toStringUseVariables() {
        return toString(getUseVariables());
    }
    
    /**
     * Collects information about a given variable list.
     * @return the string for printing
     */
    protected String toString(List<JavaVariableAccess> jvl) {
        StringBuffer buf = new StringBuffer();
        for (JavaVariableAccess jv : jvl) {
            buf.append(jv.toString());
            buf.append(", ");
        }
        
        if (buf.length() != 0) {
            return buf.substring(0, buf.length() - 2);
        } else {
            return "";
        }
    }
}

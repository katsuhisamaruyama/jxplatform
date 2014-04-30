/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaVariableAccess;

/**
 * A special variable for formal-in, formal-out, actual-in, and actual-out nodes of a CFG.
 * @author Katsuhisa Maruyama
 */
public class JavaSpecialVariable extends JavaVariableAccess {
    
    /**
     * A class containing this variable. 
     */
    private JavaClass declaringClass = null;
    
    /**
     * Creates a new, empty object.
     */
    protected JavaSpecialVariable() {
        super();
    }
    
    /**
     * Creates a new object representing a special variable.
     * @param name the name of this variable
     * @param type the type of this variable
     * @param jc the class containing this variable
     */
    public JavaSpecialVariable(String name, String type, JavaClass jc) {
        this.name = name;
        this.type = type;
        declaringClass = jc;
        declaringMethod = null;
    }
    
    /**
     * Creates a new object representing a special variable.
     * @param name the name of this variable
     * @param type the type of this variable
     * @param jm the method containing this variable
     */
    public JavaSpecialVariable(String name, String type, JavaMethod jm) {
        this.name = name;
        this.type = type;
        declaringClass = jm.getDeclaringJavaClass();
        declaringMethod = jm;
    }
    
    /**
     * Creates a new object representing a special variable.
     * @param name the name of this variable
     * @param type the type of this variable
     * @param jf the field containing this variable
     */
    public JavaSpecialVariable(String name, String type, JavaField jf) {
        this.name = name;
        this.type = type;
        declaringClass = jf.getDeclaringJavaClass();
        declaringMethod = null;
    }
    
    /**
     * Returns the name of this variable.
     * @return The name string
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the fully-qualified name of this variable.
     * @return The fully-qualified name string
     */
    public String getType() {
        return type;
    }
    
    /**
     * Returns the Java class enclosing this variable.
     * @return the enclosing class for this variable
     */
    public JavaClass getJavaClassOf() {
        return declaringClass;
    }
    
    /**
     * Returns the Java method enclosing this variable.
     * @return the enclosing method for this variable
     */
    public JavaMethod getJavaMethodOf() {
        return declaringMethod;
    }
    
    /**
     * Tests if a given variable equals to this.
     * @param obj the Java variable
     * @return <code>true</code> if the given variable equals to this, otherwise <code>false</code>
     */
    public boolean equals(Object obj) {
        if (obj instanceof JavaVariableAccess) {
            JavaVariableAccess jv = (JavaVariableAccess)obj;
            return equals(jv);
        }
        return false;
    }
    
    /**
     * Tests if a given variable equals to this.
     * @param jv the Java variable
     * @return <code>true</code> if the given variable equals to this, otherwise <code>false</code>
     */
    public boolean equals(JavaVariableAccess jv) {
        if (jv == null) {
            return false;
        }
        if (this == jv) {
            return true;
        }
        
        if (this == jv) {
            return true;
        }
        
        if (getJavaMethodOf() != null) {
            return getJavaClassOf().equals(jv.getJavaClassOf()) &&
                   getJavaMethodOf().equals(jv.getJavaMethodOf()) &&
                   getName().compareTo(jv.getName()) == 0;
        }
        
        return getJavaClassOf().equals(jv.getJavaClassOf()) &&
               getName().compareTo(jv.getName()) == 0;
    }
    
    /**
     * Returns a hash code value for this special variable.
     * @return the hash code value for the variable
     */
    public int hashCode() {
        return getName().hashCode();
    }
    
    /**
     * Collects information about the name of this variable.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getName());
        buf.append("@");
        buf.append(getType());
        
        return buf.toString();
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.jtool.eclipse.model.java.internal.ExternalJavaField;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Name;
import org.apache.log4j.Logger;

/**
 * An object representing an expression for a variable access.
 * @author Katsuhisa Maruyama
 */
public class JavaVariableAccess extends JavaExpression {
    
    static Logger logger = Logger.getLogger(JavaVariableAccess.class.getName());
    
    /**
     * The name of this variable.
     */
    protected String name;
    
    /**
     * The identification number of the variable corresponding to this variable access.
     */
    protected int variableId;
    
    /**
     * The type of the variable corresponding to this variable access.
     */
    protected String type;
    
    /**
     * A flag indicating if this variable access represents a field.
     */
    protected boolean isField = false;
    
    /**
     * A flag indicating if this variable access represents an enum constant.
     */
    protected boolean isEnumConstant = false;
    
    /**
     * A flag indicating if this object represents a parameter.
     */
    protected boolean isParameter = false;
    
    /**
     * The method containing this variable access.
     */
    protected JavaMethod declaringMethod = null;
    
    /**
     * The name of class declaring the accessed field.
     */
    protected String classNameOfAccessedField;
    
    /**
     * A flag that indicates all bindings for types, methods, and variables were found.
     */
    protected boolean bindingOk = true;
    
    /**
     * Creates a new, empty object.
     */
    protected JavaVariableAccess() {
        super();
    }
    
    /**
     * Creates a new object representing a variable access.
     * @param node an AST node for this variable access
     */
    protected JavaVariableAccess(ASTNode node) {
        super(node);
    }
    
    /**
     * Creates a new object representing a variable access.
     * @param node an AST node for this variable access
     * @param jm the method containing this variable access
     */
    public JavaVariableAccess(Name node, JavaMethod jm) {
        super(node);
        
        declaringMethod = jm;
        IVariableBinding binding = (IVariableBinding)node.resolveBinding();
        
        if (binding != null) {
            name = binding.getName();
            variableId = binding.getVariableId();
            type = binding.getType().getQualifiedName();
            isField = binding.isField();
            isEnumConstant = binding.isEnumConstant();
            isParameter = binding.isParameter();
            
            if (isField || isEnumConstant) {
                ITypeBinding tbinding = binding.getDeclaringClass();
                if (tbinding != null) {
                    classNameOfAccessedField = tbinding.getQualifiedName();
                } else {
                    JavaField jf = ExternalJavaField.create(binding);
                    classNameOfAccessedField = jf.getDeclaringJavaClass().getQualifiedName();
                }
                
            }
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
    }
    
    /**
     * Tests if the binding for this variable access was found.
     * @return <code>true</code> if the binding was found
     */
    public boolean isBindingOk() {
        return bindingOk;
    }
    
    /**
     * Tests if this variable access represents a field.
     * @return <code>true</code> if this variable access represents a field, otherwise <code>false</code>
     */
    public boolean isField() {
        return isField || isEnumConstant;
    }
    
    /**
     * Tests if this variable access represents a local variable.
     * @return <code>true</code> if this variable access represents a local variable, otherwise <code>false</code>
     */
    public boolean isLocal() {
        return !isField && !isEnumConstant;
    }
    
    /**
     * Tests if this variable access represents a parameter.
     * @return <code>true</code> if this variable access represents a parameter, otherwise <code>false</code>
     */
    public boolean isParameter() {
        return isParameter;
    }
    
    /**
     * Returns the method that declares this variable access.
     * @return the method that declares this variable access
     */
    public JavaMethod getDeclaringJavaMethod() {
        return declaringMethod;
    }
    
    /**
     * Returns the name of the accessed variable.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the fully-qualified name of the accessed variable.
     * @return the name
     */
    public String getQualifiedName() {
        if (classNameOfAccessedField != null) {
            return classNameOfAccessedField + "#" + getName();
        }
        return getName();
    }
    
    /**
     * Returns the type of the variable corresponding to this variable access.
     * @return the type of the variable
     */
    public String getType() {
        return type;
    }
    
    /**
     * Tests if the type of the variable corresponding to this variable access is primitive.
     * @return <code>true</code> if the type of this variable access is primitive, otherwise <code>false</code>
     */
    public boolean isPrimitiveType() {
        return isPrimitiveType(type);
    }
    
    /**
     * Tests if a given variable access equals to this.
     * @param jacc the variable access
     * @return <code>true</code> if the given variable access equals to this, otherwise <code>false</code>
     */
    public boolean equals(JavaVariableAccess jacc) {
        if (jacc == null) {
            return false;
        }
        
        return this == jacc || getQualifiedName().compareTo(jacc.getQualifiedName()) == 0;
    }
    
    /**
     * Returns a hash code value for this variable access.
     * @return the hash code value for the variable access
     */
    public int hashCode() {
        return getQualifiedName().hashCode();
    }
    
    /**
     * Tests if this variable access corresponds to a given field.
     * @param jf the field which is compared to
     * @return <code>true</code> if this access corresponds to the field, otherwise <code>false</code>
     */
    public boolean equals(JavaField jf) {
        if (classNameOfAccessedField == null) {
            return false;
        }
        return classNameOfAccessedField.compareTo(jf.getDeclaringJavaClass().getQualifiedName()) == 0 &&
               getName().compareTo(jf.getName()) == 0;
    }
    
    /* ================================================================================
     * The following functionalities can be used after completion of whole analysis 
     * ================================================================================ */
    
    /**
     * A field corresponding to this variable access, or <code>null</code> if this is not a field variable access.
     */
    protected JavaField jfield = null;
    
    /**
     * A local variable corresponding to this variable access, or <code>null</code> if this is not a local variable access.
     */
    protected JavaLocal jlocal = null;
    
    /**
     * Displays error log if the binding has not completed yet.
     */
    private void bindingCheck() {
        if (getBindingLevel() < 1) {
            logger.info("This API can be called after the completion of whole analysis");
        }
    }
    
    /**
     * Returns the Java class enclosing the accessed field.
     * @return the enclosing class for the accessed field
     */
    public JavaClass getJavaClassOf() {
        bindingCheck();
        
        if (isField()) {
            JavaField jf = getJavaField();
            if (jf != null) {
                return jf.getDeclaringJavaClass();
            }
        }
        return null;
    }
    
    /**
     * Returns the Java method enclosing the accessed field.
     * @return the enclosing method for the accessed field
     */
    public JavaMethod getJavaMethodOf() {
        bindingCheck();
        
        if (isLocal()) {
            JavaLocal jl = getJavaLocal();
            if (jl != null) {
                return jl.getDeclaringJavaMethod();
            }
        }
        return null;
    }
    
    /**
     * Returns a field variable corresponding to this variable access.
     * @return the found field variable, or <code>null</code> if none
     */
    public JavaField getJavaField() {
        bindingCheck();
        
        if (isField()) {
            if (jfield != null) {
                return jfield;
            }
            
            jfield = getDeclaringJavaField(classNameOfAccessedField, name);
            return jfield;
        }
        return null;
    }
    
    /**
     * Returns a local variable reachable from this variable access.
     * @return the found local variable, or <code>null</code> if none
     */
    public JavaLocal getJavaLocal() {
        bindingCheck();
        
        if (isLocal()) {
            if (jlocal != null) {
                return jlocal;
            }
            
            jlocal = getDeclaringParameter(name);
            if (jlocal != null) {
                return jlocal;
            }
            
            jlocal = getDeclaringJavaLocal(name, variableId);
            if (jlocal != null) {
                return jlocal;
            }
            
            jlocal = getReturnValue();
            if (jlocal != null) {
                return jlocal;
            }
        }
        return null;
    }
    
    /**
     * Obtains a parameter with a given name, which is reachable from this variable access.
     * @param name the name of the parameter
     * @return the found parameter, or <code>null</code> if none
     */
    private JavaLocal getDeclaringParameter(String name) {
        bindingCheck();
        
        if (declaringMethod != null) {
            JavaLocal jl = declaringMethod.getParameter(name);
            if (jl != null) {
                return jl;
            }
        }
        return null;
    }
    
    /**
     * Obtains a local variable with a given name and its identification number, which is reachable from this variable access.
     * @param name the name of the variable
     * @param vid the identification number of the variable
     * @return the found local variable, or <code>null</code> if none
     */
    private JavaLocal getDeclaringJavaLocal(String name, int vid) {
        bindingCheck();
        
        if (declaringMethod != null) {
            JavaLocal jl = declaringMethod.getJavaLocal(name, vid);
            if (jl != null) {
                return jl;
            }
        }
        return null;
    }
    
    /**
     * Obtains a virtual variable that stores the return value, which is reachable from this variable access.
     * @return the virtual variable for the return value
     */
    public JavaLocal getReturnValue() {
        bindingCheck();
        
        if (declaringMethod != null) {
            JavaLocal jl = declaringMethod.getReturnValueVariable();
            if (jl != null) {
                return jl;
            }
        }
        return null;
    }
    
    /**
     * Collects information about this field access.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        if (jfield != null) {
            buf.append(jfield.getDeclaringJavaClass().getQualifiedName());
            buf.append("#");
        }
        
        buf.append(getName());
        buf.append("@");
        buf.append(getType());
        
        if (jlocal != null) {
            buf.append("[");
            buf.append(jlocal.getId());
            buf.append("]");
        }
        
        return buf.toString();
    }
}

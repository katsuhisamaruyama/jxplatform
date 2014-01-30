/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Name;

/**
 * An object representing a local variable.
 * @author Katsuhisa Maruyama
 */
public class JavaLocal extends JavaExpression {
    
    /**
     * The name of this local variable.
     */
    protected String name;
    
    /**
     * An integer number for specifying a local variable.
     */
    protected int id;
    
    /**
     * The type of this local variable.
     */
    protected String type;
    
    /**
     * A flag indicating if the type of this variable is primitive.
     */
    protected boolean isPrimitive;
    
    /**
     * A flag indicating if this local variable is a parameter.
     */
    protected boolean isParameter;
    
    /**
     * The modifiers of this local variable.
     */
    protected int modifiers;
    
    /**
     * The method declaring this local variable.
     */
    protected JavaMethod declaringMethod = null;
    
    /**
     * Creates a new, empty object.
     */
    protected JavaLocal() {
    }
    
    /**
     * Creates a new object representing a local variable.
     * @param node the AST node for this local variable
     */
    public JavaLocal(ASTNode node) {
        super(node);
        
        name = null;
    }
    
    /**
     * Creates a new object representing a local variable.
     * @param node the AST node for this local variable
     * @param jm the method declaring this local variable
     */
    public JavaLocal(VariableDeclaration node, JavaMethod jm) {
        super(node);
        declaringMethod = jm;
        
        IVariableBinding binding = node.resolveBinding().getVariableDeclaration();
        name = binding.getName();
        id = binding.getVariableId();
        type = binding.getType().getQualifiedName();
        isPrimitive = binding.getType().isPrimitive();
        isParameter = binding.isParameter();
        modifiers = binding.getModifiers();
        
        setAnnotations(binding.getAnnotations());
    }
    
    /**
     * Creates a new object representing a local variable.
     * @param node an AST node for this local variable
     * @param jm the method declaring this local variable
     */
    public JavaLocal(SingleVariableDeclaration node, JavaMethod jm) {
        super(node);
        declaringMethod = jm;
        
        IVariableBinding binding = node.resolveBinding().getVariableDeclaration();
        name = binding.getName();
        id = binding.getVariableId();
        type = binding.getType().getQualifiedName();
        isPrimitive = binding.getType().isPrimitive();
        isParameter = binding.isParameter();
        modifiers = binding.getModifiers();
    }
    
    /**
     * Creates a new object representing a local variable.
     * @param name The name of this local variable
     * @param id the integer number for specifying a local variable
     * @param type the type of this local variable
     * @param isPrimitive a flag indicating if the type of this variable is primitive
     * @param isParameter a flag indicating if this local variable is a parameter
     * @param modifiers the modifiers of this local variable.
     * @param jm the method declaring this local variable
     */
    public JavaLocal(String name, int id, String type, boolean isPrimitive, boolean isParameter, int modifiers, JavaMethod jm) {
        super();
        
        this.name = name;
        this.id = id;
        this.type = type;
        this.isPrimitive = isPrimitive;
        this.isParameter = isParameter;
        this.modifiers = modifiers;
        this.declaringMethod = jm;
    }
    
    /**
     * Converts a local variable object into a variable access object.
     * @return the created variable access object.
     */
    public JavaVariableAccess convertJavaVariableAccess() {
        Name name = null;
        if (astNode instanceof VariableDeclaration) {
            name = ((VariableDeclaration)astNode).getName();
        } else if (astNode instanceof SingleVariableDeclaration) {
            name = ((SingleVariableDeclaration)astNode).getName();
        }
        
        if (name != null && name.resolveBinding() != null) {
            return new JavaVariableAccess(name, declaringMethod);
        }
        return null;
    }
    
    /**
     * Returns the method that declares this local variable.
     * @return the method that declares this local variable
     */
    public JavaMethod getDeclaringJavaMethod() {
        return declaringMethod;
    }
    
    /**
     * Returns the name of this local variable.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns an integer number for specifying a local variable.
     * @return the integer number
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns the type of this local variable.
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Tests if the type of this variable is primitive.
     * @return <code>true</code> if the type of this variable is primitive, otherwise <code>false</code>
     */
    public boolean isPrimitive() {
        return isPrimitive;
    }
    
    /**
     * Tests if this local variable is a parameter.
     * @return <code>true</code> if this local variable is a parameter, otherwise <code>false</code>
     */
    public boolean isParameter() {
        return isParameter;
    }
    
    /**
     * Tests if the access setting of this local variable is final.
     * @return <code>true</code> if this is a final local variable, otherwise <code>false</code>
     */
    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }
    
    /**
     * Tests if a given local variable equals to this.
     * @param jl the local variable
     * @return <code>true</code> if the given local equals to this, otherwise <code>false</code>
     */
    public boolean equals(JavaLocal jl) {
        if (jl == null || getName() == null || jl.getName() == null) {
            return false;
        }
        
        return this == jl || (getName().compareTo(jl.getName()) == 0 && getId() == jl.getId());
    }
    
    /**
     * Returns a hash code value for this local.
     * @return the hash code value for the local
     */
    public int hashCode() {
        long value = getName().hashCode() + getId();
        return Long.valueOf(value).hashCode();
    }
    
    /**
     * Collects information about this local variable.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("LOCAL: ");
        buf.append(getName());
        buf.append("@");
        buf.append(getType());
        buf.append("\n");
        
        buf.append(getAnnotationInfo());
        
        return buf.toString();
    }
}

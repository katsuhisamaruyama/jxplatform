/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.jtool.eclipse.model.java.internal.FieldInitializerCollector;
import org.jtool.eclipse.model.java.internal.MethodCallCollector;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * An object representing a field or an enum constant.
 * @author Katsuhisa Maruyama
 */
public class JavaField extends JavaExpression {
    
    static Logger logger = Logger.getLogger(JavaField.class.getName());
    
    /**
     * The name of this field.
     */
    protected String name;
    
    /**
     * The type of this field.
     */
    protected String type;
    
    /**
     * The modifiers of this field.
     */
    protected int modifiers;
    
    /**
     * A flag indicating if this field is an enum constant.
     */
    protected boolean isEnumConstant = false;
    
    /**
     * The class declaring this field.
     */
    protected JavaClass declaringClass = null;
    
    /**
     * The collection of fields that this field declaration accesses.
     */
    protected Set<String> accessedFieldNames = new HashSet<String>();
    
    /**
     * The collections of all methods that this field declaration calls.
     */
    protected Set<String> calledMethodNames = new HashSet<String>();
    
    /**
     * A flag that indicates all bindings for methods and fields were found.
     */
    protected boolean bindingOk = true;
    
    /**
     * Creates a new, empty object.
     */
    protected JavaField() {
        super();
    }
    
    /**
     * Creates a new object representing a field.
     * @param node an AST node for this field
     */
    protected JavaField(ASTNode node) {
        super(node);
    }
    
    /**
     * Creates a new object representing a field.
     * @param node an AST node for this field
     * @param jc the class declaring this field
     */
    public JavaField(VariableDeclaration node, JavaClass jc) {
        super(node);
        
        declaringClass = jc;
        IVariableBinding binding = node.resolveBinding().getVariableDeclaration();
        
        if (binding != null) {
            name = binding.getName();
            type = JavaClass.createClassName(binding.getType());
            modifiers = binding.getModifiers();
            isEnumConstant = false;
            
            collectAccessedField(node);
            collectCalledMethods(node);
            
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
        
        jc.addJavaField(this);
    }
    
    /**
     * Creates a new object representing an enum constant.
     * @param node an AST node for this enum constant
     * @param decl an AST node for this field declaration
     */
    public JavaField(EnumConstantDeclaration node, JavaClass jc) {
        super(node);
        
        declaringClass = jc;
        IVariableBinding binding = node.resolveVariable();
        
        if (binding != null) {
            name = binding.getName();
            type = binding.getType().getQualifiedName();
            modifiers = binding.getModifiers();
            isEnumConstant = true;
            
            collectAccessedField(node);
            collectCalledMethods(node);
            
            setAnnotations(binding.getAnnotations());
            
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
        
        jc.addJavaField(this);
    }
    
    /**
     * Creates a new object representing a field.
     * @param name the name of this field
     * @param type the type of this field
     * @param modifiers the modifiers of this field
     * @param isEnumConstant <code>true> if this field is an enum constant, otherwise <code>false</code>
     * @param jc the class declaring this field
     */
    public JavaField(String name, String type, int modifiers, boolean isEnumConstant, JavaClass jc) {
        super();
        
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
        this.isEnumConstant = isEnumConstant;
        declaringClass = jc;
        
        jc.addJavaField(this);
    }
    
    /**
     * Collects fields that this field accesses.
     * @param node an AST node for this field
     */
    protected void collectAccessedField(ASTNode node) {
        FieldInitializerCollector fvisitor = new FieldInitializerCollector();
        node.accept(fvisitor);
        
        for (String str : fvisitor.getAccessedFields()) {
            accessedFieldNames.add(str);
        }
        
        if (!fvisitor.isBindingOk()) {
            bindingOk = false;
        }
        fvisitor.clear();
    }
    
    /**
     * Collects methods that this field calls.
     * @param node an AST node for this field
     */
    protected void collectCalledMethods(ASTNode node) {
        MethodCallCollector mvisitor = new MethodCallCollector();
        node.accept(mvisitor);
        
        for (String str : mvisitor.getMethodCalls()) {
            calledMethodNames.add(str);
        }
        
        if (!mvisitor.isBindingOk()) {
            bindingOk = false;
        }
        mvisitor.clear();
    }
    
    /**
     * Converts a local variable object into a variable access object.
     * @return the created variable access object.
     */
    public JavaVariableAccess convertJavavariableAccess() {
        Name name = null;
        if (astNode instanceof VariableDeclaration) {
            name = ((VariableDeclaration)astNode).getName();
        } else if (astNode instanceof EnumConstantDeclaration) {
            name = ((EnumConstantDeclaration)astNode).getName();
            
        }
        
        if (name != null && name.resolveBinding() != null) {
            return new JavaVariableAccess(name, null);
        }
        return null;
    }
    
    /**
     * Tests if the binding for this field was found.
     * @return <code>true</code> if the binding was found
     */
    public boolean isBindingOk() {
        return bindingOk;
    }
    
    /**
     * Tests if this object represents a normal field.
     * @return <code>true</code> if this object represents a field, otherwise <code>false</code>
     */
    public boolean isField() {
        return !isEnumConstant;
    }
    
    /**
     * Tests if this object represents an enum constant.
     * @return <code>true</code> if this object represents an enum constant, otherwise <code>false</code>
     */
    public boolean isEnumConstant() {
        return isEnumConstant;
    }
    
    /**
     * Tests if this field exists in the project.
     * @return always <code>true</code>
     */
    public boolean isInProject() {
        return true;
    }
    
    /**
     * Returns the class that declares this field.
     * @return the class that declares this field
     */
    public JavaClass getDeclaringJavaClass() {
        return declaringClass;
    }
    
    /**
     * Returns the name of this field.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the fully-qualified name of this field.
     * @return the name
     */
    public String getQualifiedName() {
        JavaClass jc = getDeclaringJavaClass();
        if (jc != null) {
            return jc.getQualifiedName() + "#" + getName();
        }
        return getName();
    }
    
    /**
     * Returns the type of this field.
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Tests if the type of this field is primitive.
     */
    public boolean isPrimitiveType() {
        return isPrimitiveType(type);
    }
    
    /**
     * Returns the value representing modifiers of this field.
     * @return the modifiers value
     */
    public int getModifiers() {
        return modifiers;
    }
    
    /**
     * Tests if the access setting of this field is public.
     * @return <code>true</code> if this is a public field, otherwise <code>false</code>
     */
    public boolean isPublic() {
        return Modifier.isPublic(modifiers);
    }
    
    /**
     * Tests if the access setting of this field is protected.
     * @return <code>true</code> if this is a protected field, otherwise <code>false</code>
     */
    public boolean isProtected() {
        return Modifier.isProtected(modifiers);
    }
    
    /**
     * Tests if the access setting of this field is private.
     * @return <code>true</code> if this is a private field, otherwise <code>false</code>
     */
    public boolean isPrivate() {
        return Modifier.isPrivate(modifiers);
    }
    
    /**
     * Tests if the access setting of this field has default visibility.
     * @return <code>true</code> if this is a field with default visibility, otherwise <code>false</code>
     */
    public boolean isDefault() {
        return !isPublic() && !isProtected() && !isPrivate();
    }
    
    /**
     * Tests if the access setting of this field is final.
     * @return <code>true</code> if this is a final field, otherwise <code>false</code>
     */
    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }
    
    /**
     * Tests if the access setting of this field is static.
     * @return <code>true</code> if this is a static field, otherwise <code>false</code>
     */
    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }
    
    /**
     * Tests if the access setting of this field is volatile.
     * @return <code>true</code> if this is a volatile field, otherwise <code>false</code>
     */
    public boolean isVolatile() {
        return Modifier.isVolatile(modifiers);
    }
    
    /**
     * Tests if the access setting of this field is transient.
     * @return <code>true</code> if this is a transient field, otherwise <code>false</code>
     */
    public boolean isTransient() {
        return Modifier.isTransient(modifiers);
    }
    
    /**
     * Obtains the source code of the file containing this field.
     */
    public String getSource() {
        return declaringClass.getSource();
    }
    
    /**
     * Obtains a string representing a field.
     * @param fqn the name of the class declaring the field
     * @param name the name of the field
     * @return the string
     */
    public static String getString(String fqn, String name) {
        return fqn + "#" + name;
    }
    
    /**
     * Obtains the name of the class declaring the field represented by a given string
     * @param str string representing the field
     * @return the class name
     */
    public static String getFqn(String str) {
        int index = str.indexOf('#');
        return str.substring(0, index);
    }
    
    /**
     * Obtains the name of the field represented by a given string
     * @param str string representing the field
     * @return the field name
     */
    public static String getName(String str) {
        int index = str.indexOf('#');
        return str.substring(index + 1, str.length());
    }
    
    /**
     * Tests if a given field equals to this.
     * @param jf the Java field
     * @return <code>true</code> if the given field equals to this, otherwise <code>false</code>
     */
    public boolean equals(JavaField jf) {
        if (jf == null) {
            return false;
        }
        
        if (this == jf) {
            return true;
        }
        
        return getDeclaringJavaClass().equals(jf.getDeclaringJavaClass()) &&
               getName().compareTo(jf.getName()) == 0; 
    }
    
    /**
     * Returns a hash code value for this field.
     * @return the hash code value for the field
     */
    public int hashCode() {
        return getName().hashCode();
    }
    
    /**
     * Collects information about this field.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("FIELD: ");
        buf.append(getName());
        buf.append("@");
        buf.append(getType());
        buf.append("\n");
        
        buf.append(getAnnotationInfo());
        
        return buf.toString();
    }
    
    /* ================================================================================
     * The following functionalities can be used after completion of whole analysis 
     * ================================================================================ */
    
    /**
     * The collection of fields that this field declaration accesses.
     */
    protected Set<JavaField> accessedFields = new HashSet<JavaField>();
    
    /**
     * The collection of all field declarations that access this field.
     */
    protected Set<JavaField> accessingFields = new HashSet<JavaField>();
    
    /**
     * The collections of all methods that this field declaration calls.
     */
    protected Set<JavaMethod> calledMethods = new HashSet<JavaMethod>();
    
    /**
     * The collection of all methods that accesses this field.
     */
    protected Set<JavaMethod> accessingMethods = new HashSet<JavaMethod>();
    
    /**
     * Collects additional information on this method.
     */
    public void collectLevel2Info() {
        findAccessedField();
        findCalledMethods();
    }
    
    /**
     * Finds fields that this field accesses.
     */
    protected void findAccessedField() {
        for (String str : accessedFieldNames) {
            String fqn = JavaField.getFqn(str);
            String name = JavaField.getName(str);
            JavaField jf = getDeclaringJavaField(fqn, name);
            if (jf != null) {
                accessedFields.add(jf);
                jf.addAccessingJavaField(this);
            }
        }
    }
    
    /**
     * Finds methods that this field calls.
     */
    protected void findCalledMethods() {
        for (String str : calledMethodNames) {
            String fqn = JavaMethod.getFqn(str);
            String sig = JavaMethod.getSignature(str);
            JavaMethod jm = getDeclaringJavaMethod(fqn, sig);
            if (jm != null) {
                calledMethods.add(jm);
                jm.addAccessingJavaField(this);
            }
        }
    }
    
    /**
     * Adds a method that accesses this field.
     * @param jm the method
     */
    public void addCallingJavaMethod(JavaMethod jm) {
        accessingMethods.add(jm);
    }
    
    /**
     * Adds a field that accesses this field.
     * @param jf the field
     */
    public void addAccessingJavaField(JavaField jf) {
        accessingFields.add(jf);
    }
    
    /**
     * Displays error log if the binding has not completed yet.
     */
    private void bindingCheck() {
        if (getBindingLevel() < 1) {
            logger.info("This API can be called after the completion of whole analysis");
        }
    }
    
    /**
     * Returns all the fields accessed by this field.
     * @return the collection of the accessed fields
     */
    public Set<JavaField> getAccessedJavaFields() {
        bindingCheck();
        return accessedFields;
    }
    
    /**
     * Returns all the fields accessed by this field with the project.
     * @return the collection of the accessed fields
     */
    public Set<JavaField> getAccessedJavaFieldsInProject() {
        bindingCheck();
        
        Set<JavaField> fields = new HashSet<JavaField>();
        for (JavaField jf : getAccessedJavaFields()) {
            if (jf.isInProject()) {
                fields.add(jf);
            }
        }
        return fields;
    }
    
    /**
     * Returns all the fields access this field.
     * @return the collection of the accessing fields
     */
    public Set<JavaField> getAccessingJavaFields() {
        bindingCheck();
        return accessingFields;
    }
    
    /**
     * Returns all the methods access this field.
     * @return the collection of the accessing methods
     */
    public Set<JavaMethod> getAccessingJavaMethods() {
        bindingCheck();
        return accessingMethods;
    }
    
    /**
     * Returns all the methods by this field.
     * @return the collection of the called methods
     */
    public Set<JavaMethod> getCalledJavaMethods() {
        bindingCheck();
        return calledMethods;
    }
    
    /**
     * Returns all the methods by this field within the project.
     * @return the collection of the called methods within the project
     */
    public Set<JavaMethod> getCalledJavaMethodsInProject() {
        bindingCheck();
        
        Set<JavaMethod> methods = new HashSet<JavaMethod>();
        for (JavaMethod jm : getCalledJavaMethods()) {
            if (jm.isInProject()) {
                methods.add(jm);
            }
        }
        return methods;
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.jtool.eclipse.model.java.internal.LocalDeclarationCollector;
import org.jtool.eclipse.model.java.internal.MethodCallCollector;
import org.jtool.eclipse.model.java.internal.TypeCollector;
import org.jtool.eclipse.model.java.internal.FieldAccessCollector;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * An object representing a method, a constructor, or an initializer.
 * @author Katsuhisa Maruyama
 */
public class JavaMethod extends JavaElement {
    
    static Logger logger = Logger.getLogger(JavaMethod.class.getName());
    
    /**
     * The special name given to an initializer.
     */
    public static final String InitializerName = ".INITIALIZER";
    
    /**
     * The name of this method.
     */
    protected String name;
    
    /**
     * The signature of this method.
     */
    protected String signature;
    
    /**
     * The type of this method.
     */
    protected String type;
    
    /**
     * The modifiers of this method.
     */
    protected int modifiers;
    
    /**
     * A class declaring this method.
     */
    protected JavaClass declaringClass = null;
    
    /**
     * A flag indicating if this method is a constructor.
     */
    protected boolean isConstructor = false;
    
    /**
     * A flag indicating if this method is an initializer.
     */
    protected boolean isInitializer = false;
    
    /**
     * The collection of parameters for this method.
     */
    protected List<JavaLocal> parameters = new ArrayList<JavaLocal>();
    
    /**
     * A virtual variable that stores the return value of this method.
     */
    protected JavaLocal returnValue = null;
    
    /**
     * The collection of all local variables declared in this method.
     */
    protected Set<JavaLocal> locals = new HashSet<JavaLocal>();
    
    /**
     * The collection of the names of all exceptions this method or constructor might throw.
     */
    protected Set<String> exceptionNames = new HashSet<String>();
    
    /**
     * The collection of all methods that this method calls.
     */
    protected Set<String> calledMethodNames = new HashSet<String>();
    
    /**
     * The collection of all fields that this method accesses.
     */
    protected Set<String> accessedFieldNames = new HashSet<String>();
    
    /**
     * The collections of classes that this method uses.
     */
    protected Set<String> usedClassNames = new HashSet<String>();
    
    /**
     * A flag that indicates all bindings for types, methods, and variables were found.
     */
    protected boolean bindingOk = true;
    
    /**
     * Creates a new, empty object.
     */
    protected JavaMethod() {
        super();
    }
    
    /**
     * Creates a new object representing a method.
     * @param node an AST node for this method
     */
    protected JavaMethod(ASTNode node) {
        super(node);
    }
    
    /**
     * Creates a new object representing a method.
     * @param node an AST node for this method
     * @param jc the class declaring this method
     */
    @SuppressWarnings("unchecked")
    public JavaMethod(MethodDeclaration node, JavaClass jc) {
        super(node);
        
        declaringClass = jc;
        IMethodBinding binding = node.resolveBinding();
        
        if (binding != null) {
            name = binding.getName();
            signature = getSignature(binding);
            type = binding.getReturnType().getQualifiedName();
            modifiers = binding.getModifiers();
            isConstructor = binding.isConstructor();
            isInitializer = false;
            for (ITypeBinding tbinding : binding.getExceptionTypes()) {
                exceptionNames.add(tbinding.getQualifiedName());
            }
            
            setParameters(node.parameters());
            collectLocalVariables(node);
            collectAccessedFields(node);
            collectCalledMethods(node);
            collectUsedTypes(node);
            
            setAnnotations(binding.getAnnotations());
            
        } else {
            name = ".UNKNOWN";
            signature = ".UNKNOWN";
            bindingOk = false;
        }
        
        jc.addJavaMethod(this);
    }
    
    /**
     * Creates a new object representing an initializer.
     * @param node the AST node for this initializer
     */
    public JavaMethod(Initializer node, JavaClass jc) {
        super(node);
        
        declaringClass = jc;
        
        name = InitializerName;
        signature = name;
        type = "void";
        modifiers = 0;
        isConstructor = false;
        isInitializer = true;
        
        collectLocalVariables(node);
        collectAccessedFields(node);
        collectCalledMethods(node);
        collectUsedTypes(node);
        
        jc.addJavaMethod(this);
    }
    
    /**
     * Creates a new object representing a method.
     * @param name the name of this method
     * @param sig the signature of this method
     * @param type the type of this method.
     * @param modifiers the modifiers of this method.
     * @param isConstructor <code>true</code> if this method is a constructor, otherwise <code>false</code>
     * @param isInitializer <code>true</code> if this method is a initializer, otherwise <code>false</code>
     * @param jc the class declaring this method
     */
    public JavaMethod(String name, String sig, String type, int modifiers, boolean isConstructor, boolean isInitializer, JavaClass jc) {
        super();
        
        this.name = name;
        this.signature = sig;
        this.type = type;
        this.modifiers = modifiers;
        this.isConstructor = isConstructor;
        this.isInitializer = isInitializer;
        this.declaringClass = jc;
        
        jc.addJavaMethod(this);
    }
    
    /**
     * Sets parameters and the return type of this method.
     * @param decralations the list of parameter declarations
     */
    protected void setParameters(List<SingleVariableDeclaration> declarations) {
        for (SingleVariableDeclaration decl : declarations) {
            JavaLocal param = new JavaLocal(decl, this);
            parameters.add(param);
        }
        
        if (!isVoid()) {
            returnValue = new JavaLocal("$" + getName(), 0, getReturnType(), isPrimitiveReturnType(), true, getModifiers(), this);
        }
    }
    
    /**
     * Collects local variables declared in this method.
     * @param node the AST node for this method
     */
    protected void collectLocalVariables(ASTNode node) {
        LocalDeclarationCollector lvisitor = new LocalDeclarationCollector(this);
        node.accept(lvisitor);
        
        for (JavaLocal jl : lvisitor.getLocalDeclarations()) {
            locals.add(jl);
        }
        lvisitor.clear();
    }
    
    /**
     * Collects fields that this method accesses.
     * @param node an AST node for this method
     * @return <code>true</code> if all bindings for the variables were found, otherwise <code>false</code>
     */
    protected void collectAccessedFields(ASTNode node) {
        FieldAccessCollector fvisitor = new FieldAccessCollector(getDeclaringJavaClass().getJavaPackage().getJavaProject());
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
     * Collects methods that this method calls.
     * @param node an AST node for this method
     * @return @return <code>true</code> if all bindings for the methods were found, otherwise <code>false</code>
     */
    protected void collectCalledMethods(ASTNode node) {
        MethodCallCollector mvisitor = new MethodCallCollector(getDeclaringJavaClass().getJavaPackage().getJavaProject());
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
     * Collects types that this method uses.
     * @param node an AST node for this method
     */
    protected void collectUsedTypes(ASTNode node) {
        TypeCollector tvisitor = new TypeCollector(getDeclaringJavaClass().getJavaPackage().getJavaProject());
        astNode.accept(tvisitor);
        
        for (String str : tvisitor.getTypeUses()) {
            usedClassNames.add(str);
        }
        
        if (!tvisitor.isBindingOk()) {
            bindingOk = false;
        }
        tvisitor.clear();
    }
    
    /**
     * Tests if the binding for this method was found.
     * @return <code>true</code> if the binding was found
     */
    public boolean isBindingOk() {
        return bindingOk;
    }
    
    /**
     * Obtains the type list of all the parameters of a given method.
     * @param binding the binding for the method
     * @return the string including the fully qualified names of the parameter types, or an empty string if there is no parameter
     */
    private static String getParameterTypes(IMethodBinding binding) {
        StringBuffer buf = new StringBuffer();
        ITypeBinding[] types = binding.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            buf.append(" ");
            buf.append(types[i].getQualifiedName());
        }
        return buf.toString();
    }
    
    /**
     * Obtains the signature of this method.
     * @param binding the binding for the method 
     * @return the string of the method signature
     */
    public static String getSignature(IMethodBinding binding) {
        return binding.getName() + "(" + getParameterTypes(binding) + " )";
    }
    
    /**
     * Tests if this object represents a normal method.
     * @return <code>true</code> if the object represents a method, otherwise <code>false</code>
     */
    public boolean isMethod() {
        return !isConstructor && !isInitializer;
    }
    
    /**
     * Tests if this object represents a constructor.
     * @return <code>true</code> if the object represents a constructor, otherwise <code>false</code>
     */
    public boolean isConstructor() {
        return isConstructor;
    }
    
    /**
     * Tests if this object represents an initializer.
     * @return @return <code>true</code> if the object represents an initializer, otherwise <code>false</code>
     */
    public boolean isInitializer() {
        return isInitializer;
    }
    
    /**
     * Tests if this method exists in the project.
     * @return always <code>true</code>
     */
    public boolean isInProject() {
        return true;
    }
    
    /**
     * Returns the class that declares this method.
     * @return the class that declares this method
     */
    public JavaClass getDeclaringJavaClass() {
        return declaringClass;
    }
    
    /**
     * Returns the name of this method.
     * @return the name
     */
    public String getName() {
       return name;
    }
    
    /**
     * Returns the fully-qualified name of this method.
     * @return the name
     */
    public String getQualifiedName() {
        return declaringClass.getQualifiedName() + "#" + getSignature();
    }
    
    /**
     * Obtains the signature of this method.
     * @return the string of the method signature
     */
    public String getSignature() {
        return signature;
    }
    
    /**
     * Returns the return value of this method.
     * @return the return type
     */
    public String getReturnType() {
        return type;
    }
    
    /**
     * Tests if the type of this field is primitive.
     */
    public boolean isPrimitiveReturnType() {
        return isPrimitiveType(type);
    }
    
    /**
     * Tests if this method has no return value. 
     * @return <code>true</code> if there is no return value of this method, otherwise <code>false</code>
     */
    public boolean isVoid() {
        return type.compareTo("void") == 0;
    }
    
    /**
     * Returns the value representing modifiers of this method.
     * @return the modifiers value
     */
    public int getModifiers() {
        return modifiers;
    }
    
    /**
     * Returns the names of exceptions for this method.
     * @return the collection of exception names for this method
     */
    public Set<String> getExceptionNames() {
        return exceptionNames;
    }
    
    /**
     * Returns all the parameters of this method. 
     * @return the collection of the parameters
     */
    public List<JavaLocal> getParameters() {
        return parameters;
    }
    
    /**
     * Returns the number of the parameters of this method.
     * @return the number of the parameters
     */
    public int getParameterSize() {
        return parameters.size();
    }
    
    /**
     * Returns the parameter of this method or constructor at specified position.
     * @param pos the ordinal number of the parameter to be retrieved
     * @return the found parameter, <code>null</code> if none
     */
    public JavaLocal getParameter(int pos) {
        return parameters.get(pos);
    }
    
    /**
     * Returns the position of a parameter with a given name of this method.
     * @param name the name of the parameter to be retrieved
     * @return the ordinal number of the parameter to be retrieved, or <code>-1</code> if none
     */
    private int getParameterOrdinal(String name) {
        for (int pos = 0; pos < getParameterSize(); pos++) {
            JavaLocal param = getParameter(pos);
            if (param.getName().compareTo(name) == 0) {
                return pos;
            }
        }
        return -1;
    }
    
    /**
     * Returns the parameter with a given name of this method.
     * @param name the name of the parameter to be retrieved
     * @return the found parameter, or <code>null</code> if none
     */
    public JavaLocal getParameter(String name) {
        int pos = getParameterOrdinal(name);
        if (pos != -1) {
            return getParameter(pos);
        }
        return null;
    }
    
    /**
     * Returns the virtual variable that stores the return value of this method.
     * @return the virtual variable for the return value, or <code>null</code> if this method has no return value
     */
    public JavaLocal getReturnValueVariable() {
        return returnValue;
    }
    
    /**
     * Returns the local variable with a name and an integer number.
     * @param name the name of the local variable
     * @param id the integer number for specifying the local variable
     * @return the found local variable, or <code>null</code> if none
     */
    public JavaLocal getJavaLocal(String name, int id) {
        for (JavaLocal jlocal : locals) {
            if (name.compareTo(jlocal.getName()) == 0 && id == jlocal.getId()) {
                return jlocal;
            }
        }
        return null;
    }
    
    /**
     * Tests if the access setting of this method is public.
     * @return <code>true</code> if this is a public method, otherwise <code>false</code>
     */
    public boolean isPublic() {
        return Modifier.isPublic(modifiers);
    }
    
    /**
     * Tests if the access setting of this method is protected.
     * @return <code>true</code> if this is a protected method, otherwise <code>false</code>
     */
    public boolean isProtected() {
        return Modifier.isProtected(modifiers);
    }
    
    /**
     * Tests if the access setting of this method is private.
     * @return <code>true</code> if this is a private method, otherwise <code>false</code>
     */
    public boolean isPrivate() {
        return Modifier.isPrivate(modifiers);
    }
    
    /**
     * Tests if the access setting of this method has default visibility.
     * @return <code>true</code> if this is a method with default visibility, otherwise <code>false</code>
     */
    public boolean isDefault() {
        return !isPublic() && !isProtected() && !isPrivate();
    }
    
    /**
     * Tests if the access setting of this method is final.
     * @return <code>true</code> if this is a final method, otherwise <code>false</code>
     */
    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }
    
    /**
     * Tests if the access setting of this method is abstract.
     * @return <code>true</code> if this is an abstract method, otherwise <code>false</code>
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }
    
    /**
     * Tests if the access setting of this method is static.
     * @return <code>true</code> if this is a static method, otherwise <code>false</code>
     */
    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }
    
    /**
     * Tests if the access setting of this method is synchronized.
     * @return <code>true</code> if this is a synchronized method, otherwise <code>false</code>
     */
    public boolean isSynchronized() {
        return Modifier.isSynchronized(modifiers);
    }
    
    /**
     * Tests if the access setting of this method is native.
     * @return <code>true</code> if this is a native method, otherwise <code>false</code>
     */
    public boolean isNative() {
        return Modifier.isNative(modifiers);
    }
    
    /**
     * Tests if the access setting of this method is strictfp.
     * @return <code>true</code> if this is a strictfp method, otherwise <code>false</code>
     */
    public boolean isStrictfp() {
        return Modifier.isStrictfp(modifiers);
    }
    
    /**
     * Tests if this method has the signature and modifier that are the same as those of a given method.
     * @param jm the method to be checked
     * @return <code>true</code> if the signatures of this method and the given method are the same, otherwise <code>false</code>
     */
    public boolean hasSameSigantureAndModifier(JavaMethod jm) {
        return getSignature().compareTo(jm.getSignature()) == 0 && getModifiers() == jm.getModifiers();
    }
    
    /**
     * Obtains the source code of the file containing this method.
     */
    public String getSource() {
        return declaringClass.getSource();
    }
    
    /**
     * Obtains a string representing a method.
     * @param fqn the name of the class declaring the method
     * @param sig the signature of the method
     * @return the string
     */
    public static String getString(String fqn, String sig) {
        return fqn + "#" + sig;
    }
    
    /**
     * Obtains the name of the class declaring the method represented by a given string
     * @param str string representing the method
     * @return the class name
     */
    public static String getFqn(String str) {
        int index = str.indexOf('#');
        return str.substring(0, index);
    }
    
    /**
     * Obtains the signature of the method represented by a given string
     * @param str string representing the method
     * @return the method signature
     */
    public static String getSignature(String str) {
        int index = str.indexOf('#');
        return str.substring(index + 1, str.length());
    }
    
    /**
     * Tests if a given method equals to this.
     * @param jm the Java method
     * @return <code>true</code> if the given method equals to this, otherwise <code>false</code>
     */
    public boolean equals(JavaMethod jm) {
        if (jm == null) {
            return false;
        }
        
        if (this == jm) {
            return true;
        }
        
        return getDeclaringJavaClass().equals(jm.getDeclaringJavaClass()) &&
               getSignature().compareTo(jm.getSignature()) == 0;
    }
    
    /**
     * Returns a hash code value for this method.
     * @return the hash code value for the method
     */
    public int hashCode() {
        return getSignature().hashCode();
    }
    
    /**
     * Collects information about this method or constructor for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("METHOD: ");
        buf.append(getSignature());
        buf.append("@");
        buf.append(getReturnType());
        buf.append("\n");
        
        buf.append(getAnnotationInfo());
        
        return buf.toString();
    }
    
    /**
     * Collects information about all parameters of this method or constructor.
     * @return the string for printing
     */
    public String getParameterInfo() {
        StringBuffer buf = new StringBuffer();
        for (JavaLocal param : parameters) {
            buf.append(" PARAMETER : ");
            buf.append(param.getName());
            buf.append("\n");
        }
        
        return buf.toString();
    }
    
    /**
     * Collects information about all parameters of this method or constructor.
     * @return the string for printing
     */
    public String getCallMethodInfo() {
        StringBuffer buf = new StringBuffer();
        for (JavaMethod jm : getCalledJavaMethods()) {
            buf.append(" THIS METHOD CALLS : ");
            buf.append(jm.getSignature());
            buf.append("\n");
        }
        
        return buf.toString();
    }
    
    /* ================================================================================
     * The following functionalities can be used after completion of whole analysis 
     * ================================================================================ */
    
    /**
     * The collection of types representing all exceptions this method or constructor might throw.
     */
    protected Set<JavaClass> exceptions = new HashSet<JavaClass>();
    
    /**
     * The collection of all methods that call this method.
     */
    protected Set<JavaMethod> callingMethods = new HashSet<JavaMethod>();
    
    /**
     * The collection of all methods that this method calls.
     */
    protected Set<JavaMethod> calledMethods = new HashSet<JavaMethod>();
    
    /**
     * The collection of all fields that this method accesses.
     */
    protected Set<JavaField> accessedFields = new HashSet<JavaField>();
    
    /**
     * The collection of all field declarations that call this method.
     */
    protected Set<JavaField> accessingFields = new HashSet<JavaField>();
    
    /**
     * The collections of types of accessed fields and local variables.
     */
    protected Set<JavaClass> usedClasses = new HashSet<JavaClass>();
    
    /**
     * The collection of methods that this method overrides.
     */
    protected Set<JavaMethod> overriddenMethods = new HashSet<JavaMethod>();
    
    /**
     * The collection of methods that override this method.
     */
    protected Set<JavaMethod> overridingMethods = new HashSet<JavaMethod>();
    
    /**
     * Collects additional information on this method.
     */
    public void collectLevel2Info() {
        findsAccessedFields();
        findCalledMethods();
        findUsedTypes();
        findExceptionTypes();
        findOverriddenMethods();
    }
    
    /**
     * Finds fields that this method accesses.
     */
    protected void findsAccessedFields() {
        for (String str : accessedFieldNames) {
            String fqn = JavaField.getFqn(str);
            String name = JavaField.getName(str);
            
            JavaField jf = getDeclaringJavaField(fqn, name);
            if (jf != null) {
                accessedFields.add(jf);
                jf.addCallingJavaMethod(this);
            }
        }
    }
    
    /**
     * Finds methods that this method calls.
     */
    protected void findCalledMethods() {
        for (String str : calledMethodNames) {
            String fqn = JavaMethod.getFqn(str);
            String sig = JavaMethod.getSignature(str);
            JavaMethod jm = getDeclaringJavaMethod(fqn, sig);
            if (jm != null) {
                calledMethods.add(jm);
                jm.addCallingMethod(this);
            }
        }
    }
    
    /**
     * Collects types that this method uses.
     */
    protected void findUsedTypes() {
        for (String str : usedClassNames) {
            String fqn = JavaClass.getFqn(str);
            JavaClass jc = getDeclaringJavaClass(fqn);
            if (jc != null) {
                usedClasses.add(jc);
            }
        }
    }
    
    /**
     * Adds a method that calls this method.
     * @param jm the method calling this method
     */
    public void addCallingMethod(JavaMethod jm) {
        bindingCheck();
        
        callingMethods.add(jm);
    }
    
    /**
     * Adds a field that calls this method.
     * @param jf the field
     */
    public void addAccessingJavaField(JavaField jf) {
        accessingFields.add(jf);
    }
    
    /**
     * Finds types of exceptions that this method might throw.
     */
    protected void findExceptionTypes() {
        for (String fqn : getExceptionNames()) {
            JavaClass jc = JavaClass.getJavaClass(fqn);
            exceptions.add(jc);
        }
    }
    
    /**
     * Finds methods that this method overrides.
     */
    protected void findOverriddenMethods() {
        JavaClass jc = getDeclaringJavaClass();
        for (JavaClass c : getAllSuperClassess(jc)) {
             for (JavaMethod jm : c.getJavaMethods()) {
                 if (hasSameSigantureAndModifier(jm)) {
                     overriddenMethods.add(jm);
                     jm.addOverridingMethod(this);
                 }
             }
        }
    }
    
    /**
     * Returns all the super classes of this class.
     * @param jc the base class
     * @return the collection of the super classes in bottom-up order.
     */
    private List<JavaClass> getAllSuperClassess(JavaClass jc) {
        List<JavaClass> classes = new ArrayList<JavaClass>();
        JavaClass parent = getDeclaringJavaClass(jc.getSuperClassName());
        while (parent != null) {
            classes.add(parent);
            parent = getDeclaringJavaClass(parent.getSuperClassName());
        }
        return classes;
    }
    
    private void addOverridingMethod(JavaMethod jm) {
        overridingMethods.add(jm);
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
     * Returns the exceptions for this method.
     * @return the collection of exception types for this method
     */
    public Set<JavaClass> getExceptions() {
        bindingCheck();
        return exceptions;
    }
    
    /**
     * Returns all the methods that this method overrides.
     * @return the collection of the overridden methods
     */
    public Set<JavaMethod> getOverriddenJavaMethods() {
        bindingCheck();
        return overriddenMethods;
    }
    
    /**
     * Returns all the methods that overrides this method.
     * @return the collection of the overriding methods
     */
    public Set<JavaMethod> getOverridingJavaMethods() {
        bindingCheck();
        return overridingMethods;
    }
    
    /**
     * Returns all the methods that call this method.
     * @return the collection of the calling methods
     */
    public Set<JavaMethod> getCallingJavaMethods() {
        bindingCheck();
        return callingMethods;
    }
    
    /**
     * Returns all the methods that call this method.
     * @return the collection of the calling methods in the project
     */
    public Set<JavaMethod> getCallingJavaMethodsInProject() {
        bindingCheck();
        
        Set<JavaMethod> methods = new HashSet<JavaMethod>();
        for (JavaMethod jm : getCallingJavaMethods()) {
            if (jm.isInProject()) {
                methods.add(jm);
            }
        }
        return methods;
    }
    
    /**
     * Returns all the methods that this method calls.
     * @return the collection of the called methods
     */
    public Set<JavaMethod> getCalledJavaMethods() {
        bindingCheck();
        return calledMethods;
    }
    
    /**
     * Returns all the methods that this method calls.
     * @return the collection of the called methods in the project
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
    
    /**
     * Returns all the field variables accessed within this method.
     * @return the collection of the accessed variables
     */
    public Set<JavaField> getAccessedJavaFields() {
        bindingCheck();
        return accessedFields;
    }
    
    /**
     * Returns all the field variables accessed within this method within the project.
     * @return the collection of the accessed variables within the project
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
     * Returns all the fields that call this method.
     * @return the collection of the accessing fields
     */
    public Set<JavaField> getAccessingJavaFields() {
        bindingCheck();
        return accessingFields;
    }
    
    /**
     * Returns all the classes by this method.
     * @return the collection of the used classes
     */
    public Set<JavaClass> getUsedJavaClasses() {
        bindingCheck();
        return usedClasses;
    }
    
    /**
     * Returns all the classes by this method within the project.
     * @return the collection of the used classes within the project
     */
    public Set<JavaClass> getUsedJavaClassesInProject() {
        bindingCheck();
        
        Set<JavaClass> classes = new HashSet<JavaClass>();
        for (JavaClass jc : getUsedJavaClasses()) {
            if (jc.isInProject()) {
                classes.add(jc);
            }
        }
        return classes;
    }
}

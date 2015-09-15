/*
 *  Copyright 2015, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * An object representing a method call.
 * @author Katsuhisa Maruyama
 */
public class JavaMethodCall extends JavaExpression {
    
    static Logger logger = Logger.getLogger(JavaMethodCall.class.getName());
    
    /**
     * The name of the called method.
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
     * The collection of arguments for this method call.
     */
    protected List<JavaExpression> arguments = new ArrayList<JavaExpression>();
    
    /**
     * The collection of argument types for this method call.
     */
    protected List<String> argumentTypes = new ArrayList<String>();
    
    /**
     * The method containing this method call.
     */
    protected JavaMethod declaringMethod = null;
    
    /**
     * The name of class declaring the called method.
     */
    protected String classNameOfCalledMethod;
    
    /**
     * A flag that indicates all bindings for types, methods, and variables were found.
     */
    protected boolean bindingOk = true;
    
    /**
     * Creates a new, empty object.
     */
    protected JavaMethodCall() {
        super();
    }
    
    /**
     * Creates a new object representing a method call.
     * @param node an AST node for this call
     */
    protected JavaMethodCall(ASTNode node) {
        super(node);
    }
    
    /**
     * Creates a new object representing a method call.
     * @param node an AST node for this call
     * @param binding the method binding for this call
     * @param jm the method containing this call
     */
    @SuppressWarnings("unchecked")
    public JavaMethodCall(MethodInvocation node, IMethodBinding binding, JavaMethod jm) {
        super(node);
        
        declaringMethod = jm;
        
        if (binding != null) {
            name = binding.getName();
            signature = getSignature(binding);
            type = binding.getReturnType().getQualifiedName();
            
            setArguments(node.arguments());
            setArgumentTypes(binding);
            
            classNameOfCalledMethod = binding.getDeclaringClass().getQualifiedName();
            
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
    }
    
    /**
     * Creates a new object representing a method call.
     * @param node an AST node for this call
     * @param binding the method binding for this call
     * @param jm the method containing this call
     */
    @SuppressWarnings("unchecked")
    public JavaMethodCall(SuperMethodInvocation node, IMethodBinding binding, JavaMethod jm) {
        super(node);
        
        if (binding != null) {
            name = binding.getName();
            signature = getSignature(binding);
            type = binding.getReturnType().getQualifiedName();
            
            setArguments(node.arguments());
            setArgumentTypes(binding);
            
            declaringMethod = jm;
            classNameOfCalledMethod = binding.getDeclaringClass().getQualifiedName();
            
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
    }
    
    /**
     * Creates a new object representing a constructor call.
     * @param node an AST node for this call
     * @param binding the method binding for this call
     * @param jm the method containing this call
     */
    @SuppressWarnings("unchecked")
    public JavaMethodCall(ConstructorInvocation node, IMethodBinding binding, JavaMethod jm) {
        super(node);
        
        if (binding != null) {
            name = binding.getName();
            signature = getSignature(binding);
            type = binding.getName();;
            
            setArguments(node.arguments());
            setArgumentTypes(binding);
            
            declaringMethod = jm;
            classNameOfCalledMethod = binding.getDeclaringClass().getQualifiedName();
            
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
    }
    
    /**
     * Creates a new object representing a constructor call.
     * @param node an AST node for this call
     * @param binding the method binding for this call
     * @param jm the method containing this call
     */
    @SuppressWarnings("unchecked")
    public JavaMethodCall(SuperConstructorInvocation node, IMethodBinding binding, JavaMethod jm) {
        super(node);
        
        if (binding != null) {
            name = binding.getName();
            signature = getSignature(binding);
            type = binding.getName();;
            
            setArguments(node.arguments());
            setArgumentTypes(binding);
            
            declaringMethod = jm;
            classNameOfCalledMethod = binding.getDeclaringClass().getQualifiedName();
            
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
    }
    
    /**
     * Creates a new object representing instance creation with a constructor call.
     * @param node an AST node for this call
     * @param binding the method binding for this call
     * @param jm the method containing this call
     */
    @SuppressWarnings("unchecked")
    public JavaMethodCall(ClassInstanceCreation node, IMethodBinding binding, JavaMethod jm) {
        super(node);
        
        if (binding != null) {
            name = binding.getName();
            signature = getSignature(binding);
            type = binding.getName();
            
            setArguments(node.arguments());
            setArgumentTypes(binding);
            
            declaringMethod = jm;
            classNameOfCalledMethod = binding.getDeclaringClass().getQualifiedName();
            
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
    }
    
    /**
     * Creates a new object representing enum constant creation with a constructor call.
     * @param node an AST node for this call
     * @param binding the method binding for this call
     * @param jm the method containing this call
     */
    @SuppressWarnings("unchecked")
    public JavaMethodCall(EnumConstantDeclaration node, IMethodBinding binding, JavaMethod jm) {
        super(node);
        
        if (binding != null) {
            name = binding.getName();
            signature = getSignature(binding);
            type = binding.getName();
            
            setArguments(node.arguments());
            setArgumentTypes(binding);
            
            declaringMethod = jm;
            classNameOfCalledMethod = binding.getDeclaringClass().getQualifiedName();
            
        } else {
            name = ".UNKNOWN";
            bindingOk = false;
        }
    }
    
    /**
     * Sets arguments of this method call.
     * @param args the list of arguments
     */
    private void setArguments(List<Expression> args) {
        for (Expression arg : args) {
            JavaExpression expr = new JavaExpression(arg);
            arguments.add(expr);
        }
    }
    
    /**
     * Sets argument types of this method call.
     * @param binding the binding for the method
     */
    private void setArgumentTypes(IMethodBinding binding) {
        ITypeBinding[] types = binding.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            argumentTypes.add(types[i].getQualifiedName());
        }
    }
    
    /**
     * Obtains the type list of all the arguments of this method call.
     * @param binding the binding for the method
     * @return the string including the fully qualified names of the argument types, or an empty string if there is no parameter
     */
    private static String getArgumentTypes(IMethodBinding binding) {
        StringBuffer buf = new StringBuffer();
        ITypeBinding[] types = binding.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            buf.append(" ");
            buf.append(types[i].getQualifiedName());
        }
        return buf.toString();
    }
    
    /**
     * Obtains the signature of this method or constructor call.
     * @return the string of the method signature.
     */
    private static String getSignature(IMethodBinding binding) {
        return binding.getName() + "(" + getArgumentTypes(binding) + " )";
    }
    
    /**
     * Tests if the binding for this class was found.
     * @return <code>true</code> if the binding was found
     */
    public boolean isBindingOk() {
        return bindingOk;
    }
    
    /**
     * Returns the method that declares this method call.
     * @return the method that declares this method call
     */
    public JavaMethod getDeclaringJavaMethod() {
        return declaringMethod;
    }
    
    /**
     * Returns the name of the called method.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the fully-qualified name of the called method.
     * @return the name
     */
    public String getQualifiedName() {
        if (classNameOfCalledMethod != null) {
            return classNameOfCalledMethod + "#" + getSignature();
        }
        return getSignature();
    }
    
    /**
     * Returns the return type of the called method.
     * @return the return type
     */
    public String getReturnType() {
        return type;
    }
    
    /**
     * Obtains the signature of the called method.
     * @return the string of the method signature
     */
    public String getSignature() {
        return signature;
    }
    
    /**
     * Tests if the called method has no return value. 
     * @return <code>true</code> if there is no return value of the called method, otherwise <code>false</code>
     */
    public boolean isVoid() {
        return getReturnType().compareTo("void") == 0;
    }
    
    /**
     * Returns all the arguments of this method call.
     * @return the collection of the arguments
     */
    public List<JavaExpression> getArguments() {
        return arguments;
    }
    
    /**
     * Returns the number of arguments of this method call.
     * @return the number of arguments
     */
    public int getArgumentSize() {
        return arguments.size();
    }
    
    /**
     * Returns the argument of this method call at specified position.
     * @param pos the ordinal number of the argument to be retrieved
     * @return the found argument, <code>null</code> if no argument was found
     */
    public JavaExpression getArgument(int pos) {
        if (pos < arguments.size()) {
            return arguments.get(pos);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the argument type of this method call at specified position.
     * @param pos the ordinal number of the argument to be retrieved
     * @return the string of the found argument type, <code>null</code> if no argument was found
     */
    public String getArgumentType(int pos) {
        return argumentTypes.get(pos);
    }
    
    /**
     * Tests if a given method call equals to this one.
     * @param obj the method call
     * @return <code>true</code> if the given method call equals to this one, otherwise <code>false</code>
     */
    public boolean equals(Object obj) {
        if (obj instanceof JavaMethodCall) {
            JavaMethodCall jmc = (JavaMethodCall)obj;
            return equals(jmc);
        }
        return false;
    }
    
    /**
     * Tests if a given method call equals to this one.
     * @param jmc the method call
     * @return <code>true</code> if the given method call equals to this one, otherwise <code>false</code>
     */
    public boolean equals(JavaMethodCall jmc) {
        if (jmc == null) {
            return false;
        }
        
        if (this == jmc) {
            return true;
        }
        
        return equals(jmc.getJavaMethod());
    }
    
    /**
     * Tests if this method call corresponds to a given method.
     * @param jm the method which is compared to
     * @return <code>true</code> if this method call corresponds to the method, otherwise <code>false</code>
     */
    public boolean equals(JavaMethod jm) {
        if (jm == null) {
            return false;
        }
        
        return jm.equals(getJavaMethod());
    }
    
    /**
     * Returns a hash code value for this method call.
     * @return the hash code value for the method call
     */
    public int hashCode() {
        return getQualifiedName().hashCode();
    }
    
    /* ================================================================================
     * The following functionalities can be used after completion of whole analysis 
     * ================================================================================ */
    
    /**
     * The method corresponding to this method call.
     */
    protected JavaMethod jmethod;
    
    /**
     * Displays error log if the binding has not completed yet.
     */
    private void bindingCheck() {
        if (getBindingLevel() < 1) {
            logger.info("This API can be called after the completion of whole analysis");
        }
    }
    
    /**
     * Returns the Java class enclosing the called method.
     * @return the enclosing class for the called method
     */
    public JavaClass getJavaClassOf() {
        bindingCheck();
        
        JavaMethod jm = getJavaMethod();
        if (jm != null) {
            return jm.getDeclaringJavaClass();
        }
        return null;
    }
    
    /**
     * Returns a method corresponding to this method call.
     * @return the found method, or <code>null</code> if none
     */
    public JavaMethod getJavaMethod() {
        bindingCheck();
        
        if (jmethod != null) {
            return jmethod;
        }
        
        jmethod = getDeclaringJavaMethod(classNameOfCalledMethod, signature);
        return jmethod;
    }
    
    /**
     * Tests if this method call directly calls the method itself.
     * @return <code>true</code> if this method call directly calls the method itself, otherwise <code>false</code>
     */
    public boolean callSelfDirectly() {
        bindingCheck();
        
        if (declaringMethod == null) {
            return false;
        }
        return getJavaMethod().equals(declaringMethod);
    }
    
    /**
     * Tests if this method call recursively calls the method itself.
     * @return <code>true</code> if this method call recursively calls the method itself, otherwise <code>false</code>
     */
    public boolean callSelfRecursively() {
        bindingCheck();
        
        if (declaringMethod == null) {
            return false;
        }
        HashSet<JavaMethod> methods = new HashSet<JavaMethod>();
        collectCalledMethodsInProject(getJavaMethod(), methods);
        return methods.contains(declaringMethod);
    }
    
    /**
     * Collects all called methods that this method call traverses.
     * @param jm the called method to be checked and collected
     * @param methods the collection of the called methods
     */
    private void collectCalledMethodsInProject(JavaMethod jm, HashSet<JavaMethod> methods) {
        methods.add(jm);
        for (JavaMethod cm : jm.getCalledJavaMethods()) {
            if (!methods.contains(cm) && cm.isInProject()) {
                collectCalledMethodsInProject(cm, methods);
            }
        }
    }
    
    /**
     * Collects information about this method or constructor call for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("METHOD CALL: " + getQualifiedName());
        return buf.toString();
    }
}

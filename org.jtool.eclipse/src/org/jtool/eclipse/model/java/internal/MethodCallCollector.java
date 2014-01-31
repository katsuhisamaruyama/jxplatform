/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.model.java.JavaProject;
import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaMethod;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import java.util.Set;
import java.util.HashSet;

/**
 * Visits a Java program and stores information on method invocation appearing in a method or field.
 * 
 * MethodInvocation
 * SuperMethodInvocation
 * ConstructorInvocation
 * SuperConstructorInvocation
 * ClassInstanceCreation
 * 
 * @see org.eclipse.jdt.core.dom.Expression
 * @author Katsuhisa Maruyama
 */
public class MethodCallCollector extends ASTVisitor {
    
    /**
     * The project containing method calls to be collected.
     */
    private JavaProject jproject;
    
    /**
     * The collection of method calls.
     */
    private Set<String> methodCalls = new HashSet<String>();
    
    /**
     * A flag that indicates all bindings for methods were found.
     */
    private boolean bindingOk = true;
    
    /**
     * Creates a new object for collecting methods called by any method or field.
     * @param jproject the project containing the method calls or field accesses
     */
    public MethodCallCollector(JavaProject jproject) {
        super();
        
        this.jproject = jproject;
    }
    
    /**
     * Clears information about the collected methods.
     */
    public void clear() {
        methodCalls.clear();
    }
    
    /**
     * Returns all the methods that this method calls.
     * @return the collection of the methods
     */
    public Set<String> getMethodCalls() {
        return methodCalls;
    }
    
    /**
     * Tests if all method bindings were found.
     * @return <code>true</code> if all the method bindings were found
     */
    public boolean isBindingOk() {
        return bindingOk;
    }
    
    /**
     * Visits a method invocation node and stores its information.
     * @param node the method invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(MethodInvocation node) {
        addJavaMethodCall(node.resolveMethodBinding());
        return false;
    }
    
    /**
     * Visits a super-method invocation node and stores its information.
     * @param node the super-method invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SuperMethodInvocation node) {
        addJavaMethodCall(node.resolveMethodBinding());
        return false;
    }
    
    /**
     * Visits a constructor invocation node and stores its information.
     * @param node the constructor invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ConstructorInvocation node) {
        addJavaMethodCall(node.resolveConstructorBinding());
        return false;
    }
    
    /**
     * Visits a super-constructor invocation node and stores its information.
     * @param node the super-constructor invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SuperConstructorInvocation node) {
        addJavaMethodCall(node.resolveConstructorBinding());
        return false;
    }
    
    /**
     * Visits a method invocation node and stores its information.
     * @param node the method invocation node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(ClassInstanceCreation node) {
        addJavaMethodCall(node.resolveConstructorBinding());
        return false;
    }
    
    /**
     * Collects the method invocation information.
     * @param mbinding the method binding for this method invocation
     */
    private void addJavaMethodCall(IMethodBinding mbinding) {
        if (mbinding != null) {
            ITypeBinding tbinding = mbinding.getDeclaringClass();
            
            String fqn;
            if (tbinding != null && isInProject(mbinding)) {
                fqn = JavaClass.createClassName(tbinding); 
            } else {
                JavaMethod jm = ExternalJavaMethod.create(mbinding);
                fqn = jm.getDeclaringJavaClass().getQualifiedName();
            }
            
            String str = JavaMethod.getString(fqn, JavaMethod.getSignature(mbinding));
            methodCalls.add(str);
            
        } else {
            bindingOk = false;
        }
    }
    
    /**
     * Tests if the called method is contained in the project containing the method call.
     * @param mbinding the method binding of the method call
     * @return <code>true</code> if the called method is contained in the project, otherwise <code>false</code>
     */
    private boolean isInProject(IMethodBinding mbinding) {
        IJavaProject project = jproject.getJavaProject();
        try {
            IType type = project.findType(mbinding.getDeclaringClass().getQualifiedName());
            if (type != null) {
                String pdir = project.getPath().toString();
                String tname = type.getPath().toString();
                return pdir != null && tname != null && tname.startsWith(pdir);
            }
        } catch (JavaModelException e) {
            return false;
        }
        return false;
    }
}

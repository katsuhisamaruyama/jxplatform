/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import java.util.Map;
import java.util.HashMap;

/**
 * An object representing a method or a constructor whose source code exists outside the project.
 * @author Katsuhisa Maruyama
 */
public class ExternalJavaMethod extends JavaMethod {
    
    /**
     * The cache for all external methods.
     */
    protected static Map<String, ExternalJavaMethod> cache = new HashMap<String, ExternalJavaMethod>();
    
    /**
     * Creates a new, empty object.
     */
    protected ExternalJavaMethod() {
        super();
    }
    
    /**
     * Creates a new object representing a method or a constructor.
     * @param fqn the fully-qualified name of a class declaring this method
     * @param sig the signature of this method
     */
    protected ExternalJavaMethod(String fqn, String sig) {
        super();
        
        this.name = fqn + "#" + sig;
        this.signature = sig;
        this.type = null;
        declaringClass = ExternalJavaClass.create(fqn, null);
    }
    
    /**
     * Creates a new object representing a method or a constructor.
     * @param binding a method binding for the method
     */
    protected ExternalJavaMethod(IMethodBinding binding) {
        super();
        
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
            
            declaringClass = ExternalJavaClass.create(binding.getDeclaringClass());
        }
    }
    
    /**
     * Creates a new object representing a method.
     * @param binding a method binding for the method
     * @return the created object
     */
    public static ExternalJavaMethod create(IMethodBinding binding) {
        String fqn;
        if (binding.getDeclaringClass() != null) {
            fqn = JavaClass.createClassName(binding.getDeclaringClass());
        } else {
            fqn = ExternalJavaClass.getArrayClassFqn();
        }
        String sig = getSignatureString(binding);
        
        ExternalJavaMethod jmethod = cache.get(JavaMethod.getString(fqn, sig));
        if (jmethod != null) {
            return jmethod;
        }
        
        jmethod = new ExternalJavaMethod(binding);
        cache.put(JavaMethod.getString(fqn, sig), jmethod);
        return jmethod;
    }
    
    /**
     * Creates a new object representing a method.
     * @param fqn the fully-qualified name of a class declaring this method
     * @param sig the signature of this method
     * @return the created object
     */
    public static ExternalJavaMethod create(String fqn, String sig) {
        ExternalJavaMethod jmethod = cache.get(JavaMethod.getString(fqn, sig));
        if (jmethod != null) {
            return jmethod;
        }
        
        jmethod = new ExternalJavaMethod(fqn, sig);
        cache.put(JavaMethod.getString(fqn, sig), jmethod);
        return jmethod;
    }
    
    /**
     * Tests if this method exists in the project.
     * @return always <code>false</code>
     */
    public boolean isInProject() {
        return false;
    }
    
    /**
     * Returns the class that declares this method.
     * @return the class that declares this method, <code>null</code> if none
     */
    public JavaClass getDeclaringJavaClass() {
        return (ExternalJavaClass)declaringClass;
    }
    
    /**
     * Tests if a given method equals to this.
     * @param jm the Java method
     * @return <code>true</code> if the given method equals to this, otherwise <code>false</code>
     */
    public boolean equals(ExternalJavaMethod jm) {
        if (jm == null) {
            return false;
        }
        
        return this == jm || (getDeclaringJavaClass().equals(jm.getDeclaringJavaClass()) &&
                              getSignature().compareTo(jm.getSignature()) == 0); 
    }
    
    /**
     * Returns a hash code value for this method.
     * @return the hash code value for the method
     */
    public int hashCode() {
        return getSignature().hashCode();
    }
    
    /**
     * Collects information about this method.
     * @return the string for printing
     */
    public String toString() {
        return "EXTERNAL METHOD: " + signature;
    }
}

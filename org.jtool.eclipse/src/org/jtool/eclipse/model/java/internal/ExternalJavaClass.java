/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.model.java.JavaClass;
import org.eclipse.jdt.core.dom.ITypeBinding;
import java.util.Map;
import java.util.HashMap;

/**
 * An object representing a class whose source code exists outside the project.
 * @author Katsuhisa Maruyama
 */
public class ExternalJavaClass extends JavaClass {
    
    /**
     * The cache for all external classes.
     */
    protected static Map<String, ExternalJavaClass> cache = new HashMap<String, ExternalJavaClass>();
    
    /**
     * Creates a new, empty object.
     */
    protected ExternalJavaClass() {
        super();
    }
    
    /**
     * Creates a new object representing a class
     * @param fqn the fully-qualified name of the class
     */
    protected ExternalJavaClass(String fqn) {
        super();
        
        this.name = fqn;
        this.fqn = fqn;
    }
    
    /**
     * Creates a new object representing a class.
     * @param binding a type binding for the class
     * @return the created object
     */
    public static ExternalJavaClass create(ITypeBinding tbinding) {
        String fqn;
        if (tbinding != null) {
            fqn = JavaClass.createClassName(tbinding);
        } else {
            fqn = getArrayClassFqn();
        }
        
        return create(fqn);
    }
    
    /**
     * Creates a new object representing a class. 
     * @param fqn the fully-qualified name of the class
     * @return the created object
     */
    public static ExternalJavaClass create(String fqn) {
        ExternalJavaClass jclass = cache.get(JavaClass.getString(fqn));
        if (jclass != null) {
            return jclass;
        }
        
        jclass = new ExternalJavaClass(fqn);
        cache.put(JavaClass.getString(fqn), jclass);
        
        return jclass;
    }
    
    /**
     * Returns the name of the array that is a special in Java.
     * @return the name of the array
     */
    public static String getArrayClassFqn() {
        return ".JavaArray";
    }
    
    /**
     * Tests if this class exists in the project.
     * @return always <code>false</code>
     */
    public boolean isInProject() {
        return false;
    }
    
    /**
     * Tests if a given class equals to this.
     * @param jc the class
     * @return <code>true</code> if the given class equals to this, otherwise <code>false</code>
     */
    public boolean equals(ExternalJavaClass jc) {
        if (jc == null) {
            return false;
        }
        
        return this == jc || getQualifiedName().compareTo(jc.getQualifiedName()) == 0; 
    }
    
    /**
     * Returns a hash code value for this class.
     * @return the hash code value for the class
     */
    public int hashCode() {
        return getQualifiedName().hashCode();
    }
    
    /**
     * Collects information about this class.
     * @return the string for printing
     */
    public String toString() {
        return "!" + getQualifiedName();
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.model.java.JavaClass;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.ITypeBinding;
import java.util.Set;
import java.util.HashSet;

/**
 * Visits a Java program and stores information on types used in the program.
 * 
 * Type:
 *   PrimitiveType
 *   ArrayType
 *   SimpleType
 *   QualifiedType
 *   ParameterizedType
 *   WildcardType
 * PrimitiveType:
 *   byte
 *   short
 *   char
 *   int
 *   long
 *   float
 *   double
 *   boolean
 *   void
 * ArrayType:
 *   Type [ ]
 * SimpleType:
 *   TypeName
 * ParameterizedType:
 *   Type < Type { , Type } >
 * QualifiedType:
 *   Type . SimpleName
 * WildcardType:
 *   ? [ ( extends | super) Type ]
 * 
 * @see org.eclipse.jdt.core.dom.Type
 * @author Katsuhisa Maruyama
 */
public class TypeCollector extends ASTVisitor {
    
    /**
     * The collection of classes that this class uses.
     */
    private Set<String> typeUses = new HashSet<String>();
    
    /**
     * A flag that indicates all bindings for types were found.
     */
    private boolean bindingOk = true;
    
    /**
     * Creates a new object for collecting classes used in this class.
     */
    public TypeCollector() {
        super();
    }
    
    /**
     * Clears information about the collected types.
     */
    public void clear() {
        typeUses.clear();
    }
    
    /**
     * Returns all the classes that this class uses.
     * @return the collection of the classes 
     */
    public Set<String> getTypeUses() {
        return typeUses;
    }
    
    /**
     * Tests if all type bindings were found.
     * @return <code>true</code> if all the type bindings were found
     */
    public boolean isBindingOk() {
        return bindingOk;
    }
    
    /**
     * Visits a primitive type node.
     * @param node the primitive type node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(PrimitiveType node) {
        return false;
    }
    
    /**
     * Visits a simple type node and stores its information.
     * @param node the type declaration node
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SimpleType node) {
        ITypeBinding tbinding = node.resolveBinding();
        if (tbinding != null) {
            String fqn = JavaClass.createClassName(tbinding);
            typeUses.add(JavaClass.getString(fqn));
        } else {
            bindingOk = false;
        }
        
        return false;
    }
}

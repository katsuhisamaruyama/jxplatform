/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import java.util.Set;
import java.util.HashSet;

/**
 * Visits a Java program and stores information on variable access appearing in a method or field.
 * 
 * Name:
 *   SimpleName
 *   QualifiedName
 * 
 * @see org.eclipse.jdt.core.dom.Expression
 * @author Katsuhisa Maruyama
 */
public class FieldAccessCollector extends ASTVisitor {
    
    /**
     * The project containing method calls to be collected.
     */
    private JavaProject jproject;
    
    /**
     * The collection of fields accessed.
     */
    private Set<String> accessedFields = new HashSet<String>();
    
    /**
     * A flag that indicates all bindings for fields were found.
     */
    private boolean bindingOk = true;
    
    /**
     * Creates a new object for collecting fields accessed by any method or field.
     * @param jproject the project containing the method calls or field accesses
     */
    public FieldAccessCollector(JavaProject jproject) {
        super();
        
        this.jproject = jproject;
    }
    
    /**
     * Clears information about the collected fields.
     */
    public void clear() {
        accessedFields.clear();
    }
    
    /**
     * Visits a name node and stores its information.
     * @param node the name node representing the variable access
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(Name node) {
        return true;
    }
    
    /**
     * Returns all the accessed variables.
     * @return the collection of the accessed variables
     */
    public Set<String> getAccessedFields() {
        return accessedFields;
    }
    
    /**
     * Tests if all method bindings were found.
     * @return <code>true</code> if all the method bindings were found
     */
    public boolean isBindingOk() {
        return bindingOk;
    }
    
    /**
     * Visits a name node and stores its information.
     * @param node the name node representing the variable access
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(SimpleName node) { 
        addJavaVariableAccess(node.resolveBinding());
        return false;
    }
    
    /**
     * Visits a name node and stores its information.
     * @param node the name node representing the variable access
     * @return <code>true</code> if this visit is continued inside, otherwise <code>false</code>
     */
    public boolean visit(QualifiedName node) {
        addJavaVariableAccess(node.resolveBinding());
        return false;
    }
    
    /**
     * Collects the field access information.
     * @param binding the variable binding
     */
    private void addJavaVariableAccess(IBinding binding) {
        if (binding != null) {
            if (binding.getKind() == IBinding.VARIABLE) {
                IVariableBinding vbinding = (IVariableBinding)binding;
                
                if (vbinding.isField() || vbinding.isEnumConstant()) {
                    ITypeBinding tbinding = vbinding.getDeclaringClass();
                    
                    String fqn;
                    if (tbinding != null && isInProject(vbinding)) {
                        fqn = JavaClass.createClassName(tbinding);
                    } else {
                        JavaField jf = ExternalJavaField.create(vbinding);
                        fqn = jf.getDeclaringJavaClass().getQualifiedName();
                    }
                    
                    String str = JavaField.getString(fqn, vbinding.getName());
                    accessedFields.add(str);
                }
            }
            
        } else {
            bindingOk = false;
        }
    }
    
    /**
     * Tests if the accessed field is contained in the project containing the field access.
     * @param vbinding the variable binding of the field access
     * @return <code>true</code> if the accessed field is contained in the project, otherwise <code>false</code>
     */
    private boolean isInProject(IVariableBinding vbinding) {
        IJavaProject project = jproject.getJavaProject();
        try {
            IType type = project.findType(vbinding.getDeclaringClass().getQualifiedName());
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

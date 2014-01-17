/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.eclipse.jdt.core.dom.ASTVisitor;

/**
 * Visits a Java program and stores its information.
 * @author Katsuhisa Maruyama
 */
public class JavaASTVisitor extends ASTVisitor {
    
    /**
     * A file corresponding to the compilation unit to be visited
     */
    protected JavaFile jfile;
    
    /**
     * Creates a new object for visiting a Java program. 
     */
    public JavaASTVisitor() {
        super();
    }
    
    /**
     * Closes this visitor.
     */
    public void close() {
        jfile = null;
    }
    
    /**
     * Creates a new object for visiting a Java program. 
     * @param jfile the file corresponding to the compilation unit to be visited
     */
    public void setJavaFile(JavaFile jfile) {
        this.jfile = jfile;
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */
 
package org.jtool.eclipse.handlers;

import org.jtool.eclipse.model.java.JavaModelFactory;
import org.jtool.eclipse.model.cfg.CFGFactory;
import org.jtool.eclipse.model.pdg.PDGFactory;
import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaProject;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Performs an action for a project.
 * @author Katsuhisa Maruyama
 */
public class ProjectAction2 extends ProjectAction {
    
    /**
     * Executes a command with information obtained from the application context.
     * @param event an event containing all the information about the current state of the application
     * @return the result of the execution.
     * @throws ExecutionException if an exception occurred during execution
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IJavaProject project = getJavaProject(event);
        if (project != null) {
            JavaModelFactory factory = new JavaModelFactory(project);
            JavaProject jproject = factory.create();
            
            createCFGs(jproject);
            createPDGs(jproject);
        }
        return null;
    }
    
    /**
     * Creates all CFGs for methods and fields with in the project.
     * @param the project
     */
    protected void createCFGs(JavaProject jproject) {
        CFGFactory.initialize();
        
        for (JavaClass jc : jproject.getJavaClasses()) {
            CFGFactory.create(jc);
        }
    }
    
    /**
     * Creates all PDGs for methods and fields with in the project.
     * @param the project
     */
    protected void createPDGs(JavaProject jproject) {
        for (JavaClass jc : jproject.getJavaClasses()) {
            PDGFactory.create(jc);
        }
    }
}

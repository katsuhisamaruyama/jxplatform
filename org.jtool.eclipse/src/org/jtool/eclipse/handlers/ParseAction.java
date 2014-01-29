/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */
 
package org.jtool.eclipse.handlers;

import org.jtool.eclipse.model.java.JavaASTDefaultVisitor;
import org.jtool.eclipse.model.java.JavaModelFactory;
import org.jtool.eclipse.model.java.JavaProject;
import org.jtool.eclipse.model.java.JavaClass;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Performs an action of parsing source code within a project.
 * @author Katsuhisa Maruyama
 */
public class ParseAction extends JtoolHandler {
    
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
            factory.setJavaASTVisitor(new JavaASTDefaultVisitor());
            JavaProject jproject = factory.create();
            
            for (JavaClass jc : jproject.getJavaClasses()) {
                System.out.println(jc.toString());
            }
        }
        return null;
    }
}

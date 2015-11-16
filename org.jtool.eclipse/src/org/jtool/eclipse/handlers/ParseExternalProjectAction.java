/*
 *  Copyright 2015, Katsuhisa Maruyama (maru@jtool.org)
 */
 
package org.jtool.eclipse.handlers;

import org.jtool.eclipse.Activator;
import org.jtool.eclipse.model.graph.GraphNodeIdPublisher;
import org.jtool.eclipse.model.java.JavaASTDefaultVisitor;
import org.jtool.eclipse.model.java.JavaModelFactory;
import org.jtool.eclipse.model.java.JavaModelFactoryInExternalProject;
import org.jtool.eclipse.model.java.JavaProject;
import org.jtool.eclipse.model.java.JavaClass;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import java.io.File;

/**
 * Performs an action of parsing source code within an external project, which exists under a specified directory.
 * @author Katsuhisa Maruyama
 */
public class ParseExternalProjectAction extends AbstractHandler {
    
    /**
     * Executes a command with information obtained from the application context.
     * @param event an event containing all the information about the current state of the application
     * @return the result of the execution.
     * @throws ExecutionException if an exception occurred during execution
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        
        DirectoryDialog dialog = new DirectoryDialog(window.getShell(), SWT.NULL);
        String dir = dialog.open();
        
        if (dir != null) {
            File f = new File(dir);
            while (f.isFile()) {
                MessageDialog.openError(null, Activator.PLUGIN_ID, "The file " + dir + " was fould. Please specify a directory name.");
                
                dialog.setFilterPath(dir);
                dir = dialog.open();
                if (dir == null) {
                    break;
                }
                f = new File(dir);
            }
        }
        
        if (dir == null) {
            return null;
        }
        
        JavaProject.removeAllCache();
        GraphNodeIdPublisher.reset();
        
        JavaModelFactory factory = new JavaModelFactoryInExternalProject(dir, dir);
        factory.setJavaASTVisitor(new JavaASTDefaultVisitor());
        JavaProject jproject = factory.create();
        
        for (JavaClass jc : jproject.getJavaClasses()) {
            System.out.println(jc.toString());
        }
        return null;
    }
}

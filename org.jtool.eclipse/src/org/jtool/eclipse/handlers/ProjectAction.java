/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */
 
package org.jtool.eclipse.handlers;

import org.jtool.eclipse.model.java.JavaASTDefaultVisitor;
import org.jtool.eclipse.model.java.JavaModelFactory;
import org.jtool.eclipse.io.FileWriter;
import org.jtool.eclipse.io.JtoolFile;
import org.jtool.eclipse.model.java.JavaClass;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * Performs an action for a project.
 * @author Katsuhisa Maruyama
 */
public class ProjectAction extends AbstractHandler {
    
    static Logger logger = Logger.getLogger(ProjectAction.class.getName());
    
    /**
     * A workbench part.
     */
    protected IWorkbenchPart part;
    
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
            factory.create();
        }
        return null;
    }
    
    /**
     * Obtains information about the selected project.
     * @param event an event containing all the information about the current state of the application
     * @return project information if a project was selected, otherwise <code>null</code>
     */
    protected IJavaProject getJavaProject(ExecutionEvent event) {
        part = HandlerUtil.getActivePart(event);
        ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structured = (IStructuredSelection)selection;
            
            Object elem = structured.getFirstElement();
            if (elem instanceof IJavaProject) {
                return (IJavaProject)elem;
            } else if (elem instanceof IProject) {
                return (IJavaProject)JavaCore.create((IProject)elem);
            }
        }
        return null;
    }
    
    /**
     * Saves information on given classes.
     * @param project the project containing the classes
     * @param classes the collection of the classes
     */
    protected void save(IProject project, Collection<JavaClass> classes) {
        String projectDir = project.getLocation().toString();
        
        Display display = Display.getCurrent();
        Shell shell = new Shell(display); 
        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.NULL);
        dialog.setFilterPath(projectDir);
        String dirname = dialog.open();
        
        if (dirname != null) {
            File dir = new File(dirname);
            while (dir.isFile()) {
                printMessage(part, "The file " + dirname + " was fould. Please specify a directory name.");
                dialog.setFilterPath(projectDir);
                dirname = dialog.open();
                if (dirname == null) {
                    break;
                }
                dir = new File(dirname);
            }
        }
        
        if (dirname != null) {
            for (JavaClass jc : classes) {
                String saveFilename = dirname + File.separator + jc.getName() + ".jt.txt";
                save(jc, saveFilename);
            }
        }
    }
    
    /**
     * Saves information on a given class into a file.
     * @param jc the class information of which will be saved
     * @param filename the file name of the file
     */
    protected void save(JavaClass jc, String filename) {
        try { 
            String text = jc.toString();
            
            JtoolFile savefile = new JtoolFile(filename);
            savefile.makeDir();
            FileWriter.write(savefile, text);
            
            logger.info("save file: " + filename);
            
        } catch (IOException e) {
            printMessage(part, "Save error: " + filename);
        }
    }
    
    /**
     * Returns the shell in which the workbench of this editor site resides.
     * @param part the workbench part
     * @return the corresponding shell
     */
    protected Shell getShell(IWorkbenchPart part) {
        IWorkbenchSite ws = part.getSite();
        return ws.getShell();
    }
    
    /**
     * Displays a message on a dialog for this editor.
     * @param part the editor
     * @param msg the message on the dialog
     */
    protected void printMessage(IEditorPart part, String msg) {
        MessageDialog.openInformation(getShell(part), "Message", msg);
    }
    
    /**
     * Displays a message on a dialog for the workbench of this editor.
     * @param part the workbench.
     * @param msg the message on the dialog
     */
    protected void printMessage(IWorkbenchPart part, String msg) {
        MessageDialog.openInformation(getShell(part), "Message", msg);
    }
}

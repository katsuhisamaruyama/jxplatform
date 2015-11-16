/*
 *  Copyright 2015, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.jtool.eclipse.Activator;
import org.jtool.eclipse.model.java.internal.JavaParser;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates Java Model within the project.
 * @author Katsuhisa Maruyama
 */
public class JavaModelFactoryInWorkspace extends JavaModelFactory {
    
    /**
     * A project in the workspace.
     */
    private IJavaProject project;
    
    /**
     * Creates a factory object that creates models of Java programs.
     * @param proj the project in the workspace
     */
    public JavaModelFactoryInWorkspace(IJavaProject proj) {
        super();
        
        jproject = JavaProject.create(proj);
        project = proj;
    }
    
    /**
     * Parses Java programs.
     */
    protected void parse() {
        Set<ICompilationUnit> cunits = collectAllCompilationUnits(project);
        removeUnchangedCompilationUnits(cunits);
        Set<ICompilationUnit> pcunits = collectCompilationUnitsToBeParsed(cunits);
        
        createJavaModel(pcunits);
        logger.info("analyzed files = " + pcunits.size());
    }
    
    /**
     * Collects all compilation units within the project.
     * @param proj the project
     * @return the collection of the compilation units
     */
    private Set<ICompilationUnit> collectAllCompilationUnits(IJavaProject proj) {
        Set<ICompilationUnit> newUnits = new HashSet<ICompilationUnit>();
        try {
            IPackageFragment[] packages = proj.getPackageFragments();
            for (int i = 0; i < packages.length; i++) {
                ICompilationUnit[] units = packages[i].getCompilationUnits();
                   
                for (int j = 0; j < units.length; j++) {
                    IResource res = units[j].getResource();
                    if (res.getType() == IResource.FILE) {
                        
                        String pathname = units[j].getPath().toString();
                        if (pathname.endsWith(".java")) { 
                            newUnits.add(units[j]);
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            logger.error("JavaModelException occurred: " + e.getMessage());
        }
        
        return newUnits;
    }
    
    /**
     * Removes unchanged compilation units from the project store.
     * @param cunits the collection of all the compilation units within the project
     */
    private void removeUnchangedCompilationUnits(Set<ICompilationUnit> cunits) {
        for (ICompilationUnit icu : cunits) {
            try {
                if (icu.hasUnsavedChanges()) {
                    JavaProject jproj = JavaProject.getJavaProject(jproject.getName());
                    if (jproj != null) {
                        jproj.removeJavaFile(icu.getPath().toString());
                    }
                }
            } catch (JavaModelException e) {
                logger.error("JavaModelException occurred: " + e.getMessage());
            }
        }
    }
    
    /**
     * Collects compilation units to be parsed within the project.
     * @param cunits the collection of all the compilation units within the project
     * @return the collection of compilation units to be parsed
     */
    private Set<ICompilationUnit> collectCompilationUnitsToBeParsed(Set<ICompilationUnit> cunits) {
        Set<ICompilationUnit> newUnits = new HashSet<ICompilationUnit>();
        for (ICompilationUnit icu : cunits) {
            String pathname = icu.getPath().toString();
            if (jproject.getJavaFile(pathname) == null) {
                newUnits.add(icu);
            }
        }
        return newUnits;
    }
    
    /**
     * Creates a model from Java programs.
     * @param junits the collection of compilation unit that requires parsing
     */
    private void createJavaModel(final Set<ICompilationUnit> cunits) {
        try {
            final JavaParser parser = new JavaParser();
            IWorkbenchWindow workbenchWindow = Activator.getWorkbenchWindow();
            workbenchWindow.run(true, true, new IRunnableWithProgress() {
                
                /**
                 * Creates a model by parsing Java files.
                 * @param monitor the progress monitor to use to display progress and receive requests for cancellation
                 * @exception InvocationTargetException if the run method must propagate a checked exception
                 * @exception InterruptedException if the operation detects a request to cancel
                 */
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Parsing files... ", cunits.size());
                    
                    int idx = 1;
                    for (ICompilationUnit icu : cunits) {
                        monitor.subTask(idx + "/" + cunits.size() + " - " + icu.getPath().toString());
                        
                        try {
                            createJavaModel(parser, icu);
                        } catch (NullPointerException e) {
                            System.err.println("* Fatal error occurred. Skip the paser of " + icu.getPath().toString());
                            /*
                            for (StackTraceElement elem : e.getStackTrace()) {
                                System.out.println(elem.toString());
                            }
                            */
                        }
                        
                        if (monitor.isCanceled()) {
                            monitor.done();
                            throw new InterruptedException();
                        }
                        monitor.worked(1);
                        idx++;
                    }
                    monitor.done();
                }
            });
            
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            System.err.println("* InvocationTargetException occurred because " + cause);
        } catch (InterruptedException e) {
            return;
        }
    }
    
    /**
     * Creates a model from a given compilation unit.
     * @param the Java parser
     * @param icu the compilation unit that requires parsing
     */
    private void createJavaModel(JavaParser parser, ICompilationUnit icu) {
        CompilationUnit cu = (CompilationUnit)parser.parse(icu);
        
        if (cu != null) {
            List<IProblem> errors = getParseErrors(cu);
            if (errors.size() == 0) {
                logger.debug("complete parse: " + icu.getPath().toString());
            } else {
                logger.debug("incomplete parse: " + icu.getPath().toString());
            }
            
            JavaFile jfile = new JavaFile(icu, jproject);
            jfile.setParseErrors(errors);
            
            visitor.setJavaFile(jfile);
            cu.accept(visitor);
            visitor.close();
            
            jproject.addJavaFile(jfile);
        }
    }
}

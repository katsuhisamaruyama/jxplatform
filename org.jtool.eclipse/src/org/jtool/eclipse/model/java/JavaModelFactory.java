/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;

import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

/**
 * Creates Java Model within the project.
 * @author Katsuhisa Maruyama
 */
public class JavaModelFactory {
    
    static Logger logger = Logger.getLogger(JavaModelFactory.class.getName());
    
    /**
     * A project in the workspace.
     */
    private IJavaProject project;
    
    /**
     * An object that stores information on project, which provides access all the information resulting from the analysis.
     */
    private JavaProject jproject;
    
    /**
     * A visitor that visits the created AST of Java source code.
     */
    private JavaASTVisitor visitor = null;
    
    /**
     * Creates a factory object that creates models of Java programs.
     * @param proj the project in the workspace
     */
    public JavaModelFactory(IJavaProject proj) {
        super();
        
        project = proj;
        jproject = JavaProject.create(project);
    }
    
    /**
     * Sets a visitor that visits the created AST of Java source code.
     * @param the visitor
     */
    public void setJavaASTVisitor(JavaASTVisitor visitor) {
        this.visitor = visitor;
    }
    
    /**
     * Creates models for Java programs within the project.
     * @return the created project information
     */
    public JavaProject create() {
        long start = System.currentTimeMillis();
        
        Set<ICompilationUnit> cunits = collectAllCompilationUnits();
        removeUnchangedCompilationUnits(cunits);
        Set<ICompilationUnit> pcunits = collectParsedCompilationUnits(cunits);
                
        createJavaModel(pcunits);
        JavaElement.setBindingLevel(1);
        
        collectLevel2Info();
        collectLevel3Info();
        
        long end = System.currentTimeMillis();
        
        long elapsedTime = end - start;
        double minutes = elapsedTime / (60 * 1000);
        double seconds = elapsedTime / 1000;
        
        logger.info("analyzed files = " + pcunits.size());
        logger.info("total files = " + jproject.getJavaFiles().size());
        logger.info("execution time: " + minutes + "m / " + seconds + "s / " + elapsedTime + "ms");
        
        return jproject;
    }
    
    /**
     * Collects all compilation units within the project.
     * @return the collection of the compilation units
     */
    private Set<ICompilationUnit> collectAllCompilationUnits() {
        Set<ICompilationUnit> newUnits = new HashSet<ICompilationUnit>();
        try {
            IPackageFragment[] packages = project.getPackageFragments();
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
            logger.info("JavaModelException: " + e.getMessage());
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
                logger.info("JavaModelException: " + e.getMessage());
            }
        }
    }
    
    /**
     * Collects compilation units to be parsed within the project.
     * @param cunits the collection of all the compilation units within the project
     * @return the collection of compilation units to be parsed
     */
    private Set<ICompilationUnit> collectParsedCompilationUnits(Set<ICompilationUnit> cunits) {
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
                        
                        createJavaModel(icu);
                        
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
            System.out.println("# InvocationTargetException because " + cause);
            for (StackTraceElement elem : e.getStackTrace()) {
                System.err.println(elem.toString());
            }
        } catch (InterruptedException e) {
            return;
        }
    }
    
    /**
     * Creates a model from a given compilation unit.
     * @param icu the compilation unit that requires parsing
     */
    private void createJavaModel(ICompilationUnit icu) {
        JavaParser parser = JavaParser.create();
        CompilationUnit cu = (CompilationUnit)parser.parse(icu);
        
        if (cu != null && visitor != null) {
            JavaFile jfile = new JavaFile(icu, jproject);
            visitor.setJavaFile(jfile, jproject);
            cu.accept(visitor);
            visitor.close();
        }
    }
    
    /**
     * Creates a model from a Java program stored in a given file.
     * @param file the file that requires parsing
     * @param jproject the project containing the file
     */
    protected void createJavaModel(File file, JavaProject jproject) {
        JavaParser parser = JavaParser.create();
        CompilationUnit cu = parser.parse(file, jproject);
        
        if (cu != null && visitor != null) {
            JavaFile jfile = new JavaFile(file.getAbsoluteFile().getName(), jproject);
            visitor.setJavaFile(jfile, jproject);
            cu.accept(visitor);
            visitor.close();
        }
    }
    
    /**
     * Collects additional information on classes, methods, and fields within a project.
     */
    private void collectLevel2Info() {
        for (JavaClass jc : jproject.getJavaClasses()) {
            jc.collectLevel2Info();
            
            if (!jc.isBindingOk()) {
                logger.info("some binding information was missed in a class: " + jc.getQualifiedName());
            }
            
            for (JavaMethod jm : jc.getJavaMethods()) {
                jm.collectLevel2Info();
                
                if (!jm.isBindingOk()) {
                    logger.info("some binding information was missed in a method: " + jm.getQualifiedName());
                }
            }
            
            for (JavaField jf : jc.getJavaFields()) {
                jf.collectLevel2Info();
                
                if (!jf.isBindingOk()) {
                    logger.info("some binding information was missed in a field: " + jf.getQualifiedName());
                }
            }
        }
    }
    
    /**
     * Collects additional information on packages.
     */
    private void collectLevel3Info() {
        for (JavaPackage jp : jproject.getJavaPackages()) {
            jp.collectLevel3Info();
            if (!jp.isBindingOk()) {
                logger.info("some binding information was missed in a package: " + jp.getName());
            }
        }
    }
}

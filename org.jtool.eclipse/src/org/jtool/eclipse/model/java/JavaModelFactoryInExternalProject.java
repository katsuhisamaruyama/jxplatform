/*
 *  Copyright 2015, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.jtool.eclipse.Activator;
import org.jtool.eclipse.model.java.internal.JavaParser;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates Java Model within the external project.
 * @author Katsuhisa Maruyama
 */
public class JavaModelFactoryInExternalProject extends JavaModelFactory {
    
    /**
     * The variable name indicating the default class path for JRE
     */
    private static final String JRE_LIB = "JRE_LIB";
    
    /**
     * The top directory that temporarily stores files to be parsed
     */
    private static String TEMP_TOPDIR = File.separator + "#temp";
    
    
    /**
     * A project in the workspace.
     */
    private String[] classpaths;
    
    /**
     * Creates a factory object that creates models of Java programs.
     * @param name the name of the project
     * @param dir the top directory of the project
     * @param classpaths the collection of the class paths
     */
    public JavaModelFactoryInExternalProject(String name, String dir, String[] classpaths) {
        super();
        
        jproject = JavaProject.create(name, dir);
        this.classpaths = classpaths;
    }
    
    /**
     * Creates a factory object that creates models of Java programs.
     * @param name the name of the project
     * @param dir the top directory of the project
     * @param classpaths the collection of the class paths
     */
    public JavaModelFactoryInExternalProject(String name, String dir) {
        this(name, dir, new String[] { JavaCore.getClasspathVariable(JRE_LIB).toOSString() });
    }
    
    /**
     * Creates a factory object that creates models of Java programs.
     * @param name the name of the project
     * @param dir the top directory of the project
     * @param classpaths the collection of the class paths
     */
    public JavaModelFactoryInExternalProject(String name, String[] classpaths) {
        this(getTempDirPath().toOSString(), name, classpaths);
    }
    
    /**
     * Creates a factory object that creates models of Java programs.
     * @param name the name of the project
     * @param dir the top directory of the project
     * @param classpaths the collection of the class paths
     */
    public JavaModelFactoryInExternalProject(String name) {
        this(getTempDirPath().toOSString(), name, new String[] { JavaCore.getClasspathVariable(JRE_LIB).toOSString() });
    }
    
    /**
     * Parses Java programs.
     */
    protected void parse(String[] names) {
        Set<File> files = collectAllFiles(jproject.getTopDir());
        createJavaModel(files);
    }
    
    /**
     * Parses Java programs.
     */
    protected void parse() {
        Set<File> files = collectAllFiles(jproject.getTopDir());
        createJavaModel(files);
    }
    
    /**
     * Collects all files within the external project.
     * @param top the path of the specified directory
     * @return the descendant files
     */
    private Set<File> collectAllFiles(String path) {
        Set<File> files = new HashSet<File>();
        
        File dir = new File(path);
        if (dir.isFile()) {
            if (path.endsWith(".java")) {
                files.add(dir);
            }
        } else if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (File f : children) {
                files.addAll(collectAllFiles(f.getPath()));
            }
        }
        
        return files;
    }
    
    /**
     * Creates a model from Java programs.
     * @param junits the collection of compilation unit that requires parsing
     */
    private void createJavaModel(final Set<File> files) {
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
                    monitor.beginTask("Parsing files... ", files.size());
                    
                    int idx = 1;
                    for (File f : files) {
                        monitor.subTask(idx + "/" + files.size() + " - " + f.getPath().toString());
                        
                        try {
                            createJavaModel(parser, f);
                        } catch (NullPointerException e) {
                            System.err.println("* Fatal error occurred. Skip the paser of " + f.getPath().toString());
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
     * Creates a model from a Java program stored in a given file.
     * @param the Java parser
     * @param file the file that requires parsing
     */
    protected void createJavaModel(JavaParser parser, File file) {
        String[] sourcepaths = new String[]{ jproject.getTopDir() };
        CompilationUnit cu = parser.parse(file, classpaths, sourcepaths);
        
        if (cu != null) {
            List<IProblem> errors = getParseErrors(cu);
            if (errors.size() == 0) {
                logger.debug("complete parse: " + file.getAbsoluteFile().getName());
            } else {
                logger.debug("incomplete parse: " + file.getAbsoluteFile().getName());
            }
            
            JavaFile jfile = new JavaFile(file.getAbsoluteFile().getName(), jproject);
            jfile.setParseErrors(errors);
            jproject.addJavaFile(jfile);
            
            visitor.setJavaFile(jfile);
            cu.accept(visitor);
            visitor.close();
        }
    }
    
    /**
     * Returns the directory into which the files that will be parsed are temporarily stored.
     * @return the the directory path
     */
    private static IPath getTempDirPath() {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IPath workspaceDir = workspaceRoot.getLocation();
        return workspaceDir.append(TEMP_TOPDIR);
    }
}

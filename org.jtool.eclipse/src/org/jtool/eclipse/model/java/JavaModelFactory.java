/*
 *  Copyright 2015, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Creates Java Model within the project.
 * @author Katsuhisa Maruyama
 */
public abstract class JavaModelFactory {
    
    static Logger logger = Logger.getLogger(JavaModelFactory.class.getName());
    
    /**
     * An object that stores information on project, which provides access all the information resulting from the analysis.
     */
    protected JavaProject jproject;
    
    /**
     * A visitor that visits the created AST of Java source code.
     */
    protected JavaASTVisitor visitor = null;
    
    /**
     * Creates a new, empty object.
     */
    protected JavaModelFactory() {
        super();
    }
    
    /**
     * Sets a visitor that visits the created AST of Java source code.
     * @param the visitor
     */
    public void setJavaASTVisitor(JavaASTVisitor visitor) {
        this.visitor = visitor;
    }
    
    /**
     * Creates models for Java programs.
     * @return the created project information
     */
    public JavaProject create() {
        if (visitor == null) {
            System.err.println("* No visitor was given. Please set a visitor object");
            return null;
        }
        
        long start = System.currentTimeMillis();
        
        parse();
        
        JavaElement.setBindingLevel(1);
        
        collectLevel2Info();
        collectLevel3Info();
        
        long end = System.currentTimeMillis();
        
        long elapsedTime = end - start;
        double minutes = elapsedTime / (60 * 1000);
        double seconds = elapsedTime / 1000;
        
        logger.info("total files = " + jproject.getJavaFiles().size());
        logger.info("execution time: " + minutes + "m / " + seconds + "s / " + elapsedTime + "ms");
        
        return jproject;
    }
    
    /**
     * Parses Java programs.
     */
    protected abstract void parse();
    
    /**
     * Obtains the collection of parse errors for a compilation unit.
     * @param cu the parsed compilation unit
     * @return the collection of parse errors
     */
    protected List<IProblem> getParseErrors(CompilationUnit cu) {
        List<IProblem> errors = new ArrayList<IProblem>();
        
        IProblem[] problems = cu.getProblems();
        if (problems.length != 0) {
            for (IProblem problem : problems) {
                if (problem.isError()) {
                    errors.add(problem);
                }
            }
        }
        return errors;
    }
    
    /**
     * Collects additional information on classes, methods, and fields within a project.
     */
    protected void collectLevel2Info() {
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
    protected void collectLevel3Info() {
        for (JavaPackage jp : jproject.getJavaPackages()) {
            jp.collectLevel3Info();
            if (!jp.isBindingOk()) {
                logger.info("some binding information was missed in a package: " + jp.getName());
            }
        }
    }
}

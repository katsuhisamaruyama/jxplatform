/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.io.DetectCharset;
import org.jtool.eclipse.io.FileReader;
import org.jtool.eclipse.model.java.JavaProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * A Java parser for creating abstract syntax trees (ASTs).
 * @author Katsuhisa Maruyama
 */
public class JavaParser {
    
    static Logger logger = Logger.getLogger(JavaParser.class.getName());
    
    /**
     * A Java parser.
     */
    protected static JavaParser jparser = null;
    
    /**
     * A Java language parser embedded in Eclipse.
     */
    protected ASTParser parser;
    
    /**
     * Creates a new Java language parser.
     */
    protected JavaParser() {
        parser = ASTParser.newParser(AST.JLS4);
    }
    
    /**
     * Creates a new parser for Java source code.
     * @return the created parser
     */
    public static JavaParser create() {
        if (jparser != null) {
            return jparser;
        }
        jparser = new JavaParser();
        return jparser;
    }
    
    /**
     * Parsers the contents of a Java file and creates its AST.
     * @param icu a compilation unit to be parsed
     * @return the root node of the created AST
     */
    public CompilationUnit parse(ICompilationUnit icu) {
        parser.setResolveBindings(true);
        parser.setStatementsRecovery(true);
        parser.setBindingsRecovery(true);
        parser.setSource(icu);
        
        CompilationUnit cu = (CompilationUnit)parser.createAST(null);
        // cu.recordModifications();
        
        return cu;
    }
    
    /**
     * Parses the contents of a Java file and creates its AST.
     * @param file a file to be parsed
     * @param jproject a project containing a file to be parsed
     * @return the root node of the created AST, or <code>null</code> if any compile error was occurred
     */
    public CompilationUnit parse(File file, JavaProject jproject) {
        try {
            String contents = FileReader.read(file);
            String encoding = DetectCharset.getCharsetName(contents.getBytes());
            
            if (contents != null) {
                String rootDir = jproject.getTopDir();
                String[] classpaths = new String[]{ rootDir };
                String[] sourcepaths = new String[]{ rootDir };
                String name = file.getAbsoluteFile().getName();
                
                return parse(contents, encoding, classpaths, sourcepaths, name);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }
    
    /**
     * Parses the contents of a Java file and creates its AST.
     * @param contents the contents of the file
     * @param encoding the encoding of the contents of the file
     * @param classpaths the class paths during the parse of the file
     * @param sourcepaths the source paths during the parse of the file
     * @param name the name of the file
     * @return the root node of the created AST, or <code>null</code> if any compile error was occurred
     */
    private CompilationUnit parse(String contents, String encoding, String[] classpaths, String[] sourcepaths, String name) {
        parser.setResolveBindings(true);
        parser.setStatementsRecovery(true);
        parser.setBindingsRecovery(true);
        
        String[] encodings;
        if (encoding == null) {
            encodings = new String[]{ "US-ASCII", "UTF-8", "SJIS" };
        } else {
            encodings = new String[]{ "US-ASCII", "UTF-8", "SJIS", encoding };
        }
        parser.setEnvironment(classpaths, sourcepaths, encodings, true);
        parser.setUnitName(name);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(contents.toCharArray());
        
        CompilationUnit cu = (CompilationUnit)parser.createAST(null);
        // cu.recordModifications();
        
        return cu;
    }
}

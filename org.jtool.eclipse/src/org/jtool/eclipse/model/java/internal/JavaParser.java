/*
 *  Copyright 2015, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.io.DetectCharset;
import org.jtool.eclipse.io.FileReader;
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
     * A Java language parser embedded in Eclipse.
     */
    private ASTParser parser;
    
    /**
     * Creates a new Java language parser.
     */
    public JavaParser() {
        parser = ASTParser.newParser(AST.JLS8);
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
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        
        parser.setSource(icu);
        
        CompilationUnit cu = (CompilationUnit)parser.createAST(null);
        // cu.recordModifications();
        
        return cu;
    }
    
    /**
     * Parses the contents of a Java file and creates its AST.
     * @param file a file to be parsed
     * @param classpaths the class paths during the parse of the file
     * @param sourcepaths the source paths during the parse of the file
     * @return the root node of the created AST, or <code>null</code> if any compile error was occurred
     */
    public CompilationUnit parse(File file, String[] classpaths, String[] sourcepaths) {
        try {
            String contents = FileReader.read(file);
            String encoding = DetectCharset.getCharsetName(contents.getBytes());
            
            if (contents != null) {
                String name = file.getAbsoluteFile().getName();
                String[] encodings;
                if (encoding == null) {
                    encodings = new String[]{ "UTF-8" };
                } else {
                    encodings = new String[]{ encoding };
                }
                
                parser.setResolveBindings(true);
                parser.setStatementsRecovery(true);
                parser.setBindingsRecovery(true);
                parser.setKind(ASTParser.K_COMPILATION_UNIT);
                
                parser.setUnitName(name);
                parser.setEnvironment(classpaths, sourcepaths, encodings, true);
                parser.setSource(contents.toCharArray());
                
                CompilationUnit cu = (CompilationUnit)parser.createAST(null);
                // cu.recordModifications();
                
                return cu;
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }
}

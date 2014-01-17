/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

import org.jtool.eclipse.model.cfg.internal.CFGFieldFactory;
import org.jtool.eclipse.model.cfg.internal.CFGMethodFactory;
import org.jtool.eclipse.model.graph.GraphNodeIdPublisher;
import org.jtool.eclipse.model.graph.GraphEdgeIdFactory;
import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaMethod;
import java.util.Set;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * Creates a CFG for a class member (a method, constructor, initializer, and field).
 * @author Katsuhisa Maruyama
 */
public class CFGFactory {
    
    static Logger logger = Logger.getLogger(CFGFactory.class.getName());
    
    /**
     * The flag that requests the creation of actual nodes for method invocation.
     */
    private static boolean createActualNodes = true;
    
    /**
     * Initializes CFG information.
     */
    public static void initialize() {
        GraphNodeIdPublisher.reset();
        GraphEdgeIdFactory.reset();
    }
    
    /**
     * Requests the creation of actual nodes for method invocation.
     * @param bool <code>true</code> if the creation is wanted, otherwise <code>false</code>
     */
    public static void setActualNodeCreation(boolean bool) {
        createActualNodes = bool;
    }
    
    /**
     * Tests if the creation of actual nodes for method invocation is wanted.
     * @param <code>true</code> if the creation is wanted, otherwise <code>false</code>
     */
    public static boolean getActualNodeCreation() {
        return createActualNodes;
    }   
    
    /**
     * Creates CFGs for methods and fields in a given class.
     * @param jc information on the class
     * @return the collection of the created CFGs
     */
    public static Set<CFG> create(JavaClass jc) {
        Set<CFG> cfgs = new HashSet<CFG>();
        for (JavaMethod jm : jc.getJavaMethods()) {
            cfgs.add(create(jm));
        }
        
        for (JavaField jf : jc.getJavaFields()) {
            cfgs.add(create(jf));
        }
        return cfgs;
    }
    
    /**
     * Creates a CFG for a given method or constructor.
     * @param jm information on the method
     * @return the created CFG
     */
    public static CFG create(JavaMethod jm) {
        CFG cfg = CFGMethodFactory.getCFG(jm);
        
        logger.debug("\n" + cfg.toString());
        
        return cfg;
    }
    
    /**
     * Creates a CFG for a given field.
     * @param jf information on the field
     * @return the created CFG
     */
    public static CFG create(JavaField jf) {
        CFG cfg = CFGFieldFactory.getCFG(jf);
        
        logger.debug("\n" + cfg.toString());
        
        return cfg;
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFGClassEntry;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaMethod;
import org.apache.log4j.Logger;

/**
 * Creates a class dependence graph (ClDG) for a class.
 * @author Katsuhisa Maruyama
 */
public class ClDGFactory {
    
    static Logger logger = Logger.getLogger(ClDGFactory.class.getName());
    
    /**
     * Creates a ClDG for a given class.
     * @param jc the class
     * @return the created ClDG that combines PDGs for all the methods and fields declared in the class.
     */
    public static ClDG create(JavaClass jc) {
        PDGFactory.setConservative(true);
        
        ClDG cldg = new ClDG();
        
        CFGClassEntry cfgentry = new CFGClassEntry(jc, GraphNodeSort.classEntry);
        PDGClassEntry pdgentry = new PDGClassEntry(cfgentry);
        cldg.setEntryNode(pdgentry);
        
        for (JavaField jf : jc.getJavaFields()) {
            PDG pdg = PDGFactory.create(jf);
            cldg.add(pdg);
        }
        
        for (JavaMethod jm : jc.getJavaMethods()) {
            PDG pdg = PDGFactory.create(jm);
            cldg.add(pdg);
        }
        
        return cldg;
    }
    
    /**
     * Displays information about a given ClDGs.
     * @param pdgs the collection of PDGs
     */
    public static void print(ClDG cldg) {
        PDGFactory.print(cldg.getPDGs());
        logger.debug("\n" + cldg.toString());
    }
}

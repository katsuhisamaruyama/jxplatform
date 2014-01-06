/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import java.util.HashSet;
import java.util.Set;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGClassEntry;
import org.jtool.eclipse.model.cfg.CFGEntry;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.cfg.internal.CFGFactory;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.pdg.ClDG;
import org.jtool.eclipse.model.pdg.PDG;
import org.jtool.eclipse.model.pdg.PDGClassEntry;
import org.jtool.eclipse.model.pdg.PDGEntry;
import org.jtool.eclipse.model.pdg.PDGNode;
import org.jtool.eclipse.model.pdg.PDGStatement;
import org.apache.log4j.Logger;

/**
 * Creates a PDG for a class member (a method, constructor, initializer, and field).
 * @author Katsuhisa Maruyama
 */
public class PDGFactory {
    
    static Logger logger = Logger.getLogger(PDGFactory.class.getName());
    
     /**
     * Creates CFGs for all methods and fields within a Java program.
     */
    public static void create() {
        for (JavaClass jc : JavaClass.getAllClassesInCache()) {
            create(jc);
        }
    }
    
    /**
     * Creates PDGs for methods and fields in a given class.
     * @param jc information on the class
     * the collection of the created PDGs
     */
    public static Set<PDG> create(JavaClass jc) {
        Set<PDG> pdgs = new HashSet<PDG>();
        for (JavaMethod jm : jc.getJavaMethods()) {
            pdgs.add(create(jm));
        }
        
        for (JavaField jf : jc.getJavaFields()) {
            pdgs.add(create(jf));
        }
        return pdgs;
    }
    
    /**
     * Creates a ClDG class dependence graph (ClDG) for a given class.
     * @param jc information on the class
     * @return the created ClDG which combines PDGs for all the methods and fields declared in the class.
     */
    public static ClDG createClDG(JavaClass jc) {
        ClDG cldg = new ClDG();
        
        CFGClassEntry cfgEntry = new CFGClassEntry(jc, GraphNodeSort.classEntry);
        PDGClassEntry pdgEntry = new PDGClassEntry(cfgEntry);
        cldg.setEntryNode(pdgEntry);
        
        for (JavaField jf : jc.getJavaFields()) {
            PDG pdg = create(jf);
            cldg.add(pdg);
        }
        
        for (JavaMethod jm : jc.getJavaMethods()) {
            PDG pdg = create(jm);
            cldg.add(pdg);
        }
        
        return cldg;
    }
    
    /**
     * Creates a PDG for a method or constructor.
     * @param jm information on the method or constructor
     * @return the created PDG
     */
    public static PDG create(JavaMethod jm) {
        CFG cfg = CFGFactory.create(jm);
        if (cfg == null) {
            CFGFactory.create(jm);
        }
        
        PDG pdg = create(cfg);
        
        logger.debug("\n" + pdg.toString());
        
        return pdg;
    }
    
    /**
     * Creates a PDG for a field.
     * @param jf information on the field
     * @return the created PDG
     */
    public static PDG create(JavaField jf) {
        CFG cfg = CFGFactory.create(jf);
        if (cfg == null) {
            CFGFactory.create(jf);
        }
        
        PDG pdg = create(cfg);
        
        logger.debug("\n" + pdg.toString());
        
        return pdg;
    }
    
    /**
     * Creates a new PDG corresponding to a CFG.
     * @param cfg the CFG to be examined
     * @return the created PDG
     */
    public static PDG create(CFG cfg) {
        PDG pdg = new PDG();
        
        createNodes(pdg, cfg);
        
        CDFactory.create(pdg, cfg);
        DDFactory.create(pdg, cfg);
        
        return pdg;
    }
    
    /**
     * Creates new nodes of a PDG corresponding to a CFG.
     * @param pdg the PDG
     * @param cfg the CFG
     */
    private static void createNodes(PDG pdg, CFG cfg) {
        for (CFGNode cfgnode : cfg.getNodes()) {
            PDGNode pdgnode = createNode(pdg, cfgnode);
            
            if (pdgnode != null) {
                cfgnode.setPDGNode(pdgnode);
                pdg.add(pdgnode);
            }
        }
    }
    
    /**
     * Creates a new PDG node corresponding to a CFG node. 
     * @param pdg the PDG
     * @param node the node of the CFG
     * @return the created PDG node
     */
    private static PDGNode createNode(PDG pdg, CFGNode node) {
        if (node.isClassEntry() || node.isEnumEntry()) {
            PDGClassEntry pnode = new PDGClassEntry((CFGEntry)node);
            pdg.setEntryNode(pnode);
            return pnode;
            
        } else if (node.isMethodEntry() || node.isInitializerEntry() || node.isFieldEntry() || node.isEnumConstantEntry()) {
            PDGEntry pnode = new PDGEntry((CFGEntry)node);
            pdg.setEntryNode(pnode);
            return pnode;
            
        } else if (node.isStatement()) {
            PDGStatement pnode = new PDGStatement((CFGStatement)node);
            return pnode;
        }
        
        return null;
    }
}

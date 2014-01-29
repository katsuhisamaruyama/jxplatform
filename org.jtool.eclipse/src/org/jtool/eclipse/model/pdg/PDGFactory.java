/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGEntry;
import org.jtool.eclipse.model.cfg.CFGFactory;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.graph.GraphNode;
import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.jtool.eclipse.model.pdg.internal.CDFactory;
import org.jtool.eclipse.model.pdg.internal.DDFactory;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Creates a PDG for a class member (a method, constructor, initializer, and field).
 * @author Katsuhisa Maruyama
 */
public class PDGFactory {
    
    static Logger logger = Logger.getLogger(PDGFactory.class.getName());
    
    /**
     * A flag indicating if actual parameters is intended to be conservatively connected.
     */
    private static boolean isConservative = false;
    
    /**
     * Sets a flag indicating actual parameters is intended to be conservatively connected.
     * @param bool <code>true</code> if the conservative connection is needed, otherwise <code>false</code>
     */
    public static void setConservative(boolean bool) {
        isConservative = bool;
    }
    
    /**
     * Creates PDGs for methods and fields in a given class.
     * @param jc information on the class
     * the collection of the created PDGs
     */
    public static Set<PDG> create(JavaClass jc) {
        setConservative(true);
        
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
        
        if (isConservative) {
            connectActualParameters(pdg);
        }
        
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
    
    /**
     * Connects actual parameters conservatively. All actual-in nodes will be always connected to its actual-out node.
     * @param pdg the PDG containing actual parameters
     */
    private static void connectActualParameters(PDG pdg) {
        for (PDGNode callnode : pdg.getNodes()) {
            if (callnode.getCFGNode().isMethodCall()) {
                
                PDGNode aout = null;
                for (GraphNode node : callnode.getDstNodes()) {
                    PDGNode pdgnode = (PDGNode)node;
                    if (pdgnode.getCFGNode().isActualOut()) {
                        aout = pdgnode;
                    }
                }
                
                if (aout != null) {
                    for (GraphNode node : callnode.getDstNodes()) {
                        PDGNode pdgnode = (PDGNode)node;
                        
                        if (pdgnode.getCFGNode().isActualIn()) {
                            PDGStatement ain = (PDGStatement)pdgnode;
                            JavaVariableAccess jv = ain.getDefVariables().get(0);
                            
                            ParameterEdge edge = new ParameterEdge(ain, aout, jv);
                            edge.setSummary();
                            
                            pdg.add(edge);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Displays information about given PDGs and their CFGs.
     * @param pdgs the collection of PDGs
     */
    public static void print(Set<PDG> pdgs) {
        for (PDG pdg : pdgs) {
            logger.debug("\n" + pdg.getCFG().toString());
            logger.debug("\n" + pdg.toString());
        }
    }
}

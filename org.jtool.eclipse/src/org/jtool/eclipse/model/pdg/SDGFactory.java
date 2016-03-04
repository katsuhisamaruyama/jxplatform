/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFGMethodEntry;
import org.jtool.eclipse.model.cfg.CFGMethodCall;
import org.jtool.eclipse.model.cfg.CFGParameter;
import org.jtool.eclipse.model.java.JavaClass;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaField;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.jtool.eclipse.model.pdg.internal.SummaryFactory;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Creates a system dependence graph (SDG) which stores PDGs and relationships between them.
 * @author Katsuhisa Maruyama
 */
public class SDGFactory {
    
    static Logger logger = Logger.getLogger(ClDGFactory.class.getName());
    
    /**
     * A map storing pairs of a method/field and its PDG.
     */
    private static HashMap<String, PDG> pdgs = new HashMap<String, PDG>();
    
    /**
     * Creates an SDG for a given class.
     * @param jclasses the collection of the class
     * @return the created SDG containing the PDGs of the method and ones that the method calls.
     */
    public static SDG create(Set<JavaClass> jclasses) {
        SDG sdg = new SDG();
        pdgs.clear();
        
        for (JavaClass jc : jclasses) {
            create(sdg, jc);
        }
        
        return sdg;
    }
    
    /**
     * Creates an SDG for a given class.
     * @param jclasses the collection of the class
     * @return the created SDG containing the PDGs of the method and ones that the method calls.
     */
    public static SDG create(List<JavaClass> jclasses) {
        SDG sdg = new SDG();
        pdgs.clear();
        
        for (JavaClass jc : jclasses) {
            create(sdg, jc);
        }
        
        return sdg;
    }
    
    /**
     * Creates an SDG for a given class.
     * @param jc the class
     * @return the created SDG containing the PDGs of the method and ones that the method calls.
     */
    public static SDG create(JavaClass jc) {
        SDG sdg = new SDG();
        pdgs.clear();
        
        create(sdg, jc);
        
        return sdg;
    }
    
    /**
     * Creates a PDG for a given class and appends it to the SDG.
     * @param sdg the SDG containing the created PDG
     * @param jc the class
     * @return the created PDG
     */
    public static void create(SDG sdg, JavaClass jc) {
        for (JavaMethod jm : jc.getJavaMethods()) {
            create(sdg, jm);
        }
        
        for (JavaField jf : jc.getJavaFields()) {
            create(sdg, jf);
        }
    }
    
    
    /**
     * Creates an SDG for a given method.
     * @param jm the method
     * @return the created SDG containing the PDGs of the method and ones that the method calls.
     */
    public static SDG create(JavaMethod jm) {
        SDG sdg = new SDG();
        pdgs.clear();
        
        create(sdg, jm);
        
        return sdg;
    }
    
    /**
     * Creates a PDG for a given method and appends it to the SDG.
     * @param sdg the SDG containing the created PDG
     * @param jm the method
     * @return the created PDG
     */
    public static PDG create(SDG sdg, JavaMethod jm) {
        String key = JavaMethod.getString(jm.getQualifiedName(), jm.getSignature());
        PDG pdg = pdgs.get(key);
        if (pdg == null) {
            pdg = PDGFactory.create(jm);
            pdgs.put(key, pdg);
            
            sdg.add(pdg);
            
            createPDGsForMethod(sdg, pdg);
            createPDGsForField(sdg, pdg);
        }
        
        return pdg;
    }
    
    /**
     * Creates a PDG for a given field and appends it to the SDG.
     * @param sdg the SDG containing the created PDG
     * @param jf the field
     * @return the created PDG
     */
    public static PDG create(SDG sdg, JavaField jf) {
        String key = JavaField.getString(jf.getQualifiedName(), jf.getName());
        PDG pdg = pdgs.get(key);
        if (pdg == null) {
            pdg = PDGFactory.create(jf);
            pdgs.put(key, pdg);
            
            sdg.add(pdg);
            
            createPDGsForMethod(sdg, pdg);
            createPDGsForField(sdg, pdg);
        }
        
        return pdg;
    }
    
    /**
     * Creates PDGs related to a given PDG for a method.
     * @param sdg the SDG containing the created PDG
     * @param pdg the PDG
     */
    private static void createPDGsForMethod(SDG sdg, PDG pdg) {
        for (CFGMethodCall callnode : collectMethodCallNodes(pdg)) {
            JavaMethod cm = callnode.getJavaMethodCall().getJavaMethod();
            if (cm.isInProject()) {
                PDG cpdg = create(sdg, cm);
                
                connectParameters(sdg, callnode, (CFGMethodEntry)cpdg.getEntryNode().getCFGEntry());
            }
        }
        
        SummaryFactory.create(sdg, pdg);
    }
    
    /**
     * Creates PDGs related to a given PDG for a field.
     * @param sdg the SDG containing the created PDG
     * @param pdg the PDG
     */
    private static void createPDGsForField(SDG sdg, PDG pdg) {
        for (JavaVariableAccess jv : collectFieldAccesses(pdg)) {
            JavaField cf = jv.getJavaField();
            if (cf.isInProject()) {
                create(sdg, cf);
            }
        }
    }
    
    /**
     * Collects CFG nodes corresponding to the method calls within a given PDG.
     * @param pdg the PDG to be examined
     * @return the collection of the CFG nodes corresponding to the method calls
     */
    private static List<CFGMethodCall> collectMethodCallNodes(PDG pdg) {
        List<CFGMethodCall> callnodes = new ArrayList<CFGMethodCall>();
        for (PDGNode pdgnode : pdg.getNodes()) {
            if (pdgnode.getCFGNode().isMethodCall()) {
                callnodes.add((CFGMethodCall)pdgnode.getCFGNode());
            }
        }
        return callnodes;
    }
    
    /**
     * Connects actual nodes of the method call to their corresponding formal nodes of the called method.
     * @param sdg the SDG containing these nodes
     * @param caller the CFG node corresponding to the method call
     * @param callee the CFG entry node corresponding to the called method
     */
    private static void connectParameters(SDG sdg, CFGMethodCall caller, CFGMethodEntry callee) {
        for (int ordinal = 0; ordinal < caller.getActualIns().size(); ordinal++) {
            
            CFGParameter ain = caller.getActualIn(ordinal);
            CFGParameter fin = callee.getFormalIn(Math.min(ordinal, callee.getFormalIns().size() - 1));
            
            JavaVariableAccess jv = fin.getUseVariables().get(0);
            ParameterEdge pinedge = new ParameterEdge(ain.getPDGNode(), fin.getPDGNode(), jv);
            pinedge.setParameterIn();
            sdg.add(pinedge);
        }
        
        if (!callee.isVoid()) {
            CFGParameter aout = caller.getActualOuts().get(0);
            CFGParameter fout = callee.getFormalOuts().get(0);
            
            JavaVariableAccess jv = fout.getDefVariables().get(0);
            ParameterEdge poutedge = new ParameterEdge(fout.getPDGNode(), aout.getPDGNode(), jv);
            poutedge.setParameterOut();
            sdg.add(poutedge);
        }
    }
    
    /**
     * Collects variable access nodes used within a given PDG.
     * @param pdg the PDG to be examined
     * @return the collection of the field accesses
     */
    private static List<JavaVariableAccess> collectFieldAccesses(PDG pdg) {
        List<JavaVariableAccess> fieldaccesses = new ArrayList<JavaVariableAccess>();
        for (PDGNode pdgnode : pdg.getNodes()) {
            if (pdgnode.isStatement()) {
                PDGStatement stnode = (PDGStatement)pdgnode;
                for (JavaVariableAccess jv : stnode.getDefVariables()) {
                    if (jv.isField()) {
                        fieldaccesses.add(jv);
                    }
                }
                for (JavaVariableAccess jv : stnode.getUseVariables()) {
                    if (jv.isField()) {
                        fieldaccesses.add(jv);
                    }
                }
            }
        }
        return fieldaccesses;
    }
    
    /**
     * Displays information about a given SDGs.
     * @param pdgs the collection of PDGs
     */
    public static void print(SDG sdg) {
        PDGFactory.print(sdg.getPDGs());
        logger.debug("\n" + sdg.toString());
    }
}

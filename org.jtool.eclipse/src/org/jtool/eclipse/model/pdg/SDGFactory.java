/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFGMethodEntry;
import org.jtool.eclipse.model.cfg.CFGMethodCall;
import org.jtool.eclipse.model.cfg.CFGParameter;
import org.jtool.eclipse.model.java.JavaMethod;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.jtool.eclipse.model.pdg.internal.SummaryFactory;
import java.util.List;
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
     * A map storing pairs of a method and its PDG.
     */
    private static HashMap<JavaMethod, PDG> pdgs = new HashMap<JavaMethod, PDG>();
    
    /**
     * Creates a SDG for a given method.
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
        PDG pdg = pdgs.get(jm);
        if (pdg == null) {
            pdg = PDGFactory.create(jm);
            pdgs.put(jm, pdg);
            
            sdg.add(pdg);
            
            for (CFGMethodCall callnode : collectMethodCallNodes(pdg)) {
                JavaMethod cm = callnode.getJavaMethodCall().getJavaMethod();
                PDG cpdg = create(sdg, cm);
                
                connectParameters(sdg, callnode, (CFGMethodEntry)cpdg.getEntryNode().getCFGEntry());
            }
            
            SummaryFactory.create(sdg, pdg);
        }
        
        return pdg;
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
        for (int ordinal = 0; ordinal < callee.getFormalIns().size(); ordinal++) {
            CFGParameter ain = caller.getActualIn(ordinal);
            CFGParameter fin = callee.getFormalIn(ordinal);
            
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
     * Displays information about a given SDGs.
     * @param pdgs the collection of PDGs
     */
    public static void print(SDG sdg) {
        PDGFactory.print(sdg.getPDGs());
        logger.debug("\n" + sdg.toString());
    }
}

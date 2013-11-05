/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGMethodInvocation;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGParameter;
import org.jtool.eclipse.model.cfg.ControlFlow;
import org.jtool.eclipse.model.pdg.CD;
import org.jtool.eclipse.model.pdg.PDG;
import org.jtool.eclipse.model.pdg.PDGNode;
import org.apache.log4j.Logger;

/**
 * Extracts control dependences of a PDG from its CFG.
 * @author Katsuhisa Maruyama
 */
public class CDFactory {
    
    static Logger logger = Logger.getLogger(CDFactory.class.getName());
    
    /**
     * Visits all the nodes of the CFG and extracts control dependences from it.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     */
    public static void create(PDG pdg, CFG cfg) {
        findCDs(pdg, cfg);
        findControlDependencesAtEntry(pdg, cfg);
        addControlDependencesAtEntry(pdg);
    }
    
    /**
     * Extracts control dependences from a CFG.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     */
    private static void findCDs(PDG pdg, CFG cfg) {
        for (CFGNode cfgnode : cfg.getNodes()) {
            
            if (cfgnode.isBranch()) {
                findCDs(pdg, cfg, cfgnode);
                
            } else if (cfgnode.isMethodCall()) {
                findParameterCDs(pdg, (CFGMethodInvocation)cfgnode);
            }
        }
    }
        
    /**
     * Finds control dependences for a given branch node.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     * @param branchNode the branch node which will be the source node of the found control dependence
     */
    private static void findCDs(PDG pdg, CFG cfg, CFGNode branchNode) {
        PostDominator postDominator = new PostDominator(cfg, branchNode);
        for (ControlFlow branch : branchNode.getOutgoingFlows()) {            
            CFGNode branchDstNode = branch.getDstNode();
            PostDominator postDominatorLocal = new PostDominator(cfg, branchDstNode);
            postDominatorLocal.add(branchDstNode);
            
            for (CFGNode cfgnode : cfg.getNodes()) {               
                if (cfgnode.isNormalStatement() && !branchNode.equals(cfgnode) &&
                    !postDominator.contains(cfgnode) && postDominatorLocal.contains(cfgnode)) {
                    
                    CD edge = new CD(branchNode.getPDGNode(), cfgnode.getPDGNode());
                    
                    if (branch.isTrue()) {
                        edge.setTrue();
                    } else if (branch.isFalse()) {
                        edge.setFalse();
                    } else {
                        edge.setFall();
                    }
                    
                    pdg.add(edge);
                }
            }
        }
    }
    
    /**
     * Finds control dependences outgoing from an entry node.
     * @param pdg the PDG which stores the extracted information
     * @param callNode the method call node that might have actual arguments
     */
    private static void findParameterCDs(PDG pdg, CFGMethodInvocation callNode) {
        for (CFGParameter cfgnode : callNode.getActualIns()) {
            CD edge = new CD(callNode.getPDGNode(), cfgnode.getPDGNode());
            edge.setTrue();
            pdg.add(edge);
        }
        
        for (CFGParameter cfgnode : callNode.getActualOuts()) {          
            CD edge = new CD(callNode.getPDGNode(), cfgnode.getPDGNode());
            edge.setTrue();
            pdg.add(edge);
        }
    }
    
    /**
     * Finds control dependences outgoing from an entry node.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     */
    private static void findControlDependencesAtEntry(PDG pdg, CFG cfg) {
        CFGNode startNode = cfg.getStartNode();
        PostDominator postDominator = new PostDominator(cfg, startNode);
        
        for (CFGNode cfgnode : postDominator) {         
            if (cfgnode.isNormalStatement() || cfgnode.isFormal()) {
                
                CD edge = new CD(startNode.getPDGNode(), cfgnode.getPDGNode());
                edge.setTrue();
                pdg.add(edge);
            }
        }
    }
    
    /**
     * Adds control dependences outgoing from an entry node.
     * This also finds control dependences between the entry node and a formal parameter of this CFG.
     * @param pdg the PDG that stores the extracted information
     */
    private static void addControlDependencesAtEntry(PDG pdg) {
        for (PDGNode pdgnode : pdg.getNodes()) {
            if (!pdgnode.equals(pdg.getEntryNode()) && pdgnode.getNumOfIncomingTrueFalseCDs() == 0) {
                
                CD edge = new CD(pdg.getEntryNode(), pdgnode);
                edge.setTrue();
                pdg.add(edge);
            }
        }
    }
}

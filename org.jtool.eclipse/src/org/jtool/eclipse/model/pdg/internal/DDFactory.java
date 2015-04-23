/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGStatement;
import org.jtool.eclipse.model.cfg.ControlFlow;
import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.graph.GraphEdge;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.jtool.eclipse.model.pdg.DD;
import org.jtool.eclipse.model.pdg.CD;
import org.jtool.eclipse.model.pdg.Dependence;
import org.jtool.eclipse.model.pdg.PDG;
import org.jtool.eclipse.model.pdg.PDGNode;
import java.util.ArrayList;

/**
 * Extracts data dependences of a PDG from its CFG.
 * This class does not guarantee a loop carried node to be correct if a given PDG contains no control dependence.
 * @author Katsuhisa Maruyama
 */
public class DDFactory {
    
    /**
     * Visits all the nodes of the CFG and extracts control dependences from it.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     */
    public static void create(PDG pdg, CFG cfg) {
        findDDs(pdg, cfg);
        findDefOrderDDs(pdg, cfg);
    }
    
    /**
     * Extracts data dependences from a CFG.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     */
    private static void findDDs(PDG pdg, CFG cfg) {
        for (CFGNode cfgnode : cfg.getNodes()) {
            if (cfgnode.isStatement() && cfgnode.hasDefVariable()) {
                findDDs(pdg, cfg, (CFGStatement)cfgnode);
            }
        }
    }
    
    /**
     * Finds data dependences for a node.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     * @param anchor the CFG node which will be the source node of the found data dependence
     */
    private static void findDDs(PDG pdg, CFG cfg, CFGStatement anchor) {
        for (JavaVariableAccess jv : anchor.getDefVariables()) {
            findDDs(pdg, cfg, anchor, jv);
        }
    }
    
    /**
     * Finds data dependences for a node that carries a variable.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     * @param anchor the CFG node which will be the source node of the found data dependence
     * @param jv the variable carried by the found data dependence
     */
    private static void findDDs(PDG pdg, CFG cfg, CFGStatement anchor, JavaVariableAccess jv) {
        for (ControlFlow flow : anchor.getOutgoingFlows()) {
            if (!flow.isFallThrough()) {
                GraphElementSet<CFGNode> track = new GraphElementSet<CFGNode>();
                CFGNode cfgNode = (CFGNode)flow.getDstNode();
                
                checkDD(pdg, cfg, anchor, cfgNode, jv, track);
            }
        }
    }
    
    /**
     * Checks a data dependence between two nodes.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     * @param anchor the CFG node which will be the source node of the found data dependence
     * @param node the CFG node to be tested if it is the destination of the found data dependence
     * @param jv the variable carried by the found data dependence
     * @param track the collection of nodes passed on a CFG
     */
    private static void checkDD(PDG pdg, CFG cfg, CFGNode anchor, CFGNode node, JavaVariableAccess jv, GraphElementSet<CFGNode> track) {
        track.add(node);
        if (node.hasUseVariable()) {
            CFGStatement candidate = (CFGStatement)node;
            
            if (candidate.useVariable(jv)) {
                DD edge;
                if (anchor.isFormalIn()) {
                    edge = new DD(anchor.getPDGNode(), candidate.getPDGNode(), jv);
                    edge.setLIDD();
                } else if (candidate.isFormalOut()) {
                    edge = new DD(anchor.getPDGNode(), candidate.getPDGNode(), jv);
                    edge.setLIDD();
                } else {
                    PDGNode lc = getLoopCarried(pdg, cfg, anchor, candidate);
                    edge = new DD(anchor.getPDGNode(), candidate.getPDGNode(), jv);
                    if (lc != null && track.contains(lc.getCFGNode())) {
                        edge.setLCDD();
                        edge.setLoopCarriedNode(lc);
                    } else {
                        edge.setLIDD();
                    }
                }
                pdg.add(edge);
            }
        }
        
        if (node.hasDefVariable()) {
            CFGStatement candidate = (CFGStatement)node;
            if (candidate.defineVariable(jv)) {
                DD edge = new DD(anchor.getPDGNode(), candidate.getPDGNode(), jv);
                edge.setOutput();
                pdg.add(edge);
                return;
            }
        }
        
        for (ControlFlow flow : node.getOutgoingFlows()) {
            if (!flow.isFallThrough()) {
                CFGNode succ = (CFGNode)flow.getDstNode();
                if (!track.contains(succ))
                    checkDD(pdg, cfg, anchor, succ, jv, track);
            }
        } 
    }
    
    /**
     * Returns the loop carried node for a given def-use dependence.
     * The innermost node will be returned if there exist multiple candidates for the loop carried node.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     * @param def the source CFG node which defines a variable
     * @param use the destination CFG node which uses the defined variable
     * @return the loop carried node, or <code>null</code> if none
     */
    private static PDGNode getLoopCarried(PDG pdg, CFG cfg, CFGNode def, CFGNode use) {
        GraphElementSet<PDGNode> atrack = new GraphElementSet<PDGNode>();
        
        ArrayList<PDGNode> dtrack = new ArrayList<PDGNode>();
        atrack.clear();
        findDominators(def.getPDGNode(), dtrack, atrack);
        if (dtrack.isEmpty()) {
            return null;
        }
        
        ArrayList<PDGNode> utrack = new ArrayList<PDGNode>();
        atrack.clear();
        findDominators(use.getPDGNode(), utrack, atrack);
        if (utrack.isEmpty()) {
            return null;
        }
        
        ConstrainedReachableNodes path = new ConstrainedReachableNodes(cfg, def, use);
        for (PDGNode pdgnode : dtrack) {
            CFGNode cfgNode = pdgnode.getCFGNode();
            if (utrack.contains(pdgnode) && path.contains(cfgNode)) {
                return pdgnode;
            }
        }
        return null;
    }
    
    /**
     * Finds dominators that are source nodes of a PDG node with respect to control dependences.
     * @param pdgNode the PDG node.
     * @param dominators the dominators of the PDG node
     * @param atack the collection of nodes passed on a PDG
     */
    private static void findDominators(PDGNode pdgnode, ArrayList<PDGNode> dominators, GraphElementSet<PDGNode> atrack) {
        atrack.add(pdgnode);
        if (pdgnode.isLoop()) {
            dominators.add(pdgnode);
        }
        
        for (GraphEdge edge : pdgnode.getIncomingEdges()) {
            Dependence dependence = (Dependence)edge;
            
            if (dependence.isCD()) {
                PDGNode src = dependence.getSrcNode();
                if (!atrack.contains(src)) {
                    findDominators(src, dominators, atrack);
                }
            }
        }
    }
    
    /**
     * Extracts def-order data dependences from a CFG.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     */
    private static void findDefOrderDDs(PDG pdg, CFG cfg) {
        for (GraphEdge edge : pdg.getEdges()) {
            Dependence dependence = (Dependence)edge;
            
            if (dependence.isDD()) {
                DD dd = (DD)edge;
                if (dd.isOutput() && isDefOrder(dd)) {
                    dd.setDefOrder();
                }
            }
        }
    }
    
    /**
     * Tests if a given data dependence is def-order one.
     * @param dd the data dependence to be checked
     * @return <code>if this is def-order data dependence, otherwise <code>false</code>
     */
    private static boolean isDefOrder(DD dd) {
        PDGNode src = dd.getSrcNode();
        PDGNode dst = dd.getDstNode();
        
        CD srcCD = src.getIncomingCDEdges().getFirst();
        CD dstCD = dst.getIncomingCDEdges().getFirst();
        
        if (!srcCD.getSrcNode().equals(dstCD.getSrcNode())) {
            return false;
        }
        
        for (DD srcDD : src.getOutgoingDDEdges()) {
            if (srcDD.isLCDD() || srcDD.isLIDD()) {
                
                for (DD dstDD : dst.getOutgoingDDEdges()) {
                    if (dstDD.isLCDD() || dstDD.isLIDD()) {
                        if (srcDD.getDstNode().equals(dstDD.getDstNode())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}

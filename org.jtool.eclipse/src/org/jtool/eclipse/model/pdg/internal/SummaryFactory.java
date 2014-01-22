/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.java.JavaVariableAccess;
import org.jtool.eclipse.model.pdg.DD;
import org.jtool.eclipse.model.pdg.PDG;
import org.jtool.eclipse.model.pdg.SDG;
import org.jtool.eclipse.model.pdg.PDGNode;
import org.jtool.eclipse.model.pdg.PDGStatement;
import org.jtool.eclipse.model.pdg.ParameterEdge;
import java.util.Set;
import java.util.HashSet;

/**
 * Extracts summary dependences of a PDG.
 * @author Katsuhisa Maruyama
 */
public class SummaryFactory {
    
    /**
     * Visits all the nodes of the CFG and extracts control dependences from it.
     * @param pdg the PDG that stores the extracted information
     * @param cfg the CFG to be examined
     */
    public static void create(SDG sdg, PDG pdg) {
        
        Set<PDGStatement> ains = findAins(pdg);
        Set<PDGStatement> aouts = findAouts(pdg);
        
        Set<PDGStatement> nodes = new HashSet<PDGStatement>();
        for (PDGStatement aout : aouts) {
            traverseBackward(nodes, aout, ains);
            
            for (PDGStatement ain : ains) {
                if (nodes.contains(ain)) {
                    JavaVariableAccess jv = ain.getDefVariables().get(0);
                    
                    ParameterEdge edge = new ParameterEdge(ain, aout, jv);
                    edge.setSummary();
                    
                    pdg.add(edge);
                    sdg.add(edge);
                }
            }
        }
    }
    
    /**
     * Collects actual-in nodes within a PDG.
     * @param the PDG to be examined
     * @return the collection of the actual-in nodes
     */
    private static Set<PDGStatement> findAins(PDG pdg) {
        Set<PDGStatement> nodes = new HashSet<PDGStatement>();
        
        for (PDGNode node : pdg.getNodes()) {
            
            CFGNode cfgnode = node.getCFGNode();
            if (cfgnode.isActualIn()) {
                nodes.add((PDGStatement)node);
            }
        }
        
        return nodes;
    }
    
    /**
     * Collects actual-out nodes within a PDG.
     * @param the PDG to be examined
     * @return the collection of the actual-out nodes
     */
    private static Set<PDGStatement> findAouts(PDG pdg) {
        Set<PDGStatement> nodes = new HashSet<PDGStatement>();
        
        for (PDGNode node : pdg.getNodes()) {
            
            CFGNode cfgnode = node.getCFGNode();
            if (cfgnode.isActualOut()) {
                nodes.add((PDGStatement)node);
            }
        }
        
        return nodes;
    }
    
    /**
     * Backward traverses data dependence edges and nodes of the PDG nodes and collects them.
     * @param nodes the collections of the traversed nodes
     * @param node anchor the anchor node
     * @param ains the collection of actual-in nodes corresponding to the actual-node to be checked
     */
    private static void traverseBackward(Set<PDGStatement> nodes, PDGStatement anchor, Set<PDGStatement> ains) {
        nodes.add(anchor);
        System.out.println("add = " + anchor.toString());
        
        for (DD edge : anchor.getIncomingDDEdges()) {
            PDGStatement node = (PDGStatement)edge.getSrcNode();
            
            if (ains.contains(node)) {
                nodes.add(node);
                System.out.println("add = " + node.toString());
            } else if (!nodes.contains(node)) {
                traverseBackward(nodes, node, ains);
            }
        }
    }
}

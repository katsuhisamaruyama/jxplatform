/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg.internal;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGMethodEntry;
import org.jtool.eclipse.model.cfg.CFGMethodInvocation;
import org.jtool.eclipse.model.cfg.CFGNode;
import org.jtool.eclipse.model.cfg.CFGParameter;
import org.jtool.eclipse.model.graph.GraphElementSet;
import org.jtool.eclipse.model.graph.GraphNodeSort;
import org.jtool.eclipse.model.pdg.PDG;
import org.jtool.eclipse.model.pdg.PDGNode;
import org.jtool.eclipse.model.pdg.ParameterEdge;
import org.jtool.eclipse.model.pdg.SDG;

import java.util.List;
import java.util.ArrayList;

/**
 * Creates a sysm dependence graph (SDG) which stores PDGs and relationships between them.
 * @author Katsuhisa Maruyama
 */
public class SDGFactory {
    
    /**
     * The collection of PDGs which are already combined.
     */
    private static List<PDG> combinedPDGs;
    
    /**
     * Creates a system dependence graph for a given CFG.
     * @param cfg the CFG to be examined
     * @return the created SDG
     */
    public static SDG create(CFG cfg) {
        SDG sdg = new SDG();
        combinedPDGs = new ArrayList<PDG>();
        
        combinedPDGs.add(PDGFactory.create(cfg));
        collectPDGs(cfg);
        
        combine(sdg, combinedPDGs);
        
        return sdg;
    }
    
    /**
     * Collects PDGs for all methods directly and indirectly called by a given CFG.
     * @param pdg the PDG to be combined
     */
    public static void collectPDGs(CFG cfg) {
        if (cfg != null) {      
            GraphElementSet<CFGNode> callings = cfg.getCallNodes();
            for (CFGNode callnode : callings) {
                CFGMethodInvocation calling = (CFGMethodInvocation)callnode;
                
                CFGMethodEntry entry = calling.getCalledMethodEntry();
                
                PDG pdg = PDGFactory.create(entry.getCFG());
                if (pdg != null && !combinedPDGs.contains(pdg)) {
                    combinedPDGs.add(pdg);
                
                    collectPDGs(entry.getCFG());
                }
            }
        }
    }
    
    /**
     * Combines a given PDG into the SDG.
     * @param pdg the PDG to be combined
     */
    public static void combine(SDG sdg, List<PDG> pdgs) {
        for (PDG pdg : pdgs) {
            sdg.setNodes(pdg.getNodes());
            sdg.setEdges(pdg.getEdges());
            sdg.addEntryNode(pdg.getEntryNode());
            
            List<PDGNode> callings = collectMethodCallNodes(pdg.getNodes());
            for (PDGNode callnode : callings) {
                CFGMethodInvocation calling = (CFGMethodInvocation)callnode.getCFGNode();
                if (calling != null && calling.hasParameters()) {
                    CFGMethodEntry cfgEntry = calling.getCalledMethodEntry();
                    
                    if (cfgEntry != null) {
                        connect(sdg, calling, cfgEntry);
                    }
                }
            }
        }
    }
    
    /**
     * Connects actual nodes with their corresponding formal nodes of the called method.
     * @param sdg the SDG containing these nodes
     * @param calling the CFG node for the method call
     * @param entry the entry node for the CFG of the called method
     */
    private static void connect(SDG sdg, CFGMethodInvocation calling, CFGMethodEntry entry) {   
        CFGParameter aout = calling.getActualOuts().get(0);
        CFGParameter fout = entry.getFormalOuts().get(0);
        
        ParameterEdge outEdge = new ParameterEdge(fout.getPDGNode(), aout.getPDGNode());
        outEdge.setParameterOut();
        sdg.add(outEdge);
        
        for (int i = 0; i < entry.getFormalIns().size(); i++) {
            CFGParameter ain = calling.getActualIn(i);
            CFGParameter fin = entry.getFormalIn(i);
            
            ParameterEdge inEdge = new ParameterEdge(ain.getPDGNode(), fin.getPDGNode());
            inEdge.setParameterIn();
            sdg.add(inEdge);
        }
    }
    
    /**
     * Obtains the first PDG node with a specified sort.
     * @param @param nodes the collection of nodes to be examined
     * @param sort the sort of the found node
     * @return the found node, or <code>null</code> if none
     */
    public static PDGNode getFirstNodeBySort(GraphElementSet<PDGNode> nodes, GraphNodeSort sort) {
        if (nodes != null && !nodes.isEmpty()) {
            for (PDGNode node : nodes) {                
                if (node.getSort() == sort) {
                    return node;
                }
            }
        }
        return null;
    }
    
    /**
     * Collects PDG nodes with a specified sort.
     * @param nodes the collection of nodes to be examined
     * @param sort the sort of the found node
     * @return the collection of the nodes with specified sort
     */
    public static List<PDGNode> collectNodesBySort(GraphElementSet<PDGNode> nodes, GraphNodeSort sort) {
        List<PDGNode> set = new ArrayList<PDGNode>();
        
        if (nodes != null && !nodes.isEmpty()) {        
            for (PDGNode node : nodes) {                
                if (node.getSort() == sort) {
                    set.add(node);
                }
            }
        }
        
        return set;
    }
    
    /**
     * Collects PDG nodes that represent method calls.
     * @param nodes the collection of nodes to be examined
     * @return the collection of the method call nodes
     */
    public static List<PDGNode> collectMethodCallNodes(GraphElementSet<PDGNode> nodes) {
        List<PDGNode> set = new ArrayList<PDGNode>();
        
        if (nodes != null && !nodes.isEmpty()) {        
            for (PDGNode node : nodes) {                 
                if (isMethodCall(node)) {
                    set.add(node);
                }
            }
        }
        
        return set;
    }
    
    /**
     * Tests if this node represents a method call
     * @param node the node to be checked
     * @return <code>true</code> if this node represents a method call, otherwise <code>false</code>
     */
    private static boolean isMethodCall(PDGNode pdgnode) {
        GraphNodeSort sort = pdgnode.getSort();
        return sort == GraphNodeSort.methodCall ||
               sort == GraphNodeSort.constructorCall ||
               sort == GraphNodeSort.instanceCreation;
    }
}

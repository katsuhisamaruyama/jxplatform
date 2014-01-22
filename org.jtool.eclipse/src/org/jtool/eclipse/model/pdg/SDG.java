/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.Graph;

import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * An object storing information about a system dependence graph (SDG).
 * @author Katsuhisa Maruyama
 */
public class SDG extends Graph<PDGNode, Dependence> {
    
    static Logger logger = Logger.getLogger(SDG.class.getName());
    
    /**
     * The collection of entry nodes of respective PDGs.
     */
    private Set<PDGEntry> entries = new HashSet<PDGEntry>();
    
    /**
     * All PDGs contained in this SDGs.
     */
    private Set<PDG> pdgs = new HashSet<PDG>();
    
    /**
     * Creates a new, empty object.
     */
    public SDG() {
        super();
    }
    
    /**
     * Adds the entry node of a PDG into this SDG.
     * @param node the entry node of the PDG 
     */
    public void addEntryNode(PDGClassEntry node) {
        entries.add(node);
    }
    
    /**
     * Returns the entry nodes of this SDG.
     * @return the collection of the entry nodes
     */
    public Set<PDGEntry> getEntries() {
        return entries;
    }
    
    /**
     * Adds a new PDG into this ClDG.
     * @param pdg the PDG to be added
     */
    public void add(PDG pdg) {
        if (!pdgs.contains(pdg)) {
            pdgs.add(pdg);
            entries.add(pdg.getEntryNode());
            
            for (PDGNode node : pdg.getNodes()) {
                add(node);
            }
            
            for (Dependence edge : pdg.getEdges()) {
                add(edge);
            }
        }
    }
    
    /**
     * Returns all PDGs contained in this SDG.
     * @return the collection of the PDGs
     */
    public Set<PDG> getPDGs() {
        return pdgs;
    }
    
    /**
     * Collects information about this graph for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- SDG (from here) -----\n");
        buf.append(getNodeInfo()); 
        buf.append(getEdgeInfo());
        buf.append("----- SDG (to here) -----\n");
        
        return buf.toString();
    }
} 

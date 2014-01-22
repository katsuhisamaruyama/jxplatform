/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.GraphEdgeSort;

import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * An object storing information about a class dependence graph (ClDG).
 * @author Katsuhisa Maruyama
 */
public class ClDG extends PDG {
    
    static Logger logger = Logger.getLogger(ClDG.class.getName());
    
    /**
     * An entry node of this ClDG.
     */
    protected PDGClassEntry entry;
    
    /**
     * All PDGs contained in this ClDG.
     */
    private Set<PDG> pdgs = new HashSet<PDG>();
    
    /**
     * Creates a new, empty object.
     */
    public ClDG() {
        super();
    }
    
    /**
     * Sets the entry node of this ClDG.
     * @param node the entry node of this ClDG
     */
    public void setEntryNode(PDGClassEntry node) {
        entry = node;
    }
    
    /**
     * Returns the entry node of this ClDG.
     * @return the entry node of this ClDG
     */
    public PDGClassEntry getEntryNode() {
        return entry;
    }
    
    /**
     * Returns the identification number of this ClDG.
     * @return the identification number that equals to that of the entry node of this ClDG
     */
    public long getId() {
        return entry.getId();
    }
    
    /**
     * Returns the name of this PDG.
     * @return the name of the method corresponding to this PDG
     */
    public String getName() {
        return entry.getName();
    }
    
    /**
     * Adds a new PDG into this ClDG.
     * @param pdg the PDG to be added
     */
    public void add(PDG pdg) {
        if (!pdgs.contains(pdg)) {
            pdgs.add(pdg);
            
            for (PDGNode node : pdg.getNodes()) {
                add(node);
            }
            
            for (Dependence edge : pdg.getEdges()) {
                add(edge);
            }
            
            ClassMemberEdge edge = new ClassMemberEdge(entry, pdg.getEntryNode());
            edge.setSort(GraphEdgeSort.classMember);
            add(edge);
        }
    }
    
    /**
     * Returns all PDGs contained in this ClDG.
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
        buf.append("----- ClDG (from here) -----\n");
        buf.append("Name = " + getName());
        buf.append("\n");
        buf.append(getNodeInfo()); 
        buf.append(getEdgeInfo());
        buf.append("----- ClDG (to here) -----\n");
        
        return buf.toString();
    }
}

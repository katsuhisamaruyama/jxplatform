/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.cfg.CFG;
import org.jtool.eclipse.model.cfg.CFGEntry;
import org.jtool.eclipse.model.graph.Graph;
import org.apache.log4j.Logger;

/**
 * An object storing information about a program dependence graph (PDG).
 * @author Katsuhisa Maruyama
 */
public class PDG extends Graph<PDGNode, Dependence> {
    
    static Logger logger = Logger.getLogger(PDG.class.getName());
    
    /**
     * An entry node of this PDG.
     */
    protected PDGEntry entry;
    
    /**
     * Creates a new, empty object.
     */
    public PDG() {
        super();
    }
    
    /**
     * Sets the entry node of this PDG.
     * @param node the entry node of this PDG
     */
    public void setEntryNode(PDGEntry node) {
        entry = node;
        entry.setPDG(this);
    }
    
    /**
     * Returns the entry node of this PDG.
     * @return the entry node of this PDG
     */
    public PDGEntry getEntryNode() {
        return entry;
    }
    
    /**
     * Returns the identification number of this PDG.
     * @return the identification number that equals to that of the entry node of this PDG
     */
    public long getId() {
        return entry.getId();
    }
    
    /**
     * Returns the CFG that corresponds to this PDG.
     * @return the corresponding CFG
     */
    public CFG getCFG() {
        CFGEntry node = (CFGEntry)entry.getCFGNode();
        return node.getCFG();
    }
    
    /**
     * Returns the name of this PDG.
     * @return the name of the method corresponding to this PDG
     */
    public String getName() {
        return entry.getName();
    }
    
    /**
     * Appends a new PDG to this existing PDG. The nodes or edges of the both PDGs are merged respectively.
     * @param pdg the PDG to be appended
     */
    public void append(PDG pdg) {
        for (PDGNode node : pdg.getNodes()) {
            add(node);
        }
        for (Dependence edge : pdg.getEdges()) {
            add(edge);
        }
    }
    
    /**
     * Adds a node to this PDG.
     * @param node the node to be added
     */
    public void add(PDGNode node) {
        super.add(node);
    }
    
    /**
     * Adds an edge to this PDG.
     * @param edge the edge to be added
     */
    public void add(Dependence edge) {
        super.add(edge);
    }
    
    /**
     * Returns the PDG node with a given identification number.
     * @param id the identification number of the node
     * @return the corresponding PDG node, or <code>null</code> if none
     */
    public PDGNode getNode(int id) {
        for (PDGNode node : getNodes()) {
            if (id == node.getId()) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * Creates a clone of this PDG.
     * @return the clone of this PDG
     */
    public PDG clone() {
        CFG cloneCFG = getCFG().clone();
        PDG clonePDG = new PDG();
        clonePDG = PDGFactory.create(cloneCFG);
        return clonePDG;
    }
    
    /**
     * Displays information about this graph.
     */
    public void print() {
        logger.info(toString());
    }
    
    /**
     * Collects information about this graph for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- PDG (from here) -----\n");
        buf.append("Name = " + getName());
        buf.append("\n");
        buf.append(getNodeInfo()); 
        buf.append(getEdgeInfo());
        buf.append("----- PDG (to here) -----\n");
        
        return buf.toString();
    }
    
    /**
     * Collects information about edges of this CFG for printing.
     * @return the string for printing 
     */
    protected String getEdgeInfo() {
        StringBuffer buf = new StringBuffer();
        int index = 1;
        for (Dependence edge : getEdges()) {
            buf.append(String.valueOf(index));
            buf.append(": ");
            
            buf.append(edge.toString());
            buf.append("\n");
            index++;
        }
        
        return buf.toString();
    }
    
    /**
     * Displays information about the control dependence graph of this graph.
     */
    public void printCDG() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- CDG (from here) -----\n");
        buf.append("Name = " + getName());
        buf.append("\n");
        buf.append(getNodeInfo()); 
        buf.append(getCDEdgeInfo());
        buf.append("----- CDG (to here) -----\n");
        
        logger.debug(buf.toString());
    }
    
    /**
     * Displays information about the data dependence graph of this graph.
     */
    public void printDDG() {
        StringBuffer buf = new StringBuffer();
        buf.append("----- DDG (from here) -----\n");
        buf.append("Name = " + getName());
        buf.append("\n");
        buf.append(getNodeInfo()); 
        buf.append(getDDEdgeInfo());
        buf.append("----- DDG (to here) -----\n");
        
        logger.debug(buf.toString());
    }
    
    /**
     * Collects information about the control dependence edges in this graph for printing.
     * @return the string for printing
     */
    protected String getCDEdgeInfo() {
        StringBuffer buf = new StringBuffer();
        for (Dependence edge : getEdges()) {
            if (edge.isCD()) {
                buf.append(edge.toString());
                buf.append("\n");
            }
        }
        
        return buf.toString();
    }
    
    /**
     * Collects information about the data dependence edges in this graph for printing.
     * @return the string for printing
     */
    protected String getDDEdgeInfo() {
        StringBuffer buf = new StringBuffer();
        for (Dependence edge : getEdges()) {
            if (edge.isDD()) {
                buf.append(edge.toString());
                buf.append("\n");
            }
        }
        
        return buf.toString();
    }
}

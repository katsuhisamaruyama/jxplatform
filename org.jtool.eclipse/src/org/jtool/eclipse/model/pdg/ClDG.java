/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.Graph;
import org.jtool.eclipse.model.graph.GraphEdgeSort;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * An object storing information about a class dependence graph (ClDG).
 * @author Katsuhisa Maruyama
 */
public class ClDG extends Graph<PDGNode, Dependence> {
    
    static Logger logger = Logger.getLogger(ClDG.class.getName());
    
    /**
     * An entry node of this ClDG.
     */
    protected PDGClassEntry entry;
    
    /**
     * PDGs contained in this ClDG.
     */
    private List<PDG> pdgs = new ArrayList<PDG>();
    
    /**
     * The collections of class member edges.
     */
    private List<ClassMemberEdge> members = new ArrayList<ClassMemberEdge>();
    
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
     * @return the identification number that equals to that of the entry node of this ClDG.
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
     * @param pdg
     */
    public void add(PDG pdg) {
        if (!pdgs.contains(pdg)) {
            pdgs.add(pdg);
            
            ClassMemberEdge edge = new ClassMemberEdge(entry, pdg.getEntryNode());
            edge.setSort(GraphEdgeSort.classMember);
            members.add(edge);
        }
    }
}

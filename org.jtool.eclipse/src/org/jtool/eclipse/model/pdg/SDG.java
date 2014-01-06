/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.pdg;

import org.jtool.eclipse.model.graph.Graph;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * An object storing information about a class dependence graph (ClDG).
 * @author Katsuhisa Maruyama
 */
public class SDG extends Graph<PDGNode, Dependence> {
    
    static Logger logger = Logger.getLogger(SDG.class.getName());
    
    /**
     * The collection of entry nodes of respective PDGs.
     */
    private List<PDGNode> entries = new ArrayList<PDGNode>();
    
    /**
     * Creates a new, empty object.
     */
    public SDG() {
        super();
    }
    
    /**
     * Adds a node to this SDG.
     * @param node the node to be added
     */
    public void add(PDGNode node) {
        super.add(node);
    }
    
    /**
     * Adds an edge to this SDG.
     * @param edge the edge to be added
     */
    public void add(Dependence edge) {
        super.add(edge);
    }
    
    /**
     * Adds the entry node of a PDG into this SDG.
     * @param node the entry node of the PDG 
     */
    public void addEntryNode(PDGNode node) {
        entries.add(node);
    }
} 

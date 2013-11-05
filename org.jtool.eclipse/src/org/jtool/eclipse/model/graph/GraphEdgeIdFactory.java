/*
 *  Copyright 2013, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

/**
 * Generates and manages the identification numbers for graph elements.
 * @author Katsuhisa Maruyama
 */
public class GraphEdgeIdFactory {
    
    /**
     * The identification number.
     */
    private static long id = 1;
    
    /**
     * Increments the identification number and return it.
     * @return the identification number
     */
    public static long getId() {
        return id++;
    }
    
    /**
     * Resets the identification number.
     */
    public static void reset() {
        id = 1;
    }
}

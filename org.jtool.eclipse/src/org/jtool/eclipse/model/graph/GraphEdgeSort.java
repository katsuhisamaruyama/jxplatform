/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

/**
* Constant values indicating the sort of edges of graphs.
* @author Katsuhisa Maruyama
*/
public enum GraphEdgeSort {
    
    trueControlFlow,                 // Control flow outgoing to a true-branch
    falseControlFlow,                // Control flow outgoing to a false-branch
    fallThroughFlow,                 // Control flow representing a fall-through
    methodCall,                      // Flow representing the call to a method
    parameterFlow,                   // Flow representing the relationship between a class/method and its parameter
    
    controlDependence,               // Control dependence in general
    trueControlDependence,           // Control dependence with respect to a true-branch flow
    falseControlDependence,          // Control dependence with respect to a false-branch flow
    fallControlDependence,           // Control dependence with respect to a fall-through flow
    
    dataDependence,                  // Data dependence in dgeneral
    loopIndependentDefUseDependence, // Data dependence with respect to a loop-independent variable
    loopCarriedDefUseDependence,     // Data dependence with respect to a loop-carried variable
    defOrderDependence,              // Data dependence based on the order of definitions of variables
    outputDependence,                // Data dependence based on the order of outputs of variables
    antiDependence,                  // Data dependence based on the order of use and definition of variables
    parameterIn,                     // Data dependence with respect to incoming parameter passing
    parameterOut,                    // Data dependence with respect to outgoing parameter passing
    
    classMember;
    
    /**
     * Tests if this edge represents a control flow. 
     * @return <code>true</code> if this edge is a control flow, otherwise <code>false</code>
     */
    public boolean isControlFlow() {
        switch (this) {
            case trueControlFlow: return true;
            case falseControlFlow: return true;
            case fallThroughFlow: return true;
            case methodCall: return true;
            case parameterFlow: return true;
            default: return false;
        }
    }
    
    /**
     * Tests if this edge represents a control dependence. 
     * @return <code>true</code> if this edge is a control dependence, otherwise <code>false</code>
     */
    public boolean isCD() {
        switch (this) {
            case trueControlDependence: return true;
            case falseControlDependence: return true;
            case fallControlDependence: return true;
            default: return false;
        }
    }
    /**
     * Tests if this edge represents a data dependence. 
     * @return <code>true</code> if this edge is a data dependence, otherwise <code>false</code>
     */
    public boolean isDD() {
        switch (this) {
            case loopIndependentDefUseDependence: return true;
            case loopCarriedDefUseDependence: return true;
            case defOrderDependence: return true;
            case outputDependence: return true;
            case antiDependence: return true;
            case parameterIn: return true;
            case parameterOut: return true;
            default: return false;
        }
    }
}

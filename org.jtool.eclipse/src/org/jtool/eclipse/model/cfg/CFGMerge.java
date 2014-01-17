/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.cfg;

/**
 * A merge node of CFGs.
 * @author Katsuhisa Maruyama
 */
public class CFGMerge extends CFGNode {
    
    /**
     * A branch node which gives rise to this join.
     */
    private CFGStatement branch;
    
    /**
     * Creates a new, empty object.
     */
    protected CFGMerge() {
        super();
    }
    
    /**
     * Creates a new node. This node does not correspond to any Java element.
     * @param branch a branch node which gives rise to this join
     */
    public CFGMerge(CFGStatement branch) {
        super(branch.getSort());
        this.branch = branch;
    }
    
    /**
     * Sets a branch node which gives rise to this join.
     * @param branch the branch node
     */
    public void setBranch(CFGStatement branch) {
        this.branch = branch;
    }
    
    /**
     * Returns the branch node which gives rise to this join.
     * @return the corresponding branch node
     */
    public CFGStatement getBranch() {
        return branch;
    }
    
    /**
     * Creates a clone of this node.
     * @return the clone of this node
     */
    public CFGMerge clone() {
        CFGMerge cloneNode = new CFGMerge(getBranch());
        clone(cloneNode);
        return cloneNode;
    }
    
    /**
     * Copies all the attributes of this node into a given clone.
     * @param cloneNode the clone of this node
     */
    protected void clone(CFGMerge cloneNode) {
        super.clone(cloneNode);
        cloneNode.setBranch(getBranch());
    }
    
    /**
     * Displays information about this node.
     */
    public void print() {
        logger.info(toString());
    }
    
    /**
     * Collects information about this node.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + getId() + "] ");
        buf.append("merge");
        return buf.toString();
    }
}

/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * A set of <code>GraphElement</code>.
 * The implementation uses the <code>java.util.LinkedHashSet</code> class to maintain the insertion-order.
 * @author Katsuhsa Maruyama
 */
public class GraphElementSet<E extends GraphElement> implements Iterable<E> {
    
    /**
     * A set of the stored elements.
     */
    private Set<E> set = new LinkedHashSet<E>();
    
    /**
     * Creates a new, empty set.
     */
    public GraphElementSet() {
    }
    
    /**
     * Creates a new set from a given set of elements.
     * @param set the set of graph elements
     */
    public GraphElementSet(GraphElementSet<E> s) {
        addAll(s);
    }
    
    /**
     * Removes all graph elements of this set.
     */
    public void clear() {
        set.clear();
    }
    
    /**
     * Adds a given graph element to this set.
     * @param elem the graph element to be added
     * @return <code>true</code> if this set changed, otherwise <code>false</code>
     */
    public boolean add(E elem) {
        if (!contains(elem)) {
            set.add(elem);
            return true;
        }
        return false;
    }
    
    /**
     * Removes a given graph element from this set. 
     * @param elem the graph element to be removed
     * @return <code>true</code> if this set contained the removed graph element, otherwise <code>false</code>
     */
    public boolean remove(E elem) {
        if (elem != null) {
            return set.remove(elem);
        }
        return false;
    }
    
    /**
     * Adds all graph elements specified by a given set to this set.
     * @param s the set of graph elements to be added
     */
    public void addAll(GraphElementSet<E> s) {
        for (E e : s) {
            set.add(e);
        }
    }
    
    /**
     * Copies a given set of graph elements onto this set. 
     * @param s the original set of graph elements
     */
    public void copy(GraphElementSet<E> s) {
        clear();
        addAll(s);
    }
    
    /**
     * Tests if this set contains a graph element.
     * @param elem the graph element to be checked
     * @return <code>true</code> if this set contains the graph element, otherwise <code>false</code>
     */
    public boolean contains(E elem) {
        for (E e : set) {
            if (elem.equals(e)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Tests if this set contains no graph element.
     * @return <code>true</code> if this set is empty, otherwise <code>false</code>
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }
    
    /**
     * Returns the number of graph elements in this set.
     * @return the number of graph elements in this set
     */
    public int size() {
        return set.size();
    }
    
    /**
     * Returns the iterator of this set in proper sequence.
     * @return the iterator of this set
     */
    public Iterator<E> iterator() {
        return set.iterator();
    }
    
    /**
     * Returns one graph element in this set.
     * @return the first graph element of this set, or <code>null</code> if there is no element in this set
     */
    public E getFirst() {
        if(set.size() > 0) {
            return iterator().next();
        }
        return null;
    }
    
    /**
     * Returns the element with a given identification number.
     * @param id the identification number of the element to be retrieved
     * @return the found element, or <code>null</code> if none 
     */
    public E get(long id) {
        for (E e : set) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
    
    /**
     * Tests if this set is equals to a given set.
     * @param set A set to be tested.
     * @return <code>true</code> if both the sets are equal, otherwise <code>false</code>.
     */
    public boolean equals(GraphElementSet<E> s) {
        if (set.size() != s.size()) {
            return false;
        }
        GraphElementSet<E> s1 = difference(s);
        return s1.isEmpty();
    }
    
    /**
     * Obtains a union set of this set and a given set.
     * @param set A set of graph elements.
     * @return A union set.
     */
    public GraphElementSet<E> union(GraphElementSet<E> s) {
        GraphElementSet<E> s1 = new GraphElementSet<E>(this);
        for (E e : s) {
            s1.add(e);
        }
        return s1;
    }
    
    /**
     * Obtains an intersection set of this set and a given set.
     * @param set A set of graph elements.
     * @return An intersection set.
     */
    public GraphElementSet<E> intersection(GraphElementSet<E> s) {
        GraphElementSet<E> s1 = new GraphElementSet<E>();
        for (E e : s) {
            if (contains(e)) {
                s1.add(e);
            }
        }
        return s1;
    }
    
    /**
     * Obtains a difference set of this set and a given set.
     * @param set A set of graph elements.
     * @return A difference set which leaves behind elements of this set after removing elements of the given set.
     */
    public GraphElementSet<E> difference(GraphElementSet<E> s) {
        GraphElementSet<E> s1 = new GraphElementSet<E>(this);
        for (E e : s) {
            s1.remove(e);
        }
        return s1;
    }
    
    /**
     * Tests if this set is a subset of a given set.
     * @param set A set of graph elements.
     * @return <code>true</code> if this set is a subset of the given set, otherwise <code>false</code>.
     */
    public boolean subsetEqual(GraphElementSet<E> s) {
        GraphElementSet<E> s1 = difference(s);
        return s1.isEmpty();
    }
    
    /**
     * Tests if this set is a subset of a given set or this set equals to the given set.
     * @param set A set of graph elements.
     * @return <code>true</code> if this set is a subset of the given set or this set equals to the given set, otherwise <code>false</code>.
     */
    public boolean subset(GraphElementSet<E> s) {
        return subsetEqual(s) && size() < s.size();
    }
    
    /**
     * Returns an array containing all graph elements in this set in proper sequence. 
     * @return An array of graph elements.
     */
    public GraphElement[] toArray() {
        GraphElement[] elems = new GraphElement[set.size()];
        int i = 0;
        for (E e : set) {
            elems[i++] = e;
        }
        return elems;
    }
    
    /**
     * Displays information about this set.
     */
    public void print() {
        for (E e : set) {
            e.print();
        }
    }
    
    /**
     * Collects information about this set for printing.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (E e : set) {
            buf.append(e.getId());
            buf.append(", ");
        }
        
        if (buf.length() != 0) {
            return buf.substring(0, buf.length() - 2);
        } else {
            return "";
        }
    }
}

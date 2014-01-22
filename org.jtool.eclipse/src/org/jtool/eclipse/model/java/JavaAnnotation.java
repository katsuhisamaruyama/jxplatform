/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import java.util.HashMap;
import java.util.Map;

/**
 * An object representing a n annotation.
 * @author Katsuhisa Maruyama
 */
public class JavaAnnotation {
    
    /**
     * The type name of of this annotation.
     */
    protected String name;
    
    /**
     * The collections of value pairs stored in this annotation.
     */
    protected Map<String, Object> values = new HashMap<String, Object>();
    
    /**
     * Creates a new, empty object.
     */
    protected JavaAnnotation() {
    }
    
    /**
     * Creates a new object representing an annotation.
     * @param abinding the annotation binding
     */
    protected JavaAnnotation(IAnnotationBinding abinding) {
        this.name = abinding.getName();
        for (IMemberValuePairBinding pair : abinding.getDeclaredMemberValuePairs()) {
            values.put(pair.getKey(), pair.getValue());
        }
    }
    
    /**
     * Returns the type name of of this annotation.
     * @return the type name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the collections of value pairs stored in this annotation.
     * @return The collections of value pairs
     */
    public Map<String, Object> getValues() {
        return values;
    }
    
    /**
     * Collects information about this annotation.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("ANNOTATION: ");
        buf.append(getName());
        buf.append(" ");
        for (String key : values.keySet()) {
            buf.append(key + " = " + values.get(key));
        }
        
        return buf.toString();
    }
}

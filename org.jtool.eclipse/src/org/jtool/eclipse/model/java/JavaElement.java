/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.jtool.eclipse.model.java.internal.ExternalJavaClass;
import org.jtool.eclipse.model.java.internal.ExternalJavaField;
import org.jtool.eclipse.model.java.internal.ExternalJavaMethod;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import java.util.HashSet;
import java.util.Set;

/**
 * A root object for a Java program element.
 * @author Katsuhisa Maruyama
 */
public abstract class JavaElement {
    
    /**
     * An AST node for this element.
     */
    protected ASTNode astNode = null;
    
    /**
     * The character index into the original source code indicating where the code fragment for this element begins.
     */
    protected int startPosition;
    
    /**
     * The length in characters of the code fragment for this element.
     */
    protected int codeLength;
    
    /**
     * The character index into the original source code indicating where the code fragment for this element begins, including comments and whitespace.
     */
    protected int extendedStartPosition;
    
    /**
     * The length in characters of the code fragment for this element, including comments and whitespace.
     */
    protected int extendedCodeLength;
    
    /**
     * The upper line number of code fragment for this element.
     */
    protected int upperLineNumber;
    
    /**
     * The bottom line number of code fragment for this element.
     */
    protected int bottomLineNumber;
    
    /**
     * The upper line number of code fragment for this element, including comments and whitespace.
     */
    protected int extendedUpperLineNumber;
    
    /**
     * The bottom line number of code fragment for this element, including comments and whitespace.
     */
    protected int extendedBottomLineNumber;
    
    /**
     * The collections of annotations that are directly present on this class.
     */
    protected Set<JavaAnnotation> annotations = new HashSet<JavaAnnotation>();
    
    /**
     * Creates a new, empty object.
     */
    protected JavaElement() {
    }
    
    /**
     * Creates a new object representing a Java program element.
     * @param node an AST node for this element
     */
    protected JavaElement(ASTNode node) {
        astNode = node;
        
        startPosition = getStartPosition(node);
        extendedStartPosition = getExtendedStartPosition(node);
        codeLength = getCodeLength(node);
        extendedCodeLength = getExtendedCodeLength(node);
        upperLineNumber = getUpperLineNumber(node);
        extendedUpperLineNumber = getExtendedUpperLineNumber(node);
        bottomLineNumber = getBottomLineNumber(node);
        extendedBottomLineNumber = getExtendedBottomLineNumber(node);
    }
    
    /**
     * Sets the code properties with respect to positions and line numbers of this element.
     * @param start the character index indicating where the code fragment for this element begins
     * @param exstart the character index indicating where the code fragment for this element begins, including comments and whitespace
     * @param len the length in characters of the code fragment for this element
     * @param exlen the length in characters of the code fragment for this element, including comments and whitespace
     * @param upper the upper line number of code fragment for this element
     * @param exupper the upper line number of code fragment for this element, including comments and whitespace
     * @param bottom the bottom line number of code fragment for this element
     * @param exbottom the bottom line number of code fragment for this element, including comments and whitespace
     */
    public void setCodeProperties(int start, int exstart, int len, int exlen, int upper, int exupper, int bottom, int exbottom) {
        startPosition = start;
        extendedStartPosition = exstart;
        codeLength = len;
        extendedCodeLength = exlen;
        upperLineNumber = upper;
        extendedUpperLineNumber = exupper;
        bottomLineNumber = bottom;
        extendedBottomLineNumber = exbottom;
    }
    
    /**
     * Sets the code properties with respect to positions and line numbers of this element.
     * @param start the character index indicating where the code fragment for this element begins
     * @param len the length in characters of the code fragment for this element
     * @param upper the upper line number of code fragment for this element
     * @param bottom the bottom line number of code fragment for this element
     */
    public void setCodeProperties(int start, int len, int upper, int bottom) {
        setCodeProperties(start, start, len, len, upper, upper, bottom, bottom);
    }
    
    /**
     * Returns the AST node for this element.
     * @return the AST node for this element
     */
    public ASTNode getASTNode() {
        return astNode;
    }
    
    /**
     * Clears information about the AST node for this element.
     */
    public void clearASTNode() {
        astNode = null;
    }
    
    /**
     * Returns the start position of code fragment for this element.
     * @param node an AST node for this element
     * @return the index value, or <code>-1</code> if no source position information is recorded
     */
    private static int getStartPosition(ASTNode node) {
        if (node != null) {
            return node.getStartPosition();
        }
        return -1;
    }
    
    /**
     * Returns the character index into the original source code indicating where the code fragment for this element begins.
     * @return the index value, or <code>-1</code> if no source position information is recorded
     */
    public int getStartPosition() {
        return startPosition;
    }
    
    /**
     * Returns the length of code fragment for this element.
     * @param node an AST node for this element
     * @return the length of the characters, or <code>0</code> if no source position information is recorded
     */
    public static int getCodeLength(ASTNode node) {
        if (node != null) {
            return node.getLength();
        }
        return -1;
    }
    
    /**
     * Returns the length in characters of the code fragment for this element.
     * @return the length of the characters, or <code>0</code> if no source position information is recorded
     */
    public int getCodeLength() {
        return codeLength;
    }
    
    /**
     * Returns the character index into the original source code indicating where the code fragment for this element ends.
     * @param node an AST node for this element
     * @return the index value, or <code>-1</code> if no source position information is recorded
     */
    private static int getEndPosition(ASTNode node) {
        return getStartPosition(node) + getCodeLength(node) - 1;
    }
    
    /**
     * Returns the character index into the original source code indicating where the code fragment for this element ends.
     * @return the index value, or <code>-1</code> if no source position information is recorded
     */
    public int getEndPosition() {
        return startPosition + codeLength - 1;
    }
    
    /**
     * Returns a compilation unit containing this element.
     * @param node an AST node for this element
     * @return the compilation unit containing this element
     */
    private static CompilationUnit getCompilationUnit(ASTNode node) {
        if (node != null) {
            return (CompilationUnit)node.getRoot();
        }
        return null;
    }
    
    /**
     * Returns the starting position of code fragment for this element, including comments and whitespace.
     * @param node an AST node for this element
     * @return the index value, or <code>-1</code> if no source position information is recorded
     */
    private static int getExtendedStartPosition(ASTNode node) {
        CompilationUnit cu = getCompilationUnit(node);
        if (cu != null) {
            return cu.getExtendedStartPosition(node);
        }
        return -1;
    }
    
    /**
     * Returns the starting position of code fragment for this element, including comments and whitespace.
     * @return the index value, or <code>-1</code> if no source position information is recorded
     */
    public int getExtendedStartPosition() {
        return extendedStartPosition;
    }
    
    /**
     * Returns the length of code fragment for this element, including comments and whitespace.
     * @param node an AST node for this element
     * @return the length of the characters, or <code>0</code> if no source position information is recorded
     */
    private static int getExtendedCodeLength(ASTNode node) {
        CompilationUnit cu = getCompilationUnit(node);
        if (cu != null) {
            return cu.getExtendedLength(node);
        }
        return -1;
    }
    
    /**
     * Returns the length of code fragment for this element, including comments and whitespace.
     * @return the length of the characters, or <code>0</code> if no source position information is recorded
     */
    public int getExtendedCodeLength() {
        return extendedCodeLength;
    }
    
    /**
     * Returns the character index into the original source code indicating where the code fragment for this element ends.
     * It may include comments and whitespace immediately before or after the normal source range for the element.
     * @param node an AST node for this element
     * @return the index value, or <code>-1</code> if no source position information is recorded
     */
    private static int getExtendedEndPosition(ASTNode node) {
        return getExtendedStartPosition(node) + getExtendedCodeLength(node) - 1;
    }
    
    /**
     * Returns the character index into the original source code indicating where the code fragment for this element ends.
     * It may include comments and whitespace immediately before or after the normal source range for the element.
     * @return the index value, or <code>-1</code> if no source position information is recorded
     */
    public int getExtendedEndPosition() {
        return extendedStartPosition + extendedCodeLength - 1;
    }
    
    /**
     * Returns the upper line number of code fragment for this element.
     * @param node an AST node for this element
     * @return the upper line number of code fragment
     */
    private static int getUpperLineNumber(ASTNode node) {
        CompilationUnit cu = getCompilationUnit(node);
        if (cu != null) {
            return cu.getLineNumber(getStartPosition(node));
        }
        return -1;
    }
    
    /**
     * Returns the upper line number of code fragment for this element.
     * @return the upper line number of code fragment
     */
    public int getUpperLineNumber() {
        return upperLineNumber;
    }
    
    /**
     * Returns the upper line number of code fragment for this element, including comments and whitespace.
     * @param node an AST node for this element
     * @return the upper line number of code fragment
     */
    private static int getExtendedUpperLineNumber(ASTNode node) {
        CompilationUnit cu = getCompilationUnit(node);
        if (cu != null) {
            return cu.getLineNumber(getExtendedStartPosition(node));
        }
        return -1;
    }
    
    /**
     * Returns the upper line number of code fragment for this element, including comments and whitespace.
     * @return the upper line number of code fragment
     */
    public int getExtendedUpperLineNumber() {
        return extendedUpperLineNumber;
    }
    
    /**
     * Returns the bottom line number of code fragment for this element.
     * @param node an AST node for this element
     * @return the bottom line number of code fragment
     */
    private static int getBottomLineNumber(ASTNode node) {
        CompilationUnit cu = getCompilationUnit(node);
        if (cu != null) {
            return cu.getLineNumber(getEndPosition(node));
        }
        return -1;
    }
    
    /**
     * Returns the bottom line number of code fragment for this element
     * @return the number of lines of code fragment
     */
    public int getBottomLineNumber() {
        return bottomLineNumber;
    }
    
    /**
     * Returns the bottom line number of code fragment for this element, including comments and whitespace.
     * @param node an AST node for this element
     * @return the bottom line number of code fragment
     */
    private static int getExtendedBottomLineNumber(ASTNode node) {
        CompilationUnit cu = getCompilationUnit(node);
        if (cu != null) {
            return cu.getLineNumber(getExtendedEndPosition(node));
        }
        return -1;
    }
    
    /**
     * Returns the bottom line number of code fragment for this element, including comments and whitespace.
     * @return the number of lines of code fragment
     */
    public int getExtendedBottomLineNumber() {
        return extendedBottomLineNumber;
    }
    
    /**
     * Obtains the lines of code fragment for this element.
     * @return the number of lines of code fragment
     */
    public int getLoc() {
        return bottomLineNumber - upperLineNumber + 1;
    }
    
    /**
     * Obtains the lines of code fragment for this element, including comments and whitespace.
     * @return the number of lines of code fragment
     */
    public int getExtendedLoc() {
        return extendedBottomLineNumber - extendedUpperLineNumber + 1;
    }
    
    /**
     * Returns a class corresponding to a given binding.
     * @param binding the type binding
     * @return the found class, or <code>null</code> if none
     */
    public static JavaClass getDeclaringJavaClass(ITypeBinding binding) {
        if (binding != null) {
            JavaClass jc = JavaClass.getJavaClass(binding.getQualifiedName());
            if (jc != null) {
                return jc;
            }
            return ExternalJavaClass.create(binding);
        }
        return null;
    }
    
    /**
     * Returns a class corresponding to a given binding.
     * @param binding the type binding
     * @return the found class, or <code>null</code> if none
     */
    public static JavaClass getDeclaringJavaClass(String fqn) {
        if (fqn != null) {
            JavaClass jc = JavaClass.getJavaClass(fqn);
            if (jc != null) {
                return jc;
            }
            return ExternalJavaClass.create(fqn);
        }
        return null;
    }
    
    /**
     * Returns a method corresponding to a given binding.
     * @param binding the method binding
     * @return the found method, or <code>null</code> if none
     */
    public static JavaMethod getDeclaringJavaMethod(IMethodBinding binding) {
        if (binding != null) { 
            JavaClass jc = JavaClass.getJavaClass(binding.getDeclaringClass().getQualifiedName());
            
            if (jc != null) {
                JavaMethod jm = jc.getJavaMethod(getSignatureString(binding));
                if (jm != null) {
                    return jm;
                }
            }
            return ExternalJavaMethod.create(binding);
        }
        return null;
    }
    
    /**
     * Returns a method corresponding to a given signature of a class with a given class.
     * @param fqn the fully-qualified name of a class declaring the method
     * @param sig the signature of the method
     * @return the found method, or <code>null</code> if none
     */
    public static JavaMethod getDeclaringJavaMethod(String fqn, String sig) {
        if (fqn != null) { 
            JavaClass jc = JavaClass.getJavaClass(fqn);
            
            if (jc != null) {
                JavaMethod jm = jc.getJavaMethod(sig);
                if (jm != null) {
                    return jm;
                }
            }
            return ExternalJavaMethod.create(fqn, sig);
        }
        return null;
    }
    
    /**
     * Obtains the signature of a given method.
     * @param bind the binding for the method
     * @return the string of the method signature
     */
    public static String getSignatureString(IMethodBinding binding) {
        return binding.getName() + "(" + getParameterTypes(binding) +" )";
    }
    
    /**
     * Obtains the type list of all the parameters of a given method.
     * @param bind the binding for the method 
     * @return the string including the fully qualified names of the parameter types, or an empty string if there is no parameter
     */
    private static String getParameterTypes(IMethodBinding binding) {
        StringBuffer buf = new StringBuffer();
        ITypeBinding[] types = binding.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            buf.append(" ");
            buf.append(types[i].getQualifiedName());
        }
        return buf.toString();
    }
    
    /**
     * Returns a field variable corresponding to a given binding.
     * @param bind the variable binding
     * @return the found field variable, or <code>null</code> if none
     */
    public static JavaField getDeclaringJavaField(IVariableBinding binding) {
        if (binding != null) { 
            ITypeBinding tbinding = binding.getDeclaringClass();
            if (tbinding != null) {
                JavaClass jc = JavaClass.getJavaClass(tbinding.getQualifiedName());
                if (jc != null) {
                    JavaField jf = jc.getJavaField(binding.getName());
                    if (jf != null) {
                        return jf;
                    }
                }
            }
            return ExternalJavaField.create(binding);
        }
        return null;
    }
    
    /**
     * Returns a field corresponding to a given name of a class with a given class.
     * @param fqn the fully-qualified name of a class declaring the field
     * @param name the name of the field
     * @return the found field variable, or <code>null</code> if none
     */
    public static JavaField getDeclaringJavaField(String fqn, String name) {
        if (fqn != null) { 
            JavaClass jc = JavaClass.getJavaClass(fqn);
            
            if (jc != null) {
                JavaField jf = jc.getJavaField(name);
                if (jf != null) {
                    return jf;
                }
            }
            return ExternalJavaField.create(fqn, name);
        }
        return null;
    }
    
    /**
     * Returns a class that encloses this element.
     * @param node the AST corresponding to this element
     * @return the class encloses this element, <code>null</code> if none
     */
    public static JavaClass getDeclaringJavaClass(ASTNode node) {
        TypeDeclaration tnode = (TypeDeclaration)getAncestor(node, ASTNode.TYPE_DECLARATION);
        if (tnode != null) {
            return getDeclaringJavaClass(tnode.resolveBinding());
        }
        
        EnumDeclaration enode = (EnumDeclaration)getAncestor(node, ASTNode.ENUM_DECLARATION);
        if (enode != null) {
            return JavaClass.getJavaClass(enode.resolveBinding().getQualifiedName());
        }
        
        return null;
    }
    
    /**
     * Returns a method that encloses this element.
     * @param node the AST corresponding to this element
     * @return the method that encloses this element, <code>null</code> if none
     */
    public static JavaMethod getDeclaringJavaMethod(ASTNode node) {
        MethodDeclaration mnode = (MethodDeclaration)getAncestor(node, ASTNode.METHOD_DECLARATION);
        if (mnode != null) {
            return getDeclaringJavaMethod(mnode.resolveBinding());
        }
        
        Initializer inode = (Initializer)getAncestor(node, ASTNode.INITIALIZER);
        if (inode != null) {
            JavaClass jc = getDeclaringJavaClass(node);
            if (jc != null) {
                return jc.getJavaMethod(JavaMethod.InitializerName);
            }
        }
        
        return null;
    }
    
    /**
     * Obtains the AST Node with a given node type, which declares or encloses a given AST Node.
     * @param node the enclosed AST Node
     * @param sort the node type (@see org.eclipse.jdt.core.dom.ASTNode)
     * @return the found AST node, or <code>null</code> if none
     */
    public static ASTNode getAncestor(ASTNode node, int sort) {
        if (node.getNodeType() == sort) {
            return node;
        }
        
        ASTNode parent = node.getParent();
        if (parent != null) {
            return getAncestor(parent, sort);
        }
        
        return null;
    }
    
    /**
     * An integer number that indicates the binding level.
     */
    protected static int bindingLevel = 0;
    
    /**
     * Sets the binding level.
     * @param level an integer number that indicates the binding level.
     */
    protected static void setBindingLevel(int level) {
        bindingLevel = level;
    }
    
    /**
     * Returns the binding level.
     * @return An integer number that indicates the binding level.
     */
    protected static int getBindingLevel() {
        return bindingLevel;
    }
    
    /**
     * Obtains source code corresponding to this Java element.
     * @param node an AST node for this element
     * @return the contents of the source code
     */
    public static String getSource(ASTNode node) {
        StringBuffer buf = new StringBuffer(getCompilationUnitSource(node));
        return buf.substring(getStartPosition(node), getEndPosition(node) + 1);
    }
    
    /**
     * Obtains source code corresponding to this Java element.
     * It may include comments and whitespace immediately before or after the normal source range for the element.
     * @param node an AST node for this element
     * @return the contents of the source code
     */
    public static String getExtendedSource(ASTNode node) {
        StringBuffer buf = new StringBuffer(getCompilationUnitSource(node));
        return buf.substring(getExtendedStartPosition(node), getExtendedEndPosition(node) + 1);
    }
    
    /**
     * Obtains source code corresponding to the compilation unit containing this Java element.
     * @param node the AST corresponding to this element
     * @return the contents of the source code
     */
    private static String getCompilationUnitSource(ASTNode node) {
        CompilationUnit cu = getCompilationUnit(node);
        if (cu != null) {
            ICompilationUnit icu = (ICompilationUnit)cu.getJavaElement();
            if (icu != null) {
                try {
                    return icu.getSource();
                } catch (JavaModelException e) { /* empty */ }
            }
        }
        return "";
    }
    
    /**
     * Sets annotations that are directly present on this element.
     * @param abindings the array of annotation bindings
     */
    public void setAnnotations(IAnnotationBinding[] abindings) {
        for (IAnnotationBinding abinding : abindings) {
            annotations.add(new JavaAnnotation(abinding));
        }
    }
    
    /**
     * Returns the annotations that are directly present on this element.
     * @return the annotations
     */
    public Set<JavaAnnotation> getAnnotations() {
        return annotations;
    }
    
    /**
     * Collects information about all annotations that are directly present on this element.
     * @return the string for printing
     */
    public String getAnnotationInfo() {
        StringBuffer buf = new StringBuffer();
        
        if (annotations.size() != 0) {
            for (JavaAnnotation jann : annotations) {
                buf.append(jann.toString());
                buf.append("\n");
            }
        }
        
        return buf.toString();
    }
    
    /**
     * Tests if a given type is primitive.
     * @param type the string of the type to be checked
     * @return <code>true</code> if a given type is primitive, otherwise <code>false</code>
     */
    public static boolean isPrimitiveType(String type) {
        return type.compareTo("byte") == 0 ||
               type.compareTo("short") == 0 ||
               type.compareTo("int") == 0 ||
               type.compareTo("long") == 0 ||
               type.compareTo("float") == 0 ||
               type.compareTo("double") == 0 ||
               type.compareTo("char") == 0 ||
               type.compareTo("boolean") == 0;
    }
}

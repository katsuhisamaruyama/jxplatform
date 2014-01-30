/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java;

import org.eclipse.jdt.core.IJavaProject;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.apache.log4j.Logger;

/**
 * An object representing a project.
 * @author Katsuhisa Maruyama
 */
public class JavaProject {
    
    static Logger logger = Logger.getLogger(JavaFile.class.getName());
    
    /**
     * The cache for all objects of classes.
     */
    protected static HashMap<String, JavaProject> cache = new HashMap<String, JavaProject>();
    
    /**
     * The collection of all files.
     */
    protected Map<String, JavaFile> files = new HashMap<String, JavaFile>();
    
    /**
     * The collection of all packages.
     */
    protected HashMap<String, JavaPackage> packages = new HashMap<String, JavaPackage>();
    
    /**
     * The information of which stored in this project.
     */
    protected IJavaProject project;
    
    /**
     * The name of this project.
     */
    protected String name;
    
    /**
     * The name of the top directory of this project.
     */
    protected String topDir;
    
    /**
     * The time when the project information was created lastly.
     */
    protected long lastCreatedTime;
    
    /**
     * Creates a new, empty object.
     */
    JavaProject() {
        super();
    }
    
    /**
     * Creates an object that will store information about a project.
     * @param project the project information
     * @param name the name of the project
     * @param dir the top directory of the project
     */
    private JavaProject(IJavaProject project, String name, String dir) {
        this.project = project;
        this.name = name;
        this.topDir = dir;
        this.lastCreatedTime = System.currentTimeMillis();
    }
    
    /**
     * Creates an object that will store information about a project.
     * @param project the project information
     */
    public static JavaProject create(IJavaProject project) {
        String name = project.getProject().getName();
        String dir = project.getProject().getLocation().toString();
        
        return create(project, name, dir);
    }
    
    /**
     * Creates an object that will store information about a project.
     * @param name the name of the project
     * @param dir the top directory of the project
     */
    public static JavaProject create(String name, String dir) {
        return create(null, name, dir);
    }
    
    /**
     * Creates an object that will store information about a project.
     * @param project the project information
     * @param name the name of the project
     * @param dir the top directory of the project
     */
    private static JavaProject create(IJavaProject project, String name, String dir) {
        if (dir != null) {
            JavaProject jproj = cache.get(name);
            if (jproj != null) {
                return jproj;
            }
            
            jproj = new JavaProject(project, name, dir);
            cache.put(name, jproj);
            return jproj;
        }
        return null;
    }
    
    /**
     * Obtains an object that will store information about a project with a given name.
     * @param name the name of the project to be retrieved
     * @return the found object
     */
    public static JavaProject getJavaProject(String name) {
        return cache.get(name);
    }
    
    /**
     * Removes a file with a given name and its related files.
     * @param pathname the name of the file to be removed
     */
    public void removeJavaFile(String pathname) {
        for (JavaFile jf : getJavaFiles()) {
            if (pathname.compareTo(jf.getPath()) == 0) {
                JavaClass.removeClassesRelatedTo(jf);
            }
        }
        cleanJavaProjects();
    }
    
    /**
     * Cleans empty projects.
     */
    private void cleanJavaProjects() {
        for (JavaProject jproj : cache.values()) {
            if (jproj.getJavaFiles().size() == 0) {
                cache.remove(jproj.getName());
            }
        }
    }
    
    /**
     * Removes every information about this project stored in the cache.
     */
    public static void removeAllCache() {
        for (JavaProject jproj : cache.values()) {
            jproj.getJavaPackages().clear();
        }
        cache.clear();
        
        JavaClass.removeAllClassesInCache();
    }
    
    /**
     * Returns the Java project for this project.
     * @return the Java project
     */
    public IJavaProject getJavaProject() {
        return project;
    }
    
    /**
     * Returns the time when the project information was created lastly.
     * @return the last created time
     */
    public long getLastCreatedTime() {
        return lastCreatedTime;
    }
    
    /**
     * Tests if this project exists in the Eclipse's workspace
     * @return <code>true</code> if this project lays on the workspace, otherwise <code>false</code>
     */
    public boolean isInWorkspace() {
        return project != null;
    }
    
    /**
     * Returns the name of this project.
     * @return the name of the project
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the top directory of this project.
     * @return the name of the top directory of the project
     */
    public String getTopDir() {
        return topDir;
    }
    
    /**
     * Adds a file contained in this project.
     * @param jfile the file to be added
     */
    public void addJavaFile(JavaFile jfile) {
        if (files.get(jfile.getPath()) == null) {
            files.put(jfile.getPath(), jfile);
        }
    }
    
    /**
     * Returns all the files in this project.
     * @return the collection of the files
     */
    public Set<JavaFile> getJavaFiles() {
        Set<JavaFile> sets = new HashSet<JavaFile>();
        for (JavaFile jf : files.values()) {
            sets.add(jf);
        }
        return sets;
    }
    
    /**
     * Returns a file with a given path name.
     * @param path the path name of the file
     * @return the found object, or <code>null</code> if none 
     */
    public JavaFile getJavaFile(String path) {
        return files.get(path);
    }
    
    /**
     * Removes a file in this project.
     * @param jf the file to be removed
     */
    public void remove(JavaFile jf) {
        files.remove(jf.getPath());
    }
    
    /**
     * Adds a package contained in this project.
     * @param jpackage the package to be added
     */
    public void addJavaPackage(JavaPackage jpackage) {
        if (packages.get(jpackage.getName()) == null) {
            packages.put(jpackage.getName(), jpackage);
        }
    }
    
    /**
     * Returns all the packages in this project.
     * @return the collection of the packages
     */
    public Set<JavaPackage> getJavaPackages() {
        Set<JavaPackage> sets = new HashSet<JavaPackage>();
        for (JavaPackage jp : packages.values()) {
            sets.add(jp);
        }
        return sets;
    }
    
    /**
     * Returns a package with a given name.
     * @param name the name of the package
     * @return the found object, or <code>null</code> if none 
     */
    public JavaPackage getJavaPackage(String name) {
        return packages.get(name);
    }
    
    /**
     * Returns all the classes in this project.
     * @return the collection of the classes
     */
    public Set<JavaClass> getJavaClasses() {
        return JavaClass.getAllJavaClassesInCache();
    }
    
    /**
     * Returns all the classes in this project, which are sorted in dictionary order.
     * @return the collection of the classes
     */
    public List<JavaClass> getJavaClassesInDictionaryOrder() {
        return sortClasses(JavaClass.getAllJavaClassesInCache());
    }
    
    /**
     * Returns an object corresponding to a class with a given name.
     * @param fqn the fully qualified name of a class or an interface to be retrieved
     * @return the found object, or <code>null</code> if no class was found
     */
    public JavaClass getJavaClass(String fqn) {
        return JavaClass.getJavaClass(fqn);
    }
    
    /**
     * Tests if a given package equals to this.
     * @param jproj the Java project
     * @return <code>true</code> if the given package equals to this, otherwise <code>false</code>
     */
    public boolean equals(JavaProject jproj) {
        if (jproj == null) {
            return false;
        }
        
        if (this == jproj) {
            return true;
        }
        
        return getTopDir().compareTo(jproj.getTopDir()) == 0; 
    }
    
    /**
     * Returns a hash code value for this project.
     * @return the hash code value for the project
     */
    public int hashCode() {
        return getTopDir().hashCode();
    }
    
    /**
     * Collects information about this project.
     * @return the string for printing
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("PROJECT: ");
        buf.append(getName());
        buf.append(" [");
        buf.append(getTopDir());
        buf.append("]");
        buf.append("\n");
        
        return buf.toString();
    }
    
    /**
     * Obtains the sorted collection of classes in dictionary order of their names.
     * @return the sorted collection of the classes.
     */
    public static List<JavaClass> sortClasses(Set<JavaClass> sets) {
        List<JavaClass> classes = new ArrayList<JavaClass>();
        classes.addAll(sets);
        
        Collections.sort(classes, new Comparator<JavaClass>() {
            
            public int compare(JavaClass jc1, JavaClass jc2) {
                return jc1.getName().compareTo(jc2.getName());
            }
        });
        
        return classes;
    }
}

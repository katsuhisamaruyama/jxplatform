/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.java.internal;

import org.jtool.eclipse.model.java.JavaProject;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

/**
 * Implements the listener that will be notified when the resource changes complete.
 * @author Katsuhisa Maruyama
 */
public class ResourceChangeListener implements IResourceChangeListener {
    
    /**
     * Notifies this listener that some resource changes are happening, or have already happened. 
     * @param event the change event
     */
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
                ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
                event.getDelta().accept(visitor);
                
                for (String pathname : visitor.getPaths()) {
                    String name = pathname.substring(1);
                    String projectName = name.substring(0, name.indexOf(File.separatorChar));
                    
                    JavaProject jproj = JavaProject.getJavaProject(projectName);
                    if (jproj != null) {
                        jproj.removeJavaFile(pathname);
                    }
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * An objects that visits resource deltas.
     * @author Katsuhisa Maruyama
     */
    class ResourceDeltaVisitor implements IResourceDeltaVisitor {
        
        /**
         * The collection of files to be removed
         */
        private Set<String> pathnames = new HashSet<String>();
        
        /** 
         * Visits the given resource delta.
         * @return <code>true</code> if the resource delta's children should be visited; <code>false</code> if they should be skipped
         * @exception CoreException if the visit fails for some reason
         */
        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource res = delta.getResource();
            
            switch (delta.getKind()) {
                case IResourceDelta.ADDED:
                    break;
                case IResourceDelta.REMOVED:
                    String rext = res.getFileExtension();
                    if (rext != null && rext.compareTo("java") == 0) {
                        pathnames.add(res.getFullPath().toString());
                    }
                    break;
                case IResourceDelta.CHANGED:
                    String cext = res.getFileExtension();
                    if (cext != null && cext.compareTo("java") == 0) {
                        pathnames.add(res.getFullPath().toString());
                    }
                    break;
            }
            return true;
        }
        
        /**
         * Returns the collection of files to be removed.
         * @return the collection of the files
         */
        Set<String> getPaths() {
            return pathnames;
        }
     }
}

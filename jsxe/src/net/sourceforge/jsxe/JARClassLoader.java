/*
JARClassLoader.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

Loads classes from a directory of jars.

This file written by Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 2002 Ian Lewis

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe;

//{{{ Imports

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import net.sourceforge.jsxe.util.ArrayListEnumeration;

//}}}

/**
 * A class loader implementation that loads classes from JAR files.
 * @author Ian Lewis
 * @version $Id$
 */
public class JARClassLoader extends ClassLoader {
    
    //{{{ JARClassLoader constructor
    
    public JARClassLoader() {}//}}}
    
    //{{{ ClassLoader methods
    
    //{{{ findClass()
    
    protected Class findClass(String name) throws ClassNotFoundException {
        
        String classFileName = name.replace('.','/').concat(".class");
        Iterator jarItr = m_jarFiles.iterator();
        
        while (jarItr.hasNext()) {
            try {
                
                JarFile zipFile = new JarFile((File)jarItr.next());
                ZipEntry entry = zipFile.getEntry(classFileName);
                
                if (entry != null) {
                    
                    InputStream in = zipFile.getInputStream(entry);
                    
                    boolean fail = false;
                    int len = (int)entry.getSize();
                    byte[] data = new byte[len];
                    int success = 0;
                    int offset = 0;
                    
                    while(success < len && !fail) {
                        len -= success;
                        offset += success;
                        
                        success = in.read(data,offset,len);
                        if(success == -1) {
                            fail = true;
                        }
                    }
                    
                    return defineClass(name,data,0,data.length);
                }
                
            } catch(IOException io) {
                //failed, try the next jar
            }
        }
        throw new ClassNotFoundException(name);
        
    }//}}}

    //{{{ findResources()
    
    protected Enumeration findResources(String name) throws IOException {
        Iterator jarItr = m_jarFiles.iterator();
        ArrayList urls = new ArrayList();
        
        while (jarItr.hasNext()) {
            File file = (File)jarItr.next();
            JarFile jarfile = new JarFile(file);
            JarEntry entry = jarfile.getJarEntry(name);
            if (entry != null) {
                urls.add(new URL("jar:"+file.toURL().toString()+"!"+name));
            }
        }
        
        return new ArrayListEnumeration(urls);
    }//}}}
    
    //{{{ findResource
    
    protected URL findResource(String name) {
        Iterator jarItr = m_jarFiles.iterator();
        ArrayList urls = new ArrayList();
        
        while (jarItr.hasNext()) {
            try {
                File file = (File)jarItr.next();
                JarFile jarfile = new JarFile(file);
                JarEntry entry = jarfile.getJarEntry(name);
                if (entry != null) {
                    return new URL("jar:"+file.toURL().toString()+"!"+name);
                }
            } catch (IOException ioe) {}
        }
        
        return null;
    }//}}}

    //}}}
    
    //{{{ addJarFile()
    /**
     * Adds a jar file to the search path for the class loader
     * @param path the path to the jar file
     */
    public void addJarFile(String path) throws FileNotFoundException, IOException {
        addJarFile(new File(path));
    }//}}}
    
    //{{{ addJarFile()
    
    public void addJarFile(File file) throws FileNotFoundException, IOException {
        if (file.exists()) {
            JarFile jarFile = new JarFile(file);
            m_jarFiles.add(file);
        } else {
            throw new FileNotFoundException("The jar file was not found");
        }
    }//}}}
    
    //{{{ addDirectory
    
    /**
     * Adds all jar files in a directory to the seach path for the class
     * loader
     * @param path the path for the directory containing jar files
     * @return an ArrayList of pathnames of jar files that could not be loaded.
     */
     public ArrayList addDirectory(String path) {
        ArrayList errors = new ArrayList();
        
        File directory = new File(path);
        File[] files = directory.listFiles(new FileFilter() {//{{{
            public boolean accept(File pathname) {
                return (pathname.getName().endsWith(".jar"));
            }
        });//}}}
        
        for (int i=0; i<files.length; i++) {
            try {
                addJarFile(files[i]);
            } catch (IOException e) {
                errors.add(files[i].getPath());
            }
        }
        
        return errors;
    }//}}}
    
    //{{{ Private Members
    
    private ArrayList m_jarFiles = new ArrayList();
    
    //}}}
}

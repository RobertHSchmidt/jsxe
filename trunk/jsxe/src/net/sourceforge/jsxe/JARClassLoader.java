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
import java.lang.reflect.Modifier;
import net.sourceforge.jsxe.util.ArrayListEnumeration;

//}}}

/**
 * A class loader implementation that loads classes from JAR files. Also manages
 * getting files from plugin JARs.
 * @author Ian Lewis
 * @version $Id$
 */
public class JARClassLoader extends ClassLoader {
    
    //{{{ ClassLoader methods
    
    //{{{ findClass()
    
    protected Class findClass(String name) throws ClassNotFoundException {
        
        String classFileName = name.replace('.','/').concat(".class");
        Iterator jarItr = m_jarFiles.values().iterator();
        
        while (jarItr.hasNext()) {
            try {
                
                JarFile zipFile = (JarFile)jarItr.next();
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
                    Class c = defineClass(name,data,0,data.length);
                    return c;
                }
                
            } catch(IOException io) {
                //failed, try the next jar
            }
        }
        throw new ClassNotFoundException(name);
        
    }//}}}

    //{{{ findResources()
    
    protected Enumeration findResources(String name) throws IOException {
        Iterator fileItr = m_files.values().iterator();
        Iterator jarItr  = m_jarFiles.values().iterator();
        ArrayList urls = new ArrayList();
        
        while (fileItr.hasNext()) {
            File file = (File)fileItr.next();
            JarFile jarfile = (JarFile)jarItr.next();
            JarEntry entry = jarfile.getJarEntry(name);
            if (entry != null) {
                urls.add(new URL("jar:"+file.toURL().toString()+"!/"+name));
            }
        }
        
        return new ArrayListEnumeration(urls);
    }//}}}
    
    //{{{ findResource
    
    protected URL findResource(String name) {
        Iterator filesItr = m_files.values().iterator();
        Iterator jarItr = m_jarFiles.values().iterator();
        ArrayList urls = new ArrayList();
        
        while (jarItr.hasNext()) {
            try {
                File file = (File)filesItr.next();
                JarFile jarfile = (JarFile)jarItr.next();
                JarEntry entry = jarfile.getJarEntry(name);
                if (entry != null) {
                    return new URL("jar:"+file.toURL().toString()+"!/"+name);
                }
            } catch (IOException ioe) {
                jsXe.exiterror(null, "findResource:IOException: "+ioe.getMessage(), 1);
            }
        }
        
        return null;
    }//}}}

    //}}}
    
    //{{{ addJarFile()
    /**
     * Adds a jar file to the search path for the class loader and
     * loads the jar as a plugin.
     * @param path the path to the jar file
     */
    public void addJarFile(String path) throws FileNotFoundException, IOException {
        addJarFile(new File(path));
    }//}}}
    
    //{{{ addJarFile()
    /**
     * Adds a jar file to the search path for the class loader and loads
     * the jar as a plugin
     * @param file the file to add
     */
    public void addJarFile(File file) throws FileNotFoundException, IOException {
        if (file.exists()) {
            
            JarFile jarFile = new JarFile(file);
            
            setProperties(jarFile); //if there is an IOException then this jar is messed up.
            
            definePackages(jarFile);
            m_files.put(file.getName(), file);
            m_jarFiles.put(file.getName(), jarFile);
            
           // Enumeration entries = jarFile.entries();
           // while (entries.hasMoreElements()) {
           //     ZipEntry entry = (ZipEntry)entries.nextElement();
           //     String name = entry.getName();
           //     if(name.endsWith(".class")) {
           //         
           //         
           //         if (name.endsWith("Plugin.class"))
           //             pluginClasses.add(name);
           //     }
           // }
            
        } else {
            throw new FileNotFoundException("The jar file was not found");
        }
    }//}}}
    
    //{{{ addDirectory
    
    /**
     * Adds all jar files in a directory to the seach path for the class
     * loader and attempts to load the jars as plugins.
     *
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
        if (files != null) {
            for (int i=0; i<files.length; i++) {
                try {
                    addJarFile(files[i]);
                } catch (IOException e) {
                    errors.add(e);
                }
            }
        }
        
        return errors;
    }//}}}
    
    //{{{ getEntry()
    
    public JarEntry getEntry(String plugin, String name) {
        JarFile jar = (JarFile)m_jarFiles.get(plugin);
        return jar.getJarEntry(name);
    }//}}}
    
    //{{{ getAllPlugins()
    
    public ArrayList getAllPlugins() {
        Iterator pluginItr = getViewPluginNames();
        ArrayList plugins = new ArrayList();
        while (pluginItr.hasNext()) {
            String pluginName = pluginItr.next().toString();
            ViewPlugin plugin = (ViewPlugin)m_viewPlugins.get(pluginName);
            plugins.add(plugin);
        }
        
        pluginItr = getActionPluginNames();
        while (pluginItr.hasNext()) {
            String pluginName = pluginItr.next().toString();
            ActionPlugin plugin = (ActionPlugin)m_actionPlugins.get(pluginName);
            plugins.add(plugin);
        }
        
        return plugins;
    }//}}}
    
    //{{{ getViewPluginNames()
    
    public Iterator getViewPluginNames() {
        return m_viewPlugins.keySet().iterator();
    }//}}}
    
    //{{{ getViewPlugins()
    /**
     * Gets all view plugins. You should run startPlugins() before calling this function.
     * @return an ArrayList of ViewPlugin objects
     */
    public ArrayList getViewPlugins() {
        Iterator pluginItr = getViewPluginNames();
        ArrayList plugins = new ArrayList();
        while (pluginItr.hasNext()) {
            String pluginName = pluginItr.next().toString();
            ViewPlugin plugin = (ViewPlugin)m_viewPlugins.get(pluginName);
            plugins.add(plugin);
        }
        return plugins;
    }//}}}
    
    //{{{ getViewPlugin()
    
    public ViewPlugin getViewPlugin(String name) {
        return (ViewPlugin)m_viewPlugins.get(name);
    }//}}}
    
    //{{{ getActionPluginNames()
    /**
     * Returns an Iterator object containing the names of the all installed 
     * action plugins that are not view plugins.
     */
    public Iterator getActionPluginNames() {
        return m_actionPlugins.keySet().iterator();
    }//}}}
    
    //{{{ getActionPlugins()
    /**
     * Gets all action plugins that are not view plugins. You should run startPlugins()
     * before calling this function.
     * @return an ArrayList of ActionPlugin objects
     */
    public ArrayList getActionPlugins() {
        Iterator pluginItr = getActionPluginNames();
        ArrayList plugins = new ArrayList();
        while (pluginItr.hasNext()) {
            String pluginName = pluginItr.next().toString();
            ActionPlugin plugin = (ActionPlugin)m_actionPlugins.get(pluginName);
            plugins.add(plugin);
        }
        return plugins;
    }//}}}
    
    //{{{ getActionPlugin()
    /**
     * Gets an action plugin by name. Not for use in retrieving ViewPlugins.
     * @param name the name of the ActionPlugin you want to retrieve.
     * @return the ActionPlugin or null if a plugin with the name given is not loaded.
     */
    public ActionPlugin getActionPlugin(String name) {
        return (ActionPlugin)m_actionPlugins.get(name);
    }//}}}
    
    //{{{ startPlugins()
    /**
     * Starts all the plugins from their respective jar files.
     * @return an ArrayList of errors (either Strings or Exceptions).
     */
    public ArrayList startPlugins() {
        Iterator jarItr = m_jarFiles.keySet().iterator();
        ArrayList errors = new ArrayList();
        
        while (jarItr.hasNext()) {
            JarFile jarFile = (JarFile)m_jarFiles.get(jarItr.next().toString());
            
            try {
                startPlugin(jarFile);
            } catch (IOException e) {
                errors.add(e);
            } catch (PluginDependencyException e2) {
                errors.add(e2);
            } catch (PluginLoadException e3) {
                errors.add(e3);
            }
        }
        
        return errors;
        
    }//}}}
    
    //{{{ Private Members
    
    //{{{ checkDependencies
    
    private void checkDependencies(JarFile file) throws IOException, PluginDependencyException {
       // String name = getManifestAttribute(jarfile, Attributes.Name.IMPLEMENTATION_TITLE);
       // String dep;
       // while ((dep = m_pluginProperties.getProperty(name+".dependency."+i++)) != null) {
       //     //parse the dependency
       //     
       //     int index = dep.indexOf(' ');
       //     if(index == -1) {
       //         throw new IOException(name + " has an invalid dependency: " + dep);
       //     }
       //     
       //     String what = dep.substring(0,index);
       //     String arg = dep.substring(index + 1);
       //     
       //     if(what.equals("jdk"))
       //     {
       //         if(MiscUtilities.compareStrings(
       //             System.getProperty("java.version"),
       //             arg,false) < 0)
       //         {
       //             String[] args = { arg, System.getProperty("java.version") };
       //             throw new IOException(name + " requires Java 
       //             jEdit.pluginError(jar.getPath(),"plugin-error.dep-jdk",args);
       //             ok = false;
       //         }
       //     }
       //     else if(what.equals("jedit"))
       //     {
       //         if(arg.length() != 11)
       //         {
       //             Log.log(Log.ERROR,this,"Invalid jEdit version"
       //                 + " number: " + arg);
       //             ok = false;
       //         }
       //         
       //         if(MiscUtilities.compareStrings(
       //             jEdit.getBuild(),arg,false) < 0)
       //         {
       //             String needs = MiscUtilities.buildToVersion(arg);
       //             String[] args = { needs,
       //                 jEdit.getVersion() };
       //             jEdit.pluginError(jar.getPath(),
       //                 "plugin-error.dep-jedit",args);
       //             ok = false;
       //         }
       //     }
       //     else if(what.equals("plugin"))
       //     {
       //         int index2 = arg.indexOf(' ');
       //         if(index2 == -1)
       //         {
       //             Log.log(Log.ERROR,this,name 
       //                 + " has an invalid dependency: "
       //                 + dep + " (version is missing)");
       //             return false;
       //         }
       //         
       //         String plugin = arg.substring(0,index2);
       //         String needVersion = arg.substring(index2 + 1);
       //         String currVersion = jEdit.getProperty("plugin." 
       //             + plugin + ".version");
       //             
       //         if(currVersion == null)
       //         {
       //             String[] args = { needVersion, plugin };
       //             jEdit.pluginError(jar.getPath(),
       //                 "plugin-error.dep-plugin.no-version",
       //                 args);
       //             ok = false;
       //         }
       //         else if(MiscUtilities.compareStrings(currVersion,
       //             needVersion,false) < 0)
       //         {
       //             String[] args = { needVersion, plugin, currVersion };
       //             jEdit.pluginError(jar.getPath(),
       //                 "plugin-error.dep-plugin",args);
       //             ok = false;
       //         }
       //         else if(jEdit.getPlugin(plugin) instanceof EditPlugin.Broken)
       //         {
       //             String[] args = { plugin };
       //             jEdit.pluginError(jar.getPath(),
       //                 "plugin-error.dep-plugin.broken",args);
       //             ok = false;
       //         }
       //     }
       //     else if(what.equals("class"))
       //     {
       //         try
       //         {
       //             loadClass(arg,false);
       //         }
       //         catch(Exception e)
       //         {
       //             String[] args = { arg };
       //             jEdit.pluginError(jar.getPath(),
       //                 "plugin-error.dep-class",args);
       //             ok = false;
       //         }
       //     }
       //     else
       //     {
       //         Log.log(Log.ERROR,this,name + " has unknown"
       //             + " dependency: " + dep);
       //         return false;
       //     }
       // }
    }//}}}
    
    //{{{ definePackages() 
    /**
     * Defines all packages found in the given Java archive file. The
     * attributes contained in the specified Manifest will be used to obtain
     * package version and sealing information.
     */
    private void definePackages(JarFile zipFile) throws IOException {
        try {
            Manifest manifest = zipFile.getManifest();

            if (manifest != null) {
                Map entries = manifest.getEntries();
                Iterator i = entries.keySet().iterator();

                while(i.hasNext()) {
                    String path = (String)i.next();

                    if (!path.endsWith(".class")) {
                        String name = path.replace('/', '.');

                        if(name.endsWith("."))
                            name = name.substring(0, name.length() - 1);

                        // code url not implemented
                        definePackage(path,name,manifest,null);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            // should never happen, not severe anyway
        }
    } //}}}

    //{{{ definePackage()
    /**
     * Defines a new package by name in this ClassLoader. The attributes
     * contained in the specified Manifest will be used to obtain package
     * version and sealing information. For sealed packages, the additional
     * URL specifies the code source URL from which the package was loaded.
     */
    private Package definePackage(String path, String name, Manifest man, URL url) throws IllegalArgumentException {
        String specTitle = null;
        String specVersion = null;
        String specVendor = null;
        String implTitle = null;
        String implVersion = null;
        String implVendor = null;
        String sealed = null;
        URL sealBase = null;

        Attributes attr = man.getAttributes(path);

        if (attr != null) {
            specTitle = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
            specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
            specVendor = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            implTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            implVendor = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            sealed = attr.getValue(Attributes.Name.SEALED);
        }

        attr = man.getMainAttributes();

        if (attr != null) {
            if (specTitle == null) {
                specTitle = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
            }

            if (specVersion == null) {
                specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }

            if (specVendor == null) {
                specVendor = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            }

            if (implTitle == null) {
                implTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            }

            if (implVersion == null) {
                implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            }

            if (implVendor == null) {
                implVendor = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            }

            if (sealed == null) {
                sealed = attr.getValue(Attributes.Name.SEALED);
            }
        }

        return super.definePackage(name, specTitle, specVersion, specVendor,
            implTitle, implVersion, implVendor,
            sealBase);
    } //}}}
    
    //{{{ setProperties() method
    
    private void setProperties(JarFile jarFile) throws IOException {
        String pluginName = getManifestAttribute(jarFile, Attributes.Name.IMPLEMENTATION_TITLE);
        if (pluginName != null) {
            m_pluginProperties.setProperty(pluginName+".name", pluginName);
            m_pluginProperties.setProperty(pluginName+".main-class", getManifestAttribute(jarFile, Attributes.Name.MAIN_CLASS));
            m_pluginProperties.setProperty(pluginName+".version", getManifestAttribute(jarFile, Attributes.Name.IMPLEMENTATION_VERSION));
            m_pluginProperties.setProperty(pluginName+".url", getManifestAttribute(jarFile, Attributes.Name.IMPLEMENTATION_URL));
            
            //Set dependency properties
            ZipEntry entry = jarFile.getEntry("dependency.props");
            if (entry != null) {
                //No dependency file. Assume no dependencies
                
                InputStream stream = jarFile.getInputStream(entry);
                Properties dependencies = new Properties();
                dependencies.load(stream);
                
                String dep;
                int i = 0;
                while ((dep = dependencies.getProperty("dependency." + i++)) != null) {
                    m_pluginProperties.setProperty(pluginName+".dependency." + i++, dep);
                }
            }
        }
    }//}}}
    
    //{{{ startPlugin()
    
    private void startPlugin(JarFile jarfile) throws IOException, PluginDependencyException, PluginLoadException {
        
        String mainPluginClass = getManifestAttribute(jarfile, Attributes.Name.MAIN_CLASS);
        //TODO String pluginName = getManifestAttribute(jarfile, Attributes.Name.IMPLEMENTATION_TITLE);
        
        checkDependencies(jarfile);
        
        if (mainPluginClass != null) {
            try {
                Class pluginClass = loadClass(mainPluginClass);
                
                int modifiers = pluginClass.getModifiers();
                if (!Modifier.isInterface(modifiers)
                    && !Modifier.isAbstract(modifiers)
                    && ActionPlugin.class.isAssignableFrom(pluginClass)) {
                    
                    Object plugin = pluginClass.newInstance();
                    
                    if (ViewPlugin.class.isAssignableFrom(pluginClass)) {
                        //It's a view plugin
                        ViewPlugin viewPlugin = (ViewPlugin)plugin;
                        //TODO m_viewPlugins.put(pluginName, viewPlugin);
                        m_viewPlugins.put(viewPlugin.getName(), viewPlugin);
                    } else {
                        //It's an Action plugin
                        ActionPlugin actionPlugin = (ActionPlugin)plugin;
                        //TODO m_actionPlugins.put(pluginName, actionPlugin);
                        m_actionPlugins.put(actionPlugin.getName(), actionPlugin);
                    }
                } else {
                    /*
                    It's not a plugin. No biggie. We need it to be loaded
                    anyway.
                    */
                    throw new PluginLoadException(jarfile, "Main class is not a plugin class");
                }
            } catch (ClassNotFoundException e) {
                throw new IOException(e.getMessage());
            } catch (InstantiationException e) {
                throw new IOException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new IOException(e.getMessage());
            }
        } else {
            throw new PluginLoadException(jarfile, "No main class defined.");
        }
    }//}}}
    
    //{{{ getManifestAttribute()
    
    private String getManifestAttribute(JarFile file, Attributes.Name name) throws IOException {
        Manifest manifest = file.getManifest();
        if (manifest != null) {
            return manifest.getMainAttributes().getValue(name);
        } else {
            return null;
        }
    }//}}}
    
    // fileName -> File
    private static HashMap m_files = new HashMap();
    // fileName -> JarFile
    private static HashMap m_jarFiles = new HashMap();
    // pluginName -> ViewPlugin
    private static HashMap m_viewPlugins = new HashMap();
    // pluginName -> ActionPlugin
    private static HashMap m_actionPlugins = new HashMap();
    
    // internal properties used for storing name, version, etc.
    private Properties m_pluginProperties = new Properties();
    
    //}}}
}

/*
jsXe.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.action.FileSaveAction;
import net.sourceforge.jsxe.gui.*;
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
//}}}

//{{{ Swing classes
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
//}}}

//{{{ AWT Components
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
//}}}

//{{{ DOM Classes
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Java Base classes
import java.io.*;
import java.net.URL;
import java.util.*;
//}}}

//}}}

/**
 * The main class of the java simple XML editor (jsXe)
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class jsXe {
    
    //{{{ The main() method of jsXe
    
    /**
     * The main method of jsXe
     * @param args The command line arguments
     */
    public static void main(String args[]) {
        
        try {
            
            //{{{ Check the java version
            String javaVersion = System.getProperty("java.version");
            if(javaVersion.compareTo("1.3") < 0)
            {
                System.err.println(getAppTitle() + ": ERROR: " + getAppTitle() + getVersion());
                System.err.println(getAppTitle() + ": ERROR: You are running Java version " + javaVersion + ".");
                System.err.println(getAppTitle() + ": ERROR:" + getAppTitle()+" requires Java 1.3 or later.");
                System.exit(1);
            }//}}}
            
            //{{{ start logging
            String homeDir = System.getProperty("user.home");
            String fileSep = System.getProperty("file.separator");
            
            String settingsDirectory = homeDir+fileSep+".jsxe";
    
            Log.init(true, Log.ERROR, true);
            
            try {
                BufferedWriter stream = new BufferedWriter(new FileWriter(settingsDirectory+fileSep+"jsXe.log"));
                
                stream.write("Log file created on " + new Date());
                stream.write(System.getProperty("line.separator"));
                
                Log.setLogWriter(stream);
            } catch (IOException ioe) {
                Log.log(Log.ERROR, jsXe.class, ioe);
            }
            //}}}
            
            //{{{ get and load the configuration files
            initDefaultProps();
            
            File _settingsDirectory = new File(settingsDirectory);
            if(!_settingsDirectory.exists())
                _settingsDirectory.mkdirs();
            String pluginsDirectory = settingsDirectory+"/jars";
            File _pluginsDirectory = new File(pluginsDirectory);
            if(!_pluginsDirectory.exists())
                _pluginsDirectory.mkdirs();
            
            File properties = new File(settingsDirectory,"properties");
            try {
                FileInputStream filestream = new FileInputStream(properties);
                props.load(filestream);
            } catch (FileNotFoundException fnfe) {
                
                //Don't do anything right now
                
            } catch (IOException ioe) {
                System.err.println(getAppTitle() + ": I/O ERROR: Could not open settings file");
                System.err.println(getAppTitle() + ": I/O ERROR: "+ioe.toString());
            }
            
            //Load the recent files list
            File recentFiles = new File(settingsDirectory, "recent.xml");
            m_bufferHistory = new BufferHistory();
            try {
                m_bufferHistory.load(recentFiles);
            } catch (IOException ioe) {
                System.err.println(getAppTitle() + ": I/O ERROR: Could not open recent files list");
                System.err.println(getAppTitle() + ": I/O ERROR: "+ioe.toString());
            } catch (SAXException saxe) {
                System.err.println(getAppTitle() + ": I/O ERROR: recent.xml not formatted properly");
                System.err.println(getAppTitle() + ": I/O ERROR: "+saxe.toString());
            } catch (ParserConfigurationException pce) {
                System.err.println(getAppTitle() + ": I/O ERROR: Could not parse recent.xml");
                System.err.println(getAppTitle() + ": I/O ERROR: "+pce.toString());
            }
            //}}}
            
            //{{{ parse command line arguments
            String viewname = null;
            ArrayList files = new ArrayList();
            for (int i=0; i<args.length; i++) {
                if (args[i].equals("--help") || args[i].equals("-h")) {
                    printUsage();
                    System.exit(0);
                }
                if (args[i].equals("--version") || args[i].equals("-V")) {
                    System.out.println(getVersion());
                    System.exit(0);
                }
               // if (args[i].startsWith("--view") || args[i].startsWith("-v")) {
               //     if (i+1<args.length) {
               //         viewname = args[++i];
               //     } else {
               //         System.out.println(getAppTitle()+": No view name specified.");
               //         System.exit(1);
               //     }
               // } else {
                    files.add(args[i]);
               // }
            }
            //}}}
            
            //{{{ load plugins
            
            m_pluginLoader = new JARClassLoader();
            ArrayList pluginMessages = m_pluginLoader.addDirectory(pluginsDirectory);
            
            String jsXeHome = System.getProperty("jsxe.home");
            if(jsXeHome == null) {
                String classpath = System.getProperty("java.class.path");
                int index = classpath.toLowerCase().indexOf("jsxe.jar");
                int start = classpath.lastIndexOf(File.pathSeparator,index) + 1;
                // if started with java -jar jsxe.jar
                if(classpath.equalsIgnoreCase("jsxe.jar")) {
                    jsXeHome = System.getProperty("user.dir");
                } else {
                    if(index > start) {
                        jsXeHome = classpath.substring(start, index - 1);
                    } else {
                        // use user.dir as last resort
                        jsXeHome = System.getProperty("user.dir");
                    }
                }
            }
            //add the jsXe home to the plugins directory
            pluginMessages.addAll(m_pluginLoader.addDirectory(jsXeHome+"/jars"));
            //}}}
            
            //{{{ start plugins
            
            Log.log(Log.NOTICE, jsXe.class, "Starting plugins");
            pluginMessages.addAll(m_pluginLoader.startPlugins());
            Vector pluginErrors = new Vector();
            if (pluginMessages.size() != 0) {
                for (int i=0; i<pluginMessages.size(); i++) {
                    Object error = pluginMessages.get(i);
                    if ((error instanceof IOException) || (error instanceof PluginDependencyException)) {
                        Log.log(Log.ERROR, jsXe.class, ((Exception)error).getMessage());
                        pluginErrors.add(((Exception)error).getMessage());
                    } else {
                        if (error instanceof PluginLoadException) {
                            Log.log(Log.WARNING, jsXe.class, ((PluginLoadException)error).getMessage());
                        } else {
                            Log.log(Log.WARNING, jsXe.class, error.toString());
                        }
                    }
                }
            }
            
            Iterator pluginItr = m_pluginLoader.getAllPlugins().iterator();
            while (pluginItr.hasNext()) {
                
                //load properties into jsXe's properties
                ActionPlugin plugin = (ActionPlugin)pluginItr.next();
                Properties props = plugin.getProperties();
                Enumeration names = props.propertyNames();
                while (names.hasMoreElements()) {
                    String name = names.nextElement().toString();
                    setProperty(name, props.getProperty(name));
                }
                
                addActionSet(plugin.getActionSet());
            }
            
            //}}}
            
            //{{{ create the TabbedView
            
            TabbedView tabbedview = null;
            DocumentBuffer defaultBuffer = null;
            try {
                defaultBuffer = new DocumentBuffer(new StringReader(getDefaultDocument()));
                m_buffers.add(defaultBuffer);
                if (viewname == null) {
                    tabbedview = new TabbedView(defaultBuffer);
                } else {
                    try {
                        tabbedview = new TabbedView(defaultBuffer, viewname);
                    } catch (UnrecognizedPluginException e) {
                        Log.log(Log.ERROR, jsXe.class, e);
                        System.exit(1);
                    }
                }
                
            } catch (IOException ioe) {
                Log.log(Log.ERROR, jsXe.class, ioe);
                JOptionPane.showMessageDialog(null, ioe.getMessage()+".", "I/O Error", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
            //}}}
            
            //{{{ Parse files to open on the command line
            if (files.size() > 0) {
                if (openXMLDocuments(tabbedview, (String[])files.toArray(new String[] {}))) {
                    try {
                        closeDocumentBuffer(tabbedview, defaultBuffer);
                    } catch (IOException ioe) {
                        //it can't be saved. it's not dirty.
                    }
                }
            }
            //}}}
            
            tabbedview.setVisible(true);
            
            //Show plugin error dialog
            if (pluginErrors.size() > 0)
                new ErrorListDialog(tabbedview, "Plugin Error", "The following plugins could not be loaded:", new Vector(pluginErrors), true);
        } catch (Throwable e) {
            exiterror(null, e, 1);
        }
    }//}}}
    
    //{{{ getBuild()
    /**
     * Gets the internal build version for jsXe. An example is 00.03.15.00
     * @return a string of the form Major.Minor.Beta.Build
     * @since jsXe 0.3pre15
     */
    public static String getBuild() {
        // Major.Minor.Beta.Build
        return buildProps.getProperty("major.version")+"."+
               buildProps.getProperty("minor.version")+"."+
               buildProps.getProperty("beta.version")+"."+
               buildProps.getProperty("build.version");
    }//}}}
    
    //{{{ getVersion()
    /**
     * Gets the formatted, human readable version of jsXe.
     * @return The current version of jsXe.
     */
    public static String getVersion() {
        return MiscUtilities.buildToVersion(getBuild());
    }//}}}
    
    //{{{ getIcon()
    /**
     * Gets jsXe's icon that is displayed in the about menu,
     * taskbar and upper left hand corner (where appropriate)
     * @return jsXe's icon
     */
    public static ImageIcon getIcon() {
        return jsXeIcon;
    }//}}}
    
    //{{{ getAppTitle()
    /**
     * Gets the title of the jsXe application. Most likely "jsXe"
     * @return The title of the jsXe application.
     */
    public static String getAppTitle() {
        return buildProps.getProperty("application.name");
    }//}}}
    
    //{{{ showOpenFileDialog()
    /**
     * Shows an open file dialog for jsXe. When a file is selected jsXe attempts
     * to open it.
     * @param view The view that is to be the parent of the file dialog
     * @return true if the file is selected and opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean showOpenFileDialog(TabbedView view) throws IOException {
            DocumentBuffer buffer = view.getDocumentBuffer();
            File docFile = buffer.getFile();
            // if current file is null, defaults to home directory
            JFileChooser loadDialog = new jsxeFileDialog(docFile);
            loadDialog.setMultiSelectionEnabled(true);
            
            int returnVal = loadDialog.showOpenDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                boolean success = false;
                File[] files = loadDialog.getSelectedFiles();
                for (int i = 0; i < files.length; i++) {
                    //success becomes true if at least one document is opened
                    //successfully.
                    if (files[i] != null) {
                        try {
                            success = openXMLDocument(view, files[i]) || success;
                        } catch (IOException ioe) {
                            //I/O error doesn't change value of success
                            Log.log(Log.WARNING, jsXe.class, ioe);
                            JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                return success;
            }
            return false;
    }//}}}
    
    //{{{ openXMLDocument()
    /**
     * Attempts to open an XML document in jsXe from a file on disk. If the file
     * is already open then the view's focus is set to that document.
     * @param view The view to open the document in.
     * @param file The file to open.
     * @return true if the file is opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean openXMLDocument(TabbedView view, File file) throws IOException {
        /*
        Check if it's in the recent file history
        if so then use those properties
        */
        BufferHistory.BufferHistoryEntry entry = m_bufferHistory.getEntry(file.getPath());
        if (entry != null) {
            return openXMLDocument(view, file, entry.getProperties(), entry.getViewName());
        } else {
            return openXMLDocument(view, file, new Properties(), null);
        }
    }//}}}
    
    //{{{ openXMLDocument()
    /**
     * Attempts to open an XML document in jsXe from a file on disk. If the file
     * is already open then the view's focus is set to that document. The
     * properties and view name given are ignored if the document is already
     * open. If the view name is null then the document is opened in the default
     * DocumentView.
     *
     * @param view The view to open the document in.
     * @param file The file to open.
     * @param properties the properties to set to the new document
     * @param viewName the name of the view to open this document in
     * @return true if the file is opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean openXMLDocument(TabbedView view, File file, Properties properties, String viewName) throws IOException {
        if (file == null)
            return false;
        
        DocumentBuffer buffer = getOpenBuffer(file);
        if (buffer != null) {
            /*
            ignore properties since the file is already open and the user may have
            set propeties him/herself. We don't want to change what the user
            has set.
            */
            view.setDocumentBuffer(buffer);
            return true;
        } else {
            try {
                buffer = new DocumentBuffer(file, properties);
                m_buffers.add(buffer);
                if (viewName != null) {
                    try {
                        view.addDocumentBuffer(buffer, viewName);
                    } catch (IOException ioe) {
                        /*
                        we can't open in the view we want try to open in
                        all views
                        */
                        view.addDocumentBuffer(buffer);
                    } catch (UnrecognizedPluginException e) {
                        /*
                        we can't open in the view we want try to open in
                        all views
                        */
                        view.addDocumentBuffer(buffer);
                    }
                } else {
                    view.addDocumentBuffer(buffer);
                }
                /*
                if there was only one untitled, clean buffer open then go
                ahead and close it so it doesn't clutter up the user's
                workspace.
                */
                DocumentBuffer[] buffers = getDocumentBuffers();
                if (buffers.length == 2 && buffers[0].isUntitled() && !buffers[0].getStatus(DocumentBuffer.DIRTY)) {
                    closeDocumentBuffer(view, buffers[0]);
                }
                return true;
            } catch (IOException ioe) {
                m_buffers.remove(buffer);
                throw ioe;
            }
        }
        
    }//}}}
    
    //{{{ openXMLDocument()
    /**
     * Attempts to open an XML document in the form of a String object as an
     * untitled document.
     * @param view The view to open the document in.
     * @param doc The String document to open.
     * @return true if the file is opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean openXMLDocument(TabbedView view, String doc) throws IOException {
        return openXMLDocument(view, new StringReader(doc));
    }//}}}
    
    //{{{ openXMLDocument()
    /**
     * Attempts to open an XML document in the form of a Reader object as an
     * untitled document..
     * @param view The view to open the document in.
     * @param reader The Reader document to open.
     * @return true if the file is opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean openXMLDocument(TabbedView view, Reader reader) throws IOException {
        
        DocumentBuffer buffer = new DocumentBuffer(reader);
        try {
            m_buffers.add(buffer);
            view.addDocumentBuffer(buffer);
            return true;
        } catch (IOException ioe) {
            //recover by removing the document
            m_buffers.remove(buffer);
            throw ioe;
        }
        
    }//}}}
    
    //{{{ getOpenBuffer()
    /**
     * Gets the DocumentBuffer for this file if the file is open already. Returns
     * null if the file is not open.
     * @param file The file that is open in jsXe
     * @return the DocumentBuffer for the given file or null if the file not open.
     */
    public static DocumentBuffer getOpenBuffer(File file) {
        
        boolean caseInsensitiveFilesystem = (File.separatorChar == '\\'
			|| File.separatorChar == ':' /* Windows or MacOS */);
        
        for(int i=0; i < m_buffers.size();i++) {
            
            try {
                DocumentBuffer buffer = (DocumentBuffer)m_buffers.get(i);
                if (buffer.equalsOnDisk(file)) {
                    return buffer;
                }
            } catch (IOException ioe) {
                exiterror(null, ioe, 1);
            }
            
        }
        
        return null;
    }//}}}
    
    //{{{ closeDocumentBuffer()
    /**
     * Closes an open DocumentBuffer. If dirty then the user will be prompted to save.
     * @param view The view that contains the buffer.
     * @param buffer The buffer to close.
     * @return true if the buffer was closed successfully. May return false if
     *         user hits cancel when asked to save changes.
     * @throws IOException if the user chooses to save and the file cannot be saved
     *                     because of an I/O error.
     */
    public static boolean closeDocumentBuffer(TabbedView view, DocumentBuffer buffer) throws IOException {
        if (m_buffers.contains(buffer)) {
            
            if (buffer.close(view)) {
                m_bufferHistory.setEntry(buffer, getPluginLoader().getPluginProperty(view.getDocumentView().getViewPlugin(), JARClassLoader.PLUGIN_NAME));
                view.removeDocumentBuffer(buffer);
                m_buffers.remove(buffer);
                
                if (view.getBufferCount() == 0) {
                    if (!m_exiting) {
                        try {
                            openXMLDocument(view, getDefaultDocument());
                        } catch (IOException ioe) {
                            exiterror(view, "Could not open default document.", 1);
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }//}}} 
    
    //{{{ getBufferHistory()
    
    public static BufferHistory getBufferHistory() {
        return m_bufferHistory;
    }//}}}
    
    //{{{ getDefaultDocument()
    /**
     * Gets the default XML document in jsXe. This is necessary 
     * as XML documents cannot be blank files.
     * @return jsXe's default XML document.
     */
    public static String getDefaultDocument() {
        return DefaultDocument;
    }//}}}
    
    //{{{ getDocumentBuffers()
    /**
     * Gets an array of the open Buffers.
     * @return An array of DocumentBuffers that jsXe currently has open.
     */
    public static DocumentBuffer[] getDocumentBuffers() {
        DocumentBuffer[] buffers = new DocumentBuffer[m_buffers.size()];
        for (int i=0; i < m_buffers.size(); i++) {
            buffers[i] = (DocumentBuffer)m_buffers.get(i);
        }
        return buffers;
    }//}}}
    
   // //{{{ getBufferForDocument()
   // /**
   //  * Gets the DocumentBuffer for the document.
   //  * @param doc the XMLDocument
   //  * @return the DocumentBuffer for the XMLDocument or null if there is none registered
   //  */
   // public static DocumentBuffer getBufferForDocument(XMLDocument doc) {
   //     Iterator bufferItr = m_buffers.iterator();
   //     while (bufferItr.hasNext()) {
   //         DocumentBuffer buf = (DocumentBuffer)bufferItr.next();
   //         if (buf.getXMLDocument() == doc) {
   //             return buf;
   //         }
   //     }
   //     return null;
   // }//}}}
    
    //{{{ exit()
    /**
     * Called when exiting jsXe.
     * @param view The view from which the exit was called.
     */
    public static void exit(TabbedView view) {
        
        m_exiting = true;
        
        try {//saves properties
            //exit only if the view really wants to.
            if (view.close()) {
                
                String homeDir = System.getProperty("user.home");
                String fileSep = System.getProperty("file.separator");
                String settingsDirectory = homeDir+fileSep+".jsxe";
                
                try {
                    File properties = new File(settingsDirectory,"properties");
                    FileOutputStream filestream = new FileOutputStream(properties);
                    props.store(filestream, "Autogenerated jsXe properties"+System.getProperty("line.separator")+"#This file is not really meant to be edited.");
                } catch (IOException ioe) {
                    exiterror(view, "Could not save jsXe properites.\n"+ioe.toString(), 1);
                } catch (ClassCastException cce) {
                    exiterror(view, "Could not save jsXe properties.\n"+cce.toString(), 1);
                }
                
                try {
                    File recentFiles = new File(settingsDirectory, "recent.xml");
                    m_bufferHistory.save(recentFiles);
                } catch (IOException ioe) {
                    exiterror(view, "Could not save jsXe recent files list.\n"+ioe.toString(), 1);
                }
                
                //stop logging.
                Log.closeStream();
                
                System.exit(0);
            } else {
                m_exiting = false;
            }
        } catch (IOException ioe) {
            //failed save of a dirty buffer
            Log.log(Log.ERROR, jsXe.class, ioe);
            JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
            m_exiting = false;
        }
    }//}}}
    
    //{{{ exiterror()
    /**
     * Called when crashing jsXe. jsXe prints an error message and
     * exits with the error code specifed.
     * @param view The view from which the exit was called.
     * @param error The error. Either a string or Exception.
     * @param errorcode The errorcode to exit with.
     */
    public static void exiterror(TabbedView view, Object error, int errorcode) {
        String errorhdr = "jsXe has encountered a fatal error and is unable to continue.\n";
        errorhdr        +="This is most likely a bug and should be reported to the jsXe\n";
        errorhdr        +="developers. Please fill out a full bug report at\n";
        errorhdr        +="http://www.sourceforge.net/projects/jsxe/\n\n";
        
        Log.log(Log.ERROR, jsXe.class, errorhdr);
        Log.log(Log.ERROR, jsXe.class, error);
        
        if (view != null) {
            JOptionPane.showMessageDialog(view, errorhdr + error, "Fatal Error", JOptionPane.WARNING_MESSAGE);
        }
        
        //stop logging
        Log.closeStream();
        
        System.exit(errorcode);
    }//}}}
    
    //{{{ setProperty()
    /**
     * Sets a global property to jsXe.
     * @param key The key name for the property.
     * @param value The value to associate with the key.
     * @return The previous value for the key, or null if there was none.
     */
    public static Object setProperty(String key, String value) {
        return props.setProperty(key, value);
    }//}}}
    
    //{{{ getDefaultProperty()
    /**
     * Gets a default global property. Returns null if there is no default
     * property for the given key.
     * 
     */
    public static final String getDefaultProperty(String key) {
        return defaultProps.getProperty(key);
    }
    
    //}}}
    
    //{{{ getProperty()
    /**
     * Gets a jsXe global property. This returns a user defined value for the
     * property, the default value of the property if no user defined value is
     * found or null if neither exist.
     * @param key The key of the property to get.
     * @return The value associated with the key or null if the key is not found.
     */
    public static final String getProperty(String key) {
        return getProperty(key, null);
    }//}}}
    
    //{{{ getProperty()
    /**
     * Gets a jsXe global property. This returns a user defined value for the
     * property, the default value of the property if no user defined value is
     * found or the default value given if neither exist.
     * @param key The key of the property to get.
     * @param defaultValue The default value to return when the key is not found.
     * @return The value associated with the key or the default value if the key is not found.
     */
    public static final String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultProps.getProperty(key, defaultValue));
	} //}}}
    
    //{{{ getIntegerProperty()
    /**
	 * Returns the value of an integer property.
	 * @param name The property
	 * @param def The default value
	 * @since jsXe 0.2 pre24
	 */
    public static final int getIntegerProperty(String key, int defaultValue) {
        int intValue = defaultValue;
        String value = getProperty(key);
		if (value == null) {
			return defaultValue;
        } else {
            try {
				return Integer.parseInt(value.trim());
            } catch(NumberFormatException nf) {
                return defaultValue;
            }
        }
    }//}}}
    
    //{{{ setIntegerProperty() method
	/**
	 * Sets the value of an integer property.
	 * @param name The property
	 * @param value The value
	 * @since jsXe 0.2 pre24
	 */
	public static final void setIntegerProperty(String name, int value)
	{
		setProperty(name, String.valueOf(value));
	} //}}}
    
    //{{{ addActionSet()
    
    public static void addActionSet(ActionSet set) {
        m_actionSets.add(set);
    }//}}}
    
    //{{{ getAction()
    
    /**
     * Gets the action set with the given name
     */
    public static Action getAction(String name) {
        for (int i = 0; i < m_actionSets.size(); i++) {
			Action action = ((ActionSet)m_actionSets.get(i)).getAction(name);
			if (action != null) {
				return action;
            }
		}
		return null;
    }//}}}
    
    //{{{ getActionSets()
    /**
     * Gets all action sets that have been registered with jsXe
     * @return an ArrayList of ActionSet objects
     */
    public ArrayList getActionSets() {
        return m_actionSets;
    }//}}}
    
    //{{{ getOptionsPanel()
    /**
     * Gets the options panel for the jsXe application.
     * @return The OptionsPanel with the options for jsXe.
     */
    public static final OptionsPanel getOptionsPanel() {
        jsXeOptions = new jsXeOptionsPanel();
        return jsXeOptions;
    }//}}}
    
    //{{{ getPluginLoader()
    /**
     * Gets the loader 
     */
    public static JARClassLoader getPluginLoader() {
        return m_pluginLoader;
    }//}}}
    
    //{{{ isExiting()
    /**
     * Indicates whether jsXe is exiting i.e. in the exit method.
     * @return true if jsXe is exiting.
     */
    public static final boolean isExiting() {
        return m_exiting;
    }//}}}
    
    // Private static members {{{

    //{{{ openXMLDocuments()
    /**
     * Open the XML documents in the command line arguments.
     */
    private static boolean openXMLDocuments(TabbedView view, String args[]) {
    
        boolean success = false;
        for (int i = 0; i < args.length; i++) {
            //success becomes true if at least one document is opened
            //successfully.
            if (args[i] != null) {
                try {
                    success = openXMLDocument(view, new File(args[i])) || success;
                } catch (IOException ioe) {
                    //I/O error doesn't change value of success
                    Log.log(Log.WARNING, jsXe.class, ioe);
                    JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        return success;
    
    }//}}}
    
    //{{{ initDefaultProps()
    /**
     * Initialize the Default properties.
     */
    private static void initDefaultProps() {
        
        //{{{ Load jsXe default properties
        InputStream inputstream = jsXe.class.getResourceAsStream("/net/sourceforge/jsxe/properties");
        try {
            defaultProps.load(inputstream);
        } catch (IOException ioe) {
            Log.log(Log.ERROR, jsXe.class, "**** Could not open default settings file ****");
            Log.log(Log.ERROR, jsXe.class, "**** jsXe was probably not built correctly ****");
            exiterror(null, ioe, 1);
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = (int)(screenSize.getWidth() / 2);
        int windowHeight = (int)(3 * screenSize.getHeight() / 4);        
        int x = (int)(screenSize.getWidth() / 4);
        int y = (int)(screenSize.getHeight() / 8);
        
        defaultProps.setProperty("tabbedview.height",Integer.toString(windowHeight));
        defaultProps.setProperty("tabbedview.width",Integer.toString(windowWidth));
        
        defaultProps.setProperty("tabbedview.x",Integer.toString(x));
        defaultProps.setProperty("tabbedview.y",Integer.toString(y));
        //}}}
        
        //{{{ Load build properties
        inputstream = jsXe.class.getResourceAsStream("/net/sourceforge/jsxe/build.properties");
        try {
            buildProps.load(inputstream);
        } catch (IOException ioe) {
            Log.log(Log.ERROR, jsXe.class, "**** Could not open build properties file ****");
            Log.log(Log.ERROR, jsXe.class, "**** jsXe was probably not built correctly ****");
            exiterror(null, ioe, 1);
        }
        //}}}
        
        //{{{ Load default properties of installed views
       // Enumeration installedViews = DocumentViewFactory.getAvailableViewTypes();
       // while (installedViews.hasMoreElements()) {
       //     String viewname = (String)installedViews.nextElement();
       //     InputStream viewinputstream = jsXe.class.getResourceAsStream("/net/sourceforge/jsxe/gui/view/"+viewname+".props");
       //     try {
       //         Properties defViewProps = new Properties();
       //         defViewProps.load(viewinputstream);
       //         Enumeration propsList = defViewProps.propertyNames();
       //         while (propsList.hasMoreElements()) {
       //             String key = (String)propsList.nextElement();
       //             defaultProps.setProperty(key, defViewProps.getProperty(key));
       //         }
       //     } catch (IOException ioe) {
       //         System.err.println(getAppTitle() + ": Internal ERROR: Could not open default settings file");
       //         System.err.println(getAppTitle() + ": Internal ERROR: "+ioe.toString());
       //         System.err.println(getAppTitle() + ": Internal ERROR: You probobly didn't build jsXe correctly.");
       //     }
       // }
        //}}}
        
    }//}}}
    
    //{{{ jsXeOptionsPanel class
    
    private static class jsXeOptionsPanel extends OptionsPanel {
        
        //{{{ jsXeOptionsPanel constructor
        
        public jsXeOptionsPanel() {
            
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            
            setLayout(layout);
            
            int gridY = 0;
            
            int maxRecentFiles = 20;
            try {
                maxRecentFiles = Integer.parseInt(jsXe.getProperty("max.recent.files"));
            } catch (NumberFormatException nfe) {
                try {
                    maxRecentFiles = Integer.parseInt(jsXe.getDefaultProperty("max.recent.files"));
                } catch (NumberFormatException nfe2) {}
            }
            
            JLabel maxRecentFilesLabel = new JLabel("Recent files to remember:");
            
            Vector sizes = new Vector(3);
            sizes.add("10");
            sizes.add("20");
            sizes.add("30");
            maxRecentFilesComboBox = new JComboBox(sizes);
            maxRecentFilesComboBox.setEditable(true);
            maxRecentFilesComboBox.setSelectedItem(Integer.toString(maxRecentFiles));
            
            constraints.gridy      = gridY;
            constraints.gridx      = 0;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(maxRecentFilesLabel, constraints);
            add(maxRecentFilesLabel);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 1;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(maxRecentFilesComboBox, constraints);
            add(maxRecentFilesComboBox);
        }//}}}
        
        //{{{ getName()
        
        public String getName() {
            return "jsxeoptions";
        }//}}}
        
        //{{{ saveOptions()
        public void saveOptions() {
            try {
                //don't need to set dirty, no change to text
                jsXe.setProperty("max.recent.files", (new Integer(maxRecentFilesComboBox.getSelectedItem().toString())).toString());
            } catch (NumberFormatException nfe) {
                //Bad input, don't save.
            }
        }//}}}
        
        //{{{ getTitle()
        
        public String getTitle() {
            return "jsXe Global Options";
        }//}}}
        
        //{{{ toString()
        
        public String toString() {
            return getTitle();
        }//}}}
        
        //{{{ Private Members
        private JComboBox maxRecentFilesComboBox;
        //}}}
        
    }//}}}
    
    //{{{ printUsage()
    
    private static void printUsage() {
        System.out.println("jsXe "+getVersion());
        System.out.println("The Java Simple XML Editor");
        System.out.println();
        System.out.println("Copyright 2004 Ian Lewis");
        System.out.println("This is free software; see the source for copying conditions. There is NO");
        System.out.println("warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
        System.out.println();
        System.out.println("Usage: jsXe [<options>] [<files>]");
       // System.out.println("  -v, --view <viewname>     open the files using the view specified");
       // System.out.println("                            valid views are:");
       // System.out.println("                                            tree");
       // System.out.println("                                            source");
        System.out.println("  -h, --help                display this help and exit");
        System.out.println("  -V, --version             print version information and exit");
        System.out.println();
        System.out.println("Report bugs to <ian_lewis@users.sourceforge.net>");
    }//}}}
    
    private static ArrayList m_buffers = new ArrayList();
    private static final String DefaultDocument = "<?xml version='1.0' encoding='UTF-8'?>\n<default_element>default_node</default_element>";
    private static final ImageIcon jsXeIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/jsxe.jpg"), "jsXe");
    
    private static final Properties buildProps = new Properties();
    private static boolean m_exiting=false;
    private static final Properties defaultProps = new Properties();
    private static Properties props = new Properties();
    private static BufferHistory m_bufferHistory;
    private static ArrayList m_actionSets = new ArrayList();
    private static JARClassLoader m_pluginLoader;
    private static TabbedView m_tabbedview;
    
    private static OptionsPanel jsXeOptions;
    //}}}
    
}

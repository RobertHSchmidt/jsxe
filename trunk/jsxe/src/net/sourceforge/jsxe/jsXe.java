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
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.gui.view.DocumentView;
import net.sourceforge.jsxe.gui.view.DocumentViewFactory;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.XMLDocumentFactory;
import net.sourceforge.jsxe.dom.XMLDocumentListener;
import net.sourceforge.jsxe.dom.UnrecognizedDocTypeException;
//}}}

//{{{ Swing classes
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
//}}}

//{{{ AWT Components
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
//}}}

//{{{ DOM Classes
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Java Base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
//}}}

//}}}

/**
 * The main class of the java simple XML editor (jsXe)
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class jsXe {
    
    /**
     * The main method of jsXe
     * @param args The command line arguments
     */
    public static void main(String args[]) {//{{{
        
        //{{{ Check the java version
        String javaVersion = System.getProperty("java.version");
		if(javaVersion.compareTo("1.3") < 0)
		{
			System.err.println(getAppTitle() + ": ERROR: " + getAppTitle() + getVersion());
            System.err.println(getAppTitle() + ": ERROR: You are running Java version " + javaVersion + ".");
			System.err.println(getAppTitle() + ": ERROR:" + getAppTitle()+" requires Java 1.3 or later.");
			System.exit(1);
		}//}}}
        
        //{{{ get and load the configuration files
        initDefaultProps();
        props = new Properties(defaultProps);
        
        String settingsDirectory = System.getProperty("user.home")+System.getProperty("file.separator")+".jsxe";
        File _settingsDirectory = new File(settingsDirectory);
        if(!_settingsDirectory.exists())
		    _settingsDirectory.mkdirs();
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
        //}}}
        
        TabbedView tabbedview = new TabbedView();
        
        //{{{ Parse command line arguments
        
        if (args.length >= 1) {
            
            if (!openXMLDocuments(tabbedview, args)) {
                try {
                    if (!openXMLDocument(tabbedview, getDefaultDocument())) {
                        exiterror(tabbedview, "Could not open default document.",1);
                    }
                } catch (IOException ioe) {
                    exiterror(tabbedview, "Could not open default document: " + ioe.toString(),1);
                }
            }
        } else {
            try {
                if (!openXMLDocument(tabbedview, getDefaultDocument())) {
                    exiterror(tabbedview, "Could not open default document.",1);
                }
            } catch (IOException ioe) {
                exiterror(tabbedview, "Could not open default document: " + ioe.toString(),1);
            }
        }
        //}}}
        
        tabbedview.show();
    }//}}}
    
    /**
     * Gets the current version of jsXe.
     * @return The current version of jsXe.
     */
    public static String getVersion() {//{{{
        // Major.Minor.Build
        String version = buildProps.getProperty("major.version") + "." +
                         buildProps.getProperty("minor.version") + "." +
                         buildProps.getProperty("build.version");
        return version;
    }//}}}
    
    /**
     * Shows an open file dialog for jsXe. When a file is selected jsXe attempts
     * to open it.
     * @param view The view that is to be the parent of the file dialog
     * @return true if the file is selected and opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean showOpenFileDialog(TabbedView view) throws IOException {//{{{
            // if current file is null, defaults to home directory
            DocumentView docView = view.getDocumentView();
            DocumentBuffer buffer = docView.getDocumentBuffer();
            File docFile = buffer.getFile();
            JFileChooser loadDialog = new JFileChooser(docFile);
            loadDialog.setMultiSelectionEnabled(true);
            //Add a filter to display only XML files
            ArrayList extentionList = new ArrayList();
            extentionList.add(new String("xml"));
            CustomFileFilter firstFilter = new CustomFileFilter(extentionList, "XML Documents");
            loadDialog.addChoosableFileFilter(firstFilter);
            //Add a filter to display only XSL files
            extentionList = new ArrayList();
            extentionList.add(new String("xsl"));
            loadDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL Stylesheets"));
            //Add a filter to display only XSL:FO files
            extentionList = new ArrayList();
            extentionList.add(new String("fo"));
            loadDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL:FO Documents"));
            //Add a filter to display all formats
            extentionList = new ArrayList();
            extentionList.add(new String("xml"));
            extentionList.add(new String("xsl"));
            extentionList.add(new String("fo"));
            loadDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "All XML Documents"));
            
            //The "All Files" file filter is added to the dialog
            //by default. Put it at the end of the list.
            FileFilter all = loadDialog.getAcceptAllFileFilter();
            loadDialog.removeChoosableFileFilter(all);
            loadDialog.addChoosableFileFilter(all);
            loadDialog.setFileFilter(firstFilter);
            
            int returnVal = loadDialog.showOpenDialog(view);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
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
                            JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                return success;
            }
            return false;
    }//}}}
    
    /**
     * Attempts to open an XML document in jsXe from a file on disk.
     * @param view The view to open the document in.
     * @param file The file to open.
     * @return true if the file is opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean openXMLDocument(TabbedView view, File file) throws IOException {//{{{
        if (file == null)
            return false;
        
        DocumentBuffer buffer = getOpenBuffer(file);
        if (buffer != null) {
            view.setDocumentBuffer(buffer);
            return true;
        } else {
            try {
                buffer = new DocumentBuffer(file);
                m_buffers.add(buffer);
                view.addDocumentBuffer(buffer);
                return true;
            } catch (IOException ioe) {
                m_buffers.remove(buffer);
                throw ioe;
            }
        }
        
    }//}}}
    
    /**
     * Attempts to open an XML document in the form of a String object in jsXe.
     * @param view The view to open the document in.
     * @param doc The String document to open.
     * @return true if the file is opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean openXMLDocument(TabbedView view, String doc) throws IOException {//{{{
        return openXMLDocument(view, new StringReader(doc));
    }//}}}
    
    /**
     * Attempts to open an XML document in the form of a Reader object in jsXe.
     * @param view The view to open the document in.
     * @param reader The Reader document to open.
     * @return true if the file is opened successfully.
     * @throws IOException if the document does not validate or cannot be opened for some reason.
     */
    public static boolean openXMLDocument(TabbedView view, Reader reader) throws IOException {//{{{
        
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
    
    /**
     * Gets the DocumentBuffer for this file if the file is open already. Returns
     * null if the file is not open.
     * @param file The file that is open in jsXe
     * @return the DocumentBuffer for the given file or null if the file not open.
     */
    public static DocumentBuffer getOpenBuffer(File file) {//{{{
        
        boolean caseInsensitiveFilesystem = (File.separatorChar == '\\'
			|| File.separatorChar == ':' /* Windows or MacOS */);
        
        for(int i=0; i < m_buffers.size();i++) {
            
            try {
                DocumentBuffer buffer = (DocumentBuffer)m_buffers.get(i);
                if (buffer.equalsOnDisk(file)) {
                    return buffer;
                }
            } catch (IOException ioe) {
                exiterror(null, ioe.getMessage(), 1);
            }
            
        }
        
        return null;
    }//}}}
    
    /**
     * Closes an open DocumentBuffer.
     * @param view The view that contains the buffer.
     * @param buffer The buffer to close.
     * @return true if the buffer was closed successfully. May return false if
     *         user hits cancel when asked to save changes.
     */
    public static boolean closeDocumentBuffer(TabbedView view, DocumentBuffer buffer) {//{{{
        if (m_buffers.contains(buffer)) {
            
            if (buffer.isDirty()) {
            
                //If it's dirty ask if you want to save.
                String msg = buffer.getName()+" unsaved! Save it now?";
                String title = "Unsaved Changes";
                int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
                int messageType = JOptionPane.WARNING_MESSAGE;
                
                int returnVal = JOptionPane.showConfirmDialog(view,
                                    msg,
                                    title,
                                    optionType,
                                    messageType);
                
                if (returnVal == JOptionPane.YES_OPTION) {
                    //the args to the ActionEvent are arbitrary.
                    (new FileSaveAction(view)).actionPerformed(new ActionEvent(view, 101, "Save"));
                } else {
                    if (returnVal == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
            
            view.removeDocumentBuffer(buffer);
            m_buffers.remove(buffer);
            if (view.getBufferCount() == 0) {
                try {
                    openXMLDocument(view, getDefaultDocument());
                } catch (IOException ioe) {
                    exiterror(view, "Could not open default document.", 1);
                }
            }
            return true;
        } else {
            return false;
        }
    }//}}} 
    
    /**
     * Gets the default XML document in jsXe. This is necessary 
     * as XML documents cannot be blank files.
     * @return jsXe's default XML document.
     */
    public static String getDefaultDocument() {//{{{
        return DefaultDocument;
    }//}}}
    
    /**
     * Gets an array of the open Buffers.
     * @return An array of DocumentBuffers that jsXe currently has open.
     */
    public static DocumentBuffer[] getDocumentBuffers() {//{{{
        DocumentBuffer[] buffers = new DocumentBuffer[m_buffers.size()];
        for (int i=0; i < m_buffers.size(); i++) {
            buffers[i] = (DocumentBuffer)m_buffers.get(i);
        }
        return buffers;
    }//}}}
    
    /**
     * Gets jsXe's icon that is displayed in the about menu,
     * taskbar and upper left hand corner (where appropriate)
     * @return jsXe's icon
     */
    public static ImageIcon getIcon() {//{{{
        return jsXeIcon;
    }//}}}
    
    /**
     * Gets the title of the jsXe application. Most likely "jsXe"
     * @return The title of the jsXe application.
     */
    public static String getAppTitle() {//{{{
        return buildProps.getProperty("application.name");
    }//}}}
    
    /**
     * Called when exiting jsXe.
     * @param view The view from which the exit was called.
     */
    public static void exit(TabbedView view) {//{{{
        //nothing much here yet. Open documents should
        //be checked for dirty documents.
        
        m_exiting = true;
        
        //saves properties
        //exit only if the view really wants to.
        if (view.close()) {
            
            try {
                File properties = new File(System.getProperty("user.home")+System.getProperty("file.separator")+".jsxe","properties");
                FileOutputStream filestream = new FileOutputStream(properties);
                props.store(filestream, "Autogenerated jsXe properties"+System.getProperty("line.separator")+"#This file is not really meant to be edited.");
            } catch (IOException ioe) {
                exiterror(view, "Could not save jsXe settings.\n"+ioe.toString(), 1);
            } catch (ClassCastException cce) {
                exiterror(view, "Could not save jsXe settings.\n"+cce.toString(), 1);
            }
            System.exit(0);
        } else {
            m_exiting = false;
        }
    }//}}}
    
    /**
     * Called when crashing jsXe. jsXe prints an error message and
     * exits with the error code specifed.
     * @param view The view from which the exit was called.
     * @param errormsg The error message to display.
     * @param errorcode The errorcode to exit with.
     */
    public static void exiterror(TabbedView view, String errormsg, int errorcode) {//{{{
        if (view != null) {
            String errorhdr = "jsXe has encountered a fatal error and is unable to continue.\n";
            errorhdr        +="This is most likely a bug and should be reported to the jsXe\n";
            errorhdr        +="developers. Please fill out a full bug report at\n";
            errorhdr        +="http://www.sourceforge.net/projects/jsxe/\n\n";
            
            JOptionPane.showMessageDialog(view, errorhdr + errormsg, "Fatal Error", JOptionPane.WARNING_MESSAGE);
        }
        
        //print the error to the command line also.
        System.err.println(getAppTitle() + ": jsXe has encountered a fatal error and is unable to continue.");
        System.err.println(getAppTitle() + ": This is most likely a bug and should be reported to the jsXe");
        System.err.println(getAppTitle() + ": developers. Please fill out a full bug report at");
        System.err.println(getAppTitle() + ": http://www.sourceforge.net/projects/jsxe/");
        System.err.println("");
        System.err.println(getAppTitle() + ": "+errormsg);
        System.exit(errorcode);
    }//}}}
    
    /**
     * Sets a global property to jsXe.
     * @param key The key name for the property.
     * @param value The value to associate with the key.
     * @return The previous value for the key, or null if there was none.
     */
    public static Object setProperty(String key, String value) {//{{{
        return props.setProperty(key, value);
    }//}}}
    
    /**
     * Gets a jsXe global property. Returns null if the property is not found.
     * @param key The key of the property to get.
     * @return The value associated with the key or null if the key is not found.
     */
    public static final String getProperty(String key) {//{{{
        return props.getProperty(key);
    }//}}}
    
    /**
     * Gets a jsXe global property specifying a default value.
     * @param key The key of the property to get.
     * @param defaultValue The default value to return when the key is not found.
     * @return The value associated with the key or the default value if the key is not found.
     */
    public static final String getProperty(String key, String defaultValue) {//{{{
		return props.getProperty(key, defaultValue);
	} //}}}
    
    /**
     * Gets the options panel for the jsXe application.
     * @return The OptionsPanel with the options for jsXe.
     */
    public static final OptionsPanel getOptionsPanel() {//{{{
        jsXeOptions = new jsXeOptionsPanel();
        return jsXeOptions;
    }//}}}
    
    /**
     * Indicates whether jsXe is exiting i.e. in the exit method.
     * @return true if jsXe is exiting.
     */
    public static final boolean isExiting() {//{{{
        return m_exiting;
    }//}}}
    
    // Private static members {{{

    /**
     * Open the XML documents in the command line arguments.
     */
    private static boolean openXMLDocuments(TabbedView view, String args[]) {//{{{
    
        boolean success = false;
        for (int i = 0; i < args.length; i++) {
            //success becomes true if at least one document is opened
            //successfully.
            if (args[i] != null) {
                try {
                    success = openXMLDocument(view, new File(args[i])) || success;
                } catch (IOException ioe) {
                    //I/O error doesn't change value of success
                    JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        return success;
    
    }//}}}
    
    /**
     * Initialize the Default properties.
     */
    private static void initDefaultProps() {//{{{
        
        //{{{ Load jsXe default properties
        InputStream inputstream = jsXe.class.getResourceAsStream("/net/sourceforge/jsxe/properties");
        try {
            defaultProps.load(inputstream);
        } catch (IOException ioe) {
            System.err.println(getAppTitle() + ": Internal ERROR: Could not open default settings file");
            System.err.println(getAppTitle() + ": Internal ERROR: "+ioe.toString());
            System.err.println(getAppTitle() + ": Internal ERROR: You probobly didn't build jsXe correctly.");
            exiterror(null, ioe.getMessage(), 1);
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
            System.err.println(getAppTitle() + ": Internal ERROR: Could not open default settings file");
            System.err.println(getAppTitle() + ": Internal ERROR: "+ioe.toString());
            System.err.println(getAppTitle() + ": Internal ERROR: You probobly didn't build jsXe correctly.");
            exiterror(null, ioe.getMessage(), 1);
        }
        //}}}
        
        //{{{ Load default properties of installed views
        Enumeration installedViews = DocumentViewFactory.getAvailableViewTypes();
        while (installedViews.hasMoreElements()) {
            String viewname = (String)installedViews.nextElement();
            InputStream viewinputstream = jsXe.class.getResourceAsStream("/net/sourceforge/jsxe/gui/view/"+viewname+".props");
            try {
                Properties defViewProps = new Properties();
                defViewProps.load(viewinputstream);
                Enumeration propsList = defViewProps.propertyNames();
                while (propsList.hasMoreElements()) {
                    String key = (String)propsList.nextElement();
                    defaultProps.setProperty(key, defViewProps.getProperty(key));
                }
            } catch (IOException ioe) {
                System.err.println(getAppTitle() + ": Internal ERROR: Could not open default settings file");
                System.err.println(getAppTitle() + ": Internal ERROR: "+ioe.toString());
                System.err.println(getAppTitle() + ": Internal ERROR: You probobly didn't build jsXe correctly.");
            }
        }
        //}}}
        
    }//}}}
    
    private static class jsXeOptionsPanel extends OptionsPanel {//{{{
        
        public jsXeOptionsPanel() {//{{{
            super();
        }//}}}
        
        public void saveOptions() {//{{{
            
        }//}}}
        
        public String getTitle() {//{{{
            return "jsXe Global Options";
        }//}}}
        
    }//}}}
    
    private static ArrayList m_buffers = new ArrayList();
    private static final String DefaultDocument = "<?xml version='1.0' encoding='UTF-8'?>\n<default_element>default_node</default_element>";
    private static final ImageIcon jsXeIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/jsxe.jpg"), "jsXe");
    private static final Properties defaultProps = new Properties();
    private static final Properties buildProps = new Properties();
    private static boolean m_exiting=false;
    private static Properties props;
    
    private static OptionsPanel jsXeOptions;
    //}}}
    
}

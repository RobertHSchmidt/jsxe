/*
jsXe.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file written by ian Lewis (iml001@bridgewater.edu)
Copyright (C) 2002 ian Lewis

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
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.view.DocumentView;
import net.sourceforge.jsxe.gui.view.DocumentViewFactory;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.XMLDocumentFactory;
import net.sourceforge.jsxe.dom.UnrecognizedDocTypeException;
//}}}

//{{{ Swing Classes
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
//}}}

//{{{ AWT Components
import java.awt.Dimension;
import java.awt.Toolkit;
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
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
//}}}

//}}}

public class jsXe {
    
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
        
        //{{{ Create the main TabbedView
        int windowWidth = Integer.valueOf(getProperty("view.width")).intValue();
        int windowHeight = Integer.valueOf(getProperty("view.height")).intValue();
        int x = Integer.valueOf(getProperty("view.x")).intValue();
        int y = Integer.valueOf(getProperty("view.y")).intValue();
        
        TabbedView tabbedview = new TabbedView(windowWidth, windowHeight);
        tabbedview.setLocation(x,y);
        //}}}
        
        //{{{ Parse command line arguments
        if (args.length >= 1) {
            if (!openXMLDocuments(tabbedview, args)) {
                if (!openXMLDocument(tabbedview, DefaultDocument)) {
                    System.err.println(getAppTitle() + ": Internal ERROR: opening default document");
                    System.err.println(getAppTitle() + ": Internal ERROR: You probobly didn't build jsXe correctly.");
                    System.exit(1);
                }
            }
        } else {
            if (!openXMLDocument(tabbedview, DefaultDocument)) {
                System.err.println(getAppTitle() + ": Internal ERROR: opening default document");
                System.err.println(getAppTitle() + ": Internal ERROR: You probobly didn't build jsXe correctly.");
                System.exit(1);
            }
        }//}}}
        
        tabbedview.show();
    }//}}}
    
    public static String getVersion() {//{{{
        return MajorVersion + "." + MinorVersion + "." + BuildVersion + " " + BuildType;
    }//}}}
    
    public static boolean showOpenFileDialog(TabbedView view) {//{{{
            // if current file is null, defaults to home directory
            DocumentView blah = view.getDocumentView();
            XMLDocument blah2 = blah.getXMLDocument();
            File blah3 = blah2.getFile();
            JFileChooser loadDialog = new JFileChooser(blah3);
            //Add a filter to display only XML files
            Vector extentionList = new Vector();
            extentionList.add(new String("xml"));
            CustomFileFilter firstFilter = new CustomFileFilter(extentionList, "XML Documents");
            loadDialog.addChoosableFileFilter(firstFilter);
            //Add a filter to display only XSL files
            extentionList = new Vector();
            extentionList.add(new String("xsl"));
            loadDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL Stylesheets"));
            //Add a filter to display only XSL:FO files
            extentionList = new Vector();
            extentionList.add(new String("fo"));
            loadDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL:FO Documents"));
            //Add a filter to display all formats
            extentionList = new Vector();
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
                return openXMLDocument(view, loadDialog.getSelectedFile());
            }
            return true;
    }//}}}
    
    public static boolean openXMLDocument(TabbedView view, File file) {//{{{
        
        if (file == null)
            return false;
        
        boolean caseInsensitiveFilesystem = (File.separatorChar == '\\'
			|| File.separatorChar == ':' /* Windows or MacOS */);
        
        //Check if the file is already open, if so
        //change focus to that file
        for(int i=0; i < XMLDocuments.size();i++) {
            
            XMLDocument doc = (XMLDocument)XMLDocuments.get(i);
            File docfile = doc.getFile();
            if (docfile != null) {
                if (caseInsensitiveFilesystem) {
                    
                    try {
                        if (file.getCanonicalPath().equalsIgnoreCase(docfile.getCanonicalPath())) {
                            view.setDocument(doc);
                            return true;
                        }
                    } catch (IOException ioe) {
                        return false;
                    }
                    
                } else {
                    
                    try {
                        if (file.getCanonicalPath().equals(docfile.getCanonicalPath())) {
                            view.setDocument(doc);
                            return true;
                        }
                    } catch (IOException ioe) {
                        return false;
                    }
                }
            }
        }
        
        //At this point we know the file is not open
        //we need to open it.
        //right now unrecognized doc exceptions should not be thrown.
        XMLDocumentFactory factory = XMLDocumentFactory.newInstance();
        try {
            XMLDocument document = factory.newXMLDocument(file);
            
            if (document != null) {
                //for now do not open the file unless it validates.
                if (!document.validate(view)) {
                    return false;
                }
                XMLDocuments.add(document);
                view.addDocument(document);
                return true;
            } else {
                return false;
            }
        } catch (UnrecognizedDocTypeException udte) {}
        
        return false;
        
    }//}}}
    
    public static boolean openXMLDocument(TabbedView view, String doc) {//{{{
        return openXMLDocument(view, new StringReader(doc));
    }//}}}
    
    public static boolean openXMLDocument(TabbedView view, Reader reader) {//{{{
        //We are assuming the contents of the reader do not
        //exist on disk and therefore could not be opened already.
        //right now unrecognized doc exceptions should not be thrown.
        XMLDocumentFactory factory = XMLDocumentFactory.newInstance();
        try {
            XMLDocument document = factory.newXMLDocument(reader);
            if (document != null) {
                //for now do not open the file unless it validates.
                if (!document.validate(view)) {
                    return false;
                }
                
                XMLDocuments.add(document);
                view.addDocument(document);
                return true;
            } else {
                return false;
            }
        } catch (UnrecognizedDocTypeException udte) {}
        
        return false;
        
    }//}}}
    
    public static boolean openXMLDocuments(TabbedView view, String args[]) {//{{{
    
        boolean success = false;
        for (int i = 0; i < args.length; i++) {
            //success becomes true if at least one document is opened
            //successfully.
            if (args[i] != null) {
                success = success || openXMLDocument(view, new File(args[i]));
            }
        }
        return success;
    
    }//}}}
    
    public static boolean closeXMLDocument(TabbedView view, XMLDocument document) {//{{{
        view.removeDocument(document);
        XMLDocuments.remove(document);
        if (view.getDocumentCount() == 0)
            openXMLDocument(view, DefaultDocument);
        return true;
    }//}}}
    
    public static String getDefaultDocument() {//{{{
        return DefaultDocument;
    }//}}}
    
    public static XMLDocument[] getXMLDocuments() {//{{{
        XMLDocument[] documents = new XMLDocument[XMLDocuments.size()];
        for (int i=0; i < XMLDocuments.size(); i++) {
            documents[i] = (XMLDocument)XMLDocuments.get(i);
        }
        return documents;
    }//}}}
    
    public static ImageIcon getIcon() {//{{{
        return jsXeIcon;
    }//}}}
    
    public static String getAppTitle() {//{{{
        return AppTitle;
    }//}}}
    
    public static void exit(TabbedView view) {//{{{
        //nothing much here yet. Open documents should
        //be checked for dirty documents.
        try {
            File properties = new File(System.getProperty("user.home")+System.getProperty("file.separator")+".jsxe","properties");
            FileOutputStream filestream = new FileOutputStream(properties);
            props.store(filestream, "Autogenerated jsXe properties\n#This file is not really meant to be edited.");
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
        } catch (ClassCastException cce) {
            JOptionPane.showMessageDialog(view, "Could not save jsXe settings.\n"+cce.toString(), "Internal Error", JOptionPane.WARNING_MESSAGE);
        }
        System.exit(0);
    }//}}}
    
    public static Object setProperty(String key, String value) {//{{{
        return props.setProperty(key, value);
    }//}}}
    
    public static final String getProperty(String key) {//{{{
        return props.getProperty(key);
    }//}}}
    
    public static final String getProperty(String key, String defaultValue) {//{{{
		return props.getProperty(key, defaultValue);
	} //}}}
    
    // Private static members {{{
    
    private static void initDefaultProps() {//{{{
        
        //{{{ Load jsXe default properties
        InputStream inputstream = jsXe.class.getResourceAsStream("/net/sourceforge/jsxe/properties");
        try {
            defaultProps.load(inputstream);
        } catch (IOException ioe) {
            System.err.println(getAppTitle() + ": Internal ERROR: Could not open default settings file");
            System.err.println(getAppTitle() + ": Internal ERROR: "+ioe.toString());
            System.err.println(getAppTitle() + ": Internal ERROR: You probobly didn't build jsXe correctly.");
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = (int)(screenSize.getWidth() / 2);
        int windowHeight = (int)(3 * screenSize.getHeight() / 4);        
        int x = (int)(screenSize.getWidth() / 4);
        int y = (int)(screenSize.getHeight() / 8);
        
        defaultProps.setProperty("view.height",Integer.toString(windowHeight));
        defaultProps.setProperty("view.width",Integer.toString(windowWidth));
        
        defaultProps.setProperty("view.x",Integer.toString(x));
        defaultProps.setProperty("view.y",Integer.toString(y));
        //}}}
        
        //{{{ Load default properties of installed views
        Enumeration installedViews = DocumentViewFactory.getAvailableViewNames();
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
    
    private static final String MajorVersion = "00";
    private static final String MinorVersion = "01";
    private static final String BuildVersion = "01";
    private static final String BuildType    = "alpha";
    private static Vector XMLDocuments = new Vector();
    private static final String DefaultDocument = "<?xml version='1.0' encoding='UTF-8'?><default_element>default_node</default_element>";
    private static final ImageIcon jsXeIcon = new ImageIcon("net/sourceforge/jsxe/icons/jsxe.jpg", "jsXe");
    private static final String AppTitle = "jsXe";
    private static final Properties defaultProps = new Properties();
    private static Properties props;
    //}}}
    
}

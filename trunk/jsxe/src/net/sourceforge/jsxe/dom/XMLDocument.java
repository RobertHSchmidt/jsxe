/*
XMLDocument.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the abstract class that can be used to create different
types of XMLDocuments. This class represents a document that is open and
contains attributes associated with the document.

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.dom;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.CustomFileFilter;
import net.sourceforge.jsxe.gui.TabbedView;
//}}}

//{{{ Swing classes
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
//}}}

//{{{ AWT classes
import java.awt.Component;
//}}}

//{{{ DOM classes
import org.w3c.dom.Document;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Properties;
import java.util.Vector;
//}}}

//}}}

public abstract class XMLDocument {
    
    public XMLDocument(File file) {//{{{
        props.setProperty("format.output", "true");
        XMLFile = file;
        if (file!=null) {
            name = file.getName();
            try {
                XMLReader=new FileReader(file);
            } catch (FileNotFoundException fnfe) {
                System.out.println("File Not Found");
            }
        } else {
            name = getUntitledLabel();
        }
        
    }//}}}
    
    public XMLDocument(Reader reader) {//{{{
        props.setProperty("format.output", "true");
        name=getUntitledLabel();
        XMLReader=reader;
    }//}}}
    
    public XMLDocument(String string) {//{{{
        props.setProperty("format.output", "true");
        name=getUntitledLabel();
        XMLReader=new StringReader(string);
    }//}}}
    
    public abstract boolean validate(Component parent);
    
    public boolean isUntitled() {//{{{
        return (XMLFile == null);
    }//}}}
    
    public String getName() {//{{{
        return name;
    }//}}}
    
    public File getFile() {//{{{
        return XMLFile;
    }//}}}
    
    public Reader getReader() {//{{{
        return XMLReader;
    }//}}}
    
    public abstract Document getDocument();
    
    public Object setProperty(String key, String value) {//{{{
        return props.setProperty(key, value);
    }//}}}
    
    public String getProperty(String key) {//{{{
        return props.getProperty(key);
    }//}}}
    
    public boolean save(TabbedView view) {//{{{
       return save(view,XMLFile);
    }//}}}
    
    public boolean save(TabbedView view, File file) {//{{{
       // if (file == null) {
       //     return jsXe.saveAs(view);
       // } else {
       //     XMLFile = file;
       //     DOMSerializer serializer = new DOMSerializer((Boolean.valueOf(getProperty("format.output"))).booleanValue());
       //     try {
       //         serializer.serialize(document, XMLFile);
       //         return true;
       //     } catch (IOException ioe) {
       //         JOptionPane.showMessageDialog(view, ioe, "Write Error", JOptionPane.WARNING_MESSAGE);
       //         return false;
       //     }
       // }
       return true;
    }//}}}
    
    public boolean saveAs(TabbedView view) {//{{{
        //  if XMLFile is null, defaults to home directory
        JFileChooser saveDialog = new JFileChooser();
        //Add a filter to display only XML files
        Vector extentionList = new Vector();
        extentionList.add(new String("xml"));
        CustomFileFilter firstFilter = new CustomFileFilter(extentionList, "XML Documents");
        saveDialog.addChoosableFileFilter(firstFilter);
        //Add a filter to display only XSL files
        extentionList = new Vector();
        extentionList.add(new String("xsl"));
        saveDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL Stylesheets"));
        //Add a filter to display only XSL:FO files
        extentionList = new Vector();
        extentionList.add(new String("fo"));
        saveDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL:FO Documents"));
        //Add a filter to display all formats
        extentionList = new Vector();
        extentionList.add(new String("xml"));
        extentionList.add(new String("xsl"));
        extentionList.add(new String("fo"));
        saveDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "All XML Documents"));
        
        //The "All Files" file filter is added to the dialog
        //by default. Put it at the end of the list.
        FileFilter all = saveDialog.getAcceptAllFileFilter();
        saveDialog.removeChoosableFileFilter(all);
        saveDialog.addChoosableFileFilter(all);
        saveDialog.setFileFilter(firstFilter);
        
        int returnVal = saveDialog.showOpenDialog(view);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            XMLFile=saveDialog.getSelectedFile();
            return save(view);
        }
        return true;
    }//}}}
    
    //{{{ Private members
    
    private String getUntitledLabel() {//{{{
        XMLDocument[] docs = jsXe.getXMLDocuments();
        int untitledNo = 0;
        for (int i=0; i < docs.length; i++) {
            if ( docs[i].getName().startsWith("Untitled-")) {
                // Kinda stolen from jEdit
                try {
					untitledNo = Math.max(untitledNo,Integer.parseInt(docs[i].getName().substring(9)));
                }
				catch(NumberFormatException nf) {}
            }
        }
        return "Untitled-" + Integer.toString(untitledNo+1);
    }//}}}
    
    private Properties props = new Properties();
    private File XMLFile;
    private Reader XMLReader;
    private String name;
    //}}}
    
}

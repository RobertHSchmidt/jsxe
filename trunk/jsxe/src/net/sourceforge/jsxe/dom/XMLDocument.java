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
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
//}}}

//{{{ AWT classes
import java.awt.Component;
//}}}

//{{{ DOM classes
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.Vector;
//}}}

//}}}

public abstract class XMLDocument {
    
    public abstract void validate() throws SAXParseException, SAXException, ParserConfigurationException, IOException;
    
    public boolean isUntitled() {//{{{
        return (getFile() == null);
    }//}}}
    
    public abstract String getName();
    
    public abstract File getFile();
    
    public abstract String getSource() throws IOException;
    
    public abstract Document getDocument();
    
    public Object setProperty(String key, String value) {//{{{
        return props.setProperty(key, value);
    }//}}}
    
    public String getProperty(String key) {//{{{
        return props.getProperty(key);
    }//}}}
    
    public String getProperty(String key, String defaultValue) {//{{{
        return props.getProperty(key, defaultValue);
    }//}}}
    
    public abstract boolean save(TabbedView view);
    
    public abstract boolean save(TabbedView view, File file);
    
    public abstract boolean saveAs(TabbedView view);
    
    public abstract void setModel(File file) throws FileNotFoundException, IOException;
    
    public abstract void setModel(Reader reader) throws IOException;
    
    public abstract void setModel(String string) throws IOException;
    
    public abstract boolean isValidated();
    
    //{{{ Private members
    private Properties props = new Properties();
    //}}}
    
}

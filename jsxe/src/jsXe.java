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

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ Swing Classes
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
//}}}

//{{{ AWT Components
import java.awt.Dimension;
import java.awt.Toolkit;
//}}}

//{{{ Java Base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;
//}}}

//{{{ JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ DOM uses SAX Exceptions
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//}}}

//}}}

public class jsXe {

    public static void main(String args[]) {//{{{

        //check the java version
        String javaVersion = System.getProperty("java.version");
		if(javaVersion.compareTo("1.3") < 0)
		{
			System.err.println("jsXe " + getVersion());
            System.err.println("ERROR: You are running Java version " + javaVersion + ".");
			System.err.println("jsXe requires Java 1.3 or later.");
			System.exit(1);
		}
        
        //temporary method to set size of jsXe relative to screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        windowWidth = (int)(screenSize.getWidth() / 2);
        windowHeight = (int)(3 * screenSize.getHeight() / 4);
        
        view = new TabbedView(windowWidth, windowHeight);

        if (args.length >= 1) {
            if (!openXMLDocuments(view, args)) {
                openXMLDocument(view, DefaultDocument);
            }
        } else {
            openXMLDocument(view, DefaultDocument);
        }
        view.show();
    }//}}}

    public static String getVersion() {//{{{
        return MajorVersion + "." + MinorVersion + "." + BuildVersion + " " + BuildType;
    }//}}}

    public static void showOpenFileDialog(TabbedView view) {//{{{
            // if current file is null, defaults to home directory
            JFileChooser loadDialog = new JFileChooser(view.getDocumentPanel().getDOMAdapter().getFile());
            //Add a filter to display only XML files
            Vector extentionList = new Vector();
            extentionList.add(new String("xml"));
            CustomFileFilter firstFilter = new CustomFileFilter(extentionList, "XML Documents");
            loadDialog.addChoosableFileFilter(firstFilter);
            extentionList = new Vector();
            extentionList.add(new String("xsl"));
            loadDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL Stylesheets"));
            extentionList = new Vector();
            extentionList.add(new String("fo"));
            loadDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL:FO Documents"));
            extentionList = new Vector();
            extentionList.add(new String("xml"));
            extentionList.add(new String("xsl"));
            extentionList.add(new String("fo"));
            loadDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "All XML Documents"));

            //put the choose all file filter at the end of the list
            FileFilter all = loadDialog.getAcceptAllFileFilter();
            loadDialog.removeChoosableFileFilter(all);
            loadDialog.addChoosableFileFilter(all);
            loadDialog.setFileFilter(firstFilter);

            int returnVal = loadDialog.showOpenDialog(view);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                openXMLDocument(view, loadDialog.getSelectedFile());
            }
    }//}}}
    
    public static boolean openXMLDocument(TabbedView view, File file) {//{{{
        DOMAdapter adapter = DOMAdapter.getDOMAdapter(view, file);
        if (adapter != null) {
            view.setAdapter(adapter);
            return true;
        } else {
            return false;
        }
    }//}}}

    public static boolean openXMLDocument(TabbedView view, String doc) {//{{{
        return openXMLDocument(view, new StringReader(doc));
    }//}}}

    public static boolean openXMLDocument(TabbedView view, Reader reader) {//{{{
        DOMAdapter adapter = DOMAdapter.getDOMAdapter(view, reader);
        if (adapter != null) {
            view.setAdapter(adapter);
            return true;
        } else {
            return false;
        }
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

    public static boolean closeXMLDocument(TabbedView view, DOMAdapter adapter) {//{{{
        view.close(adapter);
        if (view.getDocumentCount() == 0)
            openXMLDocument(view, DefaultDocument);
        return true;
    }//}}}

    public static String getDefaultDocument() {//{{{
        return DefaultDocument;
    }//}}}

    //this is probobly bad implementation
    public static Dimension getStartingSize() {//{{{
        return new Dimension(windowWidth, windowHeight);
    }//}}}

    /*
    *************************************************
    Data Fields
    *************************************************
    *///{{{
    private static TabbedView view;
    private static String MajorVersion = "00";
    private static String MinorVersion = "01";
    private static String BuildVersion = "01";
    private static String BuildType    = "alpha";
    private static final String DefaultDocument = "<?xml version='1.0' encoding='UTF-8'?><default_element>default_node</default_element>";
    private static int windowWidth=600;
    private static int windowHeight=600;
    //}}}
}

/*
DefaultXMLDocument.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the default implementation of the XMLDocument abstract class.
It represents a generic XML document.

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
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
import java.util.Vector;
//}}}

//}}}

public class DefaultXMLDocument extends XMLDocument {
    
    protected DefaultXMLDocument(File file) throws FileNotFoundException, IOException {//{{{
        setProperty("format.output", "true");
        setModel(file);
        validated=false;
    }//}}}
    
    protected DefaultXMLDocument(Reader reader) throws IOException {//{{{
        setProperty("format.output", "true");
        setModel(reader);
        name = getUntitledLabel();
        validated=false;
    }//}}}
    
    protected DefaultXMLDocument(String string) throws IOException {//{{{
        setProperty("format.output", "true");
        setModel(string);
        name = getUntitledLabel();
        validated=false;
    }//}}}
    
    public void validate() throws SAXParseException, SAXException, ParserConfigurationException, IOException {//{{{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(source)));
        doc.getDocumentElement().normalize();
        document=doc;
        validated=true;
    }//}}}
    
    public Document getDocument() {//{{{
        return document;
    }//}}}

    public String getName() {//{{{
        return name;
    }//}}}

    public String getSource() throws IOException {//{{{
        return source;
    }//}}}
    
    public File getFile() {//{{{
        return XMLFile;
    }//}}}
    
    public void setModel(File file) throws FileNotFoundException, IOException {//{{{
        validated=false;
        XMLFile = file;
        source="";
        int nextchar=0;
        if (file!=null) {
            name = file.getName();
            FileReader reader=new FileReader(file);
            while (nextchar != -1) {
               nextchar = reader.read();
               if (nextchar != -1)
                   source+=(char)nextchar;
            }
        } else {
            name = getUntitledLabel();
        }
    }//}}}
    
    public void setModel(Reader reader) throws IOException {//{{{
        validated=false;
        source="";
        int nextchar=0;
        while (nextchar != -1) {
           nextchar = reader.read();
           if (nextchar != -1)
               source+=(char)nextchar;
        }
    }//}}}
    
    public void setModel(String string) {//{{{
        validated = false;
        source=string;
    }//}}}
    
    public boolean isValidated() {//{{{
        return validated;
    }//}}}

    public boolean save(TabbedView view) {//{{{
       return save(view,XMLFile);
    }//}}}
    
    public boolean save(TabbedView view, File file) {//{{{
        if (file == null) {
            return saveAs(view);
        } else {
            try {
                validate();
                XMLFile = file;
                //formatting the document is disabled because it doesn't work right
                DOMSerializer serializer = new DOMSerializer(false);
                try {
                    serializer.serialize(getDocument(), XMLFile);
                    return true;
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(view, ioe, "Write Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch(SAXParseException spe) {
                JOptionPane.showMessageDialog(view, "Document must be well-formed XML\n"+spe, "Parse Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            catch (SAXException sxe) {
                JOptionPane.showMessageDialog(view, "Document must be well-formed XML\n"+sxe, "Parse Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            catch (ParserConfigurationException pce) {
                JOptionPane.showMessageDialog(view, pce, "Parser Configuration Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            catch (IOException ioe) {
                JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
    }//}}}
    
    public boolean saveAs(TabbedView view) {//{{{
        try {
            validate();
            
            //  if XMLFile is null, defaults to home directory
            JFileChooser saveDialog = new JFileChooser();
            saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
            saveDialog.setDialogTitle("Save As");
            
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
            
            int returnVal = saveDialog.showSaveDialog(view);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                XMLFile=saveDialog.getSelectedFile();
                return save(view);
            }
            return true;
        } catch(SAXParseException spe) {
            JOptionPane.showMessageDialog(view, "Document must be well-formed XML\n"+spe, "Parse Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        catch (SAXException sxe) {
            JOptionPane.showMessageDialog(view, "Document must be well-formed XML\n"+sxe, "Parse Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        catch (ParserConfigurationException pce) {
            JOptionPane.showMessageDialog(view, pce, "Parser Configuration Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        catch (IOException ioe) {
            JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
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
    
    private Document document;
    private File XMLFile;
    private String name;
    private String source=new String();
    private boolean validated;
    //}}}
}

/*
DomTreeAdapter.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the adapter class that allows a DOM to serve as the model
for a JTree. This allows jsXe to display an XML document as a tree diagram.

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

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ Swing components
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
//}}}

//{{{ JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
//}}}

//{{{ DOM uses SAX Exceptions
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
//}}}

//}}}

import java.io.PrintWriter;

public class DOMAdapter implements TreeModel, TableModel {

    private DOMAdapter(TabbedView parent, File file, Document doc, String docname) {//{{{
        props.setProperty("format.output", "true");
        view = parent;
        XMLFile=file;
        document = doc;
        name = docname;
    }//}}}

    public static DOMAdapter getDOMAdapter(TabbedView parent, File file) {//{{{
        if (file == null) {
            return null;
        }
        DOMAdapter instance = null;
        if ( file.exists() ) {
            for (int i = 0; i < adapterList.size(); i++) {
                DOMAdapter currentAdapter = (DOMAdapter)adapterList.get(i);
                File currentFile = currentAdapter.getFile();
                if (currentFile != null) {
                    try {
                        // This method of comparison is used because
                        // file.equals(currentAdapter.getFile()) breaks
                        // when you specify a file through a command line arg
                        // like ../../document.xml
                        if (file.getCanonicalPath().equals(currentAdapter.getFile().getCanonicalPath())) {
                            return currentAdapter;
                        }
                    }
                    catch(IOException ioe) {
                        JOptionPane.showMessageDialog(parent, ioe, "IO Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse( file );
                doc.getDocumentElement().normalize();
                instance = new DOMAdapter(parent, file, doc, file.getName());
                adapterList.add(instance);
            }
            catch(SAXParseException spe) {
                JOptionPane.showMessageDialog(parent, spe, "Parse Error", JOptionPane.WARNING_MESSAGE);
            }
            catch (SAXException sxe) {
                JOptionPane.showMessageDialog(parent, sxe, "Parse Error", JOptionPane.WARNING_MESSAGE);
            }
            catch (ParserConfigurationException pce) {
                JOptionPane.showMessageDialog(parent, pce, "Parser Configuration Error", JOptionPane.WARNING_MESSAGE);
            }
            catch (IOException ioe) {
                JOptionPane.showMessageDialog(parent, ioe, "IO Error", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Could not open file " + file.getName(), "IO Error", JOptionPane.WARNING_MESSAGE);
        }
        return instance;
    }//}}}

    public static DOMAdapter getDOMAdapter(TabbedView parent, Reader reader) {//{{{
        if (reader == null) {
            return null;
        }
        DOMAdapter instance = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource(reader) );
            doc.getDocumentElement().normalize();
            instance = new DOMAdapter( parent, null, doc, parent.getUntitledLabel() );
            adapterList.add(instance);
        }
        catch(SAXParseException spe) {
            JOptionPane.showMessageDialog(parent, spe, "Parse Error", JOptionPane.WARNING_MESSAGE);
        }
        catch (SAXException sxe) {
            JOptionPane.showMessageDialog(parent, sxe, "Parse Error", JOptionPane.WARNING_MESSAGE);
        }
        catch (ParserConfigurationException pce) {
            JOptionPane.showMessageDialog(parent, pce, "Parser Configuration Error", JOptionPane.WARNING_MESSAGE);
        }
        catch (IOException ioe) {
            JOptionPane.showMessageDialog(parent, ioe, "IO Error", JOptionPane.WARNING_MESSAGE);
        }
        return instance;
    }//}}}

    public static DOMAdapter getDOMAdapter(TabbedView parent, String doc) {//{{{
        return getDOMAdapter(parent, new StringReader(doc));
    }//}}}

    // {{{ Implemented TreeModel methods

    public void addTreeModelListener( TreeModelListener listener ) {//{{{
        if ( listener != null && ! treeListenerList.contains( listener ) ) {
            treeListenerList.addElement( listener );
        }
    }//}}}

    public Object getChild(Object parent, int index) {//{{{
        AdapterNode node = (AdapterNode) parent;
        return node.child(index);
    }//}}}

    public int getChildCount(Object parent) {//{{{
        AdapterNode node = (AdapterNode) parent;
        return node.childCount();
    }//}}}

    public int getIndexOfChild(Object parent, Object child) {//{{{
        AdapterNode node = (AdapterNode) parent;
        return node.index((AdapterNode) child);
    }//}}}
    
    public Object getRoot() {//{{{
        return new AdapterNode(document);
    }//}}}

    public boolean isLeaf(Object aNode) {//{{{
        // Return true for any node with no children
        AdapterNode node = (AdapterNode) aNode;
        if (node.childCount() > 0) return false;
        return true;
    }//}}}

    public void removeTreeModelListener( TreeModelListener listener ) {//{{{
        if ( listener != null ) {
            treeListenerList.removeElement( listener );
        }
    }//}}}

    public void valueForPathChanged(TreePath path, Object newValue) {//{{{
        AdapterNode changedNode = (AdapterNode)path.getLastPathComponent();
        //Verify that this really is a change
        if (changedNode.toString() != newValue.toString()) {
            //get the nodes needed
            Node node = ((AdapterNode)path.getLastPathComponent()).getNode();
            Node parent = node.getParentNode();
            NodeList children = node.getChildNodes();
            //replace the changed node
            try {
                Element newNode = document.createElement(newValue.toString());
                parent.replaceChild(newNode, node);
                for (int i = 0; i < children.getLength(); i++ ) {
                    Node child = children.item(i);
                    node.removeChild(child);
                    newNode.appendChild(child);
                }
            }
            catch (DOMException dome) {
               JOptionPane.showMessageDialog(view, dome, "Internal Error", JOptionPane.WARNING_MESSAGE);
            }
            //notify the listeners that the tree structure has changed
            fireTreeNodesChanged(new TreeModelEvent(this, path));
        }
    }//}}}

    //}}}

    // {{{ Implemented TableModel methods

    public void addTableModelListener(TableModelListener l) {//{{{
        if ( l != null && !tableListenerList.contains( l ) ) {
            tableListenerList.addElement( l );
        }
    }//}}}

    public Class getColumnClass(int columnIndex) {//{{{
        //the attributes table should contain strings only
        return data[0].get(0).getClass();
    }//}}}

    public int getColumnCount() {//{{{
        //the attributes table will always contain 2 columns
        //an attribute and value
        return 2;
    }//}}}

    public String getColumnName(int columnIndex) {//{{{
        if (columnIndex==0)
            return "Attribute";
        else
            return "Value";
    }//}}}

    public int getRowCount() {//{{{
        //data[0] and data[1] are associated and will be equal in size
        return data[0].size();
    }//}}}

    public Object getValueAt(int rowIndex, int columnIndex) {//{{{
        return data[columnIndex].get(rowIndex);
    }//}}}

    public boolean isCellEditable(int rowIndex, int columnIndex) {//{{{
        //Do not allow editing of attribute values that have no
        //attribute defined yet.
        if (columnIndex==1 && (((String)getValueAt(rowIndex,0)).equals(""))) {
            return false;
        }
        return true;
    }//}}}

    public void removeTableModelListener(TableModelListener l) {//{{{
        if ( l != null ) {
            tableListenerList.removeElement( l );
        }
    }//}}}

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {//{{{
        //This algorithm makes sure that there is an empty table row
        //at the bottom of the table for users to add new attributes.

        //add intermittent blank rows (this should never happen
        //more than once)
        while (rowIndex + 1 > getRowCount()) {
            data[columnIndex].addElement("");
            data[columnIndex+1%2].addElement("");
        }
        if (columnIndex!=1 || !aValue.equals("")) {
            //If setting a value on the last row (greater shouldn't happen)
            if (rowIndex + 1 >= getRowCount()) {
                //We need to check if there really is a change.
                //If we don't the UI croaks NullPointerExceptions
                //when trying to update the UI for the table.
                if (!aValue.equals(getValueAt(rowIndex, columnIndex))) {
                    //Set the value in the table
                    data[columnIndex].set(rowIndex, aValue);
                    //If you aren't setting the final blank row
                    //it needs to be added.
                    if (!aValue.equals("")) {
                        data[columnIndex].addElement("");
                        data[columnIndex+1%2].addElement("");
                    }
                    fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE));
                }
            } else {
                //We're not on the last row so just set the value.
                if (!aValue.equals(getValueAt(rowIndex, columnIndex))) {
                    data[columnIndex].set(rowIndex, aValue);
                    fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE));
                }
            }
        }
    }//}}}

    //}}}

    public void removeRow(int rowIndex) {//{{{
        data[0].removeElementAt(rowIndex);
        data[1].removeElementAt(rowIndex);
    }//}}}

    public void updateTable(AdapterNode selectedNode) {//{{{
        NamedNodeMap attrs = selectedNode.getAttributes();
        if (attrs!=null) {
            while(getRowCount()>0) {
                removeRow(0);
            }
            for(int i = 0; i < attrs.getLength(); i++) {
                setValueAt(attrs.item(i).getNodeName(),i,0);
                setValueAt(attrs.item(i).getNodeValue(),i,1);
            }
        } else {
            if (selectedNode.getNodeType() == Node.ELEMENT_NODE) {
                //One extra table entry for adding an attribute
                setValueAt("",attrs.getLength(),0);
                setValueAt("",attrs.getLength(),1);
            }
           // while(getRowCount()>0) {
           //     removeRow(0);
           // }
        }
    }//}}}

    // {{{ Event notification methods

    private void fireTreeNodesChanged(TreeModelEvent e) {//{{{
        Enumeration listeners = treeListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener = (TreeModelListener) listeners.nextElement();
            listener.treeNodesChanged( e );
        }
    }//}}}

    private void fireTreeNodesInserted(TreeModelEvent e) {//{{{
        Enumeration listeners = treeListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener = (TreeModelListener) listeners.nextElement();
            listener.treeNodesInserted( e );
        }
    }//}}}

    private void fireTreeNodesRemoved(TreeModelEvent e) {//{{{
        Enumeration listeners = treeListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener = (TreeModelListener) listeners.nextElement();
            listener.treeNodesRemoved( e );
        }
    }//}}}

    private void fireTreeStructureChanged(TreeModelEvent e) {//{{{
        Enumeration listeners = treeListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener = (TreeModelListener) listeners.nextElement();
            listener.treeStructureChanged( e );
        }
    }//}}}

    private void fireTableChanged(TableModelEvent e) {//{{{
        Enumeration listeners = tableListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TableModelListener listener = (TableModelListener)listeners.nextElement();
            listener.tableChanged(e);
        }
    }//}}}

    // }}}

    public boolean isUntitled() {//{{{
        return (XMLFile == null);
    }//}}}

    public String getName() {//{{{
        return name;
    }//}}}

    public void save() {//{{{
        if (XMLFile == null) {
            saveAs();
        } else {
            
            DOMSerializer serializer = new DOMSerializer((Boolean.valueOf(getProperty("format.output"))).booleanValue());
            try {
                serializer.serialize(document, XMLFile);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(view, ioe, "Write Error"
                    , JOptionPane.WARNING_MESSAGE);
            }
        }
    }//}}}

    public void saveAs() {//{{{
        //  if XMLFile is null, defaults to home directory
        JFileChooser saveDialog = new JFileChooser(XMLFile);
        saveDialog.setFileFilter(//{{{
            new FileFilter() {
                public boolean accept(File f) {
                    if(f != null) {
                        if(f.isDirectory()) {
                            return true;
                        }
                        String extention = getExtension(f);
                        if(extention!=null && extention.compareTo(new String("xml"))==0) {
                            return true;
                        }
                    }
                    return false;
                }
                public String getDescription() {
                    return new String("XML Documents (.xml)");
                }
                private String getExtension(File f) {
                    if(f != null) {
                        String filename = f.getName();
                        int i = filename.lastIndexOf('.');
                        if (i>0 && i<filename.length()-1) {
                            return filename.substring(i+1).toLowerCase();
                        }
                    }
                    return null;
                }
            });//}}}

        int returnVal = saveDialog.showOpenDialog(view);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            DOMSerializer serializer = new DOMSerializer((Boolean.valueOf(getProperty("format.output"))).booleanValue());
            try {
                File newFile = saveDialog.getSelectedFile();
                serializer.serialize(document, newFile);
                jsXe.openXMLDocument(view, newFile);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(view, ioe, "Write Error"
                    , JOptionPane.WARNING_MESSAGE);
            }
        }
    }//}}}

    public File getFile() {//{{{
        return XMLFile;
    }//}}}

    public String getSource() {//{{{
        String source = null;
        DOMSerializer serializer = new DOMSerializer((Boolean.valueOf(getProperty("format.output"))).booleanValue());
        StringWriter writer = new StringWriter();
        try {
            serializer.serialize(document, writer);
            source = writer.toString();
        } catch (IOException ioe) {
                JOptionPane.showMessageDialog(view, ioe, "Write Error"
                    , JOptionPane.WARNING_MESSAGE);
        }
        return source;
    }//}}}

    public void setSource(TabbedView view, String source) { //{{{
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(source)));
            document.getDocumentElement().normalize();
        }
        catch(SAXParseException spe) {
            JOptionPane.showMessageDialog(view, spe, "Parse Error", JOptionPane.WARNING_MESSAGE);
        }
        catch (SAXException sxe) {
            JOptionPane.showMessageDialog(view, sxe, "Parse Error", JOptionPane.WARNING_MESSAGE);
        }
        catch (ParserConfigurationException pce) {
            JOptionPane.showMessageDialog(view, pce, "Parser Configuration Error", JOptionPane.WARNING_MESSAGE);
        }
        catch (IOException ioe) {
            JOptionPane.showMessageDialog(view, ioe, "IO Error", JOptionPane.WARNING_MESSAGE);
        }
        //notify the listeners that the tree structure has changed
        fireTreeStructureChanged(new TreeModelEvent(this, new TreePath(document.getDocumentElement())));
    }//}}}

    public Document getDocument() {//{{{
        return document;
    }//}}}

    public Object setProperty(String key, String value) {//{{{
        return props.setProperty(key, value);
    }//}}}

    public String getProperty(String key) {//{{{
        return props.getProperty(key);
    }//}}}

    /*
    *************************************************
    Private Data Fields
    *************************************************
    *///{{{
    private Properties props = new Properties();
    private Document document;
    private File XMLFile;
    private String name;
    private TabbedView view;
    private Vector treeListenerList = new Vector();
    private Vector tableListenerList = new Vector();
    private static Vector adapterList = new Vector(10, 1);
    private Vector[] data={
        new Vector(),
        new Vector()
    };
    //}}}
}

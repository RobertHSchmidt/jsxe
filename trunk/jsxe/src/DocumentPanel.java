/*
DocumentPanel.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains all the code for the panel shows the xml document. Since you
don't want a panel for every document each with its own splitpane dimensions this
is a singleton. It may not be forever as jsXe may one day may be multi-threaded
and have multiple views.

This file written by ian Lewis (iml001@bridgewater.edu)
Copyright ? 2002 by ian Lewis

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
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JFileChooser;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
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

//{{{ DOM classes
import org.w3c.dom.Document;    
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
//}}}

//}}}

public class DocumentPanel extends JPanel {
    
    private DocumentPanel(Dimension size) {//{{{

        JScrollPane treeView = new JScrollPane(tree);

        //Create html editor pane
        htmlPane.setEditable(false);
        JScrollPane htmlView = new JScrollPane(htmlPane);

        //create a table model
        //attributesTable = new JTable(attrTableModel);
        JScrollPane attrView = new JScrollPane(attributesTable);

        //layout of the panes is defined by splitpanes
        vertSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeView, attrView);

        horizSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vertSplitPane, htmlView ); 
        horizSplitPane.setContinuousLayout(true);

        vertSplitPane.setDividerLocation(size.height/2);
        horizSplitPane.setDividerLocation(size.width/2);

        tree.addTreeSelectionListener(//{{{
            new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath selPath = e.getPath();
                    AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                    if ( selectedNode.getNodeValue() != null )
                        htmlPane.setText(selectedNode.getNodeValue());
                    else
                        htmlPane.setText("");
                    //if the selected node can be edited in the tree
                    tree.setEditable(selectedNode.canEditInJTree());
                    currentAdapter.updateTable(selectedNode);
                    attributesTable.updateUI();
                    previousSelection = selPath;
                }
            });//}}}

        setLayout(new BorderLayout());
        add(horizSplitPane, BorderLayout.CENTER);
    } //}}}

    private static void open(TabbedView view, DOMAdapter adapter) {//{{{
        //This adapter may have the listener already.
        //addTreeModelListener does not add the listener
        //if it is already added.
        adapter.addTreeModelListener(treeListener);
        adapter.addTableModelListener(tableListener);
        tree.setModel(adapter);
        attributesTable.setModel(adapter);
        //Clear the attributes table and right hand pane
        //of previous values.
        adapter.updateTable((AdapterNode)tree.getLastSelectedPathComponent());
        htmlPane.setText("");
        //update the UI so that the components
        //are redrawn.
        attributesTable.updateUI();
        tree.updateUI();
        instance.updateUI();
        currentAdapter = adapter;
    } //}}}

    public static DocumentPanel getDocumentPanel(TabbedView view, DOMAdapter adapter) {//{{{
        if (view == null) {
            return null;
        }
        if (adapter == null) {
            return instance;
        }
        instance.open(view, adapter);

        return instance;
    }//}}}

    public static DOMAdapter getDOMAdapter() {//{{{
        return currentAdapter;
    }//}}}

    /*
    *************************************************
    Private Data Fields
    *************************************************
    *///{{{
    private static JTree tree = new JTree();
    private static TreePath previousSelection;
    private static DOMAdapter currentAdapter;
    private static JEditorPane htmlPane = new JEditorPane("text/plain","");
    private static JTable attributesTable = new JTable();
    private static JSplitPane vertSplitPane;
    private static JSplitPane horizSplitPane;
    private static DocumentPanel instance = new DocumentPanel(jsXe.getStartingSize());
    private static TableModelListener tableListener = new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
           // int firstRow = e.getFirstRow();
           // int lastRow = e.getLastRow();
           // int column = e.getColumn();
           attributesTable.updateUI();
        }
    };
    private static TreeModelListener treeListener = new TreeModelListener() {//{{{
            public void treeNodesChanged(TreeModelEvent e) {
                tree.updateUI();
            }
            public void treeNodesInserted(TreeModelEvent e) {
                tree.updateUI();
            }
            public void treeNodesRemoved(TreeModelEvent e) {
                tree.updateUI();
            }
            public void treeStructureChanged(TreeModelEvent e) {
                tree.updateUI();
            }
        };//}}}
    //}}}
}

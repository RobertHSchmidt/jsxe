/*
SchemaView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the view class for the Schema view.

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

package schemaview;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.ViewPlugin;
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Swing components
import javax.swing.*;
import javax.swing.event.*;
//}}}

//{{{ AWT components
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
//}}}

//{{{ Java classes
import java.io.IOException;
//}}}

//{{{ JGraph classes
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
//}}}

//}}}

public class SchemaView extends JPanel implements DocumentView {
    
    //{{{ Instance variables
    private DocumentBuffer m_document;
    private ViewPlugin m_plugin;
    //}}}
    
    //{{{ SchemaView constructor
    
    public SchemaView(DocumentBuffer document, ViewPlugin plugin) throws IOException {
        
        setLayout(new BorderLayout());
        
        m_plugin = plugin;
        
        final JGraph graph = new JGraph();
        
        graph.setModel(new SchemaViewGraphModel(document));
        
       // org.jgraph.graph.GraphModel model = graph.getModel();
       // 
       // int rootcount = model.getRootCount();
       // for (int j=0; j<rootcount; j++) {
       //     Object root = model.getRootAt(j);
       //     Log.log(Log.DEBUG,this,"root: "+root.getClass().toString()+" : "+root.toString());
       //     int count = model.getChildCount(root);
       //     boolean children = false;
       //     for (int i=0;i<count;i++) {
       //         children = true;
       //         Object child = model.getChild(root, i);
       //         Log.log(Log.DEBUG,this,child.getClass().toString()+" : "+child.toString());
       //         int count2 = model.getChildCount(child);
       //         for (int k=0;k<count2;k++) {
       //             Object child2 = model.getChild(child, k);
       //             Log.log(Log.DEBUG,this,"   "+child2.getClass().toString()+" : "+child2.toString());
       //         }
       //     }
       //     if (!children) {
       //         Log.log(Log.DEBUG,this,"NO CHILDREN!");
       //     }
       // }
        
       // addComponentListener(new ComponentListener() {//{{{
       //     
       //     public void componentHidden(ComponentEvent e) {}
       //     
       //     public void componentMoved(ComponentEvent e) {}
       //     
       //     public void componentResized(ComponentEvent e) {}
       //     
       //     public void componentShown(ComponentEvent e) {
       //         ((SchemaViewGraphModel)graph.getModel()).layout();
       //         updateUI();
       //     }
       //     
       // });//}}}
        
        graph.setMoveable(false);
        graph.setConnectable(false);
        graph.setDisconnectable(false);
        graph.setBendable(false);
        graph.setSizeable(false);
        
        JScrollPane scrollPane = new JScrollPane(graph);
        add(scrollPane);
        setDocumentBuffer(document);
    }//}}}
    
    //{{{ DocumentView methods
    
    //{{{ close
    public boolean close() {
        return true;
    }//}}}
    
    //{{{ getDocumentViewComponent()
    public Component getDocumentViewComponent() {
        return this;
    }//}}}
    
    //{{{ getMenus()
    public JMenu[] getMenus() {
        return null;
    }
    //}}}
    
    //{{{ getOptionsPanel()
    public OptionsPanel getOptionsPanel() {
        return null;
    }
    //}}}
    
    //{{{ getViewPlugin()
    public ViewPlugin getViewPlugin() {
        return m_plugin;
    }
    //}}}
    
    //{{{ getDocumentBuffer()
    /**
     * Gets the DocumentBuffer that this view is currently using.
     * @return the DocumentBuffer for this view
     */
    public DocumentBuffer getDocumentBuffer() {
        return m_document;
    }
    //}}}
    
    //{{{ setDocumentBuffer()
    /**
     * Sets the current document. This method is used when you want to
     * view another document using this view object.
     * @param view the ownerview that made the request
     * @param document the new document to view
     * @throws IOException if the document cannot be viewed using this view
     */
    public void setDocumentBuffer(DocumentBuffer document) throws IOException {
        
        try {
            document.checkWellFormedness();
        } catch (Exception e) {
            String errormsg = "The tree view requires XML documents to be well-formed.\n\n"+
            e.toString();
            throw new IOException(errormsg);
        }
        
        m_document = document;
    }//}}}

    //}}}

}

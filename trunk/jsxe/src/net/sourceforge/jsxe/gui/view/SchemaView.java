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

package net.sourceforge.jsxe.gui.view;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.gui.OptionsPanel;
//}}}

//{{{ Swing components
import javax.swing.*;
import javax.swing.event.*;
//}}}

//{{{ AWT components
import java.awt.*;
import java.awt.event.*;
//}}}

//{{{ Java classes
import java.io.IOException;
//}}}

//{{{ JGraph classes
//import org.jgraph.JGraph;
//}}}

//}}}

public class SchemaView extends JPanel implements DocumentView {
    
    private static final String _VIEWNAME = "schema";
    
    //{{{ Instance variables
    private DocumentBuffer m_document;
    //}}}
    
    //{{{ SchemaView constructor
    
    public SchemaView(DocumentBuffer document) throws IOException {
        
        setLayout(new BorderLayout());
        
       // JGraph graph = new JGraph();
       // JScrollPane scrollPane = new JScrollPane(graph);
       // add(scrollPane);
        setDocumentBuffer(document);
    }//}}}
    
    //{{{ getHumanReadableName()
    /**
     * The human readable name that is displayed on menus and user visible
     * things
     * @return the human readable name of this view
     */
    public static String getHumanReadableName() {
        return "Schema View";
    }
    //}}}
    
    //{{{ DocumentView methods
    
    //{{{ close
    
    public boolean close() {
        return true;
    }//}}}
    
    //{{{ getDescription()
    /**
     * Gets a short, one line, description of this view.
     * @return a short description of the view
     */
    public String getDescription() {
        return "View a Schema document in a diagram";
    }//}}}
    
    //{{{ getDocumentViewComponent()
    /**
     * Gets the the Component used to render this view.
     * @return the Component used to render this view
     */
     public Component getDocumentViewComponent() {
         return this;
     }//}}}
    
    //{{{ getMenus()
    /**
     * Gets the menus that this view has associated with it. This can be
     * used when the view is loaded to set the menu bar.
     * @return the menus for this view
     */
    public JMenu[] getMenus() {
        return null;
    }
    //}}}
    
    //{{{ getOptionsPanel()
    /**
     * Gets the view's options panel. This is used when displaying options
     * that are associated with this view.
     * @return the OptionsPanel for this view
     */
    public OptionsPanel getOptionsPanel() {
        return null;
    }
    //}}}
    
    //{{{ getViewName()
    /**
     * Gets the name of the view used when the DocumentViewFactory selects
     * a view to create.
     * @return the name of the view
     */
    public String getViewName() {
        return _VIEWNAME;
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

/*
DocumentView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the abstract class that defines the interface for
views that are used by jsXe.

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

package net.sourceforge.jsxe.gui;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.DocumentBuffer;
//}}}

//{{{ AWT classes
import java.awt.Component;
//}}}

//{{{ Swing classes
import javax.swing.*;
//}}}


//{{{ Java base classes
import java.io.IOException;
//}}}

//}}}

/**
 * The DocumentView class defines the methods that will be implemented by
 * all views in jsXe that provide features for editing XML documents. This is
 * the precursor to jsXe's view/plugin interface.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see TabbedView
 */
public interface DocumentView {
    
    //{{{ close
    /**
     * Closes the current view. This method is called whenever the view is
     * being discarded. This is usually used to save properties associated
     * with this view.
     */
    public boolean close();
    //}}}
    
    //{{{ getDescription()
    /**
     * Gets a short, one line, description of this view.
     * @return a short description of the view
     */
    public String getDescription();
    //}}}
    
    //{{{ getDocumentViewComponent()
    /**
     * Gets the the Component used to render this view.
     * @return the Component used to render this view
     */
    public Component getDocumentViewComponent();//}}}
    
    //{{{ getMenus()
    /**
     * Gets the menus that this view has associated with it. This can be
     * used when the view is loaded to set the menu bar.
     * @return the menus for this view
     */
    public JMenu[] getMenus();
    //}}}
    
    //{{{ getOptionsPanel()
    /**
     * Gets the view's options panel. This is used when displaying options
     * that are associated with this view.
     * @return the OptionsPanel for this view
     */
    public OptionsPanel getOptionsPanel();
    //}}}
    
    //{{{ getViewName()
    /**
     * Gets the name of the view used when the DocumentViewFactory selects
     * a view to create.
     * @return the name of the view
     */
    public String getViewName();
    //}}}
    
    //{{{ getDocumentBuffer()
    /**
     * Gets the DocumentBuffer that this view is currently using.
     * @return the DocumentBuffer for this view
     */
    public DocumentBuffer getDocumentBuffer();
    //}}}
    
    //{{{ setDocumentBuffer()
    /**
     * Sets the current document. This method is used when you want to
     * view another document using this view object.
     * @param view the ownerview that made the request
     * @param document the new document to view
     * @throws IOException if the document cannot be viewed using this view
     */
    public void setDocumentBuffer(DocumentBuffer document) throws IOException;
    //}}}

}

/*
ViewPlugin.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This is the interface that view plugins implement.

Copyright (C) 2004 Ian Lewis

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

import net.sourceforge.jsxe.gui.DocumentView;

import java.io.IOException;

//}}}

/**
 * This abstract class defines plugins that specify a view that is used to edit
 * XML documents. This views are used to edit different types of XML documents.
 * ViewPlugins are also ActionPlugins which means that they specify actions that
 * jsXe can perform. These can be added to menus and dialogs etc.
 * 
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public abstract class ViewPlugin extends ActionPlugin {
    
    //{{{ newDocumentView()
    /**
     * Creates a new DocumentView for the given document. The DocumentView can
     * is then used to retrieve a component for this view plugin.
     * @param document the document to open the DocumentView with
     * @throws IOException if the view cannot be used to view this document
     */
    public abstract DocumentView newDocumentView(DocumentBuffer document) throws IOException;//}}}
    
}

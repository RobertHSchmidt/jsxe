/*
TreeViewPlugin.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the plugin class for the Tree View

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

package treeview;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.ViewPlugin;
import net.sourceforge.jsxe.gui.DocumentView;
//}}}

//{{{ Java classes
import java.io.IOException;
//}}}

//}}}

public class TreeViewPlugin extends ViewPlugin {
    
    //{{{ TreeViewPlugin
    
    public TreeViewPlugin() {
        super(NAME, HUMAN_READABLE_NAME, VERSION, DESCRIPTION);
    }//}}}
    
    //{{{ newDocumentView()
    
    public DocumentView newDocumentView(DocumentBuffer document) throws IOException {
        return new DefaultView(document);
    }//}}}
    
    //{{{ Private members
    
    private static String NAME = "tree";
    private static String HUMAN_READABLE_NAME = "Tree View";
    private static String VERSION = "0.1";
    private static String DESCRIPTION = "";
    
    //}}}
}

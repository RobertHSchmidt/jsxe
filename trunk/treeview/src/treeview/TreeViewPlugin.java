/*
TreeViewPlugin.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)

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

import treeview.action.*;

//{{{ jsXe classes
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.ViewPlugin;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.options.OptionPane;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Java classes
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
//}}}

//}}}

public class TreeViewPlugin extends ViewPlugin {
    
    public static final String PLUGIN_NAME = "tree";
    
    //{{{ TreeViewPlugin constructor
    public TreeViewPlugin() {
        //add actions
        addAction(new AddNodeAction("treeview.add.element.node", "new_element", "", AdapterNode.ELEMENT_NODE));
        addAction(new AddNodeAction("treeview.add.text.node", "", "New Text Node", AdapterNode.TEXT_NODE));
        addAction(new AddNodeAction("treeview.add.cdata.node", "", "New CDATA Section", AdapterNode.CDATA_SECTION_NODE));
        addAction(new AddNodeAction("treeview.add.pi.node", "Instruction", "New Processing Instruction", AdapterNode.PROCESSING_INSTRUCTION_NODE));
        addAction(new AddNodeAction("treeview.add.comment.node", "", "New Comment", AdapterNode.COMMENT_NODE));
        addAction(new RemoveNodeAction());
        addAction(new RenameNodeAction());
        addAction(new AddAttributeAction());
        addAction(new RemoveAttributeAction());
        addAction(new EditNodeAction());
        addAction(new AddDocTypeAction());
        addAction(new CutNodeAction());
        addAction(new CopyNodeAction());
        addAction(new PasteNodeAction());
    }//}}}
    
    //{{{ newDocumentView()
    public DocumentView newDocumentView(DocumentBuffer document) throws IOException {
        return new DefaultView(document, this);
    }//}}}
    
    //{{{ getOptionPane()
    public OptionPane getOptionPane(DocumentBuffer buffer) {
        return new TreeViewOptionPane(buffer);
    }//}}}
    
    //{{{ getProperties()
    public Properties getProperties() {
        Properties props = new Properties();
        try {
            InputStream stream = TreeViewPlugin.class.getResourceAsStream("/treeview/treeview.props");
            props.load(stream);
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, "Tree View: failed to load default properties.");
            Log.log(Log.ERROR, this, ioe.getMessage());
        }
        return props;
    }//}}}
}

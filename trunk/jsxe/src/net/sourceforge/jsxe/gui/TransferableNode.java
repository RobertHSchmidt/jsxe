/*
TransferableNode.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the TransferableNode class which is used for
data transfer of XML nodes.

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
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.dom.AdapterNode;
//}}}

//{{{ AWT classes
import java.awt.datatransfer.*;
//}}}

//{{{ Java classes
import java.io.IOException;
import java.util.*;
//}}}

//}}}

/**
 * A transferable class that manages the physical data that is transferred when
 * transferring an XML node. This class handles an AdapterNode
 * object and either returning the object itself or some representation of
 * the node (such as a string) during transfer.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class TransferableNode implements Transferable {
    
    //{{{ TransferableNode constructor
    /**
     * Creates a new TransferableNode to handle the AdapterNode given.
     * @param node The AdapterNode that is being transferred.
     */
    public TransferableNode(AdapterNode node) {
        m_node = node;
    }//}}}
    
    //{{{ getTransferDataFlavors()
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }//}}}
    
    //{{{ isDataFlavorSupported()
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavorList.contains(flavor));
    }//}}}
    
    //{{{ getTransferData()
    
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (stringFlavor.equals(flavor)) {
            return m_node.serializeToString();
        } else {
            if (nodeFlavor.equals(flavor)) {
                return m_node;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }//}}}
    
    //{{{ Private Members
    public static final DataFlavor stringFlavor = DataFlavor.stringFlavor;
    public static final DataFlavor nodeFlavor;
    
    static {
        DataFlavor flav = null;
        try {
            flav = new DataFlavor(Class.forName("net.sourceforge.jsxe.dom.AdapterNode"), "XML Node");
        } catch (ClassNotFoundException e) {
            jsXe.exiterror(null, e.getMessage(), 1);
        }
        nodeFlavor = flav;
    }
    
    private static final DataFlavor[] flavors = {
        stringFlavor,
        nodeFlavor
    };

    private static final List flavorList = Arrays.asList( flavors );
    private AdapterNode m_node;
    //}}}
    
}

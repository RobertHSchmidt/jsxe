/*
AdapterNode.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the Node class that will be used by the DomTreeAdapter
class to adapt a DOM into the model for a viewabel JTree.

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

//{{{ DOM classes
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
//}}}

//}}}

public class AdapterNode {

    public AdapterNode(Node node) {//{{{
        domNode = node;
    }//}}}

    public String toString() {//{{{
        String s = new String();
        if (typeName[domNode.getNodeType()].equals("Document"))
            return "Document Root";
        String nodeName = domNode.getNodeName();
        if (! nodeName.startsWith("#")) {	
            s += nodeName;
        }
        if (domNode.getNodeValue() != null) {
            String t = domNode.getNodeValue().trim();
            int x = t.indexOf("\n");
            if (x >= 0)
                t = t.substring(0, x);
            s += t;
        }
        return s;
    }//}}}

    public int index(AdapterNode child) {//{{{
        int count = childCount();
        for (int i=0; i<count; i++) {
            AdapterNode n = this.child(i);
            if (child == n) return i;
        }
        //Returns here when child not in tree
        return -1;
    }//}}}

    public AdapterNode child(int searchIndex) {//{{{
        //Note: JTree index is zero-based.
        Node node = domNode.getChildNodes().item(searchIndex);
        return new AdapterNode(node);
    }//}}}

    public int childCount() {//{{{
        return domNode.getChildNodes().getLength();
    }//}}}

    public boolean canEditInJTree() {//{{{
        boolean value = false;
        short nodeType = domNode.getNodeType();
        if (nodeType == Node.ELEMENT_NODE)
            value = true;
        return value;
    }//}}}
    
    public Node getNode() {//{{{
        return domNode;
    }//}}}

    public String getNodeValue() {//{{{
        return domNode.getNodeValue();
    }//}}}

    public NamedNodeMap getAttributes() {//{{{
        return domNode.getAttributes();
    }//}}}

    public short getNodeType() {//{{{
        return domNode.getNodeType();
    }//}}}

        /*
    *************************************************
    Private Data Fields
    *************************************************
    *///{{{
    private final String[] typeName = {
        "none",
        "Element",
        "Attr",
        "Text",
        "CDATA",
        "EntityRef",
        "Entity",
        "ProcInstr",
        "Comment",
        "Document",
        "DocType",
        "DocFragment",
        "Notation",
    };
    private Node domNode;
    //}}}

}

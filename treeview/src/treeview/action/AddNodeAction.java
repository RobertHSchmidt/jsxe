/*
AddNodeAction.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2005 Ian Lewis (IanLewis@member.fsf.org)

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

package treeview.action;

//{{{ imports

import treeview.*;

//{{{ AWT classes
import java.awt.event.ActionEvent;
//}}}

//{{{ Java base classes
import java.util.HashMap;
import java.util.ArrayList;
//}}}

//{{{ Swing classes
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.Action;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
//}}}

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.LocalizedAction;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.dom.completion.ElementDecl;
import net.sourceforge.jsxe.dom.completion.EntityDecl;
//}}}

//}}}

/**
 * An action that adds a node to the tree at the current selected node.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class AddNodeAction extends LocalizedAction {
    
    //{{{ AddNodeAction constructor
    /**
     * Crates an action that adds an element node based on the specified
     * element declaration. When adding a node using an ElementDecl
     * an EditTagDialog will be displayed.
     * @param element the declaration specifying the information for this node.
     */
    public AddNodeAction(ElementDecl element) {
        super(element.name); // not to be registed with the ActionManager
        init(element.name, null, AdapterNode.ELEMENT_NODE);
        m_element = element;
    }//}}}
    
    //{{{ AddNodeAction constructor
    /**
     * Crates an action that adds an entity reference node based on the specified
     * entity declaration.
     * @param entity the declaration specifying the information for this node.
     */
    public AddNodeAction(EntityDecl entity) {
        super(entity.name); // not to be registed with the ActionManager
        init(entity.name, entity.value, AdapterNode.ENTITY_REFERENCE_NODE);
        m_entity = entity;
    }//}}}
    
    //{{{ AddNodeAction constructor
    /**
     * Creates a action that adds a new node of the specified type and values
     * to the current selected node in the tree.
     * @param name the name of the action.
     * @param nodeName the qualified name of the node to add. This can be null if applicable
     * @param nodeValue the value of the node. This can be null if applicable
     * @param nodeType the type of the node. Use the values specified by the org.w3c.dom.Node class
     */
    public AddNodeAction(String name, String nodeName, String nodeValue, short nodeType) {
        super(name);
        init(nodeName, nodeValue, nodeType);
    }//}}}
    
    //{{{ invoke()
    public void invoke(TabbedView view, ActionEvent evt) {
        DocumentView docView = view.getDocumentView();
        if (docView instanceof DefaultView) {
            DefaultView defView = (DefaultView)docView;
            TreeViewTree tree = defView.getTree();
            AdapterNode selectedNode = tree.getSelectedNode();
            AdapterNode addedNode = null;
            if (selectedNode != null) {
                try {
                    if (m_element != null) {
                        XMLDocument document = selectedNode.getOwnerDocument();
                        boolean isOk = true;
                        try {
                            document.beginCompoundEdit();
                            if (m_element.getAttributes().size() > 0) {
                                EditTagDialog dialog = new EditTagDialog(jsXe.getActiveView(),
                                                                         m_element,
                                                                         new HashMap(),
                                                                         m_element.empty,
                                                                         m_element.completionInfo.getEntityHash(),
                                                                         new ArrayList(), //don't support IDs for now.
                                                                         document);
                                dialog.show();
                                isOk = (dialog.getNewNode() != null);
                                if (isOk) {
                                    addedNode = selectedNode.addAdapterNode(dialog.getNewNode());
                                }
                            } else {
                                addedNode = selectedNode.addAdapterNode(m_element.name, null, AdapterNode.ELEMENT_NODE, selectedNode.childCount());
                            }
                        } finally {
                            document.endCompoundEdit();
                        }
                    } else {
                        //add the node of the correct type to the end of the children of this node
                        addedNode = selectedNode.addAdapterNode(m_name, m_value, m_nodeType, selectedNode.childCount());
                    }
                    
                    //if we hit cancel in the dialog we don't want to do this.
                    if (isOk) {
                        tree.expandPath(tree.getLeadSelectionPath());
                        //The TreeModel doesn't automatically call treeNodesInserted() yet
                        tree.updateUI();
                    }
                    
                
                } catch (DOMException dome) {
                    JOptionPane.showMessageDialog(tree, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }//}}}
    
    //{{{ getLabel()
    public String getLabel() {
        if (m_element != null) {
            return m_element.name;
        } else {
            if (m_entity != null) {
                return m_entity.name;
            } else {
                return super.getLabel();
            }
        }
    }//}}}
    
    //{{{ Private members
    
    //{{{ init()
    public void init(String name, String value, short type) {
        m_name     = name;
        m_value    = value;
        m_nodeType = type;
    }//}}}
    
    private short m_nodeType;
    private String m_name;
    private String m_value;
    private ElementDecl m_element;
    private EntityDecl m_entity;
    //}}}
    
}


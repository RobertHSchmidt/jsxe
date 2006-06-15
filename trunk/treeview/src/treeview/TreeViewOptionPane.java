/*
TreeViewOptionPane.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2002, 2006 Ian Lewis (IanLewis@member.fsf.org)

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
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.options.AbstractOptionPane;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.gui.Messages;
//}}}

//{{{ Swing components
import javax.swing.*;
import javax.swing.text.PlainDocument;
import javax.swing.event.*;
import javax.swing.tree.*;
//}}}

//{{{ AWT components
import java.awt.*;
import java.awt.event.*;
//}}}

//{{{ Java base classes
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
//}}}

//}}}


public class TreeViewOptionPane extends AbstractOptionPane {
    
    //{{{ DefaultViewOptionPane constructor
    public TreeViewOptionPane(DocumentBuffer buffer) {
        super("defaultview");
    }//}}}
    
    //{{{ _init()
    protected void _init() {
        
        //{{{ show comments
        
        boolean showCommentNodes = Boolean.valueOf(jsXe.getProperty(DefaultView.SHOW_COMMENTS, "false")).booleanValue();
        showCommentsCheckBox = new JCheckBox(Messages.getMessage("TreeView.Options.Show.Comments"),showCommentNodes);
        
        addComponent(showCommentsCheckBox, Messages.getMessage("TreeView.Options.Show.Comments.ToolTip")); 
        
        //}}}
       
       // boolean showEmptyNodes = Boolean.valueOf(m_document.getProperty(SHOW_EMPTY_NODES, "false")).booleanValue();
       // showEmptyNodesCheckBox = new JCheckBox("Show whitespace-only nodes",showEmptyNodes);
        
        //{{{ continuous layout
        
        boolean continuousLayout = Boolean.valueOf(jsXe.getProperty(DefaultView.CONTINUOUS_LAYOUT, "false")).booleanValue();
        ContinuousLayoutCheckBox = new JCheckBox(Messages.getMessage("TreeView.Options.Continuous.Layout"),continuousLayout);
        
        addComponent(ContinuousLayoutCheckBox, Messages.getMessage("TreeView.Options.Continuous.Layout.ToolTip"));
        
        //}}}
        
        //{{{ show attributes
        
        String showAttrs = jsXe.getProperty(DefaultView.SHOW_ATTRIBUTES, "ID only");
        
        m_showAttrsComboBox = new JComboBox(new String [] {"None", "ID only", "All"});
        m_showAttrsComboBox.setSelectedItem(showAttrs);
        
        addComponent(Messages.getMessage("TreeView.Options.Show.Attributes"),
                     m_showAttrsComboBox,
                     Messages.getMessage("TreeView.Options.Show.Attributes.ToolTip"));
        
        //}}}
        
    }//}}}
    
    //{{{ _save()
    protected void _save() {
        jsXe.setProperty(DefaultView.SHOW_COMMENTS,String.valueOf(showCommentsCheckBox.isSelected()));
       // m_document.setProperty(SHOW_EMPTY_NODES,(new Boolean(showEmptyNodesCheckBox.isSelected())).toString());
        jsXe.setProperty(DefaultView.CONTINUOUS_LAYOUT,String.valueOf(ContinuousLayoutCheckBox.isSelected()));
        jsXe.setProperty(DefaultView.SHOW_ATTRIBUTES, m_showAttrsComboBox.getSelectedItem().toString());
    }//}}}
    
    //{{{ getTitle()
    public String getTitle() {
        return Messages.getMessage("TreeView.Options.Title");
    }//}}}
        
    //{{{ Private Members
        
    private JCheckBox showCommentsCheckBox;
   // private JCheckBox showEmptyNodesCheckBox;
    private JCheckBox ContinuousLayoutCheckBox;
    private JComboBox m_showAttrsComboBox;
    
    //}}}
        
}//}}}

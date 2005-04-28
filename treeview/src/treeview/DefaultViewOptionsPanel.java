/*
DefaultViewOptionsPanel.java
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

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.gui.DocumentView;
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


public class DefaultViewOptionsPanel extends OptionsPanel {
    
    //{{{ DefaultViewOptionsPanel constructor
    
    public DefaultViewOptionsPanel(DocumentBuffer buffer) {
        
        m_document = buffer;
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        
        setLayout(layout);
        
        int gridY = 0;
        
        boolean showCommentNodes = Boolean.valueOf(m_document.getProperty(DefaultView.SHOW_COMMENTS, "false")).booleanValue();
       // boolean showEmptyNodes = Boolean.valueOf(m_document.getProperty(SHOW_EMPTY_NODES, "false")).booleanValue();
        boolean continuousLayout = Boolean.valueOf(m_document.getProperty(DefaultView.CONTINUOUS_LAYOUT, "false")).booleanValue();
        
        showCommentsCheckBox = new JCheckBox("Show comment nodes",showCommentNodes);
       // showEmptyNodesCheckBox = new JCheckBox("Show whitespace-only nodes",showEmptyNodes);
        ContinuousLayoutCheckBox = new JCheckBox("Continuous layout for split-panes",continuousLayout);
        
        constraints.gridy      = gridY++;
        constraints.gridx      = 1;
        constraints.gridheight = 1;
        constraints.gridwidth  = 1;
        constraints.weightx    = 1.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(showCommentsCheckBox, constraints);
        add(showCommentsCheckBox);
        
       // constraints.gridy      = gridY++;
       // constraints.gridx      = 1;
       // constraints.gridheight = 1;
       // constraints.gridwidth  = 1;
       // constraints.weightx    = 1.0f;
       // constraints.fill       = GridBagConstraints.BOTH;
       // constraints.insets     = new Insets(1,0,1,0);
        
       // layout.setConstraints(showEmptyNodesCheckBox, constraints);
       // add(showEmptyNodesCheckBox);
        
        constraints.gridy      = gridY++;
        constraints.gridx      = 1;
        constraints.gridheight = 1;
        constraints.gridwidth  = 1;
        constraints.weightx    = 1.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(ContinuousLayoutCheckBox, constraints);
        add(ContinuousLayoutCheckBox);
    }//}}}
    
    //{{{ save()
    
    public void save() {
        m_document.setProperty(DefaultView.SHOW_COMMENTS,String.valueOf(showCommentsCheckBox.isSelected()));
       // m_document.setProperty(SHOW_EMPTY_NODES,(new Boolean(showEmptyNodesCheckBox.isSelected())).toString());
        
        m_document.setProperty(DefaultView.CONTINUOUS_LAYOUT,String.valueOf(ContinuousLayoutCheckBox.isSelected()));
    }//}}}
    
    //{{{ getName()
    
    public String getName() {
        return "defaultview";
    }//}}}
    
    //{{{ getTitle()
    
    public String getTitle() {
        return "Tree View Options";
    }//}}}
        
    //{{{ Private Members
        
    private JCheckBox showCommentsCheckBox;
   // private JCheckBox showEmptyNodesCheckBox;
    private JCheckBox ContinuousLayoutCheckBox;
    
    private DocumentBuffer m_document;
    
    //}}}
        
}//}}}

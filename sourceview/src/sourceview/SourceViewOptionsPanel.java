/*
SourceViewOptionsPanel.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the options panel for the source view

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

package sourceview;

//{{{ Imports

//{{{ jsXe classes

import net.sourceforge.jsxe.ViewPlugin;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.gui.OptionsPanel;

//}}}

//{{{ Java Classes

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JCheckBox;

//}}}

//}}}

public class SourceViewOptionsPanel extends OptionsPanel {
    
    //{{{ SourceViewOptionsPanel constructor
    
    public SourceViewOptionsPanel(DocumentBuffer buffer) {
        
        m_document = buffer;
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        
        setLayout(layout);
        
        int gridY = 0;
        
        boolean softTabs = Boolean.valueOf(m_document.getProperty(SourceView.SOFT_TABS, "false")).booleanValue();
        
        m_m_softTabsCheckBox = new JCheckBox("Soft tabs (emulated with spaces)", softTabs);
        
        constraints.gridy      = gridY++;
        constraints.gridx      = 1;
        constraints.gridheight = 1;
        constraints.gridwidth  = 1;
        constraints.weightx    = 1.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(m_m_softTabsCheckBox, constraints);
        add(m_m_softTabsCheckBox);
    }//}}}
    
    //{{{ getName()
    
    public String getName() {
        return "sourceview";
    }//}}}
    
    //{{{ save()
    
    public void save() {
        m_document.setProperty(SourceView.SOFT_TABS,(new Boolean(m_m_softTabsCheckBox.isSelected())).toString());
    }//}}}
    
    //{{{ getTitle()
    
    public String getTitle() {
        return "Source View Options";
    }//}}}
    
    //{{{ Private Members
    private JCheckBox m_m_softTabsCheckBox;
    private DocumentBuffer m_document;
    //}}}
    
}//}}}
    

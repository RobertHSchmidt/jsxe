/*
StatusBar.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

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

//{{{ Imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.DocumentBufferListener;
import net.sourceforge.jsxe.OperatingSystem;
//}}}

/**
 * The status bar used to display info to the user.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public class StatusBar extends JPanel implements DocumentBufferListener {
    //TODO: use multithreading to make jsXe more responsive and communicate
    //background processing to the user using the status bar
    
    //{{{ StatusBar constructor
    /**
     * Creates a new status bar
     */
    public StatusBar() {
        super(new BorderLayout());
        setBorder(new CompoundBorder(new EmptyBorder(4,0,0,
            (OperatingSystem.isMacOS() ? 18 : 0)),
            UIManager.getBorder("TextField.border")));
        
        m_centerLabel = new JLabel(" ");
        m_leftLabel = new JLabel(" ");
        
        m_mainPanel = new JPanel(new BorderLayout());
        m_mainPanel.add(BorderLayout.CENTER, m_centerLabel);
        m_mainPanel.add(BorderLayout.WEST, m_leftLabel);
        
        m_mainPanel.setBackground(Color.WHITE);
        m_mainPanel.setForeground(Color.BLACK);
        m_centerLabel.setBackground(Color.WHITE);
        m_centerLabel.setForeground(Color.BLACK);
        
        add(BorderLayout.CENTER, m_mainPanel);
    }//}}}
    
    //{{{ setCenterMessage()
    /**
     * Sets the currently displayed message in the center of the status bar.
     * The maximum size of this message is 50 characters.
     */
    public void setCenterMessage(String message) {
        m_centerLabel.setText(message);
    }//}}}
    
    //{{{ getCenterMessage()
    /**
     * Gets the current message being displayed in the center of the status bar
     */
     public String getCenterMessage() {
         return m_centerLabel.getText();
    }//}}}
    
    //{{{ setLeftMessage()
    /**
     * Sets the currently displayed message on the left of the status bar.
     * The maximum size of this message is 25 characters.
     */
    public void setLeftMessage(String message) {
        m_leftLabel.setText(message);
    }//}}}
    
    //{{{ getLeftMessage()
    /**
     * Gets the current message being displayed on the left of the status bar
     */
     public String getLeftMessage() {
         return m_leftLabel.getText();
    }//}}}
    
    //{{{ DocumentBufferListener methods
    
    //{{{ nameChanged()
    public void nameChanged(DocumentBuffer source, String newName) {}//}}}
    
    //{{{ bufferSaved()
    public void bufferSaved(DocumentBuffer source) {
        setCenterMessage(Messages.getMessage("DocumentBuffer.Saved.Message", new String[] { source.getName() }));
    }//}}}
    
    //{{{ statusChanged()
    
    public void statusChanged(DocumentBuffer source, int statusType, boolean oldStatus) {}//}}}
    
    //}}}
    
    //{{{ Private members
    /**
     * The main status bar label
     */
    private JLabel m_centerLabel;
    private JLabel m_leftLabel;
    private JPanel m_mainPanel;
    //}}}
}

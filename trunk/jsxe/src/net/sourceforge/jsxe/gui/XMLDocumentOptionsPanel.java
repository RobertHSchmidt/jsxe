/*
XMLDocumentOptionsPanel.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the XMLDocumentOptionsPanel class that defines
a panel specific to an XMLDocument object in the options dialog.

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
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.XMLDocument;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
//}}}

//{{{ Swing classes
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
//}}}

//{{{ Java base classes
import java.util.Enumeration;
import java.util.Vector;
//}}}

//}}}

public class XMLDocumentOptionsPanel extends OptionsPanel {
    
    public XMLDocumentOptionsPanel(XMLDocument doc) {//{{{
        document = doc;
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        
        //the grid y coordinate.
        int gridY = 0;
        
        setLayout(layout);
        
        //set up the encoding combo-box.
        supportedEncodings.add("US-ASCII");
        supportedEncodings.add("ISO-8859-1");
        supportedEncodings.add("UTF-8");
        supportedEncodings.add("UTF-16BE");
        supportedEncodings.add("UTF-16LE");
        supportedEncodings.add("UTF-16");
        
        JLabel encodingLabel = new JLabel("Encoding:");
        encodingComboBox = new JComboBox(supportedEncodings);
        encodingComboBox.setEditable(false);
        
        Enumeration encodings = supportedEncodings.elements();
        while (encodings.hasMoreElements()) {
            String nextEncoding = (String)encodings.nextElement();
            if (document.getProperty("encoding").equals(nextEncoding)) {
                encodingComboBox.setSelectedItem(nextEncoding);
            }
        }
        
        JLabel indentLabel = new JLabel("Indent width:");
        
        Vector sizes = new Vector(3);
        sizes.add("2");
        sizes.add("4");
        sizes.add("8");
        indentComboBox = new JComboBox(sizes);
        indentComboBox.setEditable(true);
        indentComboBox.setSelectedItem(document.getProperty("indent"));
        
        //set up the whitespace and format output check-boxes.
        boolean whitespace    = Boolean.valueOf(document.getProperty("whitespace-in-element-content", "true")).booleanValue();
        boolean formatOutput = Boolean.valueOf(document.getProperty("format-output", "false")).booleanValue();
        
        whitespaceCheckBox = new JCheckBox("Whitespace in element content", whitespace);
        formatCheckBox     = new JCheckBox("Format XML output", formatOutput);
        
        whitespaceCheckBox.addChangeListener(new WhiteSpaceChangeListener());
        
        formatCheckBox.setEnabled(!whitespace);
        
        constraints.gridy      = gridY;
        constraints.gridx      = 0;
        constraints.gridheight = 1;
        constraints.gridwidth  = 1;
        constraints.weightx    = 1.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(encodingLabel, constraints);
        add(encodingLabel);
        
        constraints.gridy      = gridY++;
        constraints.gridx      = 1;
        constraints.gridheight = 1;
        constraints.gridwidth  = 1;
        constraints.weightx    = 1.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(encodingComboBox, constraints);
        add(encodingComboBox);
        
        constraints.gridy      = gridY;
        constraints.gridx      = 0;
        constraints.gridheight = 1;
        constraints.gridwidth  = 1;
        constraints.weightx    = 1.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(indentLabel, constraints);
        add(indentLabel);
        
        constraints.gridy      = gridY++;
        constraints.gridx      = 1;
        constraints.gridheight = 1;
        constraints.gridwidth  = 1;
        constraints.weightx    = 1.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(indentComboBox, constraints);
        add(indentComboBox);
        
        constraints.gridy      = gridY++;
        constraints.gridx      = 0;
        constraints.gridheight = 1;
        constraints.gridwidth  = GridBagConstraints.REMAINDER;
        constraints.weightx    = 0.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(whitespaceCheckBox, constraints);
        add(whitespaceCheckBox);
        
        constraints.gridy      = gridY++;
        constraints.gridx      = 0;
        constraints.gridheight = 1;
        constraints.gridwidth  = GridBagConstraints.REMAINDER;
        constraints.weightx    = 0.0f;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.insets     = new Insets(1,0,1,0);
        
        layout.setConstraints(formatCheckBox, constraints);
        add(formatCheckBox);
        
    }//}}}
    
    public void saveOptions() {//{{{
        document.setProperty("format-output", (new Boolean(formatCheckBox.isSelected())).toString());
        document.setProperty("whitespace-in-element-content", (new Boolean(whitespaceCheckBox.isSelected())).toString());
        document.setProperty("encoding", encodingComboBox.getSelectedItem().toString());
        try {
            document.setProperty("indent", (new Integer(indentComboBox.getSelectedItem().toString())).toString());
        } catch (NumberFormatException nfe) {
            //Bad input, don't save.
        }
    };//}}}
    
    public String getTitle() {//{{{
        return "XML Document Options";
    };//}}}
    
    private class WhiteSpaceChangeListener implements ChangeListener {//{{{
        
        public void stateChanged(ChangeEvent e) {//{{{
            boolean whitespace = whitespaceCheckBox.isSelected();
            if (whitespace) {
                formatCheckBox.setSelected(false);
            }
            formatCheckBox.setEnabled(!whitespace);
        }//}}}
        
    }//}}}
    
    private XMLDocument document;
    private JComboBox encodingComboBox;
    private JComboBox indentComboBox;
    private JCheckBox whitespaceCheckBox;
    private final Vector supportedEncodings = new Vector(6);
    private JCheckBox formatCheckBox;
}

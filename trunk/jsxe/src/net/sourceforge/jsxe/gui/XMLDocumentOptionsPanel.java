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
        
        setLayout(layout);
        
        constraints.fill = GridBagConstraints.NORTHWEST;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        
       // boolean serializeNamespaces = Boolean.valueOf(currentDoc.getProperty("serialize-namespaces", "false")).booleanValue();
        boolean whitespace    = Boolean.valueOf(document.getProperty("whitespace-in-element-content", "true")).booleanValue();
        boolean formatOutput = Boolean.valueOf(document.getProperty("format-output", "false")).booleanValue();
        
        whitespaceCheckBox = new JCheckBox("Whitespace in element content", whitespace);
        formatCheckBox     = new JCheckBox("Format XML output", formatOutput);
        
        whitespaceCheckBox.addChangeListener(new WhiteSpaceChangeListener());
        
        formatCheckBox.setEnabled(!whitespace);
        
        supportedEncodings.add("US-ASCII");
        supportedEncodings.add("ISO-8859-1");
        supportedEncodings.add("UTF-8");
        supportedEncodings.add("UTF-16BE");
        supportedEncodings.add("UTF-16LE");
        supportedEncodings.add("UTF-16");
        
        encodingComboBox = new JComboBox(supportedEncodings);
        encodingComboBox.setEditable(false);
        
        Enumeration encodings = supportedEncodings.elements();
        while (encodings.hasMoreElements()) {
            String nextEncoding = (String)encodings.nextElement();
            if (document.getProperty("encoding").equals(nextEncoding)) {
                encodingComboBox.setSelectedItem(nextEncoding);
            }
        }
        
        layout.setConstraints(whitespaceCheckBox, constraints);
        add(whitespaceCheckBox);
        layout.setConstraints(formatCheckBox, constraints);
        add(formatCheckBox);
        layout.setConstraints(encodingComboBox, constraints);
        add(encodingComboBox);
        
    }//}}}
    
    public void saveOptions() {//{{{
        document.setProperty("format-output", (new Boolean(formatCheckBox.isSelected())).toString());
        document.setProperty("whitespace-in-element-content", (new Boolean(whitespaceCheckBox.isSelected())).toString());
        document.setProperty("encoding", encodingComboBox.getSelectedItem().toString());
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
    private JCheckBox whitespaceCheckBox;
    private final Vector supportedEncodings = new Vector(6);
    private JCheckBox formatCheckBox;
}

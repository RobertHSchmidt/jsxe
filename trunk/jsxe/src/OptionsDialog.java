/*
OptionsDialog.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the OptionsDialog class that defines the options dialog
in jsXe.

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

//{{{ Swing components
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//}}}

//}}}

public class OptionsDialog extends JDialog {
    
    public OptionsDialog(TabbedView view) {//{{{
        super(view, true);
        
        DocumentPanel panel = view.getDocumentPanel();
        adapter = panel.getDOMAdapter();
        
        //Create components
        JPanel OptionsNorthPanel = new JPanel();
        JPanel OptionsSouthPanel = new JPanel();
        formatCheckBox = new JCheckBox("Format XML Output", (Boolean.valueOf(adapter.getProperty("format.output"))).booleanValue());
        JButton OKButton = new JButton("OK");
        JButton CancelButton = new JButton("Cancel");
        Border border = BorderFactory.createEmptyBorder(10,10,10,10);
        
        //set component options
        OKButton.addActionListener(new OKAction(this));
        CancelButton.addActionListener(new CancelAction(this));
        OptionsNorthPanel.setBorder(border);
        OptionsSouthPanel.setBorder(border);
        setTitle("Options");
        setSize(dialogWidth,dialogHeight);
        setLocationRelativeTo(view);
        
        //add components to build the dialog
        OptionsNorthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        OptionsNorthPanel.add(formatCheckBox);
        OptionsSouthPanel.add(OKButton);
        OptionsSouthPanel.add(CancelButton);
        getContentPane().add(OptionsNorthPanel, BorderLayout.NORTH);
        getContentPane().add(OptionsSouthPanel, BorderLayout.SOUTH);
    } //}}}
    
    private class OKAction implements ActionListener {//{{{
        public OKAction(Dialog p) {
            parent = p;
        }
        public void actionPerformed(ActionEvent e) {
            parent.dispose();
            adapter.setProperty("format.output", (new Boolean(formatCheckBox.isSelected())).toString());
        }
        private Dialog parent;
    } //}}}
    
    private class CancelAction implements ActionListener {//{{{
        public CancelAction(Dialog p) {
            parent = p;
        }
        public void actionPerformed(ActionEvent e) {
            parent.dispose();
        }
        private Dialog parent;
    } //}}}
    
    /*
    *************************************************
    Data Fields
    *************************************************
    *///{{{
    private int dialogWidth=200;
    private int dialogHeight=200;
    private JCheckBox formatCheckBox;
    private DOMAdapter adapter;
    //}}}
}

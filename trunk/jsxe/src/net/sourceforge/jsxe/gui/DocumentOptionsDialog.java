/*
DocumentOptionsDialog.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2005 Trish Harnett (trishah136@member.fsf.org)

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

//{{{ jsXe imports
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Java imports
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
//}}}

//}}}

/**
 * The DocumentOptions class represents the options that are specific to an XML
 * Document. The options on this dialog can be edited on a per document basis.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public class DocumentOptionsDialog extends EnhancedDialog {
    
    //{{{ DocumentOptionsDialog constructor
    /**
     * Creates a new DocumentOptionsDialog
     * @param parent the parent view.
     */
    public DocumentOptionsDialog(TabbedView parent) {
        super(parent, Messages.getMessage("Document.Options.Title"), true);
        m_view = parent;
        DocumentBuffer buffer = m_view.getDocumentBuffer();
        m_optionsPanel = buffer.getOptionsPanel();
        
        JPanel content = new JPanel(new BorderLayout(12,12));
        content.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(content);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        panel.add(createMultilineLabel(Messages.getMessage("Document.Options.Message")), BorderLayout.NORTH);
        panel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
        panel.add(m_optionsPanel, BorderLayout.SOUTH);
        
        getContentPane().add(panel, BorderLayout.CENTER);
        
        Box buttons = new Box(BoxLayout.X_AXIS);
        buttons.add(Box.createGlue());
        
        m_okButton = new JButton(Messages.getMessage("common.ok"));
        m_okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ok();
            }
        });
        buttons.add(m_okButton);
        
        m_cancelButton = new JButton(Messages.getMessage("common.cancel"));
        m_cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancel();
            }
        });
        buttons.add(m_cancelButton);
        
        buttons.add(Box.createGlue());
        
        getContentPane().add(buttons, BorderLayout.SOUTH);
        
        loadGeometry(this, DIALOG_NAME);
        
        setVisible(true);
    }//}}}
    
    //{{{ ok()
    public void ok() {
        m_optionsPanel.save();
        cancel();
    }//}}}
    
    //{{{ cancel()
    public void cancel() {
        saveGeometry(this, DIALOG_NAME);
        dispose();
    }//}}}
    
    //{{{ Private Members
    private static final String DIALOG_NAME = "Document.Options";
    
    //{{{ createMultilineLabel()
	/**
	 * Creates a component that displays a multiple line message. This
	 * is implemented by assembling a number of <code>JLabels</code> in
	 * a <code>JPanel</code>.
	 * @param str The string, with lines delimited by newline
	 * (<code>\n</code>) characters.
	 */
	public static JComponent createMultilineLabel(String str) {
		JPanel panel = new JPanel(new VariableGridLayout(VariableGridLayout.FIXED_NUM_COLUMNS,1,1,1));
		int lastOffset = 0;
		for(;;) {
			int index = str.indexOf('\n',lastOffset);
			if (index == -1) {
				break;
			} else {
				panel.add(new JLabel(str.substring(lastOffset,index)));
				lastOffset = index + 1;
			}
		}

		if (lastOffset != str.length()) {
			panel.add(new JLabel(str.substring(lastOffset)));
        }

		return panel;
	} //}}}
    
    /**
     * The parent view
     */
    private TabbedView m_view;
    private JButton m_okButton;
    private JButton m_cancelButton;
    /**
     * The document options panel for the current document
     */
    private OptionsPanel m_optionsPanel;
    //}}}
}

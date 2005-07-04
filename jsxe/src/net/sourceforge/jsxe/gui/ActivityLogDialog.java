/*
ActivityLog.java
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
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ Java SDK classes
import java.util.*;
import java.io.*;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;
//}}}

//{{{ JSXE classes
import net.sourceforge.jsxe.util.Log;
//}}}

//}}}


/**
 * Dialog box which appears in response to ActivityLOgaction being triggered
 *
 * @author  Trish Hartnett
 * @version $Id$
 */
public class ActivityLogDialog  extends EnhancedDialog{
	
	// Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton OKJButton;
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JLabel iconJLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane activityLogJScrollPane;
    private javax.swing.JLabel messageJLabel;
    private javax.swing.JPanel middleJPanel;
    private javax.swing.JPanel topJPanel;
    // End of variables declaration//GEN-END:variables
	
//	{{{ActivityLogDialog()
    /**
     * @param TabbedView parent view containing the JSXE editor.
     * Constructor for the ActivityLogDialog class
     * @since jsXe 0.3pre15
     */
	public ActivityLogDialog(TabbedView parent) {
		super(parent, Messages.getMessage("ActivityLogDialog.Dialog.Title"), true);
		ArrayList contents = getActivityLogContents();
			
		DefaultListModel contentsJListModel = new DefaultListModel();
		JTextArea newArea = new JTextArea(5, 30);
		for (Iterator it=contents.iterator(); it.hasNext(); ) {
		 	String line = (String)it.next();	
			contentsJListModel.addElement(line);
		}
		JList contentsJList = new JList(contentsJListModel);
		initComponents(contentsJList);
		this.show();
	}//}}}
	
//	{{{ initComponents()
    /**
     * @param JList containing contents of log file
     * Arranges all the components of the GUI
     * @since jsXe 0.3pre15
     */
    private void initComponents(JList contentsJList) {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        topJPanel = new javax.swing.JPanel();
        iconJLabel = new javax.swing.JLabel();
        messageJLabel = new javax.swing.JLabel();
        middleJPanel = new javax.swing.JPanel();
        activityLogJScrollPane = new javax.swing.JScrollPane(contentsJList);
        bottomJPanel = new javax.swing.JPanel();
        OKJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        topJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        iconJLabel.setIcon(new javax.swing.ImageIcon(DirtyFilesDialog.class
				.getResource("/net/sourceforge/jsxe/icons/metal-Inform.png")));
        topJPanel.add(iconJLabel);

        messageJLabel.setText(Messages.getMessage("ActivityLogDialog.Dialog.Message"));
        topJPanel.add(messageJLabel);

        getContentPane().add(topJPanel, java.awt.BorderLayout.NORTH);

        activityLogJScrollPane.setViewportBorder(new javax.swing.border.EtchedBorder());
        activityLogJScrollPane.setAutoscrolls(true);
        activityLogJScrollPane.setPreferredSize(new java.awt.Dimension(350, 250));
        middleJPanel.add(activityLogJScrollPane);

        getContentPane().add(middleJPanel, java.awt.BorderLayout.CENTER);

        OKJButton.setText(Messages.getMessage("ActivityLogDialog.Button.OK.Title"));
        OKJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okayJButtonActionPerformed(evt);
			}
		});
        bottomJPanel.add(OKJButton);

        getContentPane().add(bottomJPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }//}}}
    
	
//	{{{ okayJButtonActionPerformed()
    /**
     * Provides action for clicking on the OK button
     * @since jsXe 0.3pre15
     */
	private void okayJButtonActionPerformed(java.awt.event.ActionEvent evt) {
		Log.log(Log.NOTICE, DirtyFilesDialog.class,
				"91 using the ActivityLogDialog OK button ");
		cancel();
		Log.closeStream();
	}//}}}
	
//	{{{ getActivityLogContents()
    /**
     * Gets contents of the ativity log jsxe.log
     * @return ArrayList containing lines from the activity log
     * @since jsXe 0.3pre15
     */	
	public ArrayList getActivityLogContents(){
		String homeDir = System.getProperty("user.home");
		File activityLog = new File(homeDir+ System.getProperty("file.separator")+".jsxe"+System.getProperty("file.separator")+"jsXe.log");
			
		String line;
		ArrayList logContents = new ArrayList();
		try {
			BufferedReader reader = new BufferedReader( new FileReader(activityLog));			
			try {
				while ((line = reader.readLine()) != null){
					logContents.add(line);
				}
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return logContents;		
	}
	 	
//	{{{ ok()
	public void ok() {
		cancel();
	}//}}}

	//{{{ cancel()
	public void cancel() {
		saveGeometry(this, "pluginmgr");
		dispose();
	}//}}}

}



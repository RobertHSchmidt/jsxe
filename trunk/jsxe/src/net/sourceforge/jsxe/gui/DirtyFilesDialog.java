/*
DirtyFilesDialog.java
:tabSize=4:indentSize=4:noTabs=false:
:folding=explicit:collapseFolds=1:

Copyright (C) 2005 Trish Harnett (trishah136@member.fsf.org)
Portions Copyright (C) 2005 Ian Lewis (IanLewis@member.fsf.org)

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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
//}}}

//{{{ jsXe classes
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.util.Log;
//}}}

//}}}

/**
 * The Dialog box which comes up when user is exiting jsXe and their 
 * are dirty files existing.
 *
 * @author Trish Hartnett
 * @author Ian Lewis
 * @since jsXe 0.4pre2
 * @version $Id$
 */
public class DirtyFilesDialog extends EnhancedDialog {

	private TabbedView newTabbedView;
	private JList dirtyFilesJList;
	private DefaultListModel dirtyFilesJListModel;
	private ArrayList newDirtyBuffers;
	private String[] dirtyFiles;	
	private javax.swing.JPanel bottomJPanel;
	private javax.swing.JButton cancelJButton;
	private javax.swing.JPanel centerJPanel;
	private javax.swing.JButton discardSelectedJButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane;
	private javax.swing.JButton saveSelectedJButton;
	private javax.swing.JButton selectAllJButton;
	private javax.swing.JLabel topJLabel;
	private javax.swing.JPanel topJPanel;	
	private boolean cancelFlag = false;  //if someone hits the cancel button, the exit doesn't go ahead
	private static final String m_geometryName = "dirtyfiles";
	
	 //{{{ getCancelFlag()
    /**
     * Gets the current value of the cancelFlag
     * @return a boolean which stores the current value of cancelFlag
     */
	public boolean  getCancelFlag(){
		return cancelFlag;
	}//}}}
		
	//{{{ setCancelFlag()
    /**
     * Sets the current value of the cancelFlag
     * @param newValue The new value for the cancelFlag
     */
	public void setCancelFlag(boolean newValue){
		cancelFlag = newValue;
	}//}}}

	//{{{ DirtyFilesDialog constructor
    /**
     * Constructor for the DirtyFilesDialog class
     * @param parent TabbedView
     * @param dirtyBuffers ArrayList of dirty buffers
     */
	public DirtyFilesDialog(TabbedView parent, ArrayList dirtyBuffers) {
		super(parent, Messages.getMessage("DirtyFilesDialog.Dialog.Title"), true);
		newTabbedView = parent;
		newDirtyBuffers = dirtyBuffers;
		
		loadGeometry(this, m_geometryName);
		
		dirtyFiles = getDirtyFileNames(dirtyBuffers);
		dirtyFilesJListModel = new DefaultListModel();
		for (int i = 0; i < dirtyFiles.length; i++){
			dirtyFilesJListModel.addElement(dirtyFiles[i]);
		}
		dirtyFilesJList = new JList(dirtyFilesJListModel);
		dirtyFilesJList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		initComponents(dirtyFilesJList);
	}//}}}

	//{{{ getDirtyFileNames()
    /**
     * Gets array of dirty file names from the dirty buffers list
     * @param dirtyBuffers ArrayList of all the dirtyBuffers
     * @return a String array which stores the names 
     *         of dirty files from the dirty buffers list.
     */
	public String[] getDirtyFileNames(ArrayList dirtyBuffers) {
		int size = dirtyBuffers.size();
		String[] dirtyFileNames = new String[size];
		int counter = 0;

		for (Iterator it = dirtyBuffers.iterator(); it.hasNext();) {
			DocumentBuffer db = (DocumentBuffer) it.next();
			String filename = db.getName();
			dirtyFileNames[counter] = filename;
			counter++;
		}
		return dirtyFileNames;
	}//}}}

	//{{{ initComponents()
	/**
     * Inits components for the Dialog box
     * @param dirtyFiles JList containing list of dirty files names
     */
	private void initComponents(JList dirtyFiles) {
		topJPanel = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		topJLabel = new javax.swing.JLabel();
		centerJPanel = new javax.swing.JPanel();
		jScrollPane = new javax.swing.JScrollPane(dirtyFiles);
		bottomJPanel = new javax.swing.JPanel();
		selectAllJButton = new javax.swing.JButton();
		saveSelectedJButton = new javax.swing.JButton();
		discardSelectedJButton = new javax.swing.JButton();
		cancelJButton = new javax.swing.JButton();

		jLabel1.setIcon(new javax.swing.ImageIcon(DirtyFilesDialog.class
				.getResource("/net/sourceforge/jsxe/icons/metal-Warn.png")));
		topJPanel.add(jLabel1);

		topJLabel.setText(Messages.getMessage("DirtyFilesDialog.Dialog.Message"));
		topJPanel.add(topJLabel);

		getContentPane().add(topJPanel, java.awt.BorderLayout.NORTH);

		jScrollPane.setPreferredSize(new java.awt.Dimension(300, 40));
		centerJPanel.add(jScrollPane);

		getContentPane().add(centerJPanel, java.awt.BorderLayout.CENTER);

		selectAllJButton.setText(Messages.getMessage("DirtyFilesDialog.Button.SelectAll.Title"));
	//	selectAllJButton.setToolTipText(Messages.getMessage("DirtyFilesDialog.Button.SelectAll.ToolTip"));
		selectAllJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectAllJButtonActionPerformed(evt);
			}
		});
		bottomJPanel.add(selectAllJButton);

		saveSelectedJButton.setText(Messages.getMessage("DirtyFilesDialog.Button.SaveSelected.Title"));
	//	saveSelectedJButton.setToolTipText(Messages.getMessage("DirtyFilesDialog.Button.SaveSelected.ToolTip"));
		saveSelectedJButton	.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveSelectedJButtonActionPerformed(evt);
			}
		});
		bottomJPanel.add(saveSelectedJButton);

		discardSelectedJButton.setText(Messages.getMessage("DirtyFilesDialog.Button.DiscardSelected.Title"));
	//	discardSelectedJButton.setToolTipText(Messages.getMessage("DirtyFilesDialog.Button.DiscardSelected.ToolTip"));
		
		discardSelectedJButton
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						discardSelectedJButtonActionPerformed(evt);
					}
				});
		bottomJPanel.add(discardSelectedJButton);

		cancelJButton.setText(Messages.getMessage("common.cancel"));
	//	cancelJButton.setToolTipText(Messages.getMessage("common.cancel"));
		cancelJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelJButtonActionPerformed(evt);
			}
		});
		bottomJPanel.add(cancelJButton);

		getContentPane().add(bottomJPanel, java.awt.BorderLayout.SOUTH);

		pack();
		setVisible(true);
	}//}}}

	//{{{ selectAllJButtonActionPerformed()
	/**
     * Does actions for clicking on the Select All JButton
     * @param evt ActionEvent of user clicking on Select All
     */
	private void selectAllJButtonActionPerformed(ActionEvent evt) {
		int size = dirtyFilesJListModel.getSize();
		int [] selectedIndices = new int[size];
		for(int i= 0; i< size; i++){
			selectedIndices[i] = i;
		}
		dirtyFilesJList.setSelectedIndices(selectedIndices);
		dirtyFilesJList.repaint();
	}//}}}

	//{{{ saveSelectedJButtonActionPerformed()
	/**
     * Does actions for clicking on the Save Selected JButton
     * @param evt ActionEvent of user clicking on Save Selected JButton
     */
	private void saveSelectedJButtonActionPerformed(ActionEvent evt) {
		try {
			Object[] selected = dirtyFilesJList.getSelectedValues();
			int counter = 0;
			boolean closeSuccessful = true;
			
			for (Iterator it = newDirtyBuffers.iterator(); it.hasNext() && closeSuccessful;) {
				DocumentBuffer db = (DocumentBuffer) it.next();
				String unsavedName = db.getName();
				for (int i = 0; i < selected.length; i++) {
					//match the relevant unsaved file name from the list with it's buffer
					if (selected[i].equals(unsavedName)) {
						if (db.save(newTabbedView)) {
							closeSuccessful = jsXe.closeDocumentBuffer(newTabbedView, db, false);
							if (closeSuccessful) {
							//	Log.log(Log.NOTICE, DirtyFilesDialog.class,
							//			"211 About to remove " + unsavedName
							//			+ " from the list of unsaved files");
								//removeUnsavedFileFromList(counter, newDirtyBuffers
								//.size(), unsavedName);
								removeUnsavedFileFromList(unsavedName);
								removeUnsavedFileFromDirtyBufferList(unsavedName);
							}
						}
					}
					counter++;
				}
			}
			if(selected.length == (dirtyFiles.length+1)){
				ok();
			}
		} catch (IOException e) {
			Log.log(Log.ERROR, this, e);
			JOptionPane.showMessageDialog(newTabbedView, e.getMessage(), Messages.getMessage("IO.Error.Title"), JOptionPane.WARNING_MESSAGE);
		}
	}//}}}

	//{{{ discardSelectedJButtonActionPerformed()
	/**
     * Does actions for clicking on the Discard Selected JButton
     * @param evt ActionEvent of user clicking on Discard Selected JButton
     */
	private void discardSelectedJButtonActionPerformed(ActionEvent evt) {
			
		boolean allDiscardFlag = false;
		
		Object[] selected = dirtyFilesJList.getSelectedValues();
		
		if (selected.length == dirtyFiles.length){allDiscardFlag = true;}

		for (int i = 0; i < selected.length ; i++) {
			test:
				for (Iterator it = newDirtyBuffers.iterator(); it.hasNext() && selected[i]!=null;) {
				DocumentBuffer db = (DocumentBuffer) it.next();
				String unsavedName = db.getName();	
				
				//match the relevant unsaved file name from the list with it's buffer
				if (selected[i].equals(unsavedName)) {					
					//removeUnsavedFileFromList(counter, newDirtyBuffers.size(),
					//		unsavedName);
					try {
						if (jsXe.closeDocumentBuffer(newTabbedView, db, false)) {
							removeUnsavedFileFromList(unsavedName);
							removeUnsavedFileFromDirtyBufferList(unsavedName);
						}
					} catch (IOException e) {
						Log.log(Log.ERROR, this, e);
						JOptionPane.showMessageDialog(newTabbedView, e.getMessage(), Messages.getMessage("IO.Error.Title"), JOptionPane.WARNING_MESSAGE);
					}
					continue test;
				}
			}
		}
		
		if(selected.length == (dirtyFiles.length+1)){
			ok();
		}
		if(allDiscardFlag == true){
			ok();
		}
	}//}}}

	//{{{ cancelJButtonActionPerformed()
	/**
     * Does actions for clicking on the Cancel JButton
     * @param evt ActionEvent of user clicking on Cancel JButton
     */ 
	private void cancelJButtonActionPerformed(ActionEvent evt) {
	//	Log.log(Log.NOTICE, DirtyFilesDialog.class,
	//			"351 using the dirtyFilesDialog cancel button ");
		setCancelFlag(true);
		cancel();
	}//}}}

	//{{{ removeUnsavedFileFromList()
	/**
     * Removes dirty files from the JList
     * @param name name of file selected by the user
     */ 
	public void removeUnsavedFileFromList(String name) {

		//have to remove the element from the dirtyFilesJList in the
		//dirtyFilesDialog box.
		ArrayList updatedDirtyFilesList = new ArrayList();
	//	Log.log(Log.NOTICE, DirtyFilesDialog.class, "368 Removeing " + name
	//			+ " from the list of unsaved files");
		for (int i = 0; i < dirtyFiles.length; i++) {
			//go through each element in the dirty files names list
			//for each element, 
			//	if the file name is not the same as the filename selected by the user,
			//	add it to a new list of unsaved dirty filenames

		//	Log.log(Log.NOTICE, DirtyFilesDialog.class, "376 DirtyFiles length: "
		//			+ dirtyFiles.length
		//			+ " current DirtyFile checking against: " + dirtyFiles[i]);

			if (dirtyFiles[i]!=null && dirtyFiles[i].equals(name)) {
			//	Log.log(Log.NOTICE, DirtyFilesDialog.class,
			//			"382 i: "+i+" DirtyFile: " + dirtyFiles[i]
			//					+ " matches newly saved File: " + name+", so the filename ("+name+") can be removed from the JList");
				
				try{
					int sizeListModel =dirtyFilesJListModel.getSize();
				//	Log.log(Log.NOTICE, DirtyFilesDialog.class,
				//			"388 sizeListModel: "+sizeListModel);				
					if(i==sizeListModel){
						i = sizeListModel-1;
					}
					if(sizeListModel != 0){
						dirtyFilesJListModel.removeElementAt(i);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					//some times selectedRows() return false number and it cause exception
					Log.log(Log.ERROR, this, e);
				}
				dirtyFilesJList.repaint();
				jScrollPane.repaint();
			}
		}
	}//}}}

	//{{{ removeUnsavedFileFromDirtyBufferList()
	/**
     * Removes dirty files from the dirty buffers list
     * @param unsavedName name of file selected by the user
     */ 
	public void removeUnsavedFileFromDirtyBufferList(String unsavedName){
		String[] newDirtyFilesList = new String[dirtyFiles.length];
		
		ArrayList test = new ArrayList();
	
		for (int i = 0; i < dirtyFiles.length; i++) {
			//go through each element in the dirty files names list
			//for each element, 
			//	if the file name is the same as the filename selected by the user,
			//	remove it from the list of unsaved dirty filenames
			
		//	Log.log(Log.NOTICE, DirtyFilesDialog.class,
		//		"413 Checking for match, remove discarded dirty file from List " 
		//		+ dirtyFiles[i]
		//		+ " , unsavedName: "+ unsavedName);
			if (dirtyFiles[i]!=null && !dirtyFiles[i].equals(unsavedName)) {
			//	Log.log(Log.NOTICE, DirtyFilesDialog.class,
			//			"418 new dirty files list - new element added:  " 
			//			+ dirtyFiles[i]);	
				test.add(dirtyFiles[i]);
			}			
		}
				
		Object[] testArrayVersion = test.toArray();
		String[] tempArray = new String[test.size()];
		
		for (int i = 0; i< test.size(); i++ ) {
	        tempArray[i] = (String)testArrayVersion[i];
	    }
	//	Log.log(Log.NOTICE, DirtyFilesDialog.class,
	//			"431 new dirty files list length: "+ tempArray.length);
		dirtyFiles = tempArray;		
	}//}}}
	
	//{{{ populateDirtyFileList()
	/**
     * Populates the JList with the names of files from the dirty buffers list
     * @param dirtyList ArrayList containing names of dirty files
     * @param model DefaultListModel for the ArrayList
     */ 
	public void populateDirtyFileList(ArrayList dirtyList, DefaultListModel model) {	
		 for (Iterator it=dirtyList.iterator(); it.hasNext(); ) {
		 	String filename = (String) it.next();
		 	model.addElement(filename);
		 }
	}//}}}

	//{{{ ok()
	public void ok() {
		cancel();
	}//}}}

	//{{{ cancel()
	public void cancel() {
		saveGeometry(this, m_geometryName);
		dispose();
	}//}}}
}

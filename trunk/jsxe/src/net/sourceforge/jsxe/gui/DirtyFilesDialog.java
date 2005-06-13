/*
 DirtyFilesDialog.java
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

import javax.swing.DefaultListModel;
import javax.swing.JList;
//}}}

//{{{ jsXe classes
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.util.Log;
//}}}

//}}}


/**
 * The Dialog box which comes up when user is exiting jsXE and their 
 * are dirty files existing.
 *
 * @author  Trish Hartnett
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

	 //{{{ getCancelFlag()
    /**
     * Gets the current value of the cancelFlag
     * @return a boolean which stores the current value of cancelFlag
     * @since jsXe 0.3pre15
     */
	public boolean  getCancelFlag(){
		return cancelFlag;
	}//}}}
		
	 //{{{ setCancelFlag()
    /**
     * Sets the current value of the cancelFlag
     * @param newValue The new value for the cancelFlag
     * @return void
     * @since jsXe 0.3pre15
     */
	public void setCancelFlag(boolean newValue){
		cancelFlag = newValue;
	}//}}}
		
   //	{{{ DirtyFilesDialog()
    /**
     * Constructor for the DirtyFilesDialog class
     * @param parent TabbedView
     * @param dirtyBuffers ArrayList of dirty buffers
     * @since jsXe 0.3pre15
     */
	public DirtyFilesDialog(TabbedView parent, ArrayList dirtyBuffers) {
		super(parent, Messages.getMessage("Dirty.Files.Title"), true);
		newTabbedView = parent;
		newDirtyBuffers = dirtyBuffers;
		setLocationRelativeTo(parent);

		dirtyFiles = getDirtyFileNames(dirtyBuffers);
		dirtyFilesJListModel = new DefaultListModel();
		for (int i = 0; i < dirtyFiles.length; i++){
			dirtyFilesJListModel.addElement(dirtyFiles[i]);
		}
		dirtyFilesJList = new JList(dirtyFilesJListModel);
		dirtyFilesJList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		initComponents(dirtyFilesJList);

		boolean debug = false;
		//      {{{ start logging
		Log.init(true, Log.ERROR, debug);
		try {
			//        	{{{ set settings dirs
			String homeDir = System.getProperty("user.home");
			String fileSep = System.getProperty("file.separator");

			String settingsDirectory = homeDir + fileSep + ".jsxe";
			BufferedWriter stream = new BufferedWriter(new FileWriter(new File(
					settingsDirectory + fileSep + "jsXe.log")));

			stream.write("Log file created on " + new Date());
			stream.write(System.getProperty("line.separator"));

			Log.setLogWriter(stream);
		} catch (IOException ioe) {
			Log.log(Log.ERROR, jsXe.class, ioe);
		}//}}};
	}//}}}
	
   //	{{{ getDirtyFileNames()
    /**
     * Gets array of dirty file names from the dirty buffers list
     * @param dirtyBuffers ArrayList of all the dirtyBuffers
     * @return a String array which stores the names 
     *         of dirty files from the dirty buffers list.
     * @since jsXe 0.3pre15
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

	 //	{{{ initComponents()
	/**
     * Inits components for the Dialog box
     * @param dirtyFiles JList containing list of dirty files names
     * @since jsXe 0.3pre15
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

		this.setTitle("Unsaved Changes");
		this.setName("unsavedChangesDialog");

		//setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jLabel1.setIcon(new javax.swing.ImageIcon(DirtyFilesDialog.class
				.getResource("/net/sourceforge/jsxe/icons/metal-Warn.png")));
		topJPanel.add(jLabel1);

		topJLabel.setText("The following files have unsaved changes: ");
		topJPanel.add(topJLabel);

		getContentPane().add(topJPanel, java.awt.BorderLayout.NORTH);

		jScrollPane.setPreferredSize(new java.awt.Dimension(300, 40));
		centerJPanel.add(jScrollPane);

		getContentPane().add(centerJPanel, java.awt.BorderLayout.CENTER);

		selectAllJButton.setText("Select All");
		selectAllJButton.setToolTipText("Select All");
		selectAllJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectAllJButtonActionPerformed(evt);
			}
		});
		bottomJPanel.add(selectAllJButton);

		saveSelectedJButton.setText("Save Selected");
		saveSelectedJButton.setToolTipText("Save Selected");
		saveSelectedJButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveSelectedJButtonActionPerformed(evt);
					}
				});
		bottomJPanel.add(saveSelectedJButton);

		discardSelectedJButton.setText("Discard Selected");
		discardSelectedJButton.setToolTipText("Discard Selected");
		
		discardSelectedJButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						discardSelectedJButtonActionPerformed(evt);
					}
				});
		bottomJPanel.add(discardSelectedJButton);

		cancelJButton.setText("Cancel");
		cancelJButton.setToolTipText("Cancel");
		cancelJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelJButtonActionPerformed(evt);
			}
		});
		bottomJPanel.add(cancelJButton);

		getContentPane().add(bottomJPanel, java.awt.BorderLayout.SOUTH);

		pack();
		setVisible(true);
	}//}}}

	 //	{{{ selectAllJButtonActionPerformed()
	/**
     * Does actions for clicking on the Select All JButton
     * @param evt ActionEvent of user clicking on Select All
     * @since jsXe 0.3pre15
     */
	private void selectAllJButtonActionPerformed(java.awt.event.ActionEvent evt) {
		int size = dirtyFilesJListModel.getSize();
		int [] selectedIndices = new int[size];
		for(int i= 0; i< size; i++){
			selectedIndices[i] = i;
		}
		dirtyFilesJList.setSelectedIndices(selectedIndices);
		dirtyFilesJList.repaint();
	}//}}}

	
	 //	{{{ saveSelectedJButtonActionPerformed()
	/**
     * Does actions for clicking on the Save Selected JButton
     * @param evt ActionEvent of user clicking on Save Selected JButton
     * @since jsXe 0.3pre15
     */
	private void saveSelectedJButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
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
						closeSuccessful = jsXe.closeDocumentBuffer(newTabbedView, db, false);
						if(closeSuccessful){
							Log.log(Log.NOTICE, DirtyFilesDialog.class,
									"211 About to remove " + unsavedName
									+ " from the list of unsaved files");
							//removeUnsavedFileFromList(counter, newDirtyBuffers
							//.size(), unsavedName);
							removeUnsavedFileFromList(unsavedName);
						}								
					}
					counter++;
				}
			}
			if(closeSuccessful){this.dispose();}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.closeStream();
	}//}}}

	 //	{{{ discardSelectedJButtonActionPerformed()
	/**
     * Does actions for clicking on the Discard Selected JButton
     * @param evt ActionEvent of user clicking on Discard Selected JButton
     * @since jsXe 0.3pre15
     */
	private void discardSelectedJButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
			
		boolean allDiscardFlag = false;
		
		Object[] selected = dirtyFilesJList.getSelectedValues();
		
		if(selected.length == dirtyFiles.length){allDiscardFlag = true;}

		for (int i = 0; i < selected.length ; i++) {
			test:
				for (Iterator it = newDirtyBuffers.iterator(); it.hasNext() && selected[i]!=null;) {
				DocumentBuffer db = (DocumentBuffer) it.next();
				String unsavedName = db.getName();	
				
				//match the relevant unsaved file name from the list with it's buffer
				if (selected[i].equals(unsavedName)) {					
					//removeUnsavedFileFromList(counter, newDirtyBuffers.size(),
					//		unsavedName);
					removeUnsavedFileFromList(unsavedName);
					removeUnsavedFileFromDirtyBufferList(unsavedName);				
					continue test;
				}
			}
		}
		
		if(selected.length == (dirtyFiles.length+1)){
			dispose();
		}
		if(allDiscardFlag == true){
			dispose();
		}
		Log.closeStream();
	}//}}}

   //	{{{  cancelJButtonActionPerformed()
	/**
     * Does actions for clicking on the Cancel JButton
     * @param evt ActionEvent of user clicking on Cancel JButton
     * @since jsXe 0.3pre15
     */ 
	private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {
		Log.log(Log.NOTICE, DirtyFilesDialog.class,
				"348 using the dirtyFilesDialog cancel button ");
		setCancelFlag(true);
		dispose();
		Log.closeStream();
	}//}}}

	 //	{{{ removeUnsavedFileFromList()
	/**
     * Removes dirty files from the JList
     * @param name name of file selected bu the user
     * @since jsXe 0.3pre15
     */ 
	public void removeUnsavedFileFromList(String name) {

		//have to remove the element from the dirtyFilesJList in the
		//dirtyFilesDialog box.
		ArrayList updatedDirtyFilesList = new ArrayList();
		Log.log(Log.NOTICE, DirtyFilesDialog.class, "366 Removeing " + name
				+ " from the list of unsaved files");
		for (int i = 0; i < dirtyFiles.length; i++) {
			//go through each element in the dirty files names list
			//for each element, 
			//	if the file name is not the same as the filename selected by the user,
			//	add it to a new list of unsaved dirty filenames

			Log.log(Log.NOTICE, DirtyFilesDialog.class, "374 DirtyFiles length: "
					+ dirtyFiles.length
					+ " current DirtyFile checking against: " + dirtyFiles[i]);

			if (dirtyFiles[i]!=null && dirtyFiles[i].equals(name)) {
				Log.log(Log.NOTICE, DirtyFilesDialog.class,
						"380 current DirtyFile: " + dirtyFiles[i]
								+ " doesn't match newly saved File: " + name+" so it remains in JList");
				dirtyFilesJListModel.removeElementAt(i);
//				DefaultListModel newModel =(DefaultListModel)dirtyFilesJList.getModel();
//				updatedDirtyFilesList.add(dirtyFiles[i]);
//				// repaint the JScrollPane with the new ArrayList
//				populateDirtyFileList(updatedDirtyFilesList, newModel);
				dirtyFilesJList.repaint();
				dirtyFilesJList.revalidate();
				jScrollPane.repaint();
				}
		}
	}//}}}

	 //	{{{ removeUnsavedFileFromDirtyBufferList()
	/**
     * Removes dirty files from the dirty buffers list
     * @param unsavedName name of file selected bu the user
     * @since jsXe 0.3pre15
     */ 
	public void removeUnsavedFileFromDirtyBufferList(String unsavedName){
		String[] newDirtyFilesList = new String[dirtyFiles.length];
		
		ArrayList test = new ArrayList();
	
		for (int i = 0; i < dirtyFiles.length; i++) {
			//go through each element in the dirty files names list
			//for each element, 
			//	if the file name is the same as the filename selected by the user,
			//	remove it from the list of unsaved dirty filenames
			
			Log.log(Log.NOTICE, DirtyFilesDialog.class,
					"413 Checking for match, remove discarded dirty file from List " 
					+ dirtyFiles[i]
				    + " , unsavedName: "+ unsavedName);
			if (dirtyFiles[i]!=null && !dirtyFiles[i].equals(unsavedName)) {
				Log.log(Log.NOTICE, DirtyFilesDialog.class,
						"418 new dirty files list - new element added:  " 
						+ dirtyFiles[i]);	
				test.add(dirtyFiles[i]);
			}			
		}
				
		Object[] testArrayVersion = test.toArray();
		String[] tempArray = new String[test.size()];
		
		for (int i = 0; i< test.size(); i++ ) {
	        tempArray[i] = (String)testArrayVersion[i];
	    }
		Log.log(Log.NOTICE, DirtyFilesDialog.class,
				"431 new dirty files list length: "+ tempArray.length);
		dirtyFiles = tempArray;		
	}//}}}
	
	 //	{{{ populateDirtyFileList()
	/**
     * Populates the JList with the names of files from the dirty buffers list
     * @param dirtyList ArrayList containing names of dirty files
     * @param model DefaultListModel for the ArrayList
     * @since jsXe 0.3pre15
     */ 
	public void populateDirtyFileList(ArrayList dirtyList, DefaultListModel model) {	
		 for (Iterator it=dirtyList.iterator(); it.hasNext(); ) {
		 	String filename = (String) it.next();
		 	model.addElement(filename);
		 }
	}//}}}

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

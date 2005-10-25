/*
ProgressSplashScreenWindow.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 1998, 1999, 2001 Trish Hartnett
Portions Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)

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


import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import net.sourceforge.jsxe.util.Log;

/**
 * A splashscreen that popups when the jsxe application starts
 * @author Trish Hartnett (<a href="mailto:trishah136@users.sourceforge.net">trishah136@users.sourceforge.net</a>)
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class ProgressSplashScreenWindow extends JWindow {	
	private JProgressBar progressBar;
	private JTextArea taskOutput;
	private JScrollPane outputScroll;
	private JLabel imageLabel;

	 //{{{ ProgressSplashScreenWindow constructor
	public ProgressSplashScreenWindow() {
		initComponents();
		setSize(200, 200);	
		updateSplashScreenDialog(0); // set status to 1 initially
	}
	//	}}}

	 //{{{ initComponents()
    /**
     * Initializes the components for the splash screen.
     *
     */
	public void initComponents() {
		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imageLabel.setVerticalAlignment(SwingConstants.TOP);
		imageLabel.setIcon(new javax.swing.ImageIcon(
				ProgressSplashScreenWindow.class
						.getResource("/net/sourceforge/jsxe/icons/jsxeBig.jpg")));

		JPanel middlePanel = new JPanel();
		int barLength = 100; 
		progressBar = new JProgressBar(0, barLength);
		progressBar.setSize(100, 20);
		progressBar.setValue(0);
		middlePanel.setLayout(new BorderLayout());
		middlePanel.add(progressBar, java.awt.BorderLayout.CENTER);

		taskOutput = new JTextArea(1, 20);
		taskOutput.setMargin(new Insets(5, 5, 5, 5));
		taskOutput.setEditable(false);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(imageLabel, java.awt.BorderLayout.NORTH);
		contentPane.add(middlePanel, java.awt.BorderLayout.CENTER);
		contentPane.add(taskOutput, java.awt.BorderLayout.SOUTH);
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
	}
	//	}}}

	 //{{{ updateSplashScreenDialog()
    /**
     * Updates the progress bar displayed by the splashscreen
     *
     * @param progress The new progress setting for the progress bar. if progress is 4, splashscreen disposes itself.
     */
	public void updateSplashScreenDialog(int progress) {
		updateProgessBar(progress);
		updateTextArea(progress);
	//	try {
	//		Thread.sleep(1000);
	//	} catch (InterruptedException e) {
	//		e.printStackTrace();
	//	}
	}
	//	}}}

	 //{{{ updateProgessBar()
    /**
     * Updates the progress bar displayed by the splashscreen
     *
     * @param percentage Updates percentage completed of progressbar
     */
	public void updateProgessBar(int percentage) {		
		progressBar.setValue(percentage);
	}
	//	}}}
	
	 //{{{ updateTextArea()
    /**
     * Updates the progress bar displayed by the splashscreen
     *
     * @param percentage Updates message displayed in text area.
     */
	public void updateTextArea(int percentage) {;
		String message = "Completed " + percentage + "%";
		taskOutput.setText(message);
		taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
	}
	//	}}}

}

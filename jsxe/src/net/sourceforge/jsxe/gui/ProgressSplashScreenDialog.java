/*
 * Created on 19-Aug-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.jsxe.gui;

/*
 //Where member variables are declared:
 JProgressBar progressBar;
 //Where the GUI is constructed:
 progressBar = new JProgressBar(0, task.getLengthOfTask());
 progressBar.setValue(0);
 progressBar.setStringPainted(true);

 */

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
 * @author Trish Hartnett
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
//public class ProgressSplashScreenDialog extends EnhancedDialog {
public class ProgressSplashScreenDialog extends JWindow {	
	private JProgressBar progressBar;
	private JTextArea taskOutput;
	private JScrollPane outputScroll;
	private JLabel imageLabel;
	//private String newline = "\n";
	private int lengthOfTask = 0;

	public ProgressSplashScreenDialog(TabbedView parent,
			int lengthOfTaskVariable) {
		/*
		 * super(parent, Messages.getMessage("ProgressSplashScreenDialog.Title"),
				false);
		 */
		lengthOfTask = lengthOfTaskVariable;
		initComponents();
		setSize(200, 200);	
		

		//start logging here
		String homeDir = System.getProperty("user.home");
        String fileSep = System.getProperty("file.separator");
        
        String settingsDirectory = homeDir+fileSep+".jsxe";
        boolean debug = false;
        
		Log.init(true, Log.ERROR, debug);

		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter(new File(
					settingsDirectory + fileSep + "jsXe.log")));
			stream.flush();
			stream.write("Log file created on " + new Date());
			stream.write(System.getProperty("line.separator"));

			Log.setLogWriter(stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateSplashScreenDialog(1); // set status to 1 initially
	}

	public void initComponents() {
		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imageLabel.setVerticalAlignment(SwingConstants.TOP);
		imageLabel.setIcon(new javax.swing.ImageIcon(
				ProgressSplashScreenDialog.class
						.getResource("/net/sourceforge/jsxe/icons/jsxeBig.jpg")));
		//imageLabel.setSize(70, 75);
		
		JPanel middlePanel = new JPanel();
		int barLength = 100; 
		progressBar = new JProgressBar(0, barLength);
		progressBar.setSize(100, 20);
		progressBar.setValue(0);
		//progressBar.setStringPainted(true);
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

	//	{{{ ok()
	//public void ok() {
	//	cancel();
	//}//}}}

	//{{{ cancel()
	//public void cancel() {
	//	saveGeometry(this, "pluginmgr");
	//	dispose();
	//}//}}}

	public void updateSplashScreenDialog(int progress) {
		Log.log(Log.MESSAGE, ProgressSplashScreenDialog.class, "line 119 updating progress screen, new progress: "+ progress);
		int percentage = progress * 100/4 ;
		updateProgessBar(percentage);
		updateTextArea(percentage);			
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(progress == 4){
			this.dispose();
		}
	}

	public void updateProgessBar(int percentage) {		
		Log.log(Log.MESSAGE, ProgressSplashScreenDialog.class, "line 135 updating progress bar, new progress: "+ percentage);
		progressBar.setValue(percentage);
	}

	public void updateTextArea(int percentage) {;
		String message = "Completed " + percentage + "%";
		//taskOutput.append(message + newline);
		taskOutput.setText(message);
		taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
		Log.log(Log.MESSAGE, ProgressSplashScreenDialog.class, "line 143 updating text area, progress: "+percentage+", percentage: "+percentage+", text: "+ taskOutput.getText()+", message: "+message);
	}

}

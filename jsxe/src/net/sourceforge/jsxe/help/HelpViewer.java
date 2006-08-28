/*
HelpViewer.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 1999, 2002 Slava Pestov
Portions Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.help;

//{{{ Imports

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.gui.RolloverButton;
import net.sourceforge.jsxe.gui.GUIUtilities;
//}}}

//{{{ Swing classes
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
//}}}

//{{{ AWT classes
import java.awt.*;
import java.awt.event.*;
//}}}

//{{{ Java classes
import java.beans.*;
import java.io.*;
import java.net.*;
//}}}

//}}}

/**
 * jsXe's searchable help viewer. It uses a Swing JEditorPane to display the HTML,
 * and implements a URL history.
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre4
 */
public class HelpViewer extends JFrame {
    
    //{{{ HelpViewer constructor
    /**
     * Creates a new help viewer with the default help page.
     */
    public HelpViewer() {
        this("welcome.html");
    } //}}}

    //{{{ HelpViewer constructor
    /**
     * Creates a new help viewer for the specified URL.
     * @param url The URL
     */
    public HelpViewer(URL url)
    {
        this(url.toString());
    } //}}}

    //{{{ HelpViewer constructor
    /**
     * Creates a new help viewer for the specified URL.
     * @param url The URL
     */
    public HelpViewer(String url) {
        super(Messages.getMessage("helpviewer.title"));
        
        setIconImage(jsXe.getIcon().getImage());
        
        try {
            baseURL = new File(MiscUtilities.constructPath(jsXe.getInstallDirectory(),"doc")).toURL().toString();
        } catch(MalformedURLException mu) {
            Log.log(Log.ERROR,this,mu);
            // what to do?
        }

        history = new String[25];

        ActionHandler actionListener = new ActionHandler();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(Messages.getMessage("helpviewer.toc.label"), toc = new HelpTOCPanel(this));
        tabs.addTab(Messages.getMessage("helpviewer.search.label"), new HelpSearchPanel(this));
        tabs.setMinimumSize(new Dimension(0,0));

        JPanel rightPanel = new JPanel(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(title = new JLabel());
        toolBar.add(Box.createGlue());

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
        buttons.setBorder(new EmptyBorder(0,12,0,0));
        back = new RolloverButton(GUIUtilities.loadIcon(jsXe.getProperty("helpviewer.back.icon")));
        back.setToolTipText(Messages.getMessage("helpviewer.back.label"));
        back.addActionListener(actionListener);
        toolBar.add(back);
        forward = new RolloverButton(GUIUtilities.loadIcon(jsXe.getProperty("helpviewer.forward.icon")));
        forward.addActionListener(actionListener);
        forward.setToolTipText(Messages.getMessage("helpviewer.forward.label"));
        toolBar.add(forward);
        back.setPreferredSize(forward.getPreferredSize());

        rightPanel.add(BorderLayout.NORTH,toolBar);

        viewer = new JEditorPane();
        viewer.setEditable(false);
        viewer.addHyperlinkListener(new LinkHandler());
        viewer.setFont(new Font("Monospaced",Font.PLAIN,12));
        viewer.addPropertyChangeListener(new PropertyChangeHandler());

        rightPanel.add(BorderLayout.CENTER,new JScrollPane(viewer));

        splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            tabs,rightPanel);
        splitter.setBorder(null);

        getContentPane().add(BorderLayout.CENTER,splitter);

        gotoURL(url,true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getRootPane().setPreferredSize(new Dimension(750,500));

        pack();
        GUIUtilities.loadGeometry(this,"helpviewer");

       // EditBus.addToBus(this);

        setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                splitter.setDividerLocation(jsXe.getIntegerProperty("helpviewer.splitter",250));
                viewer.requestFocus();
            }
        });
    } //}}}

    //{{{ gotoURL() method
    /**
     * Displays the specified URL in the HTML component.
     * @param url The URL
     * @param addToHistory Should the URL be added to the back/forward
     * history?
     */
    public void gotoURL(String url, boolean addToHistory) {
        
        // the TOC pane looks up user's guide URLs relative to the
        // doc directory...
        String shortURL;
        if (MiscUtilities.isURL(url)) {
            if(url.startsWith(baseURL)) {
                shortURL = url.substring(baseURL.length());
                if (shortURL.startsWith("/")) {
                    shortURL = shortURL.substring(1);
                }
            } else {
                shortURL = url;
            }
        } else {
            shortURL = url;
            if (baseURL.endsWith("/")) {
                url = baseURL + url;
            } else {
                url = baseURL + '/' + url;
            }
        }

        // reset default cursor so that the hand cursor doesn't
        // stick around
        viewer.setCursor(Cursor.getDefaultCursor());

        URL _url = null;
        try {
            _url = new URL(url);

            if (!_url.equals(viewer.getPage())) {
                title.setText(Messages.getMessage("helpviewer.loading"));
            } else {
                /* don't show loading msg because we won't
                   receive a propertyChanged */
            }

            viewer.setPage(_url);
            if (addToHistory) {
                history[historyPos] = url;
                if (historyPos + 1 == history.length) {
                    System.arraycopy(history,1,history,0,history.length - 1);
                    history[historyPos] = null;
                } else {
                    historyPos++;
                }
            }
        } catch(MalformedURLException mf) {
            Log.log(Log.ERROR,this,mf);
            String[] args = { url, mf.getMessage() };
            GUIUtilities.error(this,"badurl",args);
            return;
        } catch(IOException io) {
            Log.log(Log.ERROR,this,io);
            String[] args = { url, io.toString() };
            GUIUtilities.error(this,"read-error",args);
            return;
        }

        this.shortURL = shortURL;

        // select the appropriate tree node.
        if (shortURL != null) {
            toc.selectNode(shortURL);
        }
    } //}}}

    //{{{ dispose() method
    public void dispose() {
       // EditBus.removeFromBus(this);
        jsXe.setIntegerProperty("helpviewer.splitter", splitter.getDividerLocation());
        GUIUtilities.saveGeometry(this,"helpviewer");
        super.dispose();
    } //}}}

    //{{{ handleMessage() method
    public void handleMessage(EBMessage msg)
    {
        if(msg instanceof PluginUpdate)
        {
            PluginUpdate pmsg = (PluginUpdate)msg;
            if(pmsg.getWhat() == PluginUpdate.LOADED
                || pmsg.getWhat() == PluginUpdate.UNLOADED)
            {
                if(!pmsg.isExiting())
                {
                    if(!queuedTOCReload)
                        queueTOCReload();
                    queuedTOCReload = true;
                }
            }
        }
    } //}}}

    //{{{ getBaseURL() method
    public String getBaseURL()
    {
        return baseURL;
    } //}}}

    //{{{ getShortURL() method
    String getShortURL()
    {
        return shortURL;
    } //}}}

    //{{{ Private members

    //{{{ Instance members
    private String baseURL;
    private String shortURL;
    private JButton back;
    private JButton forward;
    private JEditorPane viewer;
    private JLabel title;
    private JSplitPane splitter;
    private String[] history;
    private int historyPos;
    private HelpTOCPanel toc;
    private boolean queuedTOCReload;
    //}}}

    //{{{ queueTOCReload() method
    public void queueTOCReload()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                queuedTOCReload = false;
                toc.load();
            }
        });
    } //}}}

    //}}}

    //{{{ Inner classes

    //{{{ ActionHandler class
    class ActionHandler implements ActionListener
    {
        //{{{ actionPerformed() class
        public void actionPerformed(ActionEvent evt)
        {
            Object source = evt.getSource();
            if(source == back)
            {
                if(historyPos <= 1)
                    getToolkit().beep();
                else
                {
                    String url = history[--historyPos - 1];
                    gotoURL(url,false);
                }
            }
            else if(source == forward)
            {
                if(history.length - historyPos <= 1)
                    getToolkit().beep();
                else
                {
                    String url = history[historyPos];
                    if(url == null)
                        getToolkit().beep();
                    else
                    {
                        historyPos++;
                        gotoURL(url,false);
                    }
                }
            }
        } //}}}
    } //}}}

    //{{{ LinkHandler class
    class LinkHandler implements HyperlinkListener
    {
        //{{{ hyperlinkUpdate() method
        public void hyperlinkUpdate(HyperlinkEvent evt)
        {
            if(evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
                if(evt instanceof HTMLFrameHyperlinkEvent)
                {
                    ((HTMLDocument)viewer.getDocument())
                        .processHTMLFrameHyperlinkEvent(
                        (HTMLFrameHyperlinkEvent)evt);
                }
                else
                {
                    URL url = evt.getURL();
                    if(url != null)
                        gotoURL(url.toString(),true);
                }
            }
            else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                viewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
                viewer.setCursor(Cursor.getDefaultCursor());
            }
        } //}}}
    } //}}}

    //{{{ PropertyChangeHandler class
    class PropertyChangeHandler implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent evt)
        {
            if("page".equals(evt.getPropertyName()))
            {
                String titleStr = (String)viewer.getDocument()
                    .getProperty("title");
                if(titleStr == null)
                {
                    titleStr = MiscUtilities.getFileName(
                        viewer.getPage().toString());
                }
                title.setText(titleStr);
            }
        }
    } //}}}

    //}}}
}

/*
ActionPlugin.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2004 Ian Lewis (IanLewis@member.fsf.org)

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

package net.sourceforge.jsxe;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Swing classes
import javax.swing.Action;
import javax.swing.JMenu;
//}}}

//{{{ Java classes
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Enumeration;
//}}}

//}}}

/**
 * This defines the general interface that all plugins for jsXe must implement.
 * There are two types of plugins, ViewPlugins and ActionPlugins. ViewPlugins
 * specify a view that can be used to edit XML documents. ActionPlugins add actions
 * to jsXe allowing it to do extra tasks such as XSLT transformation.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @since jsXe 0.3 beta
 * @version $Id$
 */
public abstract class ActionPlugin {
    
    //{{{ Private members
    private ActionSet m_actionSet;
    //}}}
    
    //{{{ ActionPlugin constructor
    /**
     * Constructs an ActionPlugin with the supplied values.
     */
    public ActionPlugin() {
        m_actionSet = new ActionSet(jsXe.getPluginLoader().getPluginProperty(this, JARClassLoader.PLUGIN_HUMAN_READABLE_NAME));
    }//}}}
    
    //{{{ getPluginMenu()
    /**
     * Returns the menu that appears in the tools menu in jsXe. This can
     * include menu items for actions that are included with the plugin.
     * The default implementation returns null.
     * @return the menu for this plugin.
     */
    public JMenu getPluginMenu() {
        return null;
    }//}}}
    
    //{{{ getOptionsPanel()
    /**
     * Gets the options panel for setting general plugin options. The default
     * implementation returns null.
     * @return an OptionsPanel for editing this plugin's options
     */
    public OptionsPanel getOptionsPanel(DocumentBuffer buffer) {
        return null;
    }//}}}
    
    //{{{ getProperties()
    /**
     * The properties to add to jsXe when the plugin is loaded. The default
     * iplementation returns an empty properties set.
     * @return the plugin's properties
     */
    public Properties getProperties() {
        return new Properties();
    }//}}}
    
    //{{{ addAction()
    /**
     * Allows subclasses to add actions to the ActionSet for the plugin. Normally
     * this would be done during initialization of the plugin.
     * @param name the name of the action
     * @param action the action itself
     */
    protected void addAction(String name, Action action) {
        m_actionSet.addAction(name, action);
    }//}}}
    
    //{{{ getActionSet()
    /**
     * Gets the ActionSet for this plugin. These actions can be executed
     * through menus and such.
     * @return the ActionSet containing actions added by this plugin.
     */
    public ActionSet getActionSet() {
        return m_actionSet;
    }//}}}
    
    //{{{ getMessages()
    /**
     * Gets the localized messages for the plugin for the ISO-639 language code
     * given. The default behavior is to load the messages from a properties
     * file in plugin jar file in the messages/ directory of the form 
     * messages.&lt;ISO-639 language code&gt;. For example, the english messages
     * file would be messages.en
     *
     * This method can be overidden but in most cases it shouldn't be necessary.
     *
     * @param lang the ISO-639 language code
     * @since jsXe 0.4 pre3
     */
    public Properties getMessages(String lang) {
        Log.log(Log.DEBUG,this, "Getting messages for plugin: "+this.toString());
        Properties messages = new Properties();
        try {
            //get default english language messages
            InputStream stream = getClass().getResourceAsStream("/messages/messages.en");
            if (stream != null) {
                messages.load(stream);
                Log.log(Log.DEBUG,this, "loaded /messages/messages.en");
            } else {
                Log.log(Log.WARNING, this, "Plugin does have default messages file messages.en");
            }
            
            //get localized messages
            Properties localMessages = new Properties();
            stream = getClass().getResourceAsStream("/messages/messages."+lang);
            if (stream != null) {
                localMessages.load(stream);
                Log.log(Log.DEBUG,this, "loaded /messages/messages."+lang);
                Enumeration names = localMessages.propertyNames();
                while (names.hasMoreElements()) {
                    String name = names.nextElement().toString();
                    String message = localMessages.getProperty(name);
                    messages.setProperty(name, message);
                }
            } else {
                Log.log(Log.WARNING, this, "Plugin does not contain messages file for current locale: "+lang);
            }
        } catch (IOException e) {
            Log.log(Log.ERROR, this, e);
        }
        Log.log(Log.DEBUG,this,"messages: "+messages.toString());
        return messages;
    }//}}}
    
    //{{{ Broken class
    
    public static class Broken extends ActionPlugin {
        
        //{{{ Broken constructor
        
        public Broken() {}//}}}
        
    }//}}}
    
}

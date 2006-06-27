/*
Messages.java
:tabSize=4:indentSize=4:noTabs=true:
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

//{{{ Java base classes
import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Iterator;
import java.text.MessageFormat;
//}}}

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.util.Log;
//}}}

//}}}

/**
 * Messages is the mechanism that jsXe uses to localize messages into the
 * locale of the user. This class will automatically use the current default
 * locale of the system but this can be overridden by calling the
 * {@link #setLanguage(String)} method.
 *
 * Messages are automatically loaded from the properties files located in the
 * 'messages' directory in the jsXe install. These files are named
 * <code>message.<i>language</i></code>. Where language is the ISO-639 language
 * code. The default being english.
 *
 * @author Trish Hartnett (<a href="mailto:trishah136@member.fsf.org">trishah136@member.fsf.org</a>)
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.4 pre1
 * @see java.util.Locale
 */
public class Messages {
    
    //{{{ Private static members
    private static Properties m_propertiesObject = new Properties();
    private static Properties m_pluginMessages = new Properties();
    private static Properties m_defaultProperties = new Properties();
    private static String m_language;
    private static String m_directory = "."; //default to current directory
    //}}}
    
    //{{{ getLanguage()
    /**
     * @return Returns the ISO-639 language code.
     */
    public static String getLanguage() {
        return m_language;
    }//}}}
    
    //{{{ setLanguage()
    /**
     * @param newLanguage The ISO-639 language code
     */
    public static void setLanguage(String newLanguage) {
        initLocale(newLanguage, m_directory);
    }//}}}

    //{{{ getMessage()
    /**
     * <p>Returns the message with the specified name. When a Messages is
     * queried for a message it first looks for the message in the current
     * language and returns it. If it cannot find the message in the messages
     * for the current language it looks for it in english and returns it. If
     * it still doesn't find the message it returns null.</p>
     *
     * @param String propertyName - the name of the property you want the value for
     * @return Returns the value of a property from the propertiesObject.
     */
    public static synchronized String getMessage(String propertyName){
        if (m_language == null) {
            //setLanguage("en");
            Locale newLocal = Locale.getDefault();
            String isoLanguage = newLocal.getLanguage();
            setLanguage(isoLanguage);
        }
        
        //search in order, localized messages->default messages->plugin messages
        String message = m_propertiesObject.getProperty(propertyName, m_defaultProperties.getProperty(propertyName, m_pluginMessages.getProperty(propertyName)));
        if (message == null) {
            Log.log(Log.WARNING, Messages.class, "Unregistered message requested: "+propertyName);
        }
        return message;
    }//}}}
    
    //{{{ getMessage()
    /**
     * <p>Returns the message with the specified name. When a Messages is
     * queried for a message it first looks for the message in the current
     * language and returns it. If it cannot find the message in the messages
     * for the current language it looks for it in english and returns it. If
     * it still doesn't find the message it returns null.</p>
     *
     * <p>The elements of the <code>args</code> array are substituted
     * into the value of the property in place of strings of the
     * form <code>{<i>n</i>}</code>, where <code><i>n</i></code> is an index
     * in the array.</p>
     *
     * You can find out more about this feature by reading the
     * documentation for the <code>format</code> method of the
     * <code>java.text.MessageFormat</code> class.
     *
     * @param name The property
     * @param args The positional parameters
     * @since jsXe 0.4 pre2
     */
    public static synchronized String getMessage(String name, Object[] args) {
        if(name == null) {
            return null;
        }
        if (args == null) {
            return getMessage(name);
        } else {
            String value = getMessage(name);
            if (value == null) {
                return null;
            } else {
                return MessageFormat.format(value,args);
            }
        }
    }//}}}
    
    //{{{ initLocale()
    /**
     * Initializes localized messages for jsXe.
     * @param language The language for the propertiesObject.
     * @param directory The directory where the messages files are located.
     */
    public static void initLocale(String language, String directory) {
        String isoLanguage = language;
        if (isoLanguage == null){
            //setLanguage("en");
            Locale newLocal = Locale.getDefault();
            isoLanguage = newLocal.getLanguage();
        }
        
        Log.log(Log.MESSAGE, Messages.class, "Loading messages for language: "+isoLanguage);
        
        File messagesFile =  new File(directory+System.getProperty("file.separator")+"messages."+isoLanguage);
        if (!messagesFile.exists()) {
            Log.log(Log.WARNING, Messages.class, "Default messages file for current language not found");
        } else {
            loadMessages(m_propertiesObject, messagesFile);
            m_language = isoLanguage;
        }
        m_directory = directory;
        
        //load default english
        messagesFile = new File(directory+System.getProperty("file.separator")+"messages.en");
        if (!messagesFile.exists()) {
            Log.log(Log.ERROR, Messages.class, "Default messages file for English not found");
        } else {
            if (m_language == null) {
                m_language = "en";
            }
        }
        loadMessages(m_defaultProperties, messagesFile);
    }//}}}
    
    //{{{ loadPluginMessages()
    /**
     * Loads the localized messages from installed plugins and merges them into
     * the plugin messages.
     * This method should only be called on jsXe startup.
     */
    public static void loadPluginMessages(Properties pluginMessages) {
        Enumeration names = pluginMessages.propertyNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement().toString();
            String message = pluginMessages.getProperty(name);
            m_pluginMessages.setProperty(name, message);
        }
    }//}}}

    //{{{ Private Members
    
    //{{{ loadMessages()
    /**
     * 
     * @param propertiesObject The propertiesObject which will store the values from the messages file.
     * @param messssagesFile The name of the messages file to be used.
     */
    private static void loadMessages(Properties propertiesObject, File messagesFile) {
        try {
            //create input stream from messages file 
             FileInputStream in = new FileInputStream(messagesFile);    
             propertiesObject.load(in);
        } catch (FileNotFoundException e) {
            Log.log(Log.ERROR, Messages.class, e);
        } catch(IOException e) {
            Log.log(Log.ERROR, Messages.class, e);
        }       
    }//}}}
    
    //}}}
    
}

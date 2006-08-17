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
import java.util.*;
import java.text.MessageFormat;
import java.net.URL;
//}}}

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.JARClassLoader;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
//}}}

//}}}

/**
 * Messages is the mechanism that jsXe uses to localize messages into the
 * locale of the user. This class will automatically use the current default
 * locale of the system but this can be overridden by calling the
 * {@link #setLanguage(String)} method.
 *
 * Messages are automatically loaded from the properties files located in the
 * 'messages' directory in the jsXe install directory. The messages files are
 * names with the format messages.<code>language</code>.<code>country</code>.<code>variant</code>.
 * The Messages class searches these files in the following order.
 *
 * Messages files should be in a format that the <code>Properties</code> class
 * can understand. This means that messages files that contain localized
 * characters should have the native2ascii tool run on them. This ensures
 * that these characters are in the \\u... format that can be read by the
 * <code>Properties</code> class.
 *
 * <ul>
 * <li>messages.<code>language</code>_<code>country</code>_<code>variant</code></li>
 * <li>messages.<code>language</code>_<code>country</code></li>
 * <li>messages.<code>language</code></li>
 * <li>messages</li>
 * </ul>
 *
 * @author Trish Hartnett (<a href="mailto:trishah136@member.fsf.org">trishah136@member.fsf.org</a>)
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.4 pre1
 * @see java.util.Locale
 */
public class Messages {
    
    //{{{ Private static members
    /**
     * properties containing the messages for jsXe
     */
    private static Properties m_messages;
    /**
     * plugin properties
     */
    private static Properties m_pluginMessages;
    
   // static {
   //    // Locale.setDefault(new Locale("sv"));
   //    // Locale.setDefault(Locale.GERMANY);
   //    // Locale.setDefault(Locale.JAPAN);
   //    // Locale.setDefault(new Locale("ru", "RU"));
   // }
    private static Locale m_locale = Locale.getDefault();
    
    
    private static boolean initialized = false;
    private static boolean plugins_initialized = false;
    
    //{{{ getMessagesFileName()
    private static String getMessagesFileName(Locale locale) {
        StringBuffer messagesFile = new StringBuffer("messages");
                
        if (locale != null) {
            messagesFile.append(".").append(locale.toString());
           // String language = locale.getLanguage();
           // if (language != null && !language.equals("")) {
           //     messagesFile.append(".").append(language);
                
           //     String country = locale.getCountry();
           //     if (country != null && !country.equals("")) {
           //         messagesFile.append(".").append(country);
                    
           //         String variant = locale.getVariant();
           //         if (variant != null && !variant.equals("")) {
           //             messagesFile.append(".").append(variant);
           //         }
           //     }
           // }
        }
        
        return messagesFile.toString();
    }//}}}
    
    //{{{ loadMessages()
    private static void loadMessages(Locale locale) {
        
        String messagesFile = getMessagesFileName(locale);
        
        try {
            Properties props = new Properties();
            
            //create input stream from messages file
            FileInputStream in = new FileInputStream(jsXe.getInstallDirectory()+
                                                     System.getProperty("file.separator")+
                                                     "messages"+
                                                     System.getProperty("file.separator")+
                                                     messagesFile);
            
            Log.log(Log.NOTICE, Messages.class, "Loading message file: "+messagesFile);
            
            props.load(in);
            
            //reset the messages in UTF-8
            //TODO: fix this so we don't have to do a encoding conversion
           // Enumeration keys = props.keys();
           // while (keys.hasMoreElements()) {
           //     String key = keys.nextElement().toString();
           //     String value = props.getProperty(key);
           //     if (value != null) {
           //         props.setProperty(key, new String(value.getBytes("ISO-8859-1"),"UTF-8"));
           //     } else {
           //         Log.log(Log.DEBUG, Messages.class, "null value: "+key);
           //     }
           // }
            
            m_messages = MiscUtilities.mergeProperties(m_messages, props);
            
        } catch (FileNotFoundException e) {
            // just fall through
        } catch(IOException e) {
            Log.log(Log.ERROR, Messages.class, e);
        }
    }//}}}
    
    //{{{ loadPluginMessages()
    private static void loadPluginMessages(Locale locale) {
        
        String messagesFile = getMessagesFileName(locale);
        JARClassLoader loader = jsXe.getPluginLoader();
        
        //create input stream from messages file
        try {
            Enumeration resources = loader.getPluginResources("messages/"+messagesFile);
            
            while (resources.hasMoreElements()) {
                try {
                    Properties props = new Properties();
                    URL resource = (URL)resources.nextElement();
                    
                    Log.log(Log.NOTICE, Messages.class, "Loading plugin message file: "+resource.toString());
                    
                    props.load(resource.openStream());
                    
                    //reset the messages in UTF-8
                    //TODO: fix this so we don't have to do a encoding conversion
                   // Enumeration keys = props.keys();
                   // while (keys.hasMoreElements()) {
                   //     String key = keys.nextElement().toString();
                   //     String value = props.getProperty(key);
                   //     if (value != null) {
                   //         props.setProperty(key, new String(value.getBytes("ISO-8859-1"),"UTF-8"));
                   //     } else {
                   //         Log.log(Log.DEBUG, Messages.class, "null value: "+key);
                   //     }
                   // }
                    
                    m_pluginMessages = MiscUtilities.mergeProperties(m_pluginMessages, props);
                } catch (FileNotFoundException e) {
                    //fall through
                } catch(IOException e) {
                    Log.log(Log.ERROR, Messages.class, e);
                }
            }
        } catch(IOException e) {
            Log.log(Log.ERROR, Messages.class, e);
        }
            
    }//}}}
    
    //}}}
    
    //{{{ initMessages()
    public static void initMessages() {
        if (!initialized) {
            
            Log.log(Log.NOTICE, Messages.class, "Loading localized messages: "+m_locale.toString());
            
            m_messages = new Properties();
            
            //{{{ load messages files that match the locale
            
            boolean hasLang = (m_locale.getLanguage() != null && !m_locale.getLanguage().equals(""));
            boolean hasCountry = (m_locale.getCountry() != null && !m_locale.getCountry().equals(""));
            boolean hasVariant = (m_locale.getVariant() != null && !m_locale.getVariant().equals(""));
            
            if (hasVariant) {
                // use full locale
                loadMessages(m_locale);
            }
            
            if (hasCountry) {
                //use only language and country
                loadMessages(new Locale(m_locale.getLanguage(), m_locale.getCountry()));
            }
            
            if (hasLang) {
                //use only the language
                loadMessages(new Locale(m_locale.getLanguage()));
            }
            
            //}}}
            
            // load the default messages
            
            loadMessages(null);
            
            initialized = true;
        }
    }//}}}
    
    //{{{ initPluginMessages()
    public static void initPluginMessages() {
        if (!plugins_initialized) {
            m_pluginMessages = new Properties();
            
            Log.log(Log.NOTICE, Messages.class, "Loading localized messages for plugins: "+m_locale.toString());
            
            //{{{ load messages files that match the locale
            
            boolean hasLang = (m_locale.getLanguage() != null && !m_locale.getLanguage().equals(""));
            boolean hasCountry = (m_locale.getCountry() != null && !m_locale.getCountry().equals(""));
            boolean hasVariant = (m_locale.getVariant() != null && !m_locale.getVariant().equals(""));
            
            if (hasVariant) {
                // use full locale
                loadPluginMessages(m_locale);
            }
            
            if (hasCountry) {
                //use only language and country
                loadPluginMessages(new Locale(m_locale.getLanguage(), m_locale.getCountry()));
            }
            if (hasLang) {
                //use only the language
                loadPluginMessages(new Locale(m_locale.getLanguage()));
            }
            
            //}}}
            
            // load the default messages.
            
            loadPluginMessages(null);
            
            plugins_initialized = true;
        }
    }//}}}
    
    //{{{ getLocale()
    /**
     * The current locale that jsXe is using to display localized messages.
     */
    public Locale getLocale() {
        return m_locale;
    }//}}}
    
    //{{{ setLocale()
    /**
     * @param locale the new locale
     */
    public static void setLocale(Locale locale) {
        m_locale = locale;
        if (initialized) {
            initialized = false;
            initMessages();
            
            if (plugins_initialized) {
                plugins_initialized = false;
                initPluginMessages();
            }
        }
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
        
        //search in order, localized messages->default messages->plugin messages
        String message = m_messages.getProperty(propertyName);
        
        if (message == null) {
            message = m_pluginMessages.getProperty(propertyName);
        }
        
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
    
}

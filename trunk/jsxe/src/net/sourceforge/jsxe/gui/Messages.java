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
 * <ul>
 * <li>messages.<code>language</code>.<code>country</code>.<code>variant</code></li>
 * <li>messages.<code>language</code>.<code>country</code></li>
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
     * A Locale to Properties object map which is used when searching
     * for a message resource.
     */
    private static HashMap m_messagesMap = new HashMap();
    /**
     * This is a list of URLs messages files for plugins which have been loaded.
     */
    private static ArrayList m_resources = new ArrayList();
    /**
     * The default properties file
     */
    private static Properties m_default;
    private static Locale m_locale = Locale.getDefault();
    
    //{{{ getMessagesFileName()
    private static String getMessagesFileName(Locale locale) {
        StringBuffer messagesFile = new StringBuffer("messages");
                
        if (locale != null) {
            String language = locale.getLanguage();
            if (language != null && !language.equals("")) {
                messagesFile.append(".").append(language);
                
                String country = locale.getCountry();
                if (country != null && !country.equals("")) {
                    messagesFile.append(".").append(country);
                    
                    String variant = locale.getVariant();
                    if (variant != null && !variant.equals("")) {
                        messagesFile.append(".").append(variant);
                    }
                }
            }
        }
        
        return messagesFile.toString();
    }//}}}
    
    //{{{ loadMessages()
    /**
     * @param locale the locale to use when searching for the messages file.
     *               The language, country, and variant must be exact. Null
     *               loads the default messages file.
     */
    private static Properties loadMessages(Locale locale) {
        Properties props = null;
        try {
            
            if (locale != null) {
                props = (Properties)m_messagesMap.get(locale);
            } else {
                props = m_default;
            }
            
            if (props == null) {
                //{{{ Load the properties file
                
                String messagesFile = getMessagesFileName(locale);
                
                //create input stream from messages file
                FileInputStream in = new FileInputStream(jsXe.getInstallDirectory()+
                                                         System.getProperty("file.separator")+
                                                         "messages"+
                                                         System.getProperty("file.separator")+
                                                         messagesFile.toString());
                Log.log(Log.NOTICE, Messages.class, "Loading messages file: "+messagesFile);
                props = new Properties();
                props.load(in);
                if (locale != null) {
                    m_messagesMap.put(locale, props);
                } else {
                    m_default = props;
                }
                //}}}
            }
        } catch (FileNotFoundException e) {
            // just fall through
        } catch(IOException e) {
            Log.log(Log.ERROR, Messages.class, e);
        }
        
        //{{{ Check for plugin resources
       // Log.log(Log.DEBUG, Messages.class, "loading plugin messages");
        try {
            JARClassLoader loader = jsXe.getPluginLoader();
            if (loader != null) {
                String messagesFile = getMessagesFileName(locale);
                //TODO: inefficient
                Enumeration pluginMessages = loader.getPluginResources("messages/"+messagesFile);
               // Log.log(Log.DEBUG, Messages.class, "looking for: "+"messages/"+messagesFile);
                while (pluginMessages.hasMoreElements()) {
                    URL resource = (URL)pluginMessages.nextElement();
                   // Log.log(Log.DEBUG, Messages.class, resource.toString());
                    if (!m_resources.contains(resource)) {
                        Properties resourceProps = new Properties();
                        Log.log(Log.NOTICE, Messages.class, "Loading plugin messages file: "+resource.toString());
                        resourceProps.load(resource.openStream());
                        props = MiscUtilities.mergeProperties(props, resourceProps);
                        m_resources.add(resource);
                        if (locale != null) {
                            m_messagesMap.put(locale, props);
                        } else {
                            m_default = props;
                        }
                    }
                }
            }
        } catch(IOException e) {
            Log.log(Log.ERROR, Messages.class, e);
        }//}}}
        
        return props;
    }//}}}
    
    //{{{ getMessages()
    /**
     * Searches the available resources for a resource where it can draw
     * the messages that are needed for the current locale.
     */
    private static Properties getMessages(Locale locale) {
        Properties messages = loadMessages(locale);
        if (messages == null) {
            messages = loadMessages(new Locale(locale.getLanguage(), locale.getCountry()));
            if (messages == null) {
                messages = loadMessages(new Locale(locale.getLanguage()));
                if (messages == null) {
                    messages = loadMessages(null);
                }
            }
        }
        return messages;
    }//}}}
    
    //}}}
    
    //{{{ getLocale()
    public Locale getLocale() {
        return m_locale;
    }//}}}
    
    //{{{ setLocale()
    /**
     * @param locale the new locale The ISO-639 language code
     */
    public static void setLocale(Locale locale) {
        m_locale = locale;
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
        
        Properties messages = getMessages(m_locale);
        
        //search in order, localized messages->default messages->plugin messages
        String message = messages.getProperty(propertyName);
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

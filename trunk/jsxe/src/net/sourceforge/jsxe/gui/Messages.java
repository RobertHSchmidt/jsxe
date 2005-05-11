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
//}}}

//{{{ jsXe classes
import net.sourceforge.jsxe.util.Log;
//}}}

//}}}

/**
 * Gets the messages for the current locale according to the JVM.
 * @author Trish Hartnett (<a href="mailto:trishah136@member.fsf.org">trishah136@member.fsf.org</a>)
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.4 pre1
 */
public class Messages {
    
     //{{{ Private static members
    private static Properties m_propertiesObject = new Properties();
    private static Properties m_defaultProperties = new Properties();
    private static String m_language;
    private static String m_directory = "."; //default to current directory
     //}}}
    
    /**
     * @return Returns the language.
     */
    public static String getLanguage() {
        return m_language;
    }
    
    /**
     * @param newLanguage The language to set.
     */
    public static void setLanguage(String newLanguage) {
        initializePropertiesObject(newLanguage, m_directory);
    }

    /**
     * @param String propertyName - the name of the property you want the value for
     * @return Returns the value of a property from the propertiesObject.
     */
    public static synchronized String getMessage(String propertyName){
        if (m_language == null) {
            //setLanguage("en");
            Locale newLocal = Locale.getDefault();
            String isoLanguage =newLocal.getLanguage();
            setLanguage(isoLanguage);
        }
        return m_propertiesObject.getProperty(propertyName, m_defaultProperties.getProperty(propertyName));
    }
    
    /**
    
     * @param language The language for the propertiesObject.
     */
    public static void initializePropertiesObject(String language, String directory) {
        String isoLanguage = language;
        if (isoLanguage == null){
            //setLanguage("en");
            Locale newLocal = Locale.getDefault();
            isoLanguage = newLocal.getLanguage();
        }
        File messagesFile =  new File(directory+System.getProperty("file.separator")+"messages."+isoLanguage);
        if (!messagesFile.exists()) {
            Log.log(Log.WARNING, Messages.class, "Default messages file for current language not found");
        } else {
            m_language = isoLanguage;
        }
        m_directory = directory;
        loadMessages(m_propertiesObject, messagesFile);
        
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
    }
    
    /**
     * 
     * @param propertiesObject The propertiesObject which will store the values from the messages file.
     * @param messssagesFile The name of the messages file to be used.
     */
    public static void loadMessages(Properties propertiesObject, File messagesFile) {
        try {
            //create input stream from messages file 
             FileInputStream in = new FileInputStream(messagesFile);    
             propertiesObject.load(in);
        } catch (FileNotFoundException e) {
            Log.log(Log.ERROR, Messages.class, e);
        }catch(IOException e){
            Log.log(Log.ERROR, Messages.class, e);
        }       
    }
    

}

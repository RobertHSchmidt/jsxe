/*
Messages.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2005 Trish Harnett (trishah136@member.fsf.org)
Portions Copyright (C) 2005 Trish Harnett (trishah136@member.fsf.org)

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

//}}}

/**
 * Gets the messages for the current locale according to the JVM.
 * @author Trish Hartnett (<a href="mailto:trishah136@member.fsf.org">trishah136@member.fsf.org</a>)
 * @version $Id$
 */
public class Messages {
	
	 //{{{ Private static members
	private static Messages INSTANCE;
	private Properties propertiesObject = new Properties();
	private String language;
	 //}}}
	

	/**
	 * @return Returns the language.
	 */
	public String getLanguage() {
		return language;
	}
		
	/**
	 * @param newLanguage The language to set.
	 */
	public static void setLanguage(String newLanguage) {
			INSTANCE = new Messages();
			INSTANCE.initializePropertiesObject(newLanguage);		
	}

	/**
	 * @param String propertyName - the name of the property you want the value for
	 * @return Returns the value of a property from the propertiesObject.
	 */
	public static  synchronized String getMessage (String propertyName){
		if(INSTANCE == null){
			//setLanguage("en");	
			Locale newLocal = Locale.getDefault();
			String isoLanguage =newLocal.getISO3Language();
			setLanguage(isoLanguage);		
		}
		return INSTANCE.propertiesObject.getProperty(propertyName);
	}
	
	/**
	 * @param language The language for the propertiesObject.
	 *
	 */
	public void initializePropertiesObject(String language) {
		File messagesFile =  new File("messages"+language );
		if(messagesFile.length() == 0){
			messagesFile = new File("messages.eng");
		}
		
		loadMessages(propertiesObject, messagesFile);
	}

	
	/**
	 * 
	 * @param propertiesObject The propertiesObject which will store the values from the messages file.
	 * @param messssagesFile The name of the messages file to be used.
	 */
	public void loadMessages(Properties propertiesObject, File messagesFile){
		try {
			//create input stream from messages file 
			 FileInputStream in
			   = new FileInputStream(messagesFile);	
			 propertiesObject.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}		
	}
	
	
	
	
	

}

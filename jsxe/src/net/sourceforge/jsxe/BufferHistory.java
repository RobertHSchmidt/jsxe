/*
jsXe.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file written by Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 2002 Ian Lewis

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

//{{{ Java classes
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//}}}

//{{{ SAX classes
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
//}}}

//}}}

public class BufferHistory {

    //{{{ BufferHistory constructor
    
    public BufferHistory() {}
    //}}}
    
    //{{{ getEntry()
    
    public BufferHistoryEntry getEntry(String path) {
        Iterator historyItr = m_history.iterator();
        while (historyItr.hasNext()) {
            BufferHistoryEntry nextEntry = (BufferHistoryEntry)historyItr.next();
            if (nextEntry.getPath().equals(path)) {
                return nextEntry;
            }
        }
        return null;
    }//}}}
    
    //{{{ getEntries()
    
    public ArrayList getEntries() {
        return m_history;
    }//}}}
    
    //{{{ setEntry()
    
    public void setEntry(DocumentBuffer buffer, String viewName) {
        if (!buffer.isUntitled()) {
            String path = buffer.getFile().getPath();
            BufferHistoryEntry entry = new BufferHistoryEntry(path, viewName, buffer.getProperties());
            setEntry(entry);
        }
    }//}}}
    
    //{{{ setEntry()
    
    public void setEntry(String path, String viewName, Properties properties) {
        BufferHistoryEntry entry = new BufferHistoryEntry(path, viewName, properties);
        setEntry(entry);
    }//}}}
    
    //{{{ setEntry()
    
    public void setEntry(BufferHistoryEntry entry) {
        if (!m_history.contains(entry)) {
            String path = entry.getPath();
            BufferHistoryEntry previousEntry = getEntry(path);
            if (previousEntry != null) {
                m_history.remove(previousEntry);
            }
            m_history.add(0, entry);
            
            //remove entries from the bottom of the list if it's too big.
            int maxRecentFiles;
            try {
                maxRecentFiles = Integer.parseInt(jsXe.getProperty("max.recent.files"));
            } catch (NumberFormatException nfe) {
                try {
                    maxRecentFiles = Integer.parseInt(jsXe.getDefaultProperty("max.recent.files"));
                } catch (NumberFormatException nfe2) {}
            }
        }
    }//}}}
    
    //{{{ load()
    
    public void load(File file) throws IOException, SAXException, ParserConfigurationException {
        // if file doesn't exist then the history is empty
        m_history = new ArrayList();
        if (file.exists()) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                parser.parse(file, new BufferHistoryHandler());
                
            } catch (IllegalArgumentException iae) {}
        }
    }//}}}

    //{{{ save()
    
    public void save(File file) throws IOException {
        
        String lineSep = System.getProperty("line.separator");
        
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        
        out.write("<?xml version=\"1.0\"?>");
        out.write(lineSep);
        out.write("<recent>");
        out.write(lineSep);
        
        int maxRecentFiles = 20;
        try {
            maxRecentFiles = Integer.parseInt(jsXe.getProperty("max.recent.files"));
        } catch (NumberFormatException nfe) {
            try {
                maxRecentFiles = Integer.parseInt(jsXe.getDefaultProperty("max.recent.files"));
            } catch (NumberFormatException nfe2) {}
        }
        
        int index = 0;
        Iterator historyItr = m_history.iterator();
        while (historyItr.hasNext()) {
            ++index;
            if (index < maxRecentFiles) {
                BufferHistoryEntry entry = (BufferHistoryEntry) historyItr.next();
                out.write("<entry>");
                out.write(lineSep);
                
                String path = entry.getPath();
                out.write("<path><![CDATA[");
                out.write(path);
                out.write("]]></path>");
                out.write(lineSep);
                
                String viewName = entry.getViewName();
                out.write("<view>");
                out.write(viewName);
                out.write("</view>");
                out.write(lineSep);
                
                Properties props = entry.getProperties();
                Enumeration propertyItr = props.keys();
                while (propertyItr.hasMoreElements()) {
                    String key = propertyItr.nextElement().toString();
                    String value = props.getProperty(key);
                    out.write("<property name=\"");
                    out.write(key);
                    out.write("\" value=\"");
                    out.write(value);
                    out.write("\"/>");
                    out.write(lineSep);
                }
                
                out.write("</entry>");
                out.write(lineSep);
            }
        }
        
        out.write("</recent>");
        out.close();
        
    }//}}}
    
    //{{{ BufferHistoryEntry class
    
    public class BufferHistoryEntry {
        
        //{{{ BufferHistoryEntry constructor
        
        public BufferHistoryEntry(String path, String viewName, Properties properties) {
            m_path = path;
            m_viewName = viewName;
            m_properties = properties;
        }//}}}
        
        //{{{ getPath()
        
        public String getPath() {
            return m_path;
        }//}}}
        
        //{{{ getViewName()
        
        public String getViewName() {
            return m_viewName;
        }//}}}
        
        //{{{ getProperties()
        
        public Properties getProperties() {
            return m_properties;
        }//}}}
        
        //{{{ Private members
        
        private String m_path;
        private String m_viewName;
        private Properties m_properties;
        //}}}
    }//}}}
    
    //{{{ Private members
    
    //{{{ BufferHistoryHandler class
    
    private class BufferHistoryHandler extends DefaultHandler {
        
        //{{{ characters()
        
        public void characters(char[] ch, int start, int length) {
            m_m_charData = new String(ch, start, length);
        }//}}}
        
        //{{{ endElement()
        
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("entry")) {
                m_history.add(new BufferHistoryEntry(m_m_path, m_m_viewName, m_m_properties));
            }
            
            if (qName.equals("path")) {
                m_m_path = m_m_charData;
            }
            
            if (qName.equals("view")) {
                m_m_viewName = m_m_charData;
            }
            
        }//}}}
        
        //{{{ startElement()
        
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            
            if (qName.equals("entry")) {
                m_m_properties=new Properties();
            }
            
            if (qName.equals("property")) {
                int length = attributes.getLength();
                String propName = new String();
                String propValue = new String();
                for (int i=0; i<length; i++) {
                    String name = attributes.getQName(i);
                    if (name.equals("name")) {
                        propName = attributes.getValue(i);
                    }
                    if (name.equals("value")) {
                        propValue = attributes.getValue(i);
                    }
                }
                m_m_properties.setProperty(propName, propValue);
            }
        }//}}}
        
        //{{{ Private members
        Properties m_m_properties;;
        String m_m_charData;
        String m_m_path;
        String m_m_viewName;
        //}}}
        
    }//}}}
    
    private ArrayList m_history = new ArrayList();
    //}}}
    
}

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

import java.util.HashMap;
import java.util.Properties;
import java.io.File;

public class BufferHistory {
   
   //{{{ BufferHistory constructor
   
   public BufferHistory() {}
   //}}}
   
   //{{{ getEntry()
   
   public BufferHistoryEntry getEntry(String url) {
       return (BufferHistoryEntry)m_history.get(url);
   }//}}}
   
   //{{{ setEntry()
   
   public void setEntry(DocumentBuffer buffer, String viewName) {
       BufferHistoryEntry entry = new BufferHistoryEntry(buffer.getFile().getPath(), viewName, buffer.getProperties());
       m_history.put(entry.getURL(), entry);
   }//}}}
   
   //{{{ load()
   
   public void load(File file) {
       
   }//}}}
   
   //{{{ save()
   
   public void save(File file) {
       
   }//}}}
   
   //{{{ BufferHistoryEntry class
   
   public class BufferHistoryEntry {
      
      //{{{ BufferHistoryEntry constructor
      
      public BufferHistoryEntry(String url, String viewName, Properties properties) {
         m_url = url;
         m_viewName = viewName;
         m_properties = properties;
      }//}}}
      
      //{{{ getUrl()
      
      public String getURL() {
          return m_url;
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
      
      private String m_url;
      private String m_viewName;
      private Properties m_properties;
      //}}}
   }//}}}
   
   //{{{ Private members
   private HashMap m_history = new HashMap();
   //}}}
   
}

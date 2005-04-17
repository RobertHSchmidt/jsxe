/*
CompletionInfo.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2001, 2003 Slava Pestov
Portions Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)

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

package net.sourceforge.jsxe.dom.completion;

//{{{ Imports
//import gnu.regexp.*;
import java.util.*;
import net.sourceforge.jsxe.util.Log;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
//}}}

/**
 * Encapsulates information about an XML document structure obtained
 * from a DTD or Schema document.
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @since jsXe 0.4 pre1
 * @version $Id$
 */
public class CompletionInfo {
        public ArrayList elements;
        public HashMap elementHash;
        public ArrayList entities;
        public HashMap entityHash;
        public ArrayList elementsAllowedAnywhere;

        //{{{ CompletionInfo constructor
        public CompletionInfo()
        {
                this(new ArrayList(), new HashMap(),
                        new ArrayList(), new HashMap(),
                        new ArrayList());

                addEntity(EntityDecl.INTERNAL,"lt","<");
                addEntity(EntityDecl.INTERNAL,"gt",">");
                addEntity(EntityDecl.INTERNAL,"amp","&");
                addEntity(EntityDecl.INTERNAL,"quot","\"");
                addEntity(EntityDecl.INTERNAL,"apos","'");
        } //}}}

        //{{{ CompletionInfo constructor
        public CompletionInfo(ArrayList elements, HashMap elementHash,
                ArrayList entities, HashMap entityHash,
                ArrayList elementsAllowedAnywhere)
        {
                this.elements = elements;
                this.elementHash = elementHash;
                this.entities = entities;
                this.entityHash = entityHash;
                this.elementsAllowedAnywhere = elementsAllowedAnywhere;
        } //}}}

        //{{{ addEntity() method
        public void addEntity(int type, String name, String value)
        {
                addEntity(new EntityDecl(type,name,value));
        } //}}}

        //{{{ addEntity() method
        public void addEntity(int type, String name, String publicId, String systemId)
        {
                addEntity(new EntityDecl(type,name,publicId,systemId));
        } //}}}

        //{{{ addEntity() method
        public void addEntity(EntityDecl entity)
        {
                entities.add(entity);
                if(entity.type == EntityDecl.INTERNAL
                        && entity.value.length() == 1)
                {
                        Character ch = new Character(entity.value.charAt(0));
                        entityHash.put(entity.name,ch);
                        entityHash.put(ch,entity.name);
                }
        } //}}}

        //{{{ addElement() method
        public void addElement(ElementDecl element)
        {
                elementHash.put(element.name,element);
                elements.add(element);
        } //}}}

        //{{{ getAllElements() method
        public void getAllElements(String prefix, List out) {
            for(int i = 0; i < elements.size(); i++) {
                out.add(((ElementDecl)elements.get(i)).withPrefix(prefix));
            }
        } //}}}

        //{{{ toString() method
        public String toString()
        {
                StringBuffer buf = new StringBuffer();

                buf.append("<element-list>\n\n");

                for(int i = 0; i < elements.size(); i++)
                {
                        buf.append(elements.get(i));
                        buf.append('\n');
                }

                buf.append("\n</element-list>\n\n<entity-list>\n\n");

                buf.append("<!-- not implemented yet -->\n");
                /* for(int i = 0; i < entities.size(); i++)
                {
                        buf.append(entities.get(i));
                        buf.append('\n');
                } */

                buf.append("\n</entity-list>");

                return buf.toString();
        } //}}}

        //{{{ getCompletionInfoForNamespace() method
        public static CompletionInfo getCompletionInfoForNamespace(String namespace) {
            Object obj = completionInfoNamespaces.get(namespace);
            return (CompletionInfo)obj;
        } //}}}

       // //{{{ getCompletionInfoFromResource() method
       // public static CompletionInfo getCompletionInfoFromResource(String resource)
       // {
       //         synchronized(lock)
       //         {
       //                 CompletionInfo info = (CompletionInfo)completionInfoResources.get(resource);
       //                 if(info != null)
       //                         return info;
       //                 Log.log(Log.DEBUG,CompletionInfo.class,"Loading " + resource);
       //                 CompletionInfoHandler handler = new CompletionInfoHandler();
       //                 try
       //                 {
       //                         XMLReader parser = new org.apache.xerces.parsers.SAXParser();
       //                         parser.setFeature("http://apache.org/xml/features/validation/dynamic",true);
       //                         parser.setErrorHandler(handler);
       //                         parser.setEntityResolver(handler);
       //                         parser.setContentHandler(handler);
       //                         parser.parse(resource);
       //                 }
       //                 catch(SAXException se)
       //                 {
       //                         Throwable e = se.getException();
       //                         if(e == null)
       //                                 e = se;
       //                         Log.log(Log.ERROR,CompletionInfo.class,e);
       //                 }
       //                 catch(Exception e)
       //                 {
       //                         Log.log(Log.ERROR,CompletionInfo.class,e);
       //                 }
       //                 info = handler.getCompletionInfo();
       //                 completionInfoResources.put(resource,info);
       //                 return info;
       //         }
       // } //}}}

        //{{{ clone() method
        public Object clone() {
                return new CompletionInfo(
                        (ArrayList)elements.clone(),
                        (HashMap)elementHash.clone(),
                        (ArrayList)entities.clone(),
                        (HashMap)entityHash.clone(),
                        (ArrayList)elementsAllowedAnywhere.clone()
                );
        } //}}}

        //{{{ Private members
        private static HashMap globs;
        private static HashMap completionInfoResources;
        private static HashMap completionInfoNamespaces;

        //}}}
}

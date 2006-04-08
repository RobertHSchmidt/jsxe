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
        protected ArrayList elements;
        private HashMap elementHash;
        protected ArrayList entities;
        private HashMap entityHash;
        protected ArrayList elementsAllowedAnywhere;
        private static HashMap completionInfoResources = new HashMap();
        private static HashMap completionInfoNamespaces = new HashMap();

        //{{{ CompletionInfo constructor
        public CompletionInfo()
        {
                this(new ArrayList(), 
                     new HashMap(),
                     new ArrayList(),
                     new HashMap(),
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

        //{{{ getEntity()
        /**
         * Gets an entity with the given Name
         * @param name the name of the entity
         */
        public EntityDecl getEntity(String name) {
            Iterator itr = entities.iterator();
            while (itr.hasNext()) {
                EntityDecl decl = (EntityDecl)itr.next();
                if (decl.name.equals(name)) {
                    return decl;
                }
            }
            return null;
        }//}}}
        
        //{{{ getEntities()
        /**
         * Gets the entities for this completion info
         * @return a list of EntityDecl objects
         */
        public List getEntities() {
            return entities;
        }//}}}
        
        //{{{ getEntityHash()
        /**
         * Gets a map containing entity name to character and character to
         * entity name mappings.
         */
        public Map getEntityHash() {
            return entityHash;
        }//}}}
        
        //{{{ addElement() method
        public void addElement(ElementDecl element)
        {
                elementHash.put(element.name,element);
                elements.add(element);
        } //}}}
        
        //{{{ getElement()
        /**
         * Gets the element declaration for the element with the given
         * local name.
         */
        public ElementDecl getElement(String localName) {
            return (ElementDecl)elementHash.get(localName);
        }//}}}
        
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

        //{{{ getCompletionInfoFromResource() method
        /*
        public static CompletionInfo getCompletionInfoFromResource(String resource) {
            synchronized(lock) {
                CompletionInfo info = (CompletionInfo)completionInfoResources.get(resource);
                if(info != null)
                    return info;
                Log.log(Log.NOTICE,CompletionInfo.class,"Loading " + resource);
                CompletionInfoHandler handler = new CompletionInfoHandler();
                try {
                    XMLReader parser = new org.apache.xerces.parsers.SAXParser();
                    parser.setFeature("http://apache.org/xml/features/validation/dynamic",true);
                    parser.setErrorHandler(handler);
                    parser.setEntityResolver(handler);
                    parser.setContentHandler(handler);
                    
                    parser.parse(new InputSource(jsXe.class.getResource(resource)));
                } catch(SAXException se) {
                    Throwable e = se.getException();
                    if (e == null)
                        e = se;
                    Log.log(Log.ERROR,CompletionInfo.class,e);
                } catch(Exception e) {
                    Log.log(Log.ERROR,CompletionInfo.class,e);
                }
                info = handler.getCompletionInfo();
                completionInfoResources.put(resource,info);
                return info;
            }
        }*/ //}}}

        //{{{ clone() method
        //marked final since this violates standard contract for clone()
        public final Object clone() {
                return new CompletionInfo(
                        (ArrayList)elements.clone(),
                        (HashMap)elementHash.clone(),
                        (ArrayList)entities.clone(),
                        (HashMap)entityHash.clone(),
                        (ArrayList)elementsAllowedAnywhere.clone()
                );
        } //}}}
        
        //{{{ Private Members
        
        //{{{ CompletionInfoHandler class
        /*
        public class CompletionInfoHandler extends DefaultHandler {
            
            //{{{ CompletionInfoHandler constructor
            public CompletionInfoHandler() {
                completionInfo = new CompletionInfo();
                addEntity(new EntityDecl(EntityDecl.INTERNAL,"lt","<"));
                addEntity(new EntityDecl(EntityDecl.INTERNAL,"gt",">"));
                addEntity(new EntityDecl(EntityDecl.INTERNAL,"amp","&"));
                addEntity(new EntityDecl(EntityDecl.INTERNAL,"quot","\""));
                addEntity(new EntityDecl(EntityDecl.INTERNAL,"apos","'"));
            } //}}}
            
            //{{{ getCompletionInfo() method
            public CompletionInfo getCompletionInfo() {
                return completionInfo;
            } //}}}
            
            //{{{ setDocumentLocator() method
            public void setDocumentLocator(Locator loc) {
                this.loc = loc;
            } //}}}
            
            //{{{ resolveEntity() method
            
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
                try {
                    return CatalogManager.resolve(loc.getSystemId(),publicId,systemId);
                } catch(Exception e) {
                    throw new SAXException(e);
                }
            } //}}}
            
            //{{{ startElement() method
            public void startElement(String namespaceURI,
                                     String sName, // simple name
                                     String qName, // qualified name
                                     Attributes attrs) throws SAXException
            {
                if (sName.equals("dtd")) {
                    String extend = attrs.getValue("extend");
                    
                    if (extend != null) {
                        String infoURI = jsXe.getProperty("mode."+extend+".xml.completion-info");
                        if (infoURI != null) {
                            CompletionInfo extendInfo = CompletionInfo.getCompletionInfoFromResource(infoURI);
                            if (extendInfo != null)
                                completionInfo = (CompletionInfo)extendInfo.clone();
                        }
                    }
                } else if (sName.equals("entity")) {
                    
                    addEntity(new EntityDecl(
                        EntityDecl.INTERNAL,
                        attrs.getValue("name"),
                        attrs.getValue("value")));
                    
                } else if(sName.equals("element")) {
                    
                    element = new ElementDecl(completionInfo, attrs.getValue("name"), attrs.getValue("content"));
       
                    completionInfo.elements.add(element);
                    completionInfo.elementHash.put(element.name,element);
       
                    if ("true".equals(attrs.getValue("anywhere"))) {
                        completionInfo.elementsAllowedAnywhere.add(element);
                    }
                
                } else if(sName.equals("attribute")) {
                    String name = attrs.getValue("name");
                    String value = attrs.getValue("value");
                    String type = attrs.getValue("type");
       
                    ArrayList values;
       
                    if(type.startsWith("(")) {
                        values = new ArrayList();
       
                        StringTokenizer st = new StringTokenizer(
                            type.substring(1,type.length() - 1),"|");
                        while (st.hasMoreTokens()) {
                            values.add(st.nextToken());
                        }
                    } else
                        values = null;
       
                    boolean required = "true".equals(attrs.getValue("required"));
       
                    element.addAttribute(new ElementDecl.AttributeDecl(name, value, values, type, required));
                }
            } //}}}
            
            //{{{ Private members
            private CompletionInfo completionInfo;
            private Locator loc;
            private ElementDecl element;
            //}}}
        }*///}}}
        
        
        //}}}
}

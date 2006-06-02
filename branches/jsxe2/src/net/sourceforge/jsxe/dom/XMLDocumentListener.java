/*
XMLDocumentListener.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)

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


package net.sourceforge.jsxe.dom;

/**
 * XMLDocumentListener is used to notify objects of a change to the XMLDocument.
 * These changes can occur via changes to the text or document structure. Both
 * are maintained by the XMLDocument class.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 * @see XMLDocument
 */
public interface XMLDocumentListener {
    
    //{{{ propertyChanged()
    /**
     * Called when a property associated with the XMLDocument has changed.
     * @param source The source XMLDocument
     * @param key the key to the property that changed
     * @param oldValue the previous value of this key, null if there was none
     */
    public void propertyChanged(XMLDocument source, String key, String oldValue);//}}}
    
    //{{{ structureChanged()
    /**
     * Called when the structure of the XMLDocument has changed.
     * @param source The source XMLDocument
     * @param location The AdapterNode location where the change occurred.
     *                 If a node was removed then this is the parent of the
     *                 node that was removed. This could be null if the location
     *                 is unknown.
     * @deprecated insertUpdate and removeUpdate will be used instead.
     */
    public void structureChanged(XMLDocument source, AdapterNode location);//}}}
    
    //{{{ insertUpdate()
    /**
     * Called when text is inserted into the XML document. This will called
     * whenever text is inserted into the document via a normal text update or
     * a change to the document structure, such as a node insertion.
     * @param event the document update event.
     */
    public void insertUpdate(XMLDocumentEvent event);//}}}
    
    //{{{ removeUpdate()
    /**
     * Called when text is removed from the document. This will be whenever
     * text is removed directly or when a change to the document structure is
     * made such as a node is removal.
     * @param event the document update event.
     */
    public void removeUpdate(XMLDocumentEvent event);//}}}
    
    //{{{ changeUpdate()
    /**
     * Called when a part of the XMLDocument is changed in some way. This may
     * occur with changes to properties or attributes within the document
     * structure.
     * @param event the document update event.
     */
    public void changeUpdate(XMLDocumentEvent event);//}}}
}

/*
JARClassLoader.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

A simple class that implements enumeration and iterator
using a simple arraylist

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.util;

//{{{ imports

import java.util.*;

//}}}

public class ArrayListEnumeration implements Enumeration, Iterator {
    
    private ArrayList m_list;
    private int m_index = 0;
    
    //{{{ ArrayListEnumeration constructor
    
    public ArrayListEnumeration(ArrayList list) {
        m_list = list;
    }//}}}
    
    //{{{ Enumeration methods
    
    //{{{ hasMoreElements()
    
    public boolean hasMoreElements() {
        return (m_list.size() > m_index);
    }//}}}
    
    //{{{ nextElement()
    
    public Object nextElement() throws NoSuchElementException {
        try {
            if (hasMoreElements()) {
                return m_list.get(m_index++);
            } else {
                throw new NoSuchElementException("No more elements");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            //shouldn't happen
            throw new NoSuchElementException(e.getMessage());
        }
    }//}}}
    
    //}}}
    
    //{{{ Iterator methods
    
    //{{{ hasNext()
    
    public boolean hasNext() {
        return hasMoreElements();
    }//}}}
    
    //{{{ next()
    
    public Object next() throws NoSuchElementException {
        return nextElement();
    }//}}}
    
    //{{{ remove()
    
    public void remove() throws UnsupportedOperationException {
        //optional operation
        throw new UnsupportedOperationException("cannot call remove on this Iterator");
    }//}}}
    
    //}}}
}

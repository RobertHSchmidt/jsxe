/*
DOMSerializerConfiguration.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the code for the DOMSerializerConfiguration class that is
used to specify options to the DOMSerializer class.

This attempts to conform to the DOM3 implementation in Xerces. It conforms
to DOM3 as of Xerces 2.3.0. I'm not one to stay on the bleeding edge but
there is as close to a standard interface for load & save as you can get and I
didn't want to work around the fact that current serializers aren't very good.
This class name will have to changed because DOMWriter was changed to
DOMSerializer among other changes.

This file written by Ian Lewis (IanLewis@member.fsf.org)

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

//{{{ imports

//{{{ DOM classes
import org.w3c.dom.DOMException;
import org.apache.xerces.dom3.DOMConfiguration;
import org.apache.xerces.dom3.DOMError;
import org.apache.xerces.dom3.DOMErrorHandler;
import org.apache.xerces.dom3.DOMLocator;
import org.apache.xerces.dom3.DOMStringList;
//}}}

//{{{ Java classes
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
//}}}

//}}}

/**
 * <p>DOMSerializerConfiguration is the default implementation of the DOMConfiguration
 * interface to be used with the DOMSerializer class.</p>
 * 
 * <p>Currently, this class only supports the required options with one exception.
 *    The <code>"format-pretty-print"</code> option is supported.</p>
 *
 * @author <a href="mailto:IanLewis at member dot fsf dot org">Ian Lewis</a>
 */
public class DOMSerializerConfiguration implements DOMConfiguration {
    
    public DOMSerializerConfiguration() {//{{{
        
        //set the default boolean parameters for a DOMConfiguration
        setFeature("canonical-form",                false);
        setFeature("cdata-sections",                true);
        setFeature("comments",                      true);
        setFeature("datatype-normalization",        false);
        setFeature("entities",                      false);
        //infoset is not present because it is determined
        //by checking the values of other features.
        setFeature("namespaces",                    true);
        setFeature("namespace-declarations",        true);
        setFeature("normalize-characters",          true);
        setFeature("split-cdata-sections",          true);
        setFeature("validate",                      false);
        setFeature("validate-if-schema",            false);
        setFeature("well-formed",                   true);
        setFeature("element-content-whitespace",    true);
        
        //LSSeraializer features
        setFeature("discard-default-content",       true);
        setFeature("format-pretty-print",           false);
        setFeature("ignore-unknown-character-denormalizations", true);
        setFeature("xml-declaration",               true);
        
        //DOMSerializer parameters
        setFeature("soft-tabs",                     false);
        setParameter("indent",                      new Integer(4));
    }//}}}
    
    public DOMSerializerConfiguration(DOMConfiguration config) throws DOMException {//{{{
        Iterator iterator = m_supportedParameters.iterator();
        while (iterator.hasNext()) {
            String param = iterator.next().toString();
            setParameter(param, config.getParameter(param));
        }
    }//}}}
    
    public boolean canSetParameter(String name, Object value) {///{{{
        
        if (value == null) {
            return (m_supportedParameters.indexOf(name) != -1);
        }
        
        if (value instanceof Boolean) {
            boolean booleanValue = ((Boolean)value).booleanValue();
            
            //couldn't think of a slicker way to do this
            //that was worth the time to implement
            //and extra processing.
            if (name.equals("canonical-form")) {
                return !booleanValue;
            }
            if (name.equals("cdata-sections")) {
                return true;
            }
            if (name.equals("comments")) {
                return true;
            }
            if (name.equals("datatype-normalization")) {
                return true;
            }
            if (name.equals("entities")) {
                return true;
            }
            if (name.equals("well-formed")) {
                return true;
            }
            if (name.equals("infoset")) {
                return true;
            }
            if (name.equals("namespaces")) {
                return true;
            }
            if (name.equals("namespace-declarations")) {
                return true;
            }
            if (name.equals("normalize-characters")) {
                return true;
            }
            if (name.equals("split-cdata-sections")) {
                return true;
            }
            if (name.equals("validate")) {
                return !booleanValue;
            }
            if (name.equals("validate-if-schema")) {
                return !booleanValue;
            }
            if (name.equals("element-content-whitespace")) {
                return true;
            }
            
            if (name.equals("discard-default-content")) {
                return true;
            }
            if (name.equals("format-pretty-print")) {
                return true;
            }
            if (name.equals("ignore-unknown-character-denormalizations")) {
                return booleanValue;
            }
            if (name.equals("xml-declaration")) {
                return true;
            }
            return false;
        } else {
            if (name.equals("error-handler")) {
                if (value instanceof DOMErrorHandler || value == null) {
                    return true;
                }
            }
            if (name.equals("indent")) {
                if (value instanceof Integer || value == null) {
                    return true;
                }
            }
        }
        return false;
    }//}}}
    
    public Object getParameter(String name) throws DOMException {//{{{
        
        if (m_supportedParameters.indexOf(name) != -1) {
            
            if (name == "infoset") {
                boolean namespaceDeclarations = getFeature("namespace-declarations");
                boolean validateIfSchema      = getFeature("validate-if-schema");
                boolean entities              = getFeature("entities");
                boolean datatypeNormalization = getFeature("datatype-normalization");
                boolean cdataSections         = getFeature("cdata-sections");
                
                boolean whitespace = getFeature("whitespace-in-element-content");
                boolean comments   = getFeature("comments");
                boolean namespaces = getFeature("namespaces");
                
                return (new Boolean(!namespaceDeclarations &&
                        !validateIfSchema &&
                        !entities &&
                        !datatypeNormalization &&
                        !cdataSections &&
                        whitespace &&
                        comments &&
                        namespaces));
            } else {
                return m_parameters.get(name);
            }
            
        } else {
            
            throw new DOMException(DOMException.NOT_FOUND_ERR ,"NOT_FOUND_ERR: Parameter "+name+" not recognized");
            
        }
    }//}}}
    
    public DOMStringList getParameterNames() {//{{{
        return new DOMStringListImpl(m_supportedParameters);
    }//}}}
    
    public void setParameter(String name, Object value) throws DOMException {//{{{
        
        //if a string, attempt to use it as a boolean value.
        if (value instanceof String) {
            value = new Boolean((String)value);
        }
        
        if (m_supportedParameters.indexOf(name) != -1) {
            if ( value != null ) {
                if (canSetParameter(name, value)) {
                    /*
                    if the parameter is infoset
                    then force the other parameters to
                    values that the infoset option
                    requires.
                    */
                    if (name.equals("infoset")) {
                        setFeature("namespace-declarations",false);
                        setFeature("validate-if-schema",    false);
                        setFeature("entities",              false);
                        setFeature("datatype-normalization",false);
                        setFeature("cdata-sections",        false);
                        
                        setFeature("element-content-whitespace",    true);
                        setFeature("comments",                      true);
                        setFeature("namespaces",                    true);
                        return;
                    }
                    if (name.equals("format-pretty-print") && ((Boolean)value).booleanValue()) {
                        /*
                        setting element-content-whitespaces to false
                        because I'm not sure how to pretty print the document
                        and preserve whitespace in element content at the same
                        time.
                        */
                        setFeature("element-content-whitespace", false);
                        setFeature("canonical-form", false);
                    }
                    if (name.equals("element-content-whitespace") && ((Boolean)value).booleanValue()) {
                        setFeature("format-pretty-print", false);
                    }
                    
                    m_parameters.put(name, value);
                    
                } else {
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Parameter "+name+" and value "+value.toString()+" not supported.");
                }
            } else {
                m_parameters.remove(name);
            }
        } else {
            throw new DOMException(DOMException.NOT_FOUND_ERR, "Parameter "+name+" is not recognized.");
        }
    }//}}}
    
    /**
     * <p>A convenience method to retrieve the value that a boolean
     * parameter (feature) is set to.</p>
     * @param name The name of the feature to get the value of
     * @return The current setting for the given feature
     */
    public boolean getFeature(String name) throws DOMException {//{{{
        /*
        we know these aren't features (true or false) so provide a little better
        error handling.
        */
        if (name.equals("error-handler") || name.equals("indent")) {
            throw new DOMException(DOMException.NOT_FOUND_ERR, "NOT_FOUND_ERR: "+name+" is not a feature.");
        }
        return ((Boolean)getParameter(name)).booleanValue();
        
    }//}}}
    
    /**
     * <p>A convenience method to set the value of a boolean parameter (feature)</p>
     * @param name The feature to set the value of
     * @param value The boolean value to set to the feature
     */
    public void setFeature(String name, boolean value) throws DOMException {//{{{
        setParameter(name, new Boolean(value));
    }//}}}
    
    //{{{ private members
    
    private class DOMStringListImpl implements DOMStringList {//{{{
        
        public DOMStringListImpl(ArrayList list) {//{{{
            m_list = list;
        }//}}}
        
        public boolean contains(String str) {//{{{
            for (int i=0; i<m_list.size(); i++) {
                if (m_list.get(i).toString().equals(str)) {
                    return true;
                }
            }
            return false;
        }//}}}
        
        public int getLength() {//{{{
            return m_list.size();
        }//}}}
        
        public String item(int index) {//{{{
            return m_list.get(index).toString();
        }//}}}
        
        private ArrayList m_list;
        
    }//}}}
    
    private static ArrayList m_supportedParameters = null;
    private Hashtable m_parameters = new Hashtable(16);
    
    //}}}

    static {//{{{
        //create a vector of the supported parameters
        m_supportedParameters = new ArrayList(22);
        
        //DOMConfiguration defined parameters
        m_supportedParameters.add("canonical-form");
        m_supportedParameters.add("cdata-sections");
        m_supportedParameters.add("check-character-normalization");
        m_supportedParameters.add("comments");
        m_supportedParameters.add("datatype-normalization");
        m_supportedParameters.add("entities");
        m_supportedParameters.add("error-handler");
        m_supportedParameters.add("infoset");
        m_supportedParameters.add("namespaces");
        m_supportedParameters.add("namespace-declarations");
        m_supportedParameters.add("normalize-characters");
        m_supportedParameters.add("split-cdata-sections");
        m_supportedParameters.add("validate");
        m_supportedParameters.add("validate-if-schema");
        m_supportedParameters.add("well-formed");
        m_supportedParameters.add("element-content-whitespace");
        
        //LSSerializer defined parameters
        m_supportedParameters.add("discard-default-content");
        m_supportedParameters.add("format-pretty-print");
        m_supportedParameters.add("ignore-unknown-character-denormalizations");
        m_supportedParameters.add("xml-declaration");
        
        //Additional parameters supported by DOMSerializerConfiguration
        m_supportedParameters.add("soft-tabs");
        m_supportedParameters.add("indent");
    }//}}}
}

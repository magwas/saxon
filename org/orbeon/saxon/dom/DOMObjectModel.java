package net.sf.saxon.dom;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.ExternalObjectModel;
import net.sf.saxon.om.VirtualNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface must be implemented by any third-party object model that can
 * be wrapped with a wrapper that implements the Saxon Object Model (the NodeInfo interface).
 * This implementation of the interface supports wrapping of DOM Documents.
 */

public class DOMObjectModel implements ExternalObjectModel {

    public DOMObjectModel() {}

     /**
     * Test whether this object model recognizes a given node as one of its own
     */

    public boolean isRecognizedNode(Object object) {
         return object instanceof Node;
    }

    /**
     * Wrap a document node in the external object model in a document wrapper that implements
     * the Saxon DocumentInfo interface
     * @param node    a node (any node) in the third party document
     * @param baseURI the base URI of the node (supply "" if unknown)
     * @param config the Saxon configuration (which among other things provides access to the NamePool)
     * @return the wrapper, which must implement DocumentInfo
     */ 
    
    public DocumentInfo wrapDocument(Object node, String baseURI, Configuration config) {
        Document doc;
        if (node instanceof org.w3c.dom.Document) {
            doc = (org.w3c.dom.Document)node;
        } else {
            doc = ((org.w3c.dom.Document)node).getOwnerDocument();
        }
        return new DocumentWrapper(doc, baseURI, config);
    }

    /**
     * Wrap a node within the external object model in a node wrapper that implements the Saxon
     * VirtualNode interface (which is an extension of NodeInfo)
     * @param document the document wrapper, as a DocumentInfo object
     * @param node the node to be wrapped. This must be a node within the document wrapped by the
     * DocumentInfo provided in the first argument
     * @return the wrapper for the node, as an instance of VirtualNode
     */ 
    
    public VirtualNode wrapNode(DocumentInfo document, Object node) {
        return ((DocumentWrapper)document).wrap((Node)node);
    }
}


//
// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the
// License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations under the License.
//
// The Original Code is: all this file.
//
// The Initial Developer of the Original Code is Michael H. Kay.
//
// Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
// Contributor(s): Gunther Schadow (changes to allow access to public fields; also wrapping
// of extensions and mapping of null to empty sequence).
//

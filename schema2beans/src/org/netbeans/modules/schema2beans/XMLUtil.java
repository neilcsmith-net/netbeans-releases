/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.schema2beans;

import java.io.*;
import org.w3c.dom.*;

public class XMLUtil {
    private XMLUtil() {}

    /**
     * Takes some text to be printed into an XML stream and escapes any
     * characters that might make it invalid XML (like '<').
     */
    public static void printXML(StringBuffer out, String msg) {
        printXML(out, msg, true);
    }

    public static void printXML(StringBuffer out, String msg, boolean attribute) {
        if (msg == null)
            return;
        int msgLength = msg.length();
        for (int i = 0; i < msgLength; ++i) {
            char c = msg.charAt(i);
            printXML(out, c, attribute);
        }
    }

    public static void printXML(StringBuffer out, char msg, boolean attribute) {
        if (msg == '&')
            out.append("&amp;");
        else if (msg == '<')
            out.append("&lt;");
        else if (msg == '>')
            out.append("&gt;");
        else if (attribute) {
            if (msg == '"')
                out.append("&quot;");
            else if (attribute && msg == '\'')
                out.append("&apos;");
            else if (attribute && msg == '\n')
                out.append("&#xA");
            else if (attribute && msg == '\t')
                out.append("&#x9");
            else
                out.append(msg);
        } else
            out.append(msg);
    }

	/**
	 * Takes some text to be printed into an XML stream and escapes any
	 * characters that might make it invalid XML (like '<').
	 */
	public static void writeXML(java.io.Writer out, String msg) throws java.io.IOException {
		writeXML(out, msg, true);
	}

	public static void writeXML(java.io.Writer out, String msg, boolean attribute) throws java.io.IOException {
		if (msg == null)
			return;
		int msgLength = msg.length();
		for (int i = 0; i < msgLength; ++i) {
			char c = msg.charAt(i);
			writeXML(out, c, attribute);
		}
	}

	public static void writeXML(java.io.Writer out, char msg, boolean attribute) throws java.io.IOException {
		if (msg == '&')
			out.write("&amp;");
		else if (msg == '<')
			out.write("&lt;");
		else if (msg == '>')
			out.write("&gt;");
		else if (attribute) {
            if (msg == '"')
                out.write("&quot;");
            else if (msg == '\'')
                out.write("&apos;");
            else if (msg == '\n')
                out.write("&#xA;");
            else if (msg == '\t')
                out.write("&#x9;");
            else
                out.write(msg);
        } else
			out.write(msg);
	}

    public static boolean shouldEscape(char c) {
        if (c == '&')
            return true;
        else if (c == '<')
            return true;
        else if (c == '>')
            return true;
        return false;
    }

    public static boolean shouldEscape(String s) {
        if (s == null)
            return false;
        int msgLength = s.length();
        for (int i = 0; i < msgLength; ++i) {
            char c = s.charAt(i);
            if (shouldEscape(c))
                return true;
        }
        return false;
    }

    /**
     * Takes some text to be printed into an XML stream and escapes any
     * characters that might make it invalid XML (like '<').
     */
    public static void printXML(java.io.Writer out, String msg) throws java.io.IOException {
        printXML(out, msg, true);
    }

    public static void printXML(java.io.Writer out, String msg, boolean attribute) throws java.io.IOException {
        if (msg == null)
            return;
        int msgLength = msg.length();
        for (int i = 0; i < msgLength; ++i) {
            char c = msg.charAt(i);
            printXML(out, c, attribute);
        }
    }

    public static void printXML(java.io.Writer out, char msg, boolean attribute) throws java.io.IOException {
        if (msg == '&')
            out.write("&amp;");
        else if (msg == '<')
            out.write("&lt;");
        else if (msg == '>')
            out.write("&gt;");
        else if (attribute && msg == '"')
            out.write("&quot;");
        else if (attribute && msg == '\'')
            out.write("&apos;");
        else if (attribute && msg == '\n')
            out.write("&#xA;");
        else if (attribute && msg == '\t')
            out.write("&#x9;");
        else
            out.write(msg);
    }

    public static class DOMWriter {
        private java.io.Writer out;
        private boolean writeCData = false;
        private String docTypePublic;
        private String docTypeSystem;
        
        public DOMWriter() {
        }

        public void setWriter(java.io.Writer out) {
            this.out = out;
        }
        
        public void setWriteCData(boolean value) {
            writeCData = value;
        }

        public void setDocTypePublic(String value) {
            docTypePublic = value;
        }

        public void setDocTypeSystem(String value) {
            docTypeSystem = value;
        }

        /**
         * Same as write(OutputStream os, String encoding, Document document)
         * where encoding == null.
         */
        public void write(OutputStream os, Document document) throws java.io.IOException {
            write(os, null, document);
        }

        /**
         * Create an output Writer based on the OutputStream using the
         * encoding (use "UTF-8" if encoding == null), then write the DOM
         * graph out.
         */
        public void write(OutputStream os, String encoding, Document document) throws java.io.IOException {
            if (encoding == null)
                encoding = "UTF-8";
            out = new BufferedWriter(new OutputStreamWriter(os, encoding));
            write(document, encoding);
        }

        /**
         * Assumes that the output Writer has already been set.
         */
        public void write(Document document) throws java.io.IOException {
            write(document, null);
        }
        
        /**
         * Assumes that the output Writer has already been set.
         * @param encoding goes into the XML header.
         */
        public void write(Document document, String encoding) throws java.io.IOException {
            write(document, encoding, true);
        }
        
        /**
         * Assumes that the output Writer has already been set.
         * @param encoding goes into the XML header.
         * @param writeHeader whether or not the "<?xml ..." header gets
         *                    written out as well.
         */
        public void write(Document document, String encoding,
                          boolean writeHeader) throws java.io.IOException {
            if (writeHeader) {
                out.write("<?xml version=\"1.0\"");    // NOI18N
                if (encoding != null) {
                    out.write(" encoding=\""+encoding+"\"?>\n");    // NOI18N
                } else
                    out.write(" encoding=\"UTF-8\"?>\n");    // NOI18N
            }
            if (docTypePublic != null || docTypeSystem != null) {
                String docName = getDocTypeName(document);
                DocumentType docType = document.getDoctype();
                NamedNodeMap entities = null;
                if (docType != null)
                    entities = docType.getEntities();
                write(docName, docTypePublic, docTypeSystem, entities);
                out.write("\n");
            }
            NodeList children = document.getChildNodes();
            int length = children.getLength();
            // First print out any DocumentTypes
            for (int i = 0; i < length; ++i) {
                Node node = children.item(i);
                if (node instanceof DocumentType) {
                    write(node);
                    out.write("\n");
                }
            }
            // Now print everything, but DocumentTypes
            for (int i = 0; i < length; ++i) {
                Node node = children.item(i);
                if (!(node instanceof DocumentType)) {
                    write(node);
                    out.write("\n");
                }
            }

            out.flush();
        }
    
       public void write(Node node) throws java.io.IOException {
            boolean needsReturnBetweenChildren = false;

            NodeList children = node.getChildNodes();
            if (node instanceof Element) {
                out.write("<"+node.getNodeName());
                write(node.getAttributes());
                if (children.getLength() == 0 ||
                    (children.getLength() == 1 &&
                     children.item(0) instanceof Text &&
                     "".equals(children.item(0).getNodeValue()) )) {
                    out.write("/>");
                    return;
                }
                out.write(">");
            } else if (node instanceof Text) {
                printXML(node.getNodeValue(), false);
            } else if (node instanceof Document) {
                needsReturnBetweenChildren = true;
            } else if (node instanceof DocumentType) {
                write((DocumentType) node);
            } else if (node instanceof Comment) {
                write((Comment) node);
            } else if (node instanceof Entity) {
                write((Entity) node);
            } else if (node instanceof ProcessingInstruction) {
                write((ProcessingInstruction) node);
            } else {
                System.err.println("! schema2beans found unknown node type in DOM graph:");
                System.err.println("write: node.getClass="+node.getClass()+" node="+node);
                System.err.println("write: nodename="+node.getNodeName()+" nodevalue="+node.getNodeValue());
                System.err.println("write: getAttributes="+node.getAttributes());
            }
        
            int length = children.getLength();
            for (int i = 0; i < length; ++i) {
                write(children.item(i));
                if (needsReturnBetweenChildren)
                    out.write("\n");
            }
            if (node instanceof Element) {
                out.write("</"+node.getNodeName()+">");
            }
        }

        protected void write(DocumentType docType) throws java.io.IOException {
            //System.out.println("! FOUND DOCTYPE for "+docType.getName());
            if (docTypePublic != null || docTypeSystem != null) {
                // The header printing has already taken care of the DOCTYPE.
                return;
            }
            write(docType.getName(), docType.getPublicId(),
                  docType.getSystemId(), docType.getEntities());
        }

        protected void write(String docName, String publicId,
                             String systemId, NamedNodeMap entities) throws java.io.IOException {
            out.write("<!DOCTYPE "+docName);	// NOI18N
            if (publicId != null) {
                out.write(" PUBLIC \"");	// NOI18N
                XMLUtil.printXML(out, publicId);
                out.write("\"");	// NOI18N
                if (systemId == null)
                    systemId = "SYSTEM";	// NOI18N
            }
            if (systemId != null) {
                out.write(" \"");	// NOI18N
                XMLUtil.printXML(out, systemId);
                out.write("\"");	// NOI18N
            }
            if (entities != null) {
                int length = entities.getLength();
                if (length > 0) {
                    out.write(" [");	// NOI18N
                    for (int i = 0; i < length; ++i) {
                        Node node = entities.item(i);
                        write(node);
                    }
                    out.write("]");	// NOI18N
                }
            }
            out.write(">");	// NOI18N
        }

        protected void write(Comment comment) throws java.io.IOException {
            // Does not need to have anything escaped (no printXML).
            out.write("<!--");
            String text = comment.getNodeValue();
            // A comment is not allow to have "--" inside of it.
            int pos = text.indexOf("--");
            while (pos >= 0) {
                out.write(text.substring(0, pos));
                out.write("&#x2d;&#x2d;");
                text = text.substring(pos+2, text.length());
                pos = text.indexOf("--");
            }
            out.write(text);
            out.write("-->");
        }

        protected void write(Entity entity) throws java.io.IOException {
            out.write("<!ENTITY "+entity.getNodeName());
            /*
              We don't seem to be able to get any useful info out of the
              Entity object.
          
              out.write(" notation ");
              if (entity.getNotationName() != null)
              out.write(entity.getNotationName());
              out.write(" publicid ");
              if (entity.getPublicId() != null)
              out.write(entity.getPublicId());
              out.write(" systemid ");
              if (entity.getSystemId() != null)
              out.write(entity.getSystemId());
            */
            out.write(" UNKNOWN>");
        }

        protected void write(ProcessingInstruction pi) throws java.io.IOException {
            // Does not need to have anything escaped (no printXML).
            if ("xml".equals(pi.getTarget())) {
                // We've already printed out the standard xml PI, suppress this one.
                return;
            }
            out.write("<?"+pi.getTarget()+" "+pi.getData()+"?>");
        }

        /**
         * This is used to print attributes.
         */
        protected void write(NamedNodeMap nodes) throws java.io.IOException {
            int length = nodes.getLength();
            for (int i = 0; i < length; ++i) {
                Node node = nodes.item(i);
                out.write(" ");
                out.write(node.getNodeName());
                out.write("=\"");
                XMLUtil.printXML(out, node.getNodeValue());
                out.write("\"");
            }
        }

        protected void printXML(String msg, boolean attribute) throws java.io.IOException {
            if (writeCData && msg.indexOf("]]>") < 0) {
                boolean shouldEscape = XMLUtil.shouldEscape(msg);
                if (shouldEscape)
                    out.write("<![CDATA[");
                out.write(msg);
                if (shouldEscape)
                    out.write("]]>");
            } else
                XMLUtil.printXML(out, msg, attribute);
        }
    }

    // Given @param doc what should it's DOCTYPE name be.
    static protected String getDocTypeName(Document doc) {
        // First look for a DOCTYPE
        NodeList children = doc.getChildNodes();
        int length = children.getLength();
        for (int i = 0; i < length; ++i) {
            Node node = children.item(i);
            if (node instanceof DocumentType) {
                DocumentType docType = (DocumentType) node;
                return docType.getName();
            }
        }
        // Otherwise, check the first node of the actual document
        Node rootNode = doc.getDocumentElement();
        return rootNode.getNodeName();
    }

    /**
     * Reformat the DOM graph to make it look like pretty XML.
     *
     * @param doc The Document to create new TextNodes from.
     * @param indent The String used to indent per level
     */
    public static void reindent(Document doc, String indent) {
        reindent(doc, doc, -1, indent);
    }
    
    /**
     * Reformat the DOM graph to make it look like pretty XML.
     *
     * @param doc The Document to create new TextNodes from.
     * @param node The top of the tree to reindent from.
     * @param indent The String used to indent per level
     * @param level How far in to reindent
     * @return true if node is a Text node that has only whitespace
     */
    public static boolean reindent(Document doc, Node node,
                                   int level, String indent) {
        String nodeValue = node.getNodeValue();

        boolean hasOnlyWhitespaceTextChildren = true;
        NodeList children = node.getChildNodes();
        int length = children.getLength();
        for (int i = 0; i < length; ++i) {
            if (!reindent(doc, children.item(i), level+1, indent))
                hasOnlyWhitespaceTextChildren = false;
        }

        /*
        try {
            printLevel(System.out, level, indent,
                       node.getNodeName()+": \""+nodeValue+"\"\n");
            printLevel(System.out, level, indent,
                       "hasOnlyWhitespaceTextChildren="+hasOnlyWhitespaceTextChildren+"\n");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        */

        if (hasOnlyWhitespaceTextChildren && level >= 0  && length > 0) {
            // We can reindent this one.  So, go thru each child node
            // and make sure it's intendation is where we want it.
            
            StringBuffer idealWhitespaceBuf = new StringBuffer();
            printLevel(idealWhitespaceBuf, level, indent);
            String idealFinalWhitespace = "\n" + idealWhitespaceBuf.toString().intern();
            printLevel(idealWhitespaceBuf, 1, indent);
            String idealChildWhitespace = "\n"+idealWhitespaceBuf.toString().intern();
            //System.out.println("idealChildWhitespace='"+idealChildWhitespace+"'");
            //
            // Check to make sure the last child node is a text node.
            // If not, insert the correct spacing at the end.
            //
            if (length > 1 && !(children.item(length-1) instanceof Text)) {
                //System.out.println("Inserting additional whitespace at end of child list.");
                node.appendChild(doc.createTextNode(idealFinalWhitespace));
                ++length;
            }
            //System.out.println("node.getNodeName="+node.getNodeName()+" children.length="+length);
            
            boolean shouldBeTextNode = true;  // This alternates
            Text textNode;
            for (int i = 0; i < length; ++i) {
                Node childNode = children.item(i);
                boolean isTextNode = (childNode instanceof Text);
                //System.out.println("shouldBeTextNode="+shouldBeTextNode+" isTextNode="+isTextNode+" "+childNode.getNodeName());
                if (shouldBeTextNode) {
                    if (isTextNode) {
                        String childNodeValue = childNode.getNodeValue().intern();
                        if (length == 1) {
                            // We have a single text child, don't mess with
                            // it's contents.
                            continue;
                        }
                        
                        textNode = (Text) childNode;
                        // Need to make sure it has the correct whitespace
                        if (i == length-1) {
                            if (idealFinalWhitespace != childNodeValue) {
                                //System.out.println("!Incorrect whitespace on final!");
                                if (textNode.getLength() > 0)
                                    textNode.deleteData(0, textNode.getLength());
                                textNode.appendData(idealFinalWhitespace);
                            }
                            
                        } else {
                            if (idealChildWhitespace != childNodeValue) {
                                //System.out.println("!Incorrect whitespace: '"+childNodeValue+"' versus ideal of '"+idealChildWhitespace+"'");
                                textNode.deleteData(0, textNode.getLength());
                                textNode.appendData(idealChildWhitespace);
                            }
                        }
                        shouldBeTextNode ^= true;
                    } else {
                        // Need to insert a whitespace node
                        //System.out.println("Need to insert a whitespace node before "+childNode.getNodeName()+": "+childNode.getNodeValue());
                        if (i == length-1) {
                            //System.out.println("It's a final one!");
                            node.insertBefore(doc.createTextNode(idealChildWhitespace), childNode);
                            node.appendChild(doc.createTextNode(idealFinalWhitespace));
                            ++length;
                        } else {
                            //System.out.println("Not final.");
                            node.insertBefore(doc.createTextNode(idealChildWhitespace), childNode);
                        }
                        //
                        // We updated our list while going thru it at the same
                        // time, so update our indices to account for the
                        // new growth.
                        //
                        ++i;  
                        ++length;
                    }
                } else {
                    if (isTextNode) {
                        // The last whitespace node is correct, so this one
                        // must be extra.
                        //System.out.println("Extra unneeded whitespace");
                        node.removeChild(childNode);
                        --i;
                        --length;
                        if (i == length-1 && i >= 0) {
                            //System.out.println("It's a final one!");
                            // Go back and fix up the last node.
                            childNode = children.item(i);
                            String childNodeValue = childNode.getNodeValue().intern();
                            if (idealFinalWhitespace != childNodeValue) {
                                textNode = (Text) childNode;
                                //System.out.println("!Incorrect whitespace on final!");
                                if (textNode.getLength() > 0)
                                    textNode.deleteData(0, textNode.getLength());
                                textNode.appendData(idealFinalWhitespace);
                            }
                        }
                    } else {
                        // This is just right.
                        //System.out.println("This is just right.");
                        shouldBeTextNode ^= true;
                    }
                }
            }
        }

        // Let my caller know if I'm a Text node that has only whitespace
        // or not.
        if (node instanceof Text) {
            if (nodeValue == null)
                return true;
            return (nodeValue.trim().equals(""));
        }
        return true;
    }

    protected static void printLevel(StringBuffer out, int level, String indent) {
        for (int i = 0; i < level; ++i) {
            out.append(indent);
        }
    }
}

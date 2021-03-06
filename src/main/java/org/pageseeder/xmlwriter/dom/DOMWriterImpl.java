/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.xmlwriter.dom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.pageseeder.xmlwriter.IllegalCloseElementException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * A simple implementation of a DOM writer
 *
 * <p>Provides methods to generate well-formed XML data easily via DOM.
 *
 * @author Christophe Lauret
 */
public final class DOMWriterImpl implements DOMWriter {

  // Class attributes
  // ----------------------------------------------------------------------------------------------

  /**
   * The DOM document on which we write.
   */
  private final Document _document;

  /**
   * The new line used.
   */
  private final Node _newline;

  /**
   * Indicates whether the xml should be indented or not.
   *
   * <p>The default is <code>true</code> (indented).
   *
   * <p>The indentation is 2 white-spaces.
   */
  private boolean indent;

  /**
   * The default indentation spaces used.
   */
  private String indentChars;

  // state variables
  // ----------------------------------------------------------------------------------------------

  /**
   * Level of the depth of the xml document currently produced.
   *
   * <p>This attribute changes depending on the state of the instance.
   */
  private int depth;

  /**
   * Flag to indicate that the element open tag is not finished yet.
   */
  private boolean isNude;

  /**
   * The current node being written onto.
   *
   * <p>This node should always be an element except before and after writing where it
   * is the document node itself.
   */
  private Node currentElement;

  /**
   * An array to indicate which elements have children.
   */
  private List<Boolean> childrenFlags = new ArrayList<Boolean>();

  // Constructors
  // ----------------------------------------------------------------------------------------------

  /**
   * <p>Creates a new XML writer for DOM using the default implementation on
   * the system.
   *
   * <p>Attempts to create the DOM document using:
   * <pre>
   *  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
   *  DocumentBuilder builder = factory.newDocumentBuilder();
   *  Document document = builder.newDocument();
   * </pre>
   *
   * @throws ParserConfigurationException If thrown by the document builder factory.
   */
  public DOMWriterImpl() throws ParserConfigurationException {
    this(newDocument());
  }

  /**
   * <p>Creates a new XML writer for DOM.
   *
   * @param document The DOM provided.
   *
   * @throws NullPointerException If the handler is <code>null</code>.
   */
  public DOMWriterImpl(Document document) {
    if (document == null)
      throw new NullPointerException("The XMLWriter requires a DOM Document to write on.");
    this._document = document;
    this.currentElement = document;
    this._newline = document.createTextNode("\n");
  }

  // Setup methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Does nothing.
   */
  @Override
  public void xmlDecl() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setIndentChars(String spaces)  {
    if (this.depth != 0)
      throw new IllegalStateException("To late to set the indentation characters!");
    // check that this is a valid indentation string
    if (spaces != null) {
      for (int i = 0; i < spaces.length(); i++) {
        if (!Character.isSpaceChar(spaces.charAt(i)))
          throw new IllegalArgumentException("Not a valid indentation string.");
      }
    }
    // update the flags
    this.indentChars = spaces;
    this.indent = spaces != null;
  }

  // Write text methods
  // ----------------------------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void writeText(String text) {
    if (text == null) return;
    deNude();
    Text textNode = this._document.createTextNode(text);
    this.currentElement.appendChild(textNode);
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void writeText(char[] text, int off, int len) {
    this.writeText(new String(text, off, len));
  }

  /**
   * This method is expensive as the character has to be converted to a String for DOM.
   *
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void writeText(char c) {
    this.writeText(new String(new char[]{c}));
  }

  /**
   * Writes the string value of an object.
   *
   * <p>Does nothing if the object is <code>null</code>.
   *
   * @see Object#toString
   *
   * @param o The object that should be written as text.
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  public void writeText(Object o) {
    // TODO: what about an XML serializable ???
    // TODO: Add to interface ???
    if (o != null) {
      this.writeText(o.toString());
    }
  }

  /**
   * Writes the CDATA section to the DOM.
   *
   * <p>Does nothing if the object is <code>null</code>.
   *
   * @param data The data to write to the section.
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void writeCDATA(String data) {
    if (data == null) return;
    this._document.createCDATASection(data);
  }

  // Write xml methods are not supported
  // ----------------------------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @throws UnsupportedOperationException XML cannot be written to the DOM
   */
  @Override
  public void writeXML(String text) {
    throw new UnsupportedOperationException("Cannot use unparsed XML as DOM node.");
  }

  /**
   * {@inheritDoc}
   *
   * @throws UnsupportedOperationException XML cannot be written to the DOM
   */
  @Override
  public void writeXML(char[] text, int off, int len)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Cannot use unparsed XML as DOM node.");
  }

  // PI and comments
  // ----------------------------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void writeComment(String comment) throws DOMException {
    if (comment.indexOf("--") >= 0)
      throw new IllegalArgumentException("A comment must not contain '--'.");
    deNude();
    Node node = this._document.createComment(comment);
    this.currentElement.appendChild(node);
    if (this.indent) {
      newLine();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void writePI(String target, String data) throws DOMException {
    deNude();
    Node node = this._document.createProcessingInstruction(target, data);
    this.currentElement.appendChild(node);
    if (this.indent) {
      newLine();
    }
  }

  // Attribute methods
  // ----------------------------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void attribute(String name, String value) throws DOMException {
    if (!this.isNude)
      throw new IllegalArgumentException("Cannot write attribute: too late!");
    Attr att = this._document.createAttribute(name);
    att.setValue(value);
    this.currentElement.appendChild(att);
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void attribute(String name, int value) throws DOMException {
    attribute(name, Integer.toString(value));
  }

  // Open/close specific elements
  // ----------------------------------------------------------------------------------------------

  /**
   * Writes a start element tag correctly indented.
   *
   * <p>It is the same as <code>openElement("", name, false)</code>
   *
   * @see #openElement(java.lang.String, java.lang.String, boolean)
   *
   * @param name the name of the element
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void openElement(String name) throws DOMException {
    openElement(name, false);
  }

  /**
   * Writes a start element tag correctly indented.
   *
   * <p>Use the <code>hasChildren</code> parameter to specify whether this element is terminal
   * node or not, note: this affects the indenting. To produce correctly indented XML, you
   * should use the same value for this flag when closing the element.
   *
   * <p>The name can contain attributes and should be a valid xml name.
   *
   * @param name        The name of the element.
   * @param hasChildren <code>true</code> if this element has children.
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void openElement(String name, boolean hasChildren) throws DOMException {
    deNude();
    indent();
    this.childrenFlags.add(Boolean.valueOf(hasChildren));
    Element element = this._document.createElement(name);
    this.currentElement.appendChild(element);
    this.currentElement = element;
    this.isNude = true;
    this.depth++;
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void element(String name, String text) throws DOMException {
    this.openElement(name);
    this.writeText(text);
    closeElement();
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void closeElement() throws DOMException, IllegalCloseElementException {
    if (this.currentElement.getNodeType() == Node.DOCUMENT_NODE)
      throw new IllegalCloseElementException();
    this.depth--;
    this.isNude = false;
    Boolean hasChildren = this.childrenFlags.remove(this.childrenFlags.size() - 1);
    if (hasChildren.booleanValue()) {
      indent();
    }
    this.currentElement.normalize();
    this.currentElement = this.currentElement.getParentNode();
    // new line if parent has children
    if (this.indent) {
      Boolean b = this.childrenFlags.get(this.childrenFlags.size() - 1);
      if (b.booleanValue()) {
        newLine();
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void emptyElement(String name) throws DOMException {
    Element element = this._document.createElement(name);
    this.currentElement.appendChild(element);
  }

  // direct access to the writer ----------------------------------------------------------

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void close() {
    // Do nothing
  }

  /**
   * Normalises the current element.
   */
  @Override
  public void flush() {
    this.currentElement.normalize();
  }

  // DOM Writer methods -------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public Document getDocument() {
    return this._document;
  }

  // unsupported operations -------------------------------------------------------------------

  /**
   * Not supported.
   *
   * @param uri  This parameter is ignored.
   * @param name This parameter is ignored.
   *
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  public void openElement(String uri, String name) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces.");
  }

  /**
   * Not supported.
   *
   * @param uri         This parameter is ignored.
   * @param name        This parameter is ignored.
   * @param hasChildren This parameter is ignored.
   *
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  @Override
  public void openElement(String uri, String name, boolean hasChildren)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces.");
  }

  /**
   * Not supported.
   *
   * @param uri     This parameter is ignored.
   * @param element This parameter is ignored.
   *
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  @Override
  public void emptyElement(String uri, String element)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Not supported.
   *
   * @param uri    This parameter is ignored.
   * @param prefix This parameter is ignored.
   *
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  @Override
  public void setPrefixMapping(String uri, String prefix)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Not supported.
   *
   * @param uri    This parameter is ignored.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  @Override
  public void attribute(String uri, String name, String value)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Not supported.
   *
   * @param uri    This parameter is ignored.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  @Override
  public void attribute(String uri, String name, int value)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  // private helpers ----------------------------------------------------------------------

  /**
   * Insert the correct amount of space characterss depending on the depth and if
   * the <code>indent</code> flag is set to <code>true</code>.
   */
  void indent() {
    if (this.indent) {
      StringBuffer out = new StringBuffer(this.depth * this.indentChars.length());
      for (int i = 0; i < this.depth; i++) {
        out.append(this.indentChars);
      }
      Node node = this._document.createTextNode(out.toString());
      this.currentElement.appendChild(node);
    }
  }

  /**
   * Writes the angle bracket if the element open tag is not finished.
   */
  private void deNude() {
    if (this.isNude) {
      if (this.indent) { //TODO: hasChildren
        newLine();
      }
      this.isNude = false;
    }
  }

  /**
   * Adds a new line to the DOM.
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  private void newLine() {
    this.currentElement.appendChild(this._newline.cloneNode(false));
  }

  /**
   * Returns a new DOM document.
   *
   * <p>Attempts to create the DOM document using:
   * <pre>
   *  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
   *  DocumentBuilder builder = factory.newDocumentBuilder();
   *  Document document = builder.newDocument();
   * </pre>
   *
   * @return A new DOM document.
   *
   * @throws ParserConfigurationException If thrown by the document builder factory.
   */
  private static Document newDocument() throws ParserConfigurationException {
    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    return builder.newDocument();
  }

}

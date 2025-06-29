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

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jetbrains.annotations.NotNull;
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
 *
 * @since 1.0.0
 * @version 1.1.0
 */
public final class DOMWriterImpl implements DOMWriter {

  /**
   * The DOM document on which we write.
   */
  private final Document document;

  /**
   * The new line used.
   */
  private final Node newline;

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

  /**
   * State variable indicating the depth level the current XML context in the document.
   */
  private int depth;

  /**
   * Flag to indicate when the element open tag is complete.
   */
  private boolean isOpenTagComplete = true;

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
  private final Deque<Boolean> childrenFlags = new ArrayDeque<>();

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
  public DOMWriterImpl(@NotNull Document document) {
    this.document = Objects.requireNonNull(document, "The XMLWriter requires a DOM Document to write on.");
    this.currentElement = document;
    this.newline = document.createTextNode("\n");
  }

  /**
   * Does nothing.
   */
  @Override
  public void xmlDecl() {
    // Does nothing for DOM
  }

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

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by the method invoked on the underlying DOM document
   */
  @Override
  public void writeText(String text) {
    if (text == null) return;
    completeOpenTag();
    Text textNode = this.document.createTextNode(text);
    this.currentElement.appendChild(textNode);
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by the method invoked on the underlying DOM document
   */
  @Override
  public void writeText(char @NotNull[] text, int off, int len) {
    this.writeText(new String(text, off, len));
  }

  /**
   * This method is expensive as the character has to be converted to a String for DOM.
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void writeText(char c) {
    this.writeText(String.valueOf(c));
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
   * @throws DOMException If thrown by the method invoked on the underlying DOM document
   */
  public void writeText(Object o) {
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
    this.document.createCDATASection(data);
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
  public void writeXML(char @NotNull [] text, int off, int len)
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
  public void writeComment(@NotNull String comment) throws DOMException {
    if (comment.contains("--"))
      throw new IllegalArgumentException("A comment must not contain '--'.");
    completeOpenTag();
    Node node = this.document.createComment(comment);
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
    completeOpenTag();
    Node node = this.document.createProcessingInstruction(target, data);
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
  public void attribute(@NotNull String name, @NotNull String value) throws DOMException {
    if (this.isOpenTagComplete)
      throw new IllegalArgumentException("Cannot write attribute: too late!");
    Attr att = this.document.createAttribute(name);
    att.setValue(value);
    this.currentElement.appendChild(att);
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void attribute(@NotNull String name, int value) throws DOMException {
    attribute(name, Integer.toString(value));
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by method invoked on the underlying DOM document
   */
  @Override
  public void attribute(@NotNull String name, long value) throws DOMException {
    attribute(name, Long.toString(value));
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
  public void openElement(@NotNull String name) throws DOMException {
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
   * @throws DOMException If thrown by the method invoked on the underlying DOM document
   */
  @Override
  public void openElement(@NotNull String name, boolean hasChildren) throws DOMException {
    completeOpenTag();
    indent();
    this.childrenFlags.push(hasChildren);
    Element element = this.document.createElement(name);
    this.currentElement.appendChild(element);
    this.currentElement = element;
    this.isOpenTagComplete = false;
    this.depth++;
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by the method invoked on the underlying DOM document
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
    this.isOpenTagComplete = true;
    boolean hasChildren = this.childrenFlags.pop();
    if (hasChildren) {
      indent();
    }
    this.currentElement.normalize();
    this.currentElement = this.currentElement.getParentNode();
    // new line if parent has children
    if (this.indent && Boolean.TRUE.equals(this.childrenFlags.peek())) {
      newLine();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws DOMException If thrown by the method invoked on the underlying DOM document
   */
  @Override
  public void emptyElement(String name) throws DOMException {
    Element element = this.document.createElement(name);
    this.currentElement.appendChild(element);
  }

  /**
   * Does nothing.
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

  @Override
  public Document getDocument() {
    return this.document;
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
  public void openElement(@NotNull String uri, @NotNull String name, boolean hasChildren)
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
  public void emptyElement(@NotNull String uri, @NotNull String element)
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
  public void setPrefixMapping(@NotNull String uri, @NotNull String prefix)
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
  public void attribute(@NotNull String uri, @NotNull String name, @NotNull String value)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Not supported.
   *
   * @param uri   This parameter is ignored.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  @Override
  public void attribute(@NotNull String uri, @NotNull String name, int value)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Not supported.
   *
   * @param uri   This parameter is ignored.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  @Override
  public void attribute(@NotNull String uri, @NotNull String name, long value)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Insert the correct number of space characters depending on the depth and if
   * the <code>indent</code> flag is set to <code>true</code>.
   */
  void indent() {
    if (this.indent) {
      Node node = this.document.createTextNode(this.indentChars.repeat(Math.max(0, this.depth)));
      this.currentElement.appendChild(node);
    }
  }

  /**
   * Writes the angle bracket if the element open tag is not finished.
   */
  private void completeOpenTag() {
    if (!this.isOpenTagComplete) {
      if (this.indent) { //TODO: hasChildren
        newLine();
      }
      this.isOpenTagComplete = true;
    }
  }

  /**
   * Adds a new line to the DOM.
   *
   * @throws DOMException If thrown by the method invoked on the underlying DOM document
   */
  private void newLine() {
    this.currentElement.appendChild(this.newline.cloneNode(false));
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

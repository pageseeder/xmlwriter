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
package org.pageseeder.xmlwriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple writer for XML data that does not support namespaces.
 *
 * <p>Provides methods to generate well-formed XML data easily, wrapping a writer.
 *
 * <p>This version only supports utf-8 encoding, if writing to a file make sure that the
 * encoding of the file output stream is "utf-8".
 *
 * <p>The recommended implementation is to use a <code>BufferedWriter</code> to write.
 *
 * <pre>
 *  Writer writer =
 *     new BufferedWriter(new OutputStreamWriter(new FileOutputStream("foo.out"),"utf-8"));
 * </pre>
 *
 * <p>This class is not synchronised and does not support namespaces, and will therefore
 * throw an unsupported operation exception for each call to a method that uses namespaces.
 *
 * @author Christophe Lauret
 */
public final class XMLWriterImpl extends XMLWriterBase implements XMLWriter {

  /**
   * The root node.
   */
  private static final Element ROOT;
  static {
    ROOT = new Element("", true);
  }

  /**
   * A stack of elements to close the elements automatically.
   */
  private final List<Element> _elements = new ArrayList<Element>();

  // Constructors
  // ----------------------------------------------------------------------------------------------

  /**
   * <p>Creates a new XML writer.
   *
   * <p>Sets the depth attribute to 0 and the indentation to <code>true</code>.
   *
   * @param writer Where this writer should write the XML data.
   *
   * @throws NullPointerException If the writer is <code>null</code>.
   */
  public XMLWriterImpl(Writer writer) throws NullPointerException {
    super(writer, false);
    this._elements.add(ROOT);
  }

  /**
   * <p>Create a new XML writer.
   *
   * @param writer  Where this writer should write the XML data.
   * @param indent  Set the indentation flag.
   *
   * @throws NullPointerException If the writer is <code>null</code>.
   */
  public XMLWriterImpl(Writer writer, boolean indent) throws NullPointerException {
    super(writer, indent);
    this._elements.add(ROOT);
  }

  // Writing text
  // ----------------------------------------------------------------------------------------------

  /**
   * Writes the angle bracket if the element open tag is not finished.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  void deNude() throws IOException {
    if (this.isNude) {
      this._writer.write('>');
      if (peekElement().hasChildren && this.indent) {
        this._writer.write('\n');
      }
      this.isNude = false;
    }
  }

  // Open/close specific elements
  // ----------------------------------------------------------------------------------------------

  /**
   * Writes a start element tag correctly indented.
   *
   * <p>It is the same as <code>openElement(null, name, false)</code>
   *
   * @see #openElement(java.lang.String, java.lang.String, boolean)
   *
   * @param name The name of the element
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void openElement(String name) throws IOException {
    openElement(name, false);
  }

  /**
   * Writes a start element tag correctly indented.
   *
   * <p>Use the <code>hasChildren</code> parameter to specify whether this element is
   * terminal node or not, which affects the indenting.
   *
   * <p>The name can contain attributes and should be a valid xml name.
   *
   * @param name        The name of the element.
   * @param hasChildren <code>true</code> if this element has children.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void openElement(String name, boolean hasChildren) throws IOException {
    deNude();
    if (peekElement().hasChildren) {
      indent();
    }
    this._elements.add(new Element(name, hasChildren));
    this._writer.write('<');
    this._writer.write(name);
    this.isNude = true;
    this.depth++;
  }

  /**
   * Write the end element tag.
   *
   * @throws IOException If thrown by the wrapped writer.
   * @throws IllegalCloseElementException If there is no element to close
   */
  @Override
  public void closeElement() throws IOException, IllegalCloseElementException {
    Element elt = popElement();
    // reaching the end of the document
    if (elt == ROOT)
      throw new IllegalCloseElementException();
    this.depth--;
    // this is an empty element
    if (this.isNude) {
      this._writer.write('/');
      this.isNude = false;
      // the element contains text
    } else {
      if (elt.hasChildren) {
        indent();
      }
      this._writer.write('<');
      this._writer.write('/');
      int x = elt.name.indexOf(' ');
      if (x < 0) {
        this._writer.write(elt.name);
      } else {
        this._writer.write(elt.name.substring(0, x));
      }
    }
    this._writer.write('>');
    // take care of the new line if the indentation is on
    if (super.indent) {
      Element parent = peekElement();
      if (parent.hasChildren && parent != ROOT) {
        this._writer.write('\n');
      }
    }
  }

  /**
   * Same as <code>emptyElement(null, element);</code>.
   *
   * <p>It is possible for the element to contain attributes,
   * however, since there is no character escaping, great care
   * must be taken not to introduce invalid characters. For
   * example:
   * <pre>
   *    &lt;<i>example test="yes"</i>/&gt;
   * </pre>
   *
   * @param element the name of the element
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void emptyElement(String element) throws IOException {
    deNude();
    indent();
    this._writer.write('<');
    this._writer.write(element);
    this._writer.write('/');
    this._writer.write('>');
    if (this.indent) {
      Element parent = peekElement();
      if (parent.hasChildren && parent != ROOT) {
        this._writer.write('\n');
      }
    }
  }

  /**
   * Returns the last element in the list.
   *
   * @return The current element.
   */
  private Element peekElement() {
    return this._elements.get(this._elements.size() - 1);
  }

  /**
   * Removes the last element in the list.
   *
   * @return The current element.
   */
  private Element popElement() {
    return this._elements.remove(this._elements.size() - 1);
  }

  // Unsupported operations
  // ----------------------------------------------------------------------------------------------

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

  /**
   * Close the writer.
   *
   * @throws IOException If thrown by the wrapped writer.
   * @throws UnclosedElementException If an element has been left open.
   */
  @Override
  public void close() throws IOException, UnclosedElementException {
    Element open = peekElement();
    if (open != ROOT)
      throw new UnclosedElementException(open.name);
    this._writer.close();
  }

  // Inner class: Element
  // ----------------------------------------------------------------------------------------------

  /**
   * A light object to keep track of the element.
   *
   * <p>This object does not support namespaces.
   *
   * @author Christophe Lauret
   * @version 7 March 2005
   */
  private static final class Element {

    /**
     * The fully qualified name of the element.
     */
    private final String name;

    /**
     * Indicates whether the element has children.
     */
    private final boolean hasChildren;

    /**
     * Creates a new Element.
     *
     * @param name       The qualified name of the element.
     * @param hasChildren Whether the element has children.
     */
    public Element(String name, boolean hasChildren) {
      this.name = name;
      this.hasChildren = hasChildren;
    }
  }

}

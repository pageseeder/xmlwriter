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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;

/**
 * A Namespace-aware writer for XML data.
 *
 * <p>Provides methods to generate well-formed XML data easily. wrapping a writer.
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
 * <p>This class is not synchronised.
 *
 * @author Christophe Lauret
 *
 * @since 1.0.0
 * @version 1.1.0
 */
public final class XMLWriterNSImpl extends XMLWriterBase implements XMLWriter {

  /**
   * The default namespace mapped to the empty prefix.
   */
  private static final PrefixMapping DEFAULT_NS = new PrefixMapping(XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);

  /**
   * The root node.
   */
  private static final NSElement ROOT;
  static {
    List<PrefixMapping> mps = new ArrayList<>();
    mps.add(DEFAULT_NS);
    ROOT = new NSElement("", true, mps);
  }

  /**
   * The current prefix mapping.
   */
  private final Map<String, String> prefixMapping = new HashMap<>();

  /**
   * The list of prefix mappings to be associated with the next element.
   */
  private List<PrefixMapping> tempMapping;

  /**
   * A stack of elements to close the elements automatically.
   */
  private final List<NSElement> elements = new ArrayList<>();

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
  public XMLWriterNSImpl(Writer writer) throws NullPointerException {
    this(writer, false);
  }

  /**
   * <p>Create a new XML writer.
   *
   * @param writer  Where this writer should write the XML data.
   * @param indent  Set the indentation flag.
   *
   * @throws NullPointerException If the writer is <code>null</code>.
   */
  public XMLWriterNSImpl(Writer writer, boolean indent) throws NullPointerException {
    super(writer, indent);
    this.elements.add(ROOT);
    this.prefixMapping.put(XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);
    this.prefixMapping.put(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
  }

  /**
   * Writes the angle bracket if the element open tag is not finished.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  void completeOpenTag() throws IOException {
    if (!this.isOpenTagComplete) {
      this.writer.write('>');
      if (super.indentEnabled && peekElement().hasChildren) {
        this.writer.write('\n');
      }
      this.isOpenTagComplete = true;
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
   * @param name the name of the element
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void openElement(String name) throws IOException {
    openElement(null, name, false);
  }

  /**
   * Write a start element tag correctly indented.
   *
   * <p>It is the same as <code>openElement(name, false)</code>
   *
   * @see #openElement(java.lang.String, boolean)
   *
   * @param uri  The namespace URI of this element.
   * @param name The name of the element.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  public void openElement(String uri, String name) throws IOException {
    openElement(uri, name, false);
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
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void openElement(String name, boolean hasChildren) throws IOException {
    openElement(null, name, hasChildren);
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
   * @param uri         The namespace URI of this element.
   * @param name        The name of the element.
   * @param hasChildren true if this element has children.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void openElement(String uri, String name, boolean hasChildren) throws IOException {
    completeOpenTag();
    if (peekElement().hasChildren) {
      indent();
    }
    String qName = getQName(uri, name);
    this.elements.add(new NSElement(qName, hasChildren, this.tempMapping));
    this.writer.write('<');
    this.writer.write(qName);
    handleNamespaceDeclaration();
    this.isOpenTagComplete = false;
    this.depth++;
  }

  /**
   * Write an end element tag.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void closeElement() throws IOException {
    NSElement elt = popElement();
    // reaching the end of the document
    if (elt == ROOT)
      throw new IllegalCloseElementException();
    this.depth--;
    // this is an empty element
    if (!this.isOpenTagComplete) {
      this.writer.write('/');
      this.isOpenTagComplete = true;
      // the element contains text
    } else {
      if (elt.hasChildren) {
        indent();
      }
      this.writer.write('<');
      this.writer.write('/');
      int x = elt.qName.indexOf(' ');
      if (x < 0) {
        this.writer.write(elt.qName);
      } else {
        this.writer.write(elt.qName.substring(0, x));
      }
    }
    // restore previous mapping if necessary
    restorePrefixMapping(elt);
    this.writer.write('>');
    // take care of the new line if the indentation is on
    if (super.indentEnabled) {
      NSElement parent = peekElement();
      if (parent.hasChildren && parent != ROOT) {
        this.writer.write('\n');
      }
    }
  }

  /**
   * Same as <code>emptyElement(null, element);</code>.
   *
   * @param element the name of the element
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void emptyElement(String element) throws IOException {
    emptyElement(null, element);
  }

  /**
   * Write an empty element.
   *
   * <p>It is possible for the element to contain attributes,
   * however, since there is no character escaping, great care
   * must be taken not to introduce invalid characters. For
   * example:
   * <pre>
   *    &lt;<i>example test="yes"</i>/&gt;
   * </pre>
   *
   * @param uri     The namespace URI for this element.
   * @param element The name of the element.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  @Override
  public void emptyElement(String uri, String element) throws IOException {
    completeOpenTag();
    indent();
    this.writer.write('<');
    this.writer.write(getQName(uri, element));
    handleNamespaceDeclaration();
    this.writer.write('/');
    this.writer.write('>');
    if (super.indentEnabled) {
      this.writer.write('\n');
    }
  }

  /**
   * Returns the last element in the list.
   *
   * @return The current element.
   */
  private NSElement peekElement() {
    return this.elements.get(this.elements.size() - 1);
  }

  /**
   * Removes the last element in the list.
   *
   * @return The current element.
   */
  private NSElement popElement() {
    return this.elements.remove(this.elements.size() - 1);
  }

  /**
   * Writes an attribute.
   *
   * @param uri   The namespace URI this attribute belongs to.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws IOException If thrown by the wrapped writer.
   * @throws IllegalStateException If there is no open element or text has been written.
   */
  @Override
  public void attribute(String uri, String name, String value)
      throws IOException, IllegalStateException {
    if (this.isOpenTagComplete) throw new IllegalArgumentException("Cannot write attribute: too late!");
    this.writer.write(' ');
    this.writer.write(getQName(uri, name));
    this.writer.write("=\"");
    this.writerEscape.writeAttValue(value);
    this.writer.write('"');
    handleNamespaceDeclaration();
  }

  /**
   * Writes an attribute.
   *
   * <p>This method for number does not require escaping.
   *
   * @param uri   The namespace URI this attribute belongs to.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws IOException If thrown by the wrapped writer.
   * @throws IllegalStateException If there is no open element or text has been written.
   */
  @Override
  public void attribute(String uri, String name, int value)
      throws IOException, IllegalStateException {
    if (this.isOpenTagComplete) throw new IllegalArgumentException("Cannot write attribute: too late!");
    this.writer.write(' ');
    this.writer.write(getQName(uri, name));
    this.writer.write('=');
    this.writer.write('"');
    this.writer.write(Integer.toString(value));
    this.writer.write('"');
    handleNamespaceDeclaration();
  }

  /**
   * Writes an attribute.
   *
   * <p>This method for number does not require escaping.
   *
   * @param uri   The namespace URI this attribute belongs to.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws IOException If thrown by the wrapped writer.
   * @throws IllegalStateException If there is no open element or text has been written.
   */
  @Override
  public void attribute(String uri, String name, long value)
      throws IOException, IllegalStateException {
    if (this.isOpenTagComplete) throw new IllegalArgumentException("Cannot write attribute: too late!");
    this.writer.write(' ');
    this.writer.write(getQName(uri, name));
    this.writer.write('=');
    this.writer.write('"');
    this.writer.write(Long.toString(value));
    this.writer.write('"');
    handleNamespaceDeclaration();
  }

  // Namespace handling
  // ----------------------------------------------------------------------------------------------

  /**
   * @see org.pageseeder.xmlwriter.XMLWriter#setPrefixMapping(java.lang.String, java.lang.String)
   *
   * <p>This implementation does not keep a history of the prefix mappings so it needs to be
   * reset. If a prefix is already being used it is overridden.
   *
   * @param uri    The full namespace URI.
   * @param prefix The prefix for the namespace uri.
   *
   * @throws NullPointerException if the prefix is <code>null</code>.
   */
  @Override
  public void setPrefixMapping(String uri, String prefix) throws NullPointerException {
    //do not declare again if the same mapping already exist
    if (!prefix.equals(this.prefixMapping.get(uri))) {
      // remove the previous mapping to the prefix
      removeIfNeeded(prefix);
      // create the new prefix mapping
      PrefixMapping pm = new PrefixMapping(prefix, uri);
      this.prefixMapping.put(pm.uri, pm.prefix);
      if (this.tempMapping == null) {
        this.tempMapping = new ArrayList<>();
      }
      this.tempMapping.add(pm);
    }
  }

  /**
   * Returns the qualified name for this element using the specified namespace URI.
   *
   * @param uri  The namespace URI for the element.
   * @param name The name of the element or attribute.
   *
   * @return The qualified element name.
   *
   * @throws UndeclaredNamespaceException If the uri has not being previously declared.
   */
  private String getQName(String uri, String name) throws UndeclaredNamespaceException {
    String prefix = this.prefixMapping.get(uri != null? uri : XMLConstants.NULL_NS_URI);
    if (prefix != null) {
      if (!prefix.isEmpty())
        return this.prefixMapping.get(uri)+":"+name;
      else
        return name;
    } else if (uri == null) return name;
    else
      throw new UndeclaredNamespaceException(uri);
  }

  /**
   * Handles the namespace declaration and updates the prefix mappings.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  private void handleNamespaceDeclaration() throws IOException {
    if (this.tempMapping != null) {
      for (PrefixMapping pm : this.tempMapping) {
        if (!XMLConstants.XML_NS_PREFIX.equals(pm.prefix)) {
          this.writer.write(" xmlns");
          // specify a prefix if different from ""
          if (!pm.prefix.isEmpty()) {
            this.writer.write(':');
            this.writer.write(pm.prefix);
          }
          this.writer.write("=\"");
          this.writer.write(pm.uri);
          this.writer.write('"');
        }
      }
      this.tempMapping = null;
    }
  }

  /**
   * Restores the prefix mapping after closing an element.
   *
   * <p>This costly operation need only to be done if the method
   * {@link XMLWriterNSImpl#setPrefixMapping(String, String)} have been used
   * immediately before, therefore it should not happen often.
   *
   * @param elt The element that had some new mappings.
   */
  private void restorePrefixMapping(NSElement elt) {
    if (elt.mappings != null) {
      // for each mapping of this element
      for (int i = 0; i < elt.mappings.size(); i++) {
        boolean found = false;
        PrefixMapping mpi = elt.mappings.get(i);
        // find the first previous namespace mapping amongst the parents
        // that defines namespace mappings
        for (int j = this.elements.size() - 1; j > 0; j--) {
          if (this.elements.get(j).mappings != null) {
            List<PrefixMapping> mps = this.elements.get(j).mappings;
            // iterate through the define namespace mappings of the parent
            for (int k = 0; k < mps.size(); k++) {
              PrefixMapping mpk = mps.get(k);
              // if we found a namespace prefix for the namespace
              if (mpk.prefix.equals(mpi.prefix)) {
                found = true;
                removeIfNeeded(mpk.prefix);
                this.prefixMapping.put(mpk.uri, mpk.prefix);
                j = 0; // exit from the previous loop
                break; // exit from this one
              }
            }
          }
        }
        // if not declared in ancestors remove it now so the declaration
        // will be output again later if required
        if (!found) {
          removeIfNeeded(mpi.prefix);
        }
      }
    }
  }

  /**
   * Removes the mapping associated to the specified prefix.
   *
   * @param prefix The prefix which mapping should be removed.
   */
  private void removeIfNeeded(String prefix) {
    // remove the previous mapping to the prefix
    if (this.prefixMapping.containsValue(prefix)) {
      Entry<String, String> remove = null;
      for (Entry<String, String> e : this.prefixMapping.entrySet()) {
        if (e.getValue().equals(prefix)) {
          remove = e;
          break;
        }
      }
      // we know key should have a value
      this.prefixMapping.remove(remove.getKey());
    }
  }

  /**
   * Close the writer.
   *
   * @throws IOException If thrown by the wrapped writer.
   * @throws UnclosedElementException If an element has been left open.
   */
  @Override
  public void close() throws IOException, UnclosedElementException {
    NSElement open = peekElement();
    if (open != XMLWriterNSImpl.ROOT)
      throw new UnclosedElementException(open.qName);
    this.writer.close();
  }

  // Inner class: Element
  // ----------------------------------------------------------------------------------------------

  /**
   * A light object to keep track of the elements
   *
   * @author Christophe Lauret
   */
  private static final class NSElement {

    /**
     * The fully qualified name of the element.
     */
    private final String qName;

    /**
     * A list of prefix mappings for this element.
     *
     * <p>Can be <code>null</code>.
     */
    private final List<PrefixMapping> mappings;

    /**
     * Indicates whether the element has children.
     */
    private boolean hasChildren;

    /**
     * Creates a new Element.
     *
     * @param qName       The qualified name of the element.
     * @param hasChildren Whether the element has children.
     * @param mappings    The list of prefix mapping if any.
     */
    public NSElement(String qName, boolean hasChildren, List<PrefixMapping> mappings) {
      this.qName = qName;
      this.mappings = mappings;
      this.hasChildren = hasChildren;
    }
  }

  // Inner class: Prefix Mapping
  // ----------------------------------------------------------------------------------------------

  /**
   * Light-weight class to represent a prefix mapping.
   *
   * <p>The class attributes cannot be <code>null</code>.
   */
  private static final class PrefixMapping {

    /**
     * The prefix associated to the URI.
     */
    private final @NotNull String prefix;

    /**
     * The namespace URI.
     */
    private final @NotNull String uri;

    /**
     * Creates a new prefix mapping.
     *
     * @param prefix The prefix for the URI.
     * @param uri    The full namespace URI.
     */
    public PrefixMapping(String prefix, String uri) {
      this.prefix = prefix != null ? prefix : XMLConstants.DEFAULT_NS_PREFIX;
      this.uri = uri != null ? uri : XMLConstants.NULL_NS_URI;
    }
  }

}

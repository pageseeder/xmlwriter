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

import org.pageseeder.xmlwriter.esc.XMLEscapeWriter;
import org.pageseeder.xmlwriter.esc.XMLEscapeWriterUTF8;

/**
 * A base implementation for XML writers.
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
 * @author Christophe Lauret
 */
abstract class XMLWriterBase implements XMLWriter {

  /**
   * Where the XML data goes.
   */
  final Writer _writer;

  /**
   * Encoding of the output xml.
   */
  String encoding = "utf-8";

  /**
   * Encoding of the output xml.
   */
  XMLEscapeWriter writerEscape;

  /**
   * Level of the depth of the xml document currently produced.
   *
   * <p>This attribute changes depending on the state of the instance.
   */
  int depth = 0;

  /**
   * Indicates whether the xml should be indented or not.
   *
   * <p>The default is <code>true</code> (indented).
   *
   * <p>The indentation is 2 white-spaces.
   */
  boolean indent;

  /**
   * The default indentation spaces used.
   */
  private String indentChars = null;

  /**
   * Flag to indicate that the element open tag is not finished yet.
   */
  boolean isNude = false;

  // constructors -------------------------------------------------------------------------

  /**
   * <p>Creates a new XML writer.
   *
   * @param writer  Where this writer should write the XML data.
   * @param indent  Set the indentation flag.
   *
   * @throws NullPointerException If the writer is <code>null</code>.
   */
  public XMLWriterBase(Writer writer, boolean indent) throws NullPointerException {
    if (writer == null)
      throw new NullPointerException("XMLWriter cannot use a null writer.");
    this._writer = writer;
    this.writerEscape = new XMLEscapeWriterUTF8(writer);
    this.indent = indent;
    if (indent) {
      this.indentChars = "  ";
    }
  }

  // setup methods ------------------------------------------------------------------------

  @Override
  public final void xmlDecl() throws IOException {
    this._writer.write("<?xml version=\"1.0\" encoding=\""+this.encoding+"\"?>");
    if (this.indent) {
      this._writer.write('\n');
    }
  }

  @Override
  public final void setIndentChars(String spaces) throws IllegalStateException, IllegalArgumentException {
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
   * Sets the encoding to use.
   *
   * <p>The encoding must match the encoding used if there is an underlying
   * <code>OutputStreamWriter</code>.
   *
   * @param encoding The encoding to use.
   *
   * @throws IllegalArgumentException If the encoding is not valid.
   * @throws IllegalStateException    If the writer has already been used.
   */
  public final void setEncoding(String encoding) throws IllegalStateException, IllegalArgumentException {
    if (this.depth != 0)
      throw new IllegalStateException("To late to set the encoding!");
    this.encoding = encoding;
  }

  // Write text methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public final void writeText(String text) throws IOException {
    if (text == null) return;
    deNude();
    this.writerEscape.writeText(text);
  }

  @Override
  public final void writeText(char[] text, int off, int len) throws IOException {
    deNude();
    this.writerEscape.writeText(text, off, len);
  }

  @Override
  public final void writeText(char c) throws IOException {
    deNude();
    this.writerEscape.writeText(c);
  }

  /**
   * Writes the string value of an object.
   *
   * <p>Does nothing if the object is <code>null</code>.
   *
   * @see Object#toString
   * @see #writeText(java.lang.String)
   *
   * @param o The object that should be written as text.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  public final void writeText(Object o) throws IOException {
    // TODO: what about an XML serializable ???
    // TODO: Add to interface ???
    if (o != null) {
      this.writeText(o.toString());
    }
  }

  // Write XML methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public final void writeXML(String text) throws IOException {
    if (text == null) return;
    deNude();
    this._writer.write(text);
  }

  @Override
  public final void writeXML(char[] text, int off, int len) throws IOException {
    deNude();
    this._writer.write(text, off, len);
  }

  // Processing Instructions, CDATA sections and comments
  // ----------------------------------------------------------------------------------------------

  @Override
  public final void writeComment(String comment) throws IOException, IllegalArgumentException {
    if (comment == null)
      return;
    if (comment.indexOf("--") >= 0)
      throw new IllegalArgumentException("A comment must not contain '--'.");
    deNude();
    this._writer.write("<!-- ");
    this._writer.write(comment);
    this._writer.write(" -->");
    if (this.indent) {
      this._writer.write('\n');
    }
  }

  @Override
  public final void writePI(String target, String data) throws IOException {
    deNude();
    this._writer.write("<?");
    this._writer.write(target);
    this._writer.write(' ');
    this._writer.write(data);
    this._writer.write("?>");
    if (this.indent) {
      this._writer.write('\n');
    }
  }

  @Override
  public final void writeCDATA(String data) throws IOException {
    if (data == null) return;
    final String end = "]]>";
    if (data.indexOf(end) >= 0)
      throw new IllegalArgumentException("CDATA sections must not contain \']]>\'");
    deNude();
    this._writer.write("<![CDATA[");
    this._writer.write(data);
    this._writer.write(end);
  }

  // Attribute methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public final void attribute(String name, String value) throws IOException {
    if (!this.isNude) throw new IllegalStateException("Cannot write attribute: too late!");
    this._writer.write(' ');
    this._writer.write(name);
    this._writer.write('=');
    this._writer.write('"');
    this.writerEscape.writeAttValue(value);
    this._writer.write('"');
  }

  @Override
  public final void attribute(String name, int value) throws IOException {
    if (!this.isNude) throw new IllegalStateException("Cannot write attribute: too late!");
    this._writer.write(' ');
    this._writer.write(name);
    this._writer.write('=');
    this._writer.write('"');
    this._writer.write(Integer.toString(value));
    this._writer.write('"');
  }

  // Open/close specific elements
  // ----------------------------------------------------------------------------------------------

  @Override
  public void element(String name, String text) throws IOException {
    this.openElement(name);
    this.writeText(text);
    closeElement();
  }

  // Direct access to the writer
  // ----------------------------------------------------------------------------------------------

  @Override
  public final void flush() throws IOException {
    this._writer.flush();
  }

  // Base class and convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Writes the end of the open element tag.
   *
   * <p>After this method is invoked it is not possible to write attributes
   * for an element.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  abstract void deNude() throws IOException;

  /**
   * Insert the correct amount of space characters depending on the depth and if
   * the <code>indent</code> flag is set to <code>true</code>.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  void indent() throws IOException {
    if (this.indent) {
      for (int i = 0; i < this.depth; i++) {
        this._writer.write(this.indentChars);
      }
    }
  }

  /**
   * Does nothing.
   *
   * <p>This method exists so that we can explicitly say that we should do nothing
   * in certain conditions.
   */
  static final void doNothing() {
    return;
  }

}

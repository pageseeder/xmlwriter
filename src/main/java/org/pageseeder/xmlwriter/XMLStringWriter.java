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
import java.io.StringWriter;

import org.pageseeder.xmlwriter.XML.NamespaceAware;

/**
 * An XML which writes on to a string.
 *
 * <p>This XML writer is backed by a {@link StringWriter} and will defer the XML writer's method to
 * either a {@link XMLWriterImpl} or {@link XMLWriterNSImpl} depending on whether namespace support is
 * required.
 *
 * <p>The write methods do not throw any {@link IOException}.
 *
 * <p>If the write is not set to support namespaces, the method which require a namespace URI will
 * throw an {@link UnsupportedOperationException}.
 *
 * @author Christophe Lauret
 */
public final class XMLStringWriter implements XMLWriter {

  /**
   * Wraps an XML Writer
   */
  private final StringWriter _writer;

  /**
   * Wraps an XML Writer
   */
  private final XMLWriter _xml;

  /**
   * <p>Creates a new XML string writer.
   *
   * @param namespaces Whether this XML writer should use namespaces.
   *
   * @deprecated use constructor with enum constant instead
   */
  @Deprecated
  public XMLStringWriter(boolean namespaces) {
    this(namespaces, false);
  }

  /**
   * <p>Create a new XML string writer.
   *
   * @param namespaces Whether this XML writer should use namespaces.
   * @param indent  Set the indentation flag.
   *
   * @deprecated use constructor with enum constant instead
   */
  @Deprecated
  public XMLStringWriter(boolean namespaces, boolean indent) {
    this(namespaces? NamespaceAware.Yes : NamespaceAware.No, indent);
  }

  /**
   * <p>Creates a new XML string writer.
   *
   * @param namespaces Whether this XML writer should use namespaces.
   */
  public XMLStringWriter(NamespaceAware aware) {
    this(aware, false);
  }

  /**
   * <p>Create a new XML string writer.
   *
   * @param namespaces Whether this XML writer should use namespaces.
   * @param indent  Set the indentation flag.
   */
  public XMLStringWriter(NamespaceAware aware, boolean indent) {
    this._writer = new StringWriter();
    this._xml = aware == NamespaceAware.Yes? new XMLWriterNSImpl(this._writer, indent) : new XMLWriterImpl(this._writer, indent);
  }

  // XML Writer methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public void xmlDecl() {
    try {
      this._xml.xmlDecl();
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void setIndentChars(String spaces) {
    this._xml.setIndentChars(spaces);
  }

  @Override
  public void writeText(char c) {
    try {
      this._xml.writeText(c);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void writeText(String text) {
    try {
      this._xml.writeText(text);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void writeText(char[] text, int off, int len) {
    try {
      this._xml.writeText(text, off, len);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void writeCDATA(String cdata) {
    try {
      this._xml.writeCDATA(cdata);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void writeXML(String text) {
    try {
      this._xml.writeXML(text);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void writeXML(char[] text, int off, int len) {
    try {
      this._xml.writeXML(text, off, len);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void writeComment(String comment) {
    try {
      this._xml.writeComment(comment);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void writePI(String target, String data) {
    try {
      this._xml.writePI(target, data);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void openElement(String name) {
    try {
      this._xml.openElement(name);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void openElement(String name, boolean hasChildren) {
    try {
      this._xml.openElement(name, hasChildren);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void openElement(String uri, String name, boolean hasChildren) {
    try {
      this._xml.openElement(uri, name, hasChildren);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void closeElement() {
    try {
      this._xml.closeElement();
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void element(String name, String text) {
    try {
      this._xml.element(name, text);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void emptyElement(String element) {
    try {
      this._xml.emptyElement(element);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void emptyElement(String uri, String element) {
    try {
      this._xml.emptyElement(element);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void attribute(String name, String value) {
    try {
      this._xml.attribute(name, value);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void attribute(String name, int value) {
    try {
      this._xml.attribute(name, value);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void attribute(String uri, String name, String value) {
    try {
      this._xml.attribute(uri, name, value);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void attribute(String uri, String name, int value) {
    try {
      this._xml.attribute(uri, name, value);
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void setPrefixMapping(String uri, String prefix) {
    this._xml.setPrefixMapping(uri, prefix);
  }

  @Override
  public void flush() {
    try {
      this._xml.flush();
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  @Override
  public void close() throws UnclosedElementException {
    try {
      this._xml.close();
    } catch (IOException ex) {
      // Will not occur
      doNothing();
    }
  }

  /**
   * Returns the XML content as a {@link String}.
   *
   * @return the XML content as a {@link String}.
   */
  @Override
  public String toString() {
    return this._writer.toString();
  }

  /**
   * Do nothing
   */
  private static void doNothing(){
  }

}

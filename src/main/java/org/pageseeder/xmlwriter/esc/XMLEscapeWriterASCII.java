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
package org.pageseeder.xmlwriter.esc;

import java.io.IOException;
import java.io.Writer;

/**
 * A utility class for escaping XML data when the intended encoding is ASCII or ASCII compatible.
 *
 * <p>Any unicode code point greater then (#x7E) will be encoded usnig the numeric character entity.
 *
 * @author Christophe Lauret
 *
 * @version 0.7.7
 */
public final class XMLEscapeWriterASCII extends XMLEscapeWriterBase implements XMLEscapeWriter {

  /**
   * The encoding used for this instance.
   */
  private static final String ENCODING = "ASCII";

  /**
   * Creates a new XML escape writer using the utf-8 encoding.
   *
   * @param writer The writer to wrap.
   *
   * @throws NullPointerException if the writer is <code>null</code>.
   */
  public XMLEscapeWriterASCII(Writer writer) throws NullPointerException {
    super(writer, ENCODING);
  }

  @Override
  public void writeAttValue(char[] ch, int off, int len) throws IOException {
    char c;
    for (int i = off; i < off+len; i++) {
      c = ch[i];
      // '<' always replace with '&lt;'
      if (c == '<') {
        super.w.write("&lt;");
      } else if (c == '&') {
        super.w.write("&amp;");
      } else if (c == '"') {
        super.w.write("&quot;");
      } else if (c == '\'') {
        super.w.write("&#39;");
      } else if (c == '\n' || c == '\r' || c == '\t') {
        super.w.write(c);
      } else if (c < 0x20 || c >= 0x7F && c < 0xA0) {
        doNothing();
      } else if (c >= 0xD800 && c <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, len);
        i += Character.charCount(codePoint) - 1;
        super.w.write("&#x");
        super.w.write(Integer.toHexString(codePoint));
        super.w.write(";");
      }
      // characters outside the ASCII range
      else if (c > 0x9F) {
        super.w.write("&#x");
        super.w.write(Integer.toHexString(c));
        super.w.write(";");
      } else {
        super.w.write(c);
      }
    }
  }

  @Override
  public void writeText(char[] ch, int off, int len) throws IOException {
    char c;
    for (int i = off; i < off+len; i++) {
      c = ch[i];
      if (c == '<') {
        super.w.write("&lt;");
      } else if (c == '>') {
        super.w.write("&gt;");
      } else if (c == '&') {
        super.w.write("&amp;");
      } else if (c == '\n' || c == '\r' || c == '\t') {
        super.w.write(c);
      } else if (c < 0x20) {
        doNothing();
      } else if (c >= 0x7F && c < 0xA0) {
        doNothing();
      } else if (c >= 0xD800 && c <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, len);
        i += Character.charCount(codePoint) - 1;
        super.w.write("&#x");
        super.w.write(Integer.toHexString(codePoint));
        super.w.write(";");
      }
      // characters outside the ASCII range
      else if (c > 0x9F) {
        super.w.write("&#x");
        super.w.write(Integer.toHexString(c));
        super.w.write(';');
      } else {
        super.w.write(c);
      }
    }
  }

  /**
   * Replace characters which are invalid in element values,
   * by the corresponding entity in a given <code>String</code>.
   *
   * <p>these characters are:<br>
   * <ul>
   *  <li>'&amp' by the ampersand entity "&amp;amp"</li>
   *  <li>'&lt;' by the entity "&amp;lt;"</li>
   * </p>
   *
   * <p>Note: this function assumes that there are no entities in
   * the given String. If there are existing entities, then the
   * ampersand character will be escaped by the ampersand entity.
   *
   * {@inheritDoc}
   */
  @Override
  public void writeText(char c) throws IOException {
    // '<' always replace with '&lt;'
    if (c == '<') {
      super.w.write("&lt;");
    } else if (c == '>') {
      super.w.write("&gt;");
    } else if (c == '&') {
      super.w.write("&amp;");
    } else if (c == '\n' || c == '\r' || c == '\t') {
      super.w.write(c);
    } else if (c < 0x20 || c >= 0x7F && c < 0xA0) {
      doNothing();
    } else if (c >= 0xD800 && c <= 0xDFFF) throw new IOException("Unable to handle character #x"+Integer.toHexString(c));
    else if (c > 0xBF) {
      super.w.write("&#x");
      super.w.write(Integer.toHexString(c));
      super.w.write(';');
    } else {
      super.w.write(c);
    }
  }

  /**
   * Does nothing.
   *
   * <p>This method exists so that we can explicitly say that we should do nothing
   * in certain conditions.
   */
  private static void doNothing() {
    return;
  }

}

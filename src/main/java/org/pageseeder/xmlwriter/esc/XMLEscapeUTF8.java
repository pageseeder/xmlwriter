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

/**
 * A utility class for escaping XML data when using the UTF-8 encoding.
 *
 * <p>Only characters which must be escaped are escaped since the Unicode
 * Transformation Format should support all Unicode code points.
 *
 * <p>Escape methods in this class will escape non-BMP character for better
 * compatibility with storage mechanisms that do not support them, for
 * example, some databases.
 *
 * @author Christophe Lauret
 * @author Philip Rutherford
 *
 * @since 1.0.0
 * @version 1.1.1
 */
public final class XMLEscapeUTF8 extends XMLEscapeBase implements XMLEscape {

  /**
   * A static instance of the UTF8 escape class.
   */
  public static final XMLEscape UTF8_ESCAPE = new XMLEscapeUTF8();

  /**
   * The encoding used for this instance.
   */
  private static final String ENCODING = "utf-8";

  /**
   * Prevent creation of instances
   */
  private XMLEscapeUTF8() {
    super(ENCODING);
  }

  @Override
  @SuppressWarnings("java:S127") // We need to adjust loop counter when dealing with surrogate pairs
  public String toAttributeValue(char[] ch, int off, int len) {
    StringBuilder out = new StringBuilder();
    char c;
    for (int i = off; i < off+len; i++) {
      c = ch[i];
      // '<' always replace with '&lt;'
      if      (c == '<') {
        out.append("&lt;");
      } else if (c == '&') {
        out.append("&amp;");
      } else if (c == '"') {
        out.append("&quot;");
      } else if (c == '\'') {
        out.append("&#39;");
      } else if (c == '\n' || c == '\r' || c == '\t') {
        out.append(c);
      } else if (c < 0x20 || c >= 0x7F && c < 0xA0) {
        doNothing();
      } else if (c >= 0xD800 && c <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, off+len);
        i += Character.charCount(codePoint) - 1;
        out.append("&#x").append(Integer.toHexString(codePoint)).append(";");
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

  @Override
  @SuppressWarnings("java:S127") // We need to adjust loop counter when dealing with surrogate pairs
  public String toElementText(char[] ch, int off, int len) {
    StringBuilder out = new StringBuilder(len + len / 10);
    char c;
    for (int i = off; i < off+len; i++) {
      c = ch[i];
      // '<' always replace with '&lt;'
      if (c == '<') {
        out.append("&lt;");
      } else if (c == '&') {
        out.append("&amp;");
      } else if (c == '>') {
        out.append("&gt;");
      } else if (c == '\n' || c == '\r' || c == '\t') {
        out.append(c);
      } else if (c < 0x20 || c >= 0x7F && c < 0xA0) {
        doNothing();
      } else if (c >= 0xD800 && c <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, off+len);
        i += Character.charCount(codePoint) - 1;
        out.append("&#x").append(Integer.toHexString(codePoint)).append(";");
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

  /**
   * Does nothing.
   *
   * <p>This method exists so that we can explicitly say that we should do nothing
   * in certain conditions.
   */
  private static void doNothing() {
    // Ignored on purpose
  }

}

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
 * A singleton for escaping XML data when using the 'ASCII' encoding.
 *
 * <p>Any character that isn't part of the ASCI range is going to be replaced
 * by a character entity.
 *
 * @author Christophe Lauret
 *
 * @since 1.0.0
 * @version 1.1.1
 */
public final class XMLEscapeASCII extends XMLEscapeBase implements XMLEscape {

  /**
   * A static instance of the UTF8 escape class.
   */
  public static final XMLEscape ASCII_ESCAPE = new XMLEscapeASCII();

  /**
   * The encoding used for this instance.
   */
  private static final String ENCODING = "ASCII";

  /**
   * Prevent creation of instances
   */
  private XMLEscapeASCII() {
    super(ENCODING);
  }

  @Override
  @SuppressWarnings("java:S127") // We need to adjust loop counter when dealing with surrogate pairs
  public String toAttributeValue(char[] ch, int off, int len) {
    StringBuilder out = new StringBuilder(len + len / 10);
    for (int i = off; i < off+len; i++) {
      // 0x00 to 0x1F
      if (ch[i] < 0x20) {
        // tabs, new lines and line feeds: preserve
        if (ch[i] == 0x09 || ch[i] == 0x0A || ch[i] == 0x0D) {
          out.append(ch[i]);
        }
        // other control characters: prune
      // 0x20 to 0x7F
      } else if (ch[i] < 0x7F) {
        switch (ch[i]) {
          case '&' :
            out.append("&amp;");
            break;
          case '<' :
            out.append("&lt;");
            break;
          case '"' :
            out.append("&quot;");
            break;
          case '\'' :
            out.append("&#x27;");
            break;
          default:
            // output by default
            out.append(ch[i]);
        }
      }
      // control characters (C1): prune
      else if (ch[i] < 0xA0) {
        doNothing();
      }
      // handle surrogate pairs (for characters outside BMP)
      else if (ch[i] >= 0xD800 && ch[i] <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, off+len);
        i += Character.charCount(codePoint) - 1;
        out.append("&#x").append(Integer.toHexString(codePoint)).append(';');
      }
      // all other characters: use numerical character entity
      else {
        out.append("&#x").append(Integer.toHexString(ch[i])).append(';');
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
        out.append("&#x").append(Integer.toHexString(codePoint)).append(';');
      }
      // characters outside the ASCII range
      else if (c > 0x9F) {
        out.append("&#x").append(Integer.toHexString(ch[i])).append(';');
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

  /**
   * Does nothing.
   */
  private void doNothing() {
    // Ignore on purpose
  }

}

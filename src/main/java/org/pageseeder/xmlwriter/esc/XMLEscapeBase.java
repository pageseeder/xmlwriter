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
 * A base implementation for the XML escape classes.
 *
 * @author Christophe Lauret
 *
 * @since 1.0.0
 * @version 1.0.0
 */
abstract class XMLEscapeBase implements XMLEscape {

  /**
   * The encoding for the implementation.
   */
  private final String encoding;

  /**
   * Creates a new XML Escape.
   *
   * @param encoding The encoding used.
   */
  XMLEscapeBase(String encoding) {
    this.encoding = encoding;
  }

  /**
   * Default implementation calling the {@link XMLEscape#toAttributeValue(char[], int, int)}.
   *
   * @param value The value that needs to be attribute-escaped.
   *
   * @return A well-formed value for the attribute.
   */
  @Override
  public final String toAttributeValue(String value) {
    if (value == null || value.isEmpty()) return value;
    return toAttributeValue(value.toCharArray(), 0, value.length());
  }

  /**
   * Default implementation calling the {@link XMLEscape#toElementText(char[], int, int)}.
   *
   * @param text The text that needs to be escaped.
   *
   * @return A well-formed value for the text node.
   */
  @Override
  public final String toElementText(String text) {
    if (text == null || text.isEmpty()) return text;
    return toElementText(text.toCharArray(), 0, text.length());
  }

  @Override
  public final String getEncoding() {
    return this.encoding;
  }

}

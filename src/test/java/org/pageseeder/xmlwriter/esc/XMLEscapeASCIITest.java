/*
 * Copyright 2005-2016 Allette Systems (Australia)
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

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * A test class for the XML escape tool.
 *
 * @author Christophe Lauret
 */
final class XMLEscapeASCIITest extends XMLEscapeASCIITestBase {

  private final XMLEscape esc = XMLEscapeFactory.getInstance("ASCII");

  /**
   * Test the attribute escape method for <code>null</code>.
   */
  @Test
  void testToAttributeValue_Null() throws IOException {
    String got = escapeAttribute(null);
    assertNull(got);
  }

  /**
   * Test the element escape method for <code>null</code>.
   */
  @Test
  void testToElementText_Null() throws IOException {
    String got = escapeText(null);
    assertNull(got);
  }

  @Override
  String escapeAttribute(String value) throws IOException {
    return this.esc.toAttributeValue(value);
  }

  @Override
  String escapeText(String value) throws IOException {
    return this.esc.toElementText(value);
  }

}
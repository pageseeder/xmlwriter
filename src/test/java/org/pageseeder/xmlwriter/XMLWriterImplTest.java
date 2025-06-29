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
package org.pageseeder.xmlwriter;

import org.junit.jupiter.api.Test;

import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A test class for the <code>XMLWriterNSImpl</code>
 *
 * @author Christophe Lauret
 */
final class XMLWriterImplTest extends BaseXMLWriterTest {

  @Override
  public XMLWriter makeXMLWriter(Writer writer) {
    return new XMLWriterImpl(writer);
  }

  /**
   * Tests that the namespace-aware open element method throws an
   * unsupported open element exception.
   */
  @Test
  void testUnsupportedOperationException() {
    assertThrows(UnsupportedOperationException.class, () -> this.xml.openElement("", "x", true));
  }

  /**
   * Tests that the namespace-aware empty element method throws an
   * unsupported open element exception.
   */
  @Test
  void testUnsupportedEmptyElement() {
    assertThrows(UnsupportedOperationException.class, () -> this.xml.openElement("", "x", true));
  }

  /**
   * Tests that the namespace-aware set prefix mapping method throws an
   * unsupported open element exception.
   */
  @Test
  void testUnsupportedPrefixMapping() {
    assertThrows(UnsupportedOperationException.class, () -> this.xml.setPrefixMapping("", "x"));
  }

  /**
   * Tests that the namespace-aware attribute method throws an
   * unsupported open element exception.
   */
  @Test
  void testUnsupportedAttributeA() {
    assertThrows(UnsupportedOperationException.class, () -> this.xml.attribute("", "x", "m"));
  }

  /**
   * Tests that the namespace-aware attribute method throws an
   * unsupported open element exception.
   */
  @Test
  void testUnsupportedAttributeB() {
    assertThrows(UnsupportedOperationException.class, () -> this.xml.attribute("", "x", 0));
  }

}
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

import java.io.IOException;
import java.io.Writer;

/**
 * A test class for the <code>XMLWriterNSImpl</code>
 *
 * @author Christophe Lauret
 */
public final class XMLWriterImplTest extends BaseXMLWriterTest {

  /**
   * Default constructor.
   *
   * @param name Name of the test suite.
   */
  public XMLWriterImplTest(String name) {
    super(name);
  }

  /**
   * @see org.pageseeder.xmlwriter.BaseXMLWriterTest#makeXMLWriter(java.io.Writer)
   */
  @Override
  public XMLWriter makeXMLWriter(Writer writer) {
    return new XMLWriterImpl(writer);
  }

// test: unsupported methods ------------------------------------------------------------

  /**
   * Tests that the namesspace aware open element method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#openElement(String, String, boolean)
   */
  public void testUnsupportedOpenElement() throws IOException {
    try {
      this.xml.openElement("", "x", true);
      fail("The XML writer failed to report an unsupported operation.");
    } catch (UnsupportedOperationException ex) {
      assertTrue(true);
    }
  }

  /**
   * Tests that the namesspace aware empty element method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#emptyElement(String, String)
   */
  public void testUnsupportedEmptyElement() throws IOException {
    try {
      this.xml.openElement("", "x", true);
      fail("The XML writer failed to report an unsupported operation.");
    } catch (UnsupportedOperationException ex) {
      assertTrue(true);
    }
  }

  /**
   * Tests that the namesspace aware set prefix mapping method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#setPrefixMapping(String, String)
   */
  public void testUnsupportedPrefixMapping() {
    try {
      this.xml.setPrefixMapping("", "x");
      fail("The XML writer failed to report an unsupported operation.");
    } catch (UnsupportedOperationException ex) {
      assertTrue(true);
    }
  }

  /**
   * Tests that the namesspace aware attribute method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#attribute(String, String, String)
   */
  public void testUnsupportedAttributeA() throws IOException {
    try {
      this.xml.attribute("", "x", "m");
      fail("The XML writer failed to report an unsupported operation.");
    } catch (UnsupportedOperationException ex) {
      assertTrue(true);
    }
  }

  /**
   * Tests that the namesspace aware attribute method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#attribute(String, String, int)
   */
  public void testUnsupportedAttributeB() throws IOException {
    try {
      this.xml.attribute("", "x", 0);
      fail("The XML writer failed to report an unsupported operation.");
    } catch (UnsupportedOperationException ex) {
      assertTrue(true);
    }
  }

}
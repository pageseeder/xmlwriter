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

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.io.Writer;

/**
 * A test class for the <code>XMLWriterNSImpl</code>
 *
 * @author Christophe Lauret
 */
public final class XMLWriterImplTest extends BaseXMLWriterTest {

  /**
   * @see org.pageseeder.xmlwriter.BaseXMLWriterTest#makeXMLWriter(java.io.Writer)
   */
  @Override
  public XMLWriter makeXMLWriter(Writer writer) {
    return new XMLWriterImpl(writer);
  }

  /**
   * Tests that the namespace-aware open element method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#openElement(String, String, boolean)
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedOpenElement() throws IOException {
    this.xml.openElement("", "x", true);
    fail("The XML writer failed to report an unsupported operation.");
  }

  /**
   * Tests that the namespace-aware empty element method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#emptyElement(String, String)
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedEmptyElement() throws IOException {
    this.xml.openElement("", "x", true);
    fail("The XML writer failed to report an unsupported operation.");
  }

  /**
   * Tests that the namespace-aware set prefix mapping method throws an
   * unsupported open element exception.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedPrefixMapping() {
    this.xml.setPrefixMapping("", "x");
    fail("The XML writer failed to report an unsupported operation.");
  }

  /**
   * Tests that the namespace-aware attribute method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#attribute(String, String, String)
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedAttributeA() throws IOException {
    this.xml.attribute("", "x", "m");
    fail("The XML writer failed to report an unsupported operation.");
  }

  /**
   * Tests that the namespace-aware attribute method throws an
   * unsupported open element exception.
   *
   * @throws IOException Should an IO error occur.
   * @see XMLWriterImpl#attribute(String, String, int)
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedAttributeB() throws IOException {
    this.xml.attribute("", "x", 0);
    fail("The XML writer failed to report an unsupported operation.");
  }

}
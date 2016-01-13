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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

/**
 * A test class for the XML escape tool.
 *
 * @author Christophe Lauret
 */
public final class XMLEscapeWriterUTF8Test extends XMLEscapeUTF8TestBase {

  /**
   * Test the attribute escape method for <code>null</code>.
   */
  @Test
  public void testToAttributeValue_Null() throws IOException {
    String got = escapeAttribute(null);
    assertEquals("", got);
  }

  /**
   * Test the element escape method for <code>null</code>.
   */
  @Test public void testToElementText_Null() throws IOException {
    String got = escapeText(null);
    assertEquals("", got);
  }

  @Override
  public String escapeAttribute(String value) throws IOException {
    StringWriter got = new StringWriter();
    XMLEscapeWriterUTF8 esc = new XMLEscapeWriterUTF8(got);
    esc.writeAttValue(value);
    return got.toString();
  }

  @Override
  public String escapeText(String value) throws IOException {
    StringWriter got = new StringWriter();
    XMLEscapeWriterUTF8 esc = new XMLEscapeWriterUTF8(got);
    esc.writeText(value);
    return got.toString();
  }

}
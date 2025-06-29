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
package org.pageseeder.xmlwriter.sax;

import junit.framework.AssertionFailedError;
import org.junit.Before;
import org.junit.Test;
import org.pageseeder.xmlwriter.IllegalCloseElementException;
import org.pageseeder.xmlwriter.UnclosedElementException;
import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * A class for testing <code>XMLWriterSAX</code>s implementation.
 *
 * @author Christophe Lauret
 */
public final class XMLWriterSAXTest {

  /**
   * The XML reader.
   */
  private XMLReader reader;

  /**
   * The XML writer to test
   */
  private XMLWriterSAX xml;

  /**
   * The content handler used for checking.
   */
  private ContentHandlerChecker handler;

  /**
   * Sets up the handler for testing.
   */
  @Before
  public void setUpHandler() throws SAXException, ParserConfigurationException {
    this.handler = new ContentHandlerChecker();
    this.xml = new XMLWriterSAX(this.handler);
    if (this.reader == null) {
      this.reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
      this.reader.setFeature("http://xml.org/sax/features/validation", false);
      this.reader.setFeature("http://xml.org/sax/features/namespaces", true);
      this.reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    }
  }

  /**
   * Checks that the XML is closed properly.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testCloseElementA() throws IOException {
    this.xml.openElement("test");
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<test/>");
  }

  /**
   * Check that the XML is closed properly.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testCloseElementB() throws IOException {
    this.xml.openElement("x");
    this.xml.openElement("y");
    this.xml.closeElement();
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<x><y/></x>");
  }

  /**
   * Check that the XML is closed properly.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test(expected = IllegalCloseElementException.class)
  public void testCloseElementIllegal() throws IOException {
    this.xml.openElement("test");
    this.xml.closeElement();
    this.xml.closeElement();
  }

  /**
   * Checks that the element method works
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testElementWithText() throws IOException {
    this.xml.element("x", "text");
    this.xml.close();
    assertEquivalent("<x>text</x>");
  }

  /**
   * Checks that the element method works
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testElementNoText() throws IOException {
    this.xml.element("x", "");
    this.xml.close();
    assertEquivalent("<x/>");
  }

  /**
   * Checks that the element method works
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testElementNullText() throws IOException {
    this.xml.element("x", null);
    this.xml.close();
    assertEquivalent("<x/>");
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testEmptyElement() throws IOException {
    this.xml.emptyElement("x");
    this.xml.close();
    assertEquivalent("<x/>");
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testEmptyNSElement() throws IOException {
    this.xml.setPrefixMapping("j", "https://example.org");
    this.xml.setPrefixMapping("k", "https://example.org");
    this.xml.openElement("j:x");
    this.xml.emptyElement("k:y");
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<j:x xmlns:j='https://example.org' xmlns:k='https://example.org'><k:y/></j:x>");
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testAttributeA() throws IOException {
    this.xml.openElement("x");
    this.xml.attribute("a", "m");
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<x a='m'/>");
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testAttributeB() throws IOException {
    this.xml.openElement("x");
    this.xml.attribute("a", "m");
    this.xml.attribute("b", "n");
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<x a='m' b='n'/>");
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testAttributeNS() throws IOException {
    this.xml.setPrefixMapping("j", "https://example.org");
    this.xml.openElement("x");
    this.xml.attribute("a", "1");
    this.xml.attribute("j:b", "2");
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<x xmlns:j='https://example.org' a='m' j:b='n'/>");
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testComment() throws IOException {
    this.xml.openElement("root");
    this.xml.writeComment("comment");
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<root><!-- comment --></root>");
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testCommentNull() throws IOException {
    this.xml.openElement("root");
    this.xml.writeComment(null);
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<root></root>");
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  @Test
  public void testProcessingInstructionA() throws IOException {
    this.xml.openElement("root");
    this.xml.writePI("x", "y");
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<root><?x y?></root>");
  }

  /**
   * Check that the XML writer throws an exception when trying to close it
   * with a remaining open element.
   *
   * @throws IOException If an I/O error occurs.
   * @see UnclosedElementException
   */
  @Test(expected = UnclosedElementException.class)
  public void testCloseUnclosedException() throws IOException {
    this.xml.openElement("x");
    this.xml.close();
  }

  /**
   * Asserts that the two XML are equivalent.
   *
   * @param exp The expected XML.
   */
  public void assertEquivalent(String exp) {
    try {
      this.reader.setContentHandler(this.handler);
      this.reader.parse(new InputSource(new StringReader(exp)));

    } catch (Throwable ex) {
      System.err.println("<!-- expected XML was: -->");
      System.err.println(exp);
      throw new AssertionFailedError(ex.getMessage());
    }
  }

  /**
   * @author Christophe Lauret (Allette Systems)
   * @version 26 May 2005
   */
  private static final class ContentHandlerChecker implements ContentHandler {

    /**
     * The list of events it should produce.
     */
    private final List<String> events = new ArrayList<>();

    /**
     * The next event to check.
     */
    private transient int counter = 0;

    /**
     * The mode.
     */
    private boolean loading = true;

    /**
     * @see ContentHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) {
      String exp = "characters(\"" + new String(ch, start, length) + "\");";
      processEvent(exp);
    }

    /**
     * @see ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String local, String qName, Attributes atts) {
      // ignore the Q name and the attributes for now
      String exp = "startElement(\"" + uri + "\", \"" + local + "\");";
      processEvent(exp);
    }

    /**
     * @see ContentHandler#endElement(String, String, String)
     */
    @Override
    public void endElement(String uri, String local, String qName) {
      // ignore the Q name
      String exp = "endElement(\"" + uri + "\", " + local + "\");";
      processEvent(exp);
    }

    /**
     * @see ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    @Override
    public void startPrefixMapping(String prefix, String uri) {
      String exp = "startPrefixMapping(\"" + prefix + "\", \"" + uri + "\");";
      processEvent(exp);
    }

    @Override
    public void endPrefixMapping(String prefix) {
      String exp = "endPrefixMapping(\"" + prefix + "\");";
      processEvent(exp);
    }

    /**
     * @see ContentHandler#ignorableWhitespace(char[], int, int)
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
      String exp = "ignorableWhitespace(\"" + new String(ch, start, length) + "\");";
      processEvent(exp);
    }

    /**
     * @see ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    @Override
    public void processingInstruction(String target, String data) throws SAXException {
      String exp = "processingInstruction(\"" + target + "\", \"" + data + "\");";
      processEvent(exp);
    }

    /**
     * @see ContentHandler#skippedEntity(String)
     */
    @Override
    public void skippedEntity(String name) {
      fail();
    }

    /**
     * This method is ignored.
     *
     * @see ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    @Override
    public void setDocumentLocator(Locator locator) {
      // Ignore
    }

    /**
     * Does nothing as this event should be generated externally.
     */
    @Override
    public void startDocument() {
      // Ignore
    }

    /**
     * Does nothing as this event should be generated externally.
     */
    @Override
    public void endDocument() {
      this.loading = !this.loading;
    }

    /**
     * Asserts that the event given as a string is the same event
     * as the expected event.
     *
     * @param exp The expected event as a string.
     */
    private void processEvent(String exp) {
      if (this.loading) {
        this.events.add(exp);
      } else {
        assertTrue(this.counter < this.events.size());
        assertEquals(exp, this.events.get(this.counter++));
      }
    }

  }

}
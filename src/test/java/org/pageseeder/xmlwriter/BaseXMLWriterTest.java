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
import java.io.StringWriter;
import java.io.Writer;

import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.sequence.EventSequence;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * A base class for testing <code>XMLWriter</code>s implementation.
 *
 * <p>Note: this is not a test class for the XMLWriterBase.
 *
 * @author Christophe Lauret
 */
public abstract class BaseXMLWriterTest extends TestCase {

  /**
   * The loader to use.
   */
  private SAXRecorder recorder = new SAXRecorder();

  /**
   * The XML Writer being tested.
   */
  transient XMLWriter xml = null;

  /**
   * The underlying writer used by the XML writer.
   *
   * <p>Access using {@link #getXMLString()}.
   */
  private transient StringWriter w = null;

  /**
   * The sample ASCII string for testing.
   */
  private static final String SAMPLE_ASCII_STRING = makeSampleASCIIString();

// constructors and non-test methods ----------------------------------------------------

  /**
   * Default constructor.
   *
   * @param name Name of the test suite.
   */
  public BaseXMLWriterTest(String name) {
    super(name);
  }

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected final void setUp() throws Exception {
    this.w = new StringWriter();
    this.xml = makeXMLWriter(this.w);
  }

  /**
   * Generates the XML writer to be tested by this class.
   *
   * @param writer The writer this formatter should use.
   *
   * @return The XML Diffx Formatter to use.
   */
  public abstract XMLWriter makeXMLWriter(Writer writer);

// test: openElement / closeElement ---------------------------------------------------------------

  /**
   * Checks that the XML is closed properly.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testCloseElementA() throws IOException {
    this.xml.openElement("test");
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<test/>", getXMLString());
  }

  /**
   * Check that the XML is closed properly.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testCloseElementB() throws IOException {
    this.xml.openElement("x");
    this.xml.openElement("y");
    this.xml.closeElement();
    this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<x><y/></x>", getXMLString());
  }

  /**
   * Check that the XML is closed properly.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testCloseElementIllegal() throws IOException {
    this.xml.openElement("test");
    this.xml.closeElement();
    try {
      this.xml.closeElement();
      System.err.println("The XML writer failed to report an unclosed element.");
      assertTrue(false);
    } catch (IllegalCloseElementException ex) {
      assertTrue(true);
    }
  }

// test: element -----------------------------------------------------------------------

  /**
   * Checks that the element method works
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testElementWithText() throws IOException {
    this.xml.element("x", "text");
    this.xml.close();
    assertEquivalent("<x>text</x>", getXMLString());
  }

  /**
   * Checks that the element method works
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testElementNoText() throws IOException {
    this.xml.element("x", "");
    this.xml.close();
    assertEquivalent("<x/>", getXMLString());
  }

  /**
   * Checks that the element method works
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testElementNullText() throws IOException {
    this.xml.element("x", null);
    this.xml.close();
    assertEquivalent("<x/>", getXMLString());
  }

// test: empty element ------------------------------------------------------------------

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testEmptyElement() throws IOException {
    this.xml.emptyElement("x");
    this.xml.close();
    assertEquivalent("<x/>", getXMLString());
  }

// test: attributes ---------------------------------------------------------------------

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testAttributeA() throws IOException {
    this.xml.openElement("x");
  this.xml.attribute("a", "m");
  this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<x a='m'/>", getXMLString());
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testAttributeB() throws IOException {
    this.xml.openElement("x");
  this.xml.attribute("a", "m");
  this.xml.attribute("b", "n");
  this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<x a='m' b='n'/>", getXMLString());
  }

// test: escape -------------------------------------------------------------------------

  /**
   *
   */
  public final void testEscapeTextASCII() throws IOException {
    this.xml.element("x", SAMPLE_ASCII_STRING);
    this.xml.close();
    assertWellFormed(getXMLString());
  }

  /**
   *
   */
  public final void testEscapeAttributeASCII() throws IOException {
    this.xml.openElement("root");
    this.xml.attribute("x", SAMPLE_ASCII_STRING);
  this.xml.closeElement();
    this.xml.close();
    assertWellFormed(getXMLString());
  }

// test: comment ------------------------------------------------------------------------

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testComment() throws IOException {
    this.xml.openElement("root");
  this.xml.writeComment("comment");
  this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<root><!-- comment --></root>", getXMLString());
    assertEquals("<root><!-- comment --></root>", getXMLString());
  }

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testCommentNull() throws IOException {
    this.xml.openElement("root");
  this.xml.writeComment(null);
  this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<root></root>", getXMLString());
  }

  /**
   * Checks that the comment method throws an illegal argument exception if it contain "--".
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testCommentIllegal() throws IOException {
    try {
    this.xml.writeComment("--");
      assertTrue(false);
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    }
  }

// test: processing instructions --------------------------------------------------------

  /**
   * Checks that the empty element method works.
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testProcessingInstructionA() throws IOException {
    this.xml.openElement("root");
  this.xml.writePI("x", "y");
  this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<root><?x y?></root>", getXMLString());
  }

// test: indentation --------------------------------------------------------------------

  /**
   *
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testIndentationA() throws IOException {
  this.xml.setIndentChars("  ");
    this.xml.openElement("root", false);
  this.xml.writeText("text");
  this.xml.closeElement();
    this.xml.close();
    assertEquivalent("<root>text</root>", getXMLString());
    assertEquals("<root>text</root>", getXMLString());
  }

  /**
   *
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testIndentationB() throws IOException {
  this.xml.setIndentChars("  ");
    this.xml.openElement("root", true);
    this.xml.openElement("a");
  this.xml.writeText("text");
  this.xml.closeElement();
  this.xml.closeElement();
    this.xml.close();
  String expected = "<root>\n"
                + "  <a>text</a>\n"
          + "</root>";
    assertEquals(expected, getXMLString());
  }

  /**
   *
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testIndentationC() throws IOException {
  this.xml.setIndentChars("  ");
    this.xml.openElement("root", true);
    this.xml.element("a", "one");
    this.xml.element("b", "two");
  this.xml.closeElement();
    this.xml.close();
  String expected = "<root>\n"
                + "  <a>one</a>\n"
                + "  <b>two</b>\n"
          + "</root>";
    assertEquals(expected, getXMLString());
  }

  /**
   *
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testIndentationD() throws IOException {
  this.xml.setIndentChars(null);
    this.xml.openElement("root", true);
    this.xml.element("a", "one");
    this.xml.element("b", "two");
  this.xml.closeElement();
    this.xml.close();
  String expected = "<root><a>one</a><b>two</b></root>";
    assertEquals(expected, getXMLString());
  }

// test: close --------------------------------------------------------------------------

  /**
   * Check that the XML writer throws an exception when trying to close it
   * with a remaining open element.
   *
   * @see UnclosedElementException
   *
   * @throws IOException If an I/O error occurs.
   */
  public final void testCloseUnclosedException() throws IOException {
    this.xml.openElement("x");
    try {
      this.xml.close();
      System.err.println("The XML writer failed to report an unclosed element.");
      assertTrue(false);
    } catch (UnclosedElementException ex) {
      assertTrue(true);
    }
  }

// public methods to test checks --------------------------------------------------------

  /**
   * Asserts that the two XML are equivalent.
   *
   * @param exp The expected XML.
   * @param act The actual XML produced.
   */
  public final void assertEquivalent(String exp, String act) {
    try {
      // process the XML to get the sequence
      EventSequence exseq = this.recorder.process(exp);
      EventSequence acseq = this.recorder.process(act);
      assertEquals(exseq, acseq);
    } catch (Throwable ex) {
      System.err.println("<!-- expected XML was: -->");
      System.err.println(exp);
      System.err.println("<!-- Actual XML is: -->");
      System.err.println(act);
      throw new AssertionFailedError(ex.getMessage());
    }
  }

  /**
   * Asserts that the XML is well formed.
   *
   * @param actual The actual XML produced.
   */
  public final void assertWellFormed(String actual) {
    try {
      this.recorder.process(actual);
    } catch (Throwable ex) {
      assertTrue(false);
    }
  }

  /**
   * Returns the XML produced by the current XML Writer instance.
   *
   * @return The XML produced by the current XML Writer instance.
   */
  public final String getXMLString() {
    return this.w.toString();
  }

  /**
   * Generates a sample ASCII string.
   *
   * @return a sample ASCII string.
   */
  private static String makeSampleASCIIString() {
  StringBuffer out = new StringBuffer(255);
  for (int i = 0; i < 255; i++) {
      out.append((char)i);
  }
  return out.toString();
  }

}
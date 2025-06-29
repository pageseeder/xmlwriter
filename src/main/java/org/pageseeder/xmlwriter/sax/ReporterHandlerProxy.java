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
package org.pageseeder.xmlwriter.sax;

import java.io.PrintStream;
import java.util.Objects;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A content handler wrapping another content handler and reporting information about
 * what methods are being called.
 *
 * @author Christophe Lauret
 *
 * @since 1.0.0
 * @version 1.1.0
 */
@SuppressWarnings("java:S1192")
public final class ReporterHandlerProxy implements ContentHandler {

  /**
   * The handler that will receive events.
   */
  private final ContentHandler handler;

  /**
   * Where events should be reported.
   */
  private final PrintStream report;

  /**
   * Creates a new handler proxy reporting events to <code>System.err</code>.
   *
   * @param handler The handler that will receive events.
   */
  @SuppressWarnings("java:S106") // Deliberate use of System.err
  public ReporterHandlerProxy(ContentHandler handler) {
    this.handler = Objects.requireNonNull(handler);
    this.report = System.err;
  }

  /**
   * Creates a new handler proxy.
   *
   * @param handler The handler that will receive events.
   * @param report  The print stream where errors should be reported.
   */
  public ReporterHandlerProxy(ContentHandler handler, PrintStream report) {
    this.handler = Objects.requireNonNull(handler);
    this.report = report;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    this.report.println("characters(\"" + new String(ch, start, length) + "\");");
    this.handler.characters(ch, start, length);
  }

  @Override
  public void startElement(String uri, String local, String qName, Attributes atts)
      throws SAXException {
    this.report.println("startElement(\""+uri+"\", \""+local+"\", \""+qName+"\");");
    this.handler.startElement(uri, local, qName, atts);
  }

  @Override
  public void endElement(String uri, String local, String qName) throws SAXException {
    this.report.println("endElement(\""+uri+"\", \""+local+"\", \""+qName+"\");");
    this.handler.endElement(uri, local, qName);
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    this.report.println("startPrefixMapping(\"" + prefix + "\", \"" + uri + "\");");
    this.handler.startPrefixMapping(prefix, uri);
  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    this.report.println("endPrefixMapping(\"" + prefix + "\");");
    this.handler.endPrefixMapping(prefix);
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    this.report.println("ignorableWhitespace(\""+new String(ch, start, length)+"\");");
    this.handler.ignorableWhitespace(ch, start, length);
  }

  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    this.report.println("processingInstruction(\"" + target + "\", \"" + data + "\");");
    this.handler.processingInstruction(target, data);
  }

  @Override
  public void skippedEntity(String name) throws SAXException {
    this.report.println("skippedEntity(\""+name+"\");");
    this.handler.skippedEntity(name);
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    this.handler.setDocumentLocator(locator);
  }

  @Override
  public void startDocument() throws SAXException {
    this.report.println("startDocument();");
    this.handler.startDocument();
  }

  @Override
  public void endDocument() throws SAXException {
    this.report.println("endDocument();");
    this.handler.endDocument();
  }

  /**
   * Returns the content handler.
   *
   * @return the wrapped content handler.
   */
  public ContentHandler getContentHandler() {
    return this.handler;
  }

}

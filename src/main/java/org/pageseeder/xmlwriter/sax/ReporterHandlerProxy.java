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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A content handler wrapping another content handler and reporting information about
 * what methods are being called.
 *
 * @author Christophe Lauret
 */
public final class ReporterHandlerProxy implements ContentHandler {

  /**
   * The handler that will receive events.
   */
  private final ContentHandler _handler;

  /**
   * Where events should be reported.
   */
  private final PrintStream _report;

  /**
   * Creates a new handler proxy reporting events to <code>System.err</code>.
   *
   * @param handler The handler that will receive events.
   */
  public ReporterHandlerProxy(ContentHandler handler) {
    this._handler = handler;
    this._report = System.err;
  }

  /**
   * Creates a new handler proxy.
   *
   * @param handler The handler that will receive events.
   * @param report  The print stream where errors should be reported.
   */
  public ReporterHandlerProxy(ContentHandler handler, PrintStream report) {
    this._handler = handler;
    this._report = report;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    this._report.println("characters(\"" + new String(ch, start, length) + "\");");
    this._handler.characters(ch, start, length);
  }

  @Override
  public void startElement(String uri, String local, String qName, Attributes atts)
      throws SAXException {
    this._report.println("startElement(\""+uri+"\", \""+local+"\", \""+qName+"\");");
    this._handler.startElement(uri, local, qName, atts);
  }

  @Override
  public void endElement(String uri, String local, String qName) throws SAXException {
    this._report.println("endElement(\""+uri+"\", \""+local+"\", \""+qName+"\");");
    this._handler.endElement(uri, local, qName);
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    this._report.println("startPrefixMapping(\"" + prefix + "\", \"" + uri + "\");");
    this._handler.startPrefixMapping(prefix, uri);
  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    this._report.println("endPrefixMapping(\"" + prefix + "\");");
    this._handler.endPrefixMapping(prefix);
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    this._report.println("ignorableWhitespace(\""+new String(ch, start, length)+"\");");
    this._handler.ignorableWhitespace(ch, start, length);
  }

  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    this._report.println("processingInstruction(\"" + target + "\", \"" + data + "\");");
    this._handler.processingInstruction(target, data);
  }

  @Override
  public void skippedEntity(String name) throws SAXException {
    this._report.println("skippedEntity(\""+name+"\");");
    this._handler.skippedEntity(name);
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    this._handler.setDocumentLocator(locator);
  }

  @Override
  public void startDocument() throws SAXException {
    this._report.println("startDocument();");
    this._handler.startDocument();
  }

  @Override
  public void endDocument() throws SAXException {
    this._report.println("endDocument();");
    this._handler.endDocument();
  }

  /**
   * Returns the content handler.
   *
   * @return the wrapped content handler.
   */
  public ContentHandler getContentHandler() {
    return this._handler;
  }

}

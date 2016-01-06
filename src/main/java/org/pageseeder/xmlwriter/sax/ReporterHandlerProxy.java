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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A content handler wrapping another content handler and reporting information about
 * what methods are being called.
 */
public final class ReporterHandlerProxy implements ContentHandler {

  /**
   * The handler that will receive events.
   */
  private final ContentHandler handler;

  /**
   * Creates a new handler proxy.
   *
   * @param handler The handler that will receive events.
   */
  public ReporterHandlerProxy(ContentHandler handler) {
    this.handler = handler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    System.err.println("characters(\"" + new String(ch, start, length) + "\");");
    this.handler.characters(ch, start, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startElement(String uri, String local, String qName, Attributes atts)
      throws SAXException {
    System.err.println("startElement(\""+uri+"\", \""+local+"\", \""+qName+"\");");
    this.handler.startElement(uri, local, qName, atts);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endElement(String uri, String local, String qName) throws SAXException {
    System.err.println("endElement(\""+uri+"\", \""+local+"\", \""+qName+"\");");
    this.handler.endElement(uri, local, qName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    System.err.println("startPrefixMapping(\"" + prefix + "\", \"" + uri + "\");");
    this.handler.startPrefixMapping(prefix, uri);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    System.err.println("endPrefixMapping(\"" + prefix + "\");");
    this.handler.endPrefixMapping(prefix);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    System.err.println("ignorableWhitespace(\""+new String(ch, start, length)+"\");");
    this.handler.ignorableWhitespace(ch, start, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    System.err.println("processingInstruction(\"" + target + "\", \"" + data + "\");");
    this.handler.processingInstruction(target, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void skippedEntity(String name) throws SAXException {
    System.err.println("skippedEntity(\""+name+"\");");
    this.handler.skippedEntity(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDocumentLocator(Locator locator) {
    this.handler.setDocumentLocator(locator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startDocument() throws SAXException {
    System.err.println("startDocument();");
    this.handler.startDocument();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endDocument() throws SAXException {
    System.err.println("endDocument();");
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

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * An XMLReader implementation that can be used to parse XMLWritable objects.
 *
 * <p>Typically, XMLWritable objects are wrapped into an
 * <code>XMLWritableInputSource</code> so that the <code>XMLReader</code> API
 * methods are used; however, it is perfectly possible to parse directly an
 * <code>XMLWritable</code> object.
 *
 * @see org.xml.sax.XMLReader
 * @see org.pageseeder.xmlwriter.XMLWritable
 * @see org.pageseeder.xmlwriter.sax.XMLWritableInputSource
 *
 * @author Christophe Lauret
 *
 * @since 1.0.0
 * @version 1.1.1
 */
public final class XMLWritableReader implements XMLReader {

  /**
   * The URI of the namespace feature.
   */
  private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";

  /**
   * The URI of the namespace prefixes feature.
   */
  private static final String NS_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

  /**
   * The features used by this XML reader implementation.
   */
  private final Map<String, Boolean> features = new HashMap<>();

  /**
   * The content reader this XMLReader will use.
   */
  private @Nullable ContentHandler handler;

  /**
   * Creates a new XML Reader.
   */
  public XMLWritableReader() {
    setFeature(NAMESPACES, true);
    setFeature(NS_PREFIXES, false);
  }

  // XMLReader methods implementation -----------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public ContentHandler getContentHandler() {
    return this.handler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setContentHandler(ContentHandler handler) {
    this.handler = handler;
  }

  /**
   * Returns <code>null</code>.
   *
   * {@inheritDoc}
   */
  @Override
  public @Nullable ErrorHandler getErrorHandler() {
    return null;
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void setErrorHandler(@Nullable ErrorHandler ehandler) {
    // Does nothing by default
  }

  /**
   * Returns <code>null</code>.
   *
   * {@inheritDoc}
   */
  @Override
  public @Nullable DTDHandler getDTDHandler() {
    return null;
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void setDTDHandler(@Nullable DTDHandler handler) {
    // Does nothing by default
  }

  /**
   * Returns <code>null</code>.
   *
   * {@inheritDoc}
   */
  @Override
  public @Nullable EntityResolver getEntityResolver() {
    return null;
  }

  /**
   * Returns <code>null</code>.
   *
   * {@inheritDoc}
   */
  @Override
  public void setEntityResolver(@Nullable EntityResolver resolver) {
    // Ignored by default
  }

  /**
   * Returns <code>null</code>.
   *
   * {@inheritDoc}
   */
  @Override
  public @Nullable Object getProperty(String name) {
    return null;
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void setProperty(String name, Object value) {
    // Ignored by default
  }

  @Override
  public boolean getFeature(String name) {
    // TODO: handling of features
    return this.features.get(name).booleanValue();
  }

  @Override
  public void setFeature(String name, boolean value) {
    // TODO: handling of features
    this.features.put(name, value);
  }

  @Override
  public void parse(String systemId) throws IOException, SAXException {
    throw new SAXException(
        this.getClass().getName()
        + " cannot be used with system identifiers (URIs)");
  }

  @Override
  public void parse(InputSource input) throws IOException, SAXException {
    if (input instanceof XMLWritableInputSource) {
      parse((XMLWritableInputSource)input);
    } else
      throw new SAXException("Unsupported InputSource specified. Must be a XMLWritableInputSource");
  }

  public void parse(XMLWritableInputSource input) throws IOException, SAXException {
    parse(input.getXMLWritable());
  }

  public void parse(XMLWritable xml) throws IOException, SAXException {
    Objects.requireNonNull(xml, "Parameter xml must not be null");
    if (this.handler == null)
      throw new IllegalStateException("ContentHandler not set");
    // start handling the document
    this.handler.startDocument();
    XMLWriter xw = new XMLWriterSAX(this.handler);
    xml.toXML(xw);
    this.handler.endDocument();
  }

}

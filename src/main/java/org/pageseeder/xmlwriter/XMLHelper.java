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
package org.pageseeder.xmlwriter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Utility class that provides common XML operations so that the calling class uses lighter
 * code.
 *
 * <p>Most methods in this implementation will forward the exceptions should occur, so they
 * will have to be handled externally.
 *
 * @author Christophe Lauret
 */
public final class XMLHelper {

  /**
   * Prevents creation of instances.
   */
  private XMLHelper() {
  }

  /**
   * Creates a non-validating, non-namespace-aware {@link XMLReader} using the specified
   * {@link ContentHandler}.
   *
   * <p>If the given {@link ContentHandler} is <code>null</code>, the {@link XMLReader} is
   * not initialised.
   *
   * @param handler The content handler to use.
   *
   * @return The requested {@link XMLReader}
   *
   * @throws SAXException Should a SAX exception occur
   * @throws ParserConfigurationException Should a parser config exception occur
   */
  public static XMLReader makeXMLReader(ContentHandler handler)
      throws SAXException, ParserConfigurationException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(false);
    factory.setValidating(false);
    XMLReader reader = factory.newSAXParser().getXMLReader();
    if (handler != null) {
      reader.setContentHandler(handler);
    }
    return reader;
  }

  /**
   * Parses the given {@link File} with the specified {@link XMLReader}.
   *
   * <p>This method assumes the XML is in 'UTF-8', it will not sniff the XML to
   * determine the encoding to use.
   *
   * @param xmlreader The XML reader to use.
   * @param file      The file to parse
   *
   * @throws FileNotFoundException If the file does not exists
   * @throws SAXException          Should an SAX exception occur
   * @throws IOException           Should an I/O exception occur
   */
  public static void parse(XMLReader xmlreader, File file)
      throws FileNotFoundException, SAXException, IOException {
    // create the input source
    InputStream bin = new BufferedInputStream(new FileInputStream(file));
    Reader reader = new InputStreamReader(bin, "utf-8");
    InputSource source = new InputSource(reader);
    // parse the file
    xmlreader.parse(source);
    reader.close();
  }

}

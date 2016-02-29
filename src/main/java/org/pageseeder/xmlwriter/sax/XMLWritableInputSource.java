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

import org.pageseeder.xmlwriter.XMLWritable;
import org.xml.sax.InputSource;

/**
 * An XML input source implementation wrapping a XML writable object.
 *
 * <p>This class allows a SAX application to encapsulate information
 * about an input source in a single object.</p>
 *
 * <p>Because it does not provide a byte stream, character stream or
 * public or system identifier, this class is only meant to be used
 * by the <code>XMLWritableReader</code> which will use the
 * <code>XMLWritable</code> object.
 *
 * <p>An InputSource object belongs to the application: the SAX parser
 * shall not modify it in any way.</p>
 *
 * @see org.xml.sax.InputSource
 * @see org.pageseeder.xmlwriter.XMLWritable
 * @see org.pageseeder.xmlwriter.sax.XMLWritableReader
 *
 * @author Christophe Lauret
 */
public final class XMLWritableInputSource extends InputSource {

  /**
   * The wrapped XML writable object.
   */
  private final XMLWritable _source;

  /**
   * Creates an XML Writable object.
   *
   * @param object The XMLWritable object to wrap.
   */
  public XMLWritableInputSource(XMLWritable object) {
    this._source = object;
  }

  /**
   * Returns the XMLWritable object
   *
   * @return The XMLWritable object
   */
  public XMLWritable getXMLWritable() {
    return this._source;
  }

}

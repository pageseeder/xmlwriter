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
package org.pageseeder.xmlwriter.esc;

/**
 * Factory for XML escape classes.
 *
 * @author Christophe Lauret
 */
public final class XMLEscapeFactory {

  /**
   * Prevents creation of instances.
   */
  private XMLEscapeFactory() {
  }

  /**
   * Returns an instance of a XML Escape based on the given encoding.
   *
   * <p>This method returns <code>null</code> if the encoding is not supported.
   *
   * @param encoding The encoding for which an escape implementation is requested.
   *
   * @return A <code>XMLEscape</code> instance corresponding to the specified encoding
   *         or <code>null</code>.
   */
  public static XMLEscape getInstance(String encoding) {
    if ("utf-8".equals(encoding)) return XMLEscapeUTF8.UTF8_ESCAPE;
    if ("UTF-8".equals(encoding)) return XMLEscapeUTF8.UTF8_ESCAPE;
    if ("ASCII".equals(encoding)) return XMLEscapeASCII.ASCII_ESCAPE;
    else return null;
  }

}

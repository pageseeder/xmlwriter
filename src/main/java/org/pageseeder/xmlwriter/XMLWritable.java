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

import java.io.IOException;

/**
 * <p>An Object which implements this interface can be written as XML using a
 * {@link XMLWriter} instance.
 *
 * @author Christophe Lauret (Allette Systems)
 *
 * @version 14 September 2004
 */
public interface XMLWritable {

  /**
   * Writes the XML representation of the implementing instance using the specified
   * {@link XMLWriter}.
   *
   * @param xml The XMLWriter to use.
   *
   * @throws IOException IF an I/O exception occurs whilst writing.
   */
  void toXML(XMLWriter xml) throws IOException;

}

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

/**
 * <p>An Object which implements this interface can be formatted as XML using a <code>toXML</code>
 * method as a string.
 *
 * <p>This method is provided for convenience for small object to avoid the overhead in using
 * writers.
 *
 * @author Christophe Lauret
 *
 * @since 1.0.0
 * @version 1.1.1
 */
public interface XMLFormattable {

  /**
   * Appends the XML representation of the object of the implementing class.
   *
   * <p>Implementations must ensure that the returned string buffer is the same
   * object as the specified string buffer.
   *
   * @param xml The string buffer to which the XML representation is appended to.
   *
   * @return The modified string buffer.
   *
   * @throws NullPointerException if the specified character sequence is <code>null</code>.
   */
  StringBuffer toXML(StringBuffer xml);

  /**
   * <p>Returns an XML representation of the object of the implementing class.
   *
   * <p>Most implementation should use the following code to ensure consistent data with the
   * other <code>toXML</code> method:
   *
   * <pre>return this.toXML(new StringBuilder()).toString();</pre>
   *
   * @return a XML representation of the object of the implementing class.
   */
  default String toXML() {
    return this.toXML(new StringBuffer()).toString();
  }

}

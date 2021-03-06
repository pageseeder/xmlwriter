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

import org.pageseeder.xmlwriter.esc.XMLEscapeUTF8;

/**
 * A utility class for XML data.
 *
 * @author Christophe Lauret
 *
 * @deprecated
 */
@Deprecated
public final class XMLUtils {

  /**
   * Prevents creation of instances.
   */
  private XMLUtils() {
  }

  /**
   * Replaces characters which are invalid in element values, by the corresponding entity
   * in a given <code>String</code>.
   *
   * <p>these characters are:<br>
   * <ul>
   *  <li>'&amp' by the ampersand entity "&amp;amp"</li>
   *  <li>'&lt;' by the entity "&amp;lt;"</li>
   * </p>
   *
   * <p>Empty strings or <code>null</code> return respectively "" and <code>null</code>.
   *
   * <p>Note: this function assumes that there are no entities in the given String. If there
   * are existing entities, then the ampersand character will be escaped by the ampersand
   * entity.
   *
   * <p>This method does not replaces " (by &amp;quot;) which is an invalid character in
   * attribute values.
   *
   * @see #escapeAttr
   *
   * @param  s The String to be parsed
   *
   * @return a valid string or empty if s is <code>null</code> or empty.
   */
  public static String escape(String s) {
    return XMLEscapeUTF8.UTF8_ESCAPE.toElementText(s);
  }

  /**
   * Replace characters which are invalid in attribute values,
   * by the corresponding entity in a given <code>String</code>.
   *
   * <p>these characters are:<br>
   * <ul>
   *  <li>'&amp' by the ampersand entity "&amp;amp"</li>
   *  <li>'&lt;' by the entity "&amp;lt;"</li>
   *  <li>'&apos;' by the entity "&amp;apos;"</li>
   *  <li>'&quot;' by the entity "&amp;quot;"</li>
   * </p>
   *
   * <p>Empty strings or <code>null</code> return respectively
   * "" and <code>null</code>.
   *
   * <p>Note: this function assumes that there are no entities in
   * the given String. If there are existing entities, then the
   * ampersand character will be escaped by the ampersand entity.
   *
   *
   * @param  s The String to be parsed
   *
   * @return a valid string or empty if s is <code>null</code> or empty.
   */
  public static String escapeAttr(String s) {
    return XMLEscapeUTF8.UTF8_ESCAPE.toAttributeValue(s);
  }

  /**
   * Return a valid element name from the given string.
   *
   * <p>Letters are put to lower case and other characters are replaced by hyphens.
   * If the first character is not a letter it is replaced by 'x'.
   *
   * @param name The candidate element name
   *
   * @return A valid element name
   */
  public static String toElementName(String name) {
    return XML.toElementName(name);
  }

}

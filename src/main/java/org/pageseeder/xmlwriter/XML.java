package org.pageseeder.xmlwriter;

import java.io.IOException;

import org.pageseeder.xmlwriter.esc.XMLEscapeUTF8;

/**
 * Utility class.
 *
 * @author Christophe Lauret
 * @since 1.0.2
 */
public final class XML {

  private XML() {
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
    if (name == null) return null;
    char[] elementAsChars = name.toCharArray();
    if (!Character.isLetter(elementAsChars[0])) {
      elementAsChars[0] = 'x';
    } else {
      elementAsChars[0] = Character.toLowerCase(elementAsChars[0]);
    }
    for (int i = 1; i < elementAsChars.length; i++) {
      if (!Character.isLetter(elementAsChars[i])) {
        elementAsChars[i] = '-';
      } else {
        elementAsChars[i] = Character.toLowerCase(elementAsChars[i]);
      }
    }
    return new String(elementAsChars);
  }

  /**
   * A convenience method to serialize an <code>XMLWritable</code> object into
   * a string.
   *
   * @param o The object to serialize as XML.
   *
   * @return The element as a string.
   */
  public String toString(XMLWritable o) {
    XMLStringWriter xml = new XMLStringWriter(true);
    try {
      o.toXML(xml);
    } catch (IOException ex) {
      // Will never happen
    }
    xml.flush();
    return xml.toString();
  }

  /**
   * A convenience method to serialize an <code>XMLSerializable</code> object into
   * a string.
   *
   * @param o The object to serialize as XML.
   *
   * @return The element as a string.
   */
  public String toString(XMLSerializable o) {
    XMLStringWriter xml = new XMLStringWriter(false);
    XMLSerializer serializer = new XMLSerializer(xml);
    try {
      serializer.serializeObject(o);
    } catch (IOException ex) {
      // Will never happen
    }
    return xml.toString();
  }

}

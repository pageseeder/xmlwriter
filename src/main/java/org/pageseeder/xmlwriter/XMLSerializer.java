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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class provides methods to serialize objects to XML.
 *
 * <p>There is no mechanism to prevent infinite loops if some objects (lists,...) reference
 * themselves.
 *
 * <p>The underlying XML document is generated using an XML string buffer.
 *
 * <p>Implementation note: this class is not thread safe.
 *
 * @author Christophe Lauret
 *
 * @since 1.0.0
 * @version 1.1.0
 */
public final class XMLSerializer {

  /**
   * Formats dates using ISO 8601
   */
  private final DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

  /**
   * Used to store the xml document of this class.
   *
   * <p>Classes extending this class should use their constructors to set the size of the
   * <code>XMLStringBuffer</code>.
   */
  private final XMLWriter xml;

  /**
   * Creates a new XML serializer using the specified XML writer.
   *
   * @param xml The XML string buffer to be used
   */
  public XMLSerializer(XMLWriter xml) {
    this.xml = Objects.requireNonNull(xml);
  }

  /**
   * Returns the underlying XML document.
   *
   * @return the underlying xml write
   */
  public XMLWriter getXML() {
    return this.xml;
  }

  /**
   * Serialises the given object using the given name as element name.
   *
   * <p>This implementation is recursive. It calls itself for fields which are not of
   * primitive type.
   *
   * @param o     Object to be serialised as xml
   * @param name  Name of object
   *
   * @throws IOException Should an I/O error occur.
   */
  public void serialize(Object o, String name) throws IOException {
    if (o != null) {
      // get rid of some nasty symbols from qualified names and inner classes
      if (name.lastIndexOf('.') != -1) {
        name = name.substring(name.lastIndexOf('.')+1);
      }
      if (name.lastIndexOf('$') != -1) {
        name = name.substring(name.lastIndexOf('$')+1);
      }
      name = name.toLowerCase();
      if (o instanceof Number || o instanceof String || o instanceof Boolean) {
        this.xml.openElement(name, false);
        this.xml.writeText(o.toString());
        this.xml.closeElement();
      } else if (o instanceof Character) {
        this.xml.openElement(name, false);
        this.xml.writeText((Character)o);
        this.xml.closeElement();
      } else if (o instanceof Date) {
        this.xml.openElement(name, false);
        this.xml.writeText(this.iso8601.format((Date)o));
        this.xml.closeElement();
      } else if (o instanceof Collection<?>) {
        this.xml.openElement(name, !((Collection<?>)o).isEmpty());
        serializeCollection((Collection<?>)o);
        this.xml.closeElement();
      } else if (o instanceof Map<?,?>) {
        this.xml.openElement(name, !((Map<?, ?>) o).isEmpty());
        serializeMap((Map<?,?>)o);
        this.xml.closeElement();
      } else {
        this.xml.openElement(name, true);
        serializeObject(o);
        this.xml.closeElement();
      }
    }
  }

  /**
   * Serialises the given Collection to xml.
   *
   * <p>Iterates over every object and call the {@link #serialize} method.
   *
   * @param c The Collection to be serialised to XML
   *
   * @throws IOException Should an I/O error occur.
   */
  public void serializeCollection(Collection<?> c) throws IOException {
    for (Object o : c) {
      serialize(o, o.getClass().getName());
    }
  }

  /**
   * Serialise the given <code>Hashtable</code> to xml.
   *
   * <p>This method only works if the {@link Hashtable} contains <code>String</code>
   * objects.
   *
   * @param map The map to be serialized to XML
   *
   * @throws IOException Should an I/O error occur.
   *
   * @deprecated Use serializeMap instead
   */
  @Deprecated(since = "1.1.0", forRemoval = true)
  public void serializeHashtable(Map<?, ?> map) throws IOException {
    this.serializeMap(map);
  }

  /**
   * Serialise the given <code>Hashtable</code> to xml.
   *
   * <p>This method only works if the {@link Hashtable} contains <code>String</code>
   * objects.
   *
   * @param map The map to be serialized to XML
   *
   * @throws IOException Should an I/O error occur.
   */
  public void serializeMap(Map<?, ?> map) throws IOException {
    this.xml.openElement("map", !map.isEmpty());
    for (Map.Entry<?, ?> e : map.entrySet()) {
      Object key = e.getKey();
      Object value = e.getValue();
      this.xml.openElement("element");
      this.xml.openElement("key");
      serialize(key, "key");
      this.xml.closeElement();
      this.xml.openElement("value");
      serialize(value, "value");
      this.xml.closeElement();
      this.xml.closeElement();
    }
    this.xml.closeElement();
  }

  /**
   * Serialises the given object to xml by using the public methods <code>getXXX()</code>.
   *
   * <p>This method calls every <code>getXXX()</code> method from the object to get the
   * returned object and then calls the {@link #serialize(Object, String)} method with
   * the returned object and the name <i>xxx</i> in lower case.
   *
   * @param o The object to be serialised as XML
   *
   * @throws IOException Should an I/O error occur.
   */
  public void serializeObject(Object o) throws IOException {
    if (o instanceof XMLSerializable) {
      try {
        Object[] args = new Object[0]; // required by the invoke method
        Class<?> cls = o.getClass();
        Method[] meth = cls.getMethods();
        for (Method element : meth) {
          String methodName = element.getName();
          if (methodName.startsWith("get") && !"getClass".equals(methodName)) {
            Object retObj = element.invoke(o, args);
            String attribute = methodName.substring(3).toLowerCase();
            serialize(retObj, attribute);
          }
        }
      } catch (IllegalAccessException ex) {
        ex.printStackTrace();
      } catch (InvocationTargetException ex) {
        ex.getTargetException().printStackTrace();
      }
    } else if (o instanceof XMLWritable) {
      ((XMLWritable)o).toXML(this.xml);
    }
  }

}


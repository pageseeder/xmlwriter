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
 * Class of exceptions thrown when a namespace is being used without being declared.
 *
 * <p>To avoid this exception being thrown, the namespace URI must be explicitely associated
 * with a prefix before a node belonging to this namespace is serialiased. Namespaces can be
 * declared using the {@link XMLWriter#setPrefixMapping(String, String)} method.
 *
 * @author Christophe Lauret
 */
public final class UndeclaredNamespaceException extends RuntimeException {

  /**
   * Version number for the serialised class.
   */
  static final long serialVersionUID = 8080581405972912943L;

  /**
   * Creates a new exception for undeclared namespaces.
   *
   * @param uri The namespace URI that has not been declared.
   */
  public UndeclaredNamespaceException(String uri) {
    super("The namespace URI \""+uri+"\" has not been mapped to any prefix.");
  }

}

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
 * An Object which implements this interface can be serialized
 * as XML using the <code>XMLSerializer</code>.
 *
 * <p>It does not need to implement any method; it simply indicates
 * the XMLSerializer that it can use the public <code>getXXX</code>
 * methods to generate the XML representation of the object.
 *
 * @see XMLSerializer
 */
public interface XMLSerializable {

}

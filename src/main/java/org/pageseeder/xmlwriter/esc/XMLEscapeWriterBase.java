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

import java.io.IOException;
import java.io.Writer;

/**
 * A base implementation for the XML writer escape classes.
 */
abstract class XMLEscapeWriterBase implements XMLEscapeWriter {

  /**
   * The encoding for the implementation.
   */
  private final String encoding;

  /**
   * The wrapped writer.
   */
  final Writer w;

  /**
   * Creates a new XML escape for writers.
   *
   * @param writer   The writer to wrap.
   * @param encoding The underlying character encoding for the writer.
   *
   * @throws NullPointerException If the specified writer is <code>null</code>.
   */
  XMLEscapeWriterBase(Writer writer, String encoding) {
    if (writer == null)
      throw new NullPointerException("Cannot construct XML escape for null writer.");
    this.w = writer;
    this.encoding = encoding;
  }

  /**
   * Default implementation calling the {@link XMLEscapeWriter#writeAttValue(char[], int, int)}.
   *
   * {@inheritDoc}
   */
  @Override
  public final void writeAttValue(String value) throws IOException {
    if (value == null || "".equals(value)) return;
    writeAttValue(value.toCharArray(), 0, value.length());
  }

  /**
   * Default implementation calling the {@link XMLEscapeWriter#writeAttValue(char[], int, int)}.
   *
   * {@inheritDoc}
   */
  @Override
  public final void writeText(String value) throws IOException {
    if (value == null || "".equals(value)) return;
    writeText(value.toCharArray(), 0, value.length());
  }

  /**
   * Returns the encoding for this writer.
   *
   * {@inheritDoc}
   */
  @Override
  public final String getEncoding() {
    return this.encoding;
  }

}

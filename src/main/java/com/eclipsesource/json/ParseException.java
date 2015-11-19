/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.eclipsesource.json;

/**
 * An unchecked exception to indicate that an input does not qualify as valid JSON.
 */
@SuppressWarnings("serial") // use default serial UID
public class ParseException extends RuntimeException {

  private final int offset;
  private final int line;
  private final int column;

  ParseException(String message, int offset, int line, int column) {
    super(message + " at " + line + ":" + column);
    this.offset = offset;
    this.line = line;
    this.column = column;
  }

  /**
   * Returns the absolute index of the character at which the error occurred. The index of the first
   * character of a document is 0.
   *
   * @return the character offset at which the error occurred, will be &gt;= 0
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Returns the number of the line in which the error occurred. The first line counts as 1.
   *
   * @return the line in which the error occurred, will be &gt;= 1
   */
  public int getLine() {
    return line;
  }

  /**
   * Returns the index of the character at which the error occurred, relative to the line. The index
   * of the first character of a line is 0.
   *
   * @return the column in which the error occurred, will be &gt;= 0
   */
  public int getColumn() {
    return column;
  }

}

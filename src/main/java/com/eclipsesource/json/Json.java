/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
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

import java.io.IOException;
import java.io.Reader;


/**
 * This class serves as the entry point to the minimal-json API.
 * <p>
 * To <strong>parse</strong> a given JSON input, use the <code>parse()</code> methods like in this
 * example:
 * </p>
 * <pre>
 * JsonObject object = Json.parse(string).asObject();
 * </pre>
 * <p>
 * To <strong>create</strong> a JSON data structure to be serialized, use the methods
 * <code>value()</code>, <code>array()</code>, and <code>object()</code>. For example, the following
 * snippet will produce the JSON string <em>{"foo": 23, "bar": true}</em>:
 * </p>
 * <pre>
 * String string = Json.object().add("foo", 23).add("bar", true).toString();
 * </pre>
 * <p>
 * To create a JSON array from a given Java array, you can use one of the <code>array()</code>
 * methods with varargs parameters:
 * </p>
 * <pre>
 * String[] names = ...
 * JsonArray array = Json.array(names);
 * </pre>
 */
public final class Json {

  private Json() {
    // not meant to be instantiated
  }

  /**
   * Represents the JSON literal <code>null</code>.
   */
  public static final JsonValue NULL = new JsonLiteral("null");

  /**
   * Represents the JSON literal <code>true</code>.
   */
  public static final JsonValue TRUE = new JsonLiteral("true");

  /**
   * Represents the JSON literal <code>false</code>.
   */
  public static final JsonValue FALSE = new JsonLiteral("false");

  /**
   * Returns a JsonValue instance that represents the given <code>int</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(int value) {
    return new JsonNumber(Integer.toString(value, 10));
  }

  /**
   * Returns a JsonValue instance that represents the given <code>long</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(long value) {
    return new JsonNumber(Long.toString(value, 10));
  }

  /**
   * Returns a JsonValue instance that represents the given <code>float</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(float value) {
    if (Float.isInfinite(value) || Float.isNaN(value)) {
      throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
    }
    return new JsonNumber(cutOffPointZero(Float.toString(value)));
  }

  /**
   * Returns a JsonValue instance that represents the given <code>double</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(double value) {
    if (Double.isInfinite(value) || Double.isNaN(value)) {
      throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
    }
    return new JsonNumber(cutOffPointZero(Double.toString(value)));
  }

  /**
   * Returns a JsonValue instance that represents the given string.
   *
   * @param string
   *          the string to get a JSON representation for
   * @return a JSON value that represents the given string
   */
  public static JsonValue value(String string) {
    return string == null ? NULL : new JsonString(string);
  }

  /**
   * Returns a JsonValue instance that represents the given <code>boolean</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(boolean value) {
    return value ? TRUE : FALSE;
  }

  /**
   * Creates a new empty JsonArray. This is equivalent to creating a new JsonArray using the
   * constructor.
   *
   * @return a new empty JSON array
   */
  public static JsonValue array() {
    return new JsonArray();
  }

  /**
   * Creates a new JsonArray that contains the JSON representations of the given <code>int</code>
   * values.
   *
   * @param values
   *          the values to be included in the new JSON array
   * @return a new JSON array that contains the given values
   */
  public static JsonArray array(int... values) {
    if (values == null) {
      throw new NullPointerException("values is null");
    }
    JsonArray array = new JsonArray();
    for (int value : values) {
      array.add(value);
    }
    return array;
  }

  /**
   * Creates a new JsonArray that contains the JSON representations of the given <code>long</code>
   * values.
   *
   * @param values
   *          the values to be included in the new JSON array
   * @return a new JSON array that contains the given values
   */
  public static JsonArray array(long... values) {
    if (values == null) {
      throw new NullPointerException("values is null");
    }
    JsonArray array = new JsonArray();
    for (long value : values) {
      array.add(value);
    }
    return array;
  }

  /**
   * Creates a new JsonArray that contains the JSON representations of the given <code>float</code>
   * values.
   *
   * @param values
   *          the values to be included in the new JSON array
   * @return a new JSON array that contains the given values
   */
  public static JsonArray array(float... values) {
    if (values == null) {
      throw new NullPointerException("values is null");
    }
    JsonArray array = new JsonArray();
    for (float value : values) {
      array.add(value);
    }
    return array;
  }

  /**
   * Creates a new JsonArray that contains the JSON representations of the given <code>double</code>
   * values.
   *
   * @param values
   *          the values to be included in the new JSON array
   * @return a new JSON array that contains the given values
   */
  public static JsonArray array(double... values) {
    if (values == null) {
      throw new NullPointerException("values is null");
    }
    JsonArray array = new JsonArray();
    for (double value : values) {
      array.add(value);
    }
    return array;
  }

  /**
   * Creates a new JsonArray that contains the JSON representations of the given
   * <code>boolean</code> values.
   *
   * @param values
   *          the values to be included in the new JSON array
   * @return a new JSON array that contains the given values
   */
  public static JsonArray array(boolean... values) {
    if (values == null) {
      throw new NullPointerException("values is null");
    }
    JsonArray array = new JsonArray();
    for (boolean value : values) {
      array.add(value);
    }
    return array;
  }

  /**
   * Creates a new JsonArray that contains the JSON representations of the given strings.
   *
   * @param strings
   *          the strings to be included in the new JSON array
   * @return a new JSON array that contains the given strings
   */
  public static JsonArray array(String... strings) {
    if (strings == null) {
      throw new NullPointerException("values is null");
    }
    JsonArray array = new JsonArray();
    for (String value : strings) {
      array.add(value);
    }
    return array;
  }

  /**
   * Creates a new empty JsonObject. This is equivalent to creating a new JsonObject using the
   * constructor.
   *
   * @return a new empty JSON object
   */
  public static JsonObject object() {
    return new JsonObject();
  }

  /**
   * Parses the given input string as JSON. The input must contain a valid JSON value, optionally
   * padded with whitespace.
   *
   * @param string
   *          the input string, must be valid JSON
   * @return a value that represents the parsed JSON
   * @throws ParseException
   *           if the input is not valid JSON
   */
  public static JsonValue parse(String string) {
    if (string == null) {
      throw new NullPointerException("string is null");
    }
    try {
      return new JsonParser(string).parse();
    } catch (IOException exception) {
      // JsonParser does not throw IOException for String
      throw new RuntimeException(exception);
    }
  }

  /**
   * Reads the entire input stream from the given reader and parses it as JSON. The input must
   * contain a valid JSON value, optionally padded with whitespace.
   * <p>
   * Characters are read in chunks and buffered internally, therefore wrapping an existing reader in
   * an additional <code>BufferedReader</code> does <strong>not</strong> improve reading
   * performance.
   * </p>
   *
   * @param reader
   *          the reader to read the JSON value from
   * @return a value that represents the parsed JSON
   * @throws IOException
   *           if an I/O error occurs in the reader
   * @throws ParseException
   *           if the input is not valid JSON
   */
  public static JsonValue parse(Reader reader) throws IOException {
    if (reader == null) {
      throw new NullPointerException("reader is null");
    }
    return new JsonParser( reader ).parse();
  }

  private static String cutOffPointZero(String string) {
    if (string.endsWith(".0")) {
      return string.substring(0, string.length() - 2);
    }
    return string;
  }

}

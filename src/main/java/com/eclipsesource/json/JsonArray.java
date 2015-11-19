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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Represents a JSON array, an ordered collection of JSON values.
 * <p>
 * Elements can be added using the <code>add(...)</code> methods which accept instances of
 * {@link JsonValue}, strings, primitive numbers, and boolean values. To replace an element of an
 * array, use the <code>set(int, ...)</code> methods.
 * </p>
 * <p>
 * Elements can be accessed by their index using {@link #get(int)}. This class also supports
 * iterating over the elements in document order using an {@link #iterator()} or an enhanced for
 * loop:
 * </p>
 * <pre>
 * for (JsonValue value : jsonArray) {
 *   ...
 * }
 * </pre>
 * <p>
 * An equivalent {@link List} can be obtained from the method {@link #values()}.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads access a
 * <code>JsonArray</code> instance concurrently, while at least one of these threads modifies the
 * contents of this array, access to the instance must be synchronized externally. Failure to do so
 * may lead to an inconsistent state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings("serial") // use default serial UID
public class JsonArray extends JsonValue implements Iterable<JsonValue> {

  private final List<JsonValue> values;

  /**
   * Creates a new empty JsonArray.
   */
  public JsonArray() {
    values = new ArrayList<JsonValue>();
  }

  /**
   * Creates a new JsonArray with the contents of the specified JSON array.
   *
   * @param array
   *          the JsonArray to get the initial contents from, must not be <code>null</code>
   */
  public JsonArray(JsonArray array) {
    this(array, false);
  }

  private JsonArray(JsonArray array, boolean unmodifiable) {
    if (array == null) {
      throw new NullPointerException("array is null");
    }
    if (unmodifiable) {
      values = Collections.unmodifiableList(array.values);
    } else {
      values = new ArrayList<JsonValue>(array.values);
    }
  }

  /**
   * Reads a JSON array from the given reader.
   * <p>
   * Characters are read in chunks and buffered internally, therefore wrapping an existing reader in
   * an additional <code>BufferedReader</code> does <strong>not</strong> improve reading
   * performance.
   * </p>
   *
   * @param reader
   *          the reader to read the JSON array from
   * @return the JSON array that has been read
   * @throws IOException
   *           if an I/O error occurs in the reader
   * @throws ParseException
   *           if the input is not valid JSON
   * @throws UnsupportedOperationException
   *           if the input does not contain a JSON array
   * @deprecated Use {@link Json#parse(Reader)}{@link JsonValue#asArray() .asArray()} instead
   */
  @Deprecated
  public static JsonArray readFrom(Reader reader) throws IOException {
    return JsonValue.readFrom(reader).asArray();
  }

  /**
   * Reads a JSON array from the given string.
   *
   * @param string
   *          the string that contains the JSON array
   * @return the JSON array that has been read
   * @throws ParseException
   *           if the input is not valid JSON
   * @throws UnsupportedOperationException
   *           if the input does not contain a JSON array
   * @deprecated Use {@link Json#parse(String)}{@link JsonValue#asArray() .asArray()} instead
   */
  @Deprecated
  public static JsonArray readFrom(String string) {
    return JsonValue.readFrom(string).asArray();
  }

  /**
   * Returns an unmodifiable wrapper for the specified JsonArray. This method allows to provide
   * read-only access to a JsonArray.
   * <p>
   * The returned JsonArray is backed by the given array and reflects subsequent changes. Attempts
   * to modify the returned JsonArray result in an <code>UnsupportedOperationException</code>.
   * </p>
   *
   * @param array
   *          the JsonArray for which an unmodifiable JsonArray is to be returned
   * @return an unmodifiable view of the specified JsonArray
   */
  public static JsonArray unmodifiableArray(JsonArray array) {
    return new JsonArray(array, true);
  }

  /**
   * Appends the JSON representation of the specified <code>int</code> value to the end of this
   * array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(int value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified <code>long</code> value to the end of this
   * array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(long value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified <code>float</code> value to the end of this
   * array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(float value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified <code>double</code> value to the end of this
   * array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(double value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified <code>boolean</code> value to the end of this
   * array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(boolean value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the JSON representation of the specified string to the end of this array.
   *
   * @param value
   *          the string to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(String value) {
    values.add(Json.value(value));
    return this;
  }

  /**
   * Appends the specified JSON value to the end of this array.
   *
   * @param value
   *          the JsonValue to add to the array, must not be <code>null</code>
   * @return the array itself, to enable method chaining
   */
  public JsonArray add(JsonValue value) {
    if (value == null) {
      throw new NullPointerException("value is null");
    }
    values.add(value);
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of
   * the specified <code>int</code> value.
   *
   * @param index
   *          the index of the array element to replace
   * @param value
   *          the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonArray set(int index, int value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of
   * the specified <code>long</code> value.
   *
   * @param index
   *          the index of the array element to replace
   * @param value
   *          the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonArray set(int index, long value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of
   * the specified <code>float</code> value.
   *
   * @param index
   *          the index of the array element to replace
   * @param value
   *          the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonArray set(int index, float value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of
   * the specified <code>double</code> value.
   *
   * @param index
   *          the index of the array element to replace
   * @param value
   *          the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonArray set(int index, double value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of
   * the specified <code>boolean</code> value.
   *
   * @param index
   *          the index of the array element to replace
   * @param value
   *          the value to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonArray set(int index, boolean value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the JSON representation of
   * the specified string.
   *
   * @param index
   *          the index of the array element to replace
   * @param value
   *          the string to be stored at the specified array position
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonArray set(int index, String value) {
    values.set(index, Json.value(value));
    return this;
  }

  /**
   * Replaces the element at the specified position in this array with the specified JSON value.
   *
   * @param index
   *          the index of the array element to replace
   * @param value
   *          the value to be stored at the specified array position, must not be <code>null</code>
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonArray set(int index, JsonValue value) {
    if (value == null) {
      throw new NullPointerException("value is null");
    }
    values.set(index, value);
    return this;
  }

  /**
   * Removes the element at the specified index from this array.
   *
   * @param index
   *          the index of the element to remove
   * @return the array itself, to enable method chaining
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonArray remove(int index) {
    values.remove(index);
    return this;
  }

  /**
   * Returns the number of elements in this array.
   *
   * @return the number of elements in this array
   */
  public int size() {
    return values.size();
  }

  /**
   * Returns <code>true</code> if this array contains no elements.
   *
   * @return <code>true</code> if this array contains no elements
   */
  public boolean isEmpty() {
    return values.isEmpty();
  }

  /**
   * Returns the value of the element at the specified position in this array.
   *
   * @param index
   *          the index of the array element to return
   * @return the value of the element at the specified position
   * @throws IndexOutOfBoundsException
   *           if the index is out of range, i.e. <code>index &lt; 0</code> or
   *           <code>index &gt;= size</code>
   */
  public JsonValue get(int index) {
    return values.get(index);
  }

  /**
   * Returns a list of the values in this array in document order. The returned list is backed by
   * this array and will reflect subsequent changes. It cannot be used to modify this array.
   * Attempts to modify the returned list will result in an exception.
   *
   * @return a list of the values in this array
   */
  public List<JsonValue> values() {
    return Collections.unmodifiableList(values);
  }

  /**
   * Returns an iterator over the values of this array in document order. The returned iterator
   * cannot be used to modify this array.
   *
   * @return an iterator over the values of this array
   */
  public Iterator<JsonValue> iterator() {
    final Iterator<JsonValue> iterator = values.iterator();
    return new Iterator<JsonValue>() {

      public boolean hasNext() {
        return iterator.hasNext();
      }

      public JsonValue next() {
        return iterator.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  void write(JsonWriter writer) throws IOException {
    writer.writeArrayOpen();
    Iterator<JsonValue> iterator = iterator();
    boolean first = true;
    while (iterator.hasNext()) {
      if (!first) {
        writer.writeArraySeparator();
      }
      iterator.next().write(writer);
      first = false;
    }
    writer.writeArrayClose();
  }

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public JsonArray asArray() {
    return this;
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    JsonArray other = (JsonArray)object;
    return values.equals(other.values);
  }

}

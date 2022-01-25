/*
 * Copyright (c) 2011-2021 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.uritemplate;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.json.JsonObject;
import io.vertx.uritemplate.impl.VariablesImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds a set of key/value pairs where each value can be a {@code String} or a {@code List<String>} or a {@code Map<String, String>}.
 */
@VertxGen
public interface Variables {

  /**
   * @return an empty instance
   */
  static Variables variables() {
    return new VariablesImpl();
  }

  /**
   * Create an instance populated from a JSON object:
   *
   * <ul>
   *   <li>{@code null} are conserved</li>
   *   <li>{@code JsonArray} is converted to {@code List<String>}</li>
   *   <li>{@code JsonObject} is converted to {@code Map<String, String>}</li>
   *   <li>any other value is converted to a string</li>
   * </ul>
   *
   * Note that nested JSON elements are converted to a string, so { "user": { "first_name": "John", "last_name": "Doe", "address" : { "city": "Paris", etc... } } }
   * flattens the JSON "address" to the string "{\"city\":\"Paris\",etc...}".
   *
   * @param json the json that populates the returned variables
   * @return an instance populated from a JSON object
   */
  static Variables variables(JsonObject json) {
    return variables().addAll(json);
  }

  /**
   * Set a single variable.
   * @param name the variable name
   * @param value the variable value
   * @return a reference to this, so the API can be used fluently
   */
  @Fluent
  Variables set(String name, String value);

  /**
   * Set a list variable.
   * @param name the variable name
   * @param value the variable value
   * @return a reference to this, so the API can be used fluently
   */
  @Fluent
  Variables set(String name, List<String> value);

  /**
   * Set a map variable.
   * @param name the variable name
   * @param value the variable value
   * @return a reference to this, so the API can be used fluently
   */
  @Fluent
  Variables set(String name, Map<String, String> value);

  /**
   * Like {@link #addAll(JsonObject)} but overwrites previous variables.
   */
  @Fluent
  default Variables setAll(JsonObject json) {
    return clear().addAll(json);
  }

  /**
   * Populates with a JSON object:
   *
   * <ul>
   *   <li>{@code null} are conserved</li>
   *   <li>{@code JsonArray} is converted to {@code List<String>}</li>
   *   <li>{@code JsonObject} is converted to {@code Map<String, String>}</li>
   *   <li>any other value is converted to a string</li>
   * </ul>
   *
   * Note that nested JSON elements are converted to a string, so { "user": { "first_name": "John", "last_name": "Doe", "address" : { "city": "Paris", etc... } } }
   * flattens the JSON "address" to the string "{\"city\":\"Paris\",etc...}".
   *
   * @param json the json that populates the returned variables
   * @return a reference to this, so the API can be used fluently
   */
  @Fluent
  Variables addAll(JsonObject json);

  @Fluent
  Variables clear();

  /**
   * @return the set of variable names
   */
  Set<String> names();

  /**
   * @return the value of the variable {@code name}
   */
  Object get(String name);

  /**
   * @return the single value of the variable {@code name}
   */
  String getSingle(String name);

  /**
   * @return the list value of the variable {@code name}
   */
  List<String> getList(String name);

  /**
   * @return the map value of the variable {@code name}
   */
  Map<String, String> getMap(String name);

}

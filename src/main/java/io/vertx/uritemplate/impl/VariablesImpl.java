/*
 * Copyright (c) 2011-2022 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.uritemplate.impl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.uritemplate.Variables;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VariablesImpl implements Variables {

  private static String toString(Object o) {
    if (o == null) {
      return null;
    } else {
      return o.toString();
    }
  }

  private final Map<String, Object> variables = new LinkedHashMap<>();

  @Override
  public Variables set(String name, String value) {
    variables.put(name, value);
    return this;
  }

  @Override
  public Variables set(String name, List<String> value) {
    variables.put(name, value);
    return this;
  }

  @Override
  public Variables set(String name, Map<String, String> value) {
    variables.put(name, value);
    return this;
  }

  @Override
  public Variables addAll(JsonObject jsonObject) {
    for (Map.Entry<String, Object> entry : jsonObject) {
      String name = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof JsonObject) {
        JsonObject json = (JsonObject) value;
        LinkedHashMap<String, String> map = new LinkedHashMap<>(json.size());
        for (Map.Entry<String, Object> e : json) {
          map.put(e.getKey(), toString(e.getValue()));
        }
        set(name, map);
      } else if (value instanceof JsonArray) {
        JsonArray json = (JsonArray) value;
        List<String> list = new ArrayList<>(json.size());
        for (Object o : json) {
          list.add(toString(o));
        }
        set(name, list);
      } else {
        set(name, toString(value));
      }
    }
    return this;
  }

  @Override
  public Variables clear() {
    variables.clear();
    return this;
  }

  @Override
  public Object get(String name) {
    return variables.get(name);
  }

  @Override
  public Set<String> names() {
    return variables.keySet();
  }

  @Override
  public String getSingle(String name) {
    return (String) variables.get(name);
  }

  @Override
  public List<String> getList(String name) {
    return (List<String>) variables.get(name);
  }

  @Override
  public Map<String, String> getMap(String name) {
    return (Map<String, String>) variables.get(name);
  }
}

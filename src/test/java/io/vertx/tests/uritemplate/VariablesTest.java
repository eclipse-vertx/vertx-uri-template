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
package io.vertx.tests.uritemplate;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.uritemplate.Variables;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class VariablesTest {

  @Test
  public void testFromJson() {
    JsonObject json = new JsonObject();
    json.put("string", "the_string");
    json.put("int", 4);
    json.put("boolean", false);
    json.put("list", new JsonArray().add("foo").add(1).add(true));
    json.put("map", new JsonObject().put("map_string", "bar"));
    Variables var = Variables.variables(json);
    assertEquals(new HashSet<>(Arrays.asList("string", "int", "boolean", "list", "map")), var.names());
    assertEquals("the_string", var.get("string"));
    assertEquals("4", var.get("int"));
    assertEquals("false", var.get("boolean"));
    assertEquals(Arrays.asList("foo", "1", "true"), var.get("list"));
    assertEquals(Collections.singletonMap("map_string", "bar"), var.get("map"));
  }
}

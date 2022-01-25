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
package io.vertx.uritemplate;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;

public class TckTest {

  private static JsonObject load(String name) throws IOException {
    InputStream is = TckTest.class.getClassLoader().getResourceAsStream(name);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[256];
    int l;
    while ((l = is.read(buffer, 0, 256)) != -1) {
      baos.write(buffer, 0, l);
    }
    return new JsonObject(Buffer.buffer(baos.toByteArray()));
  }

  @Test
  public void testSpecExamples() throws IOException {
    runTCK("uritemplate-test/spec-examples.json");
  }

  @Test
  public void testSpecExamplesBySection() throws IOException {
    runTCK("uritemplate-test/spec-examples-by-section.json");
  }

  @Test
  public void testExtendedTest() throws IOException {
    runTCK("uritemplate-test/extended-tests.json");
  }

  @Test
  public void testNegativeTest() throws IOException {
    runTCK("uritemplate-test/negative-tests.json");
  }

  private void runTCK(String name) throws IOException {
    JsonObject groups = load(name);
    for (String desc : groups.fieldNames()) {
      JsonObject group = groups.getJsonObject(desc);
      String level = group.getString("level");
      Variables variables = Variables.variables(group.getJsonObject("variables"));
      JsonArray testcases = group.getJsonArray("testcases");
      testcases.forEach(testcase -> {
        JsonArray array = (JsonArray) testcase;
        String template = array.getString(0);
        Object expected = array.getValue(1);
        List<String> expectations;
        if (expected instanceof String) {
          expectations = Collections.singletonList((String) expected);
        } else if (expected instanceof JsonArray) {
          expectations = ((JsonArray) expected).stream().map(o -> (String) o).collect(Collectors.toList());
        } else if (expected == Boolean.FALSE) {
          // Failure
          try {
            UriTemplate.of(template).expandToString(variables);
            fail("Was expecting " + template + " compilation or evaluation to fail");
          } catch (Exception ignore) {
          }
          return;
        } else {
          throw new UnsupportedOperationException("Not supported: " + expected);
        }
        String result;
        try {
          result = UriTemplate.of(template).expandToString(variables);
        } catch (Exception e) {
          throw new AssertionError("Failed to evaluate " + template + " with variables " + group.getJsonObject("variables") + " to evaluate to " + expected, e);
        }
        if (!expectations.contains(result)) {
          fail("Expected " + template + " evaluated with variables " + group.getJsonObject("variables") + " to <" + result + "> to match one of " + expectations);
        }
      });
    }
  }
}

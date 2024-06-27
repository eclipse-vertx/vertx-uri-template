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

import io.vertx.uritemplate.UriTemplate;
import io.vertx.uritemplate.impl.UriTemplateImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UriTemplateTest {

  @Test
  public void testParseURITemplate() {
    new UriTemplateImpl.Parser().parseURITemplate("http://example.org/~{username}/");
  }

  @Test
  public void testParseLiteral() {
    UriTemplateImpl.Parser parser = new UriTemplateImpl.Parser();
    assertEquals(3, parser.parseLiterals("foo", 0));
    assertEquals(2, parser.parseLiterals("\ud83c\udf09", 0));
    assertEquals("%F0%9F%8C%89", parser.literals.toString());
  }

  @Test
  public void testParseExpression() {
    assertEquals(0, new UriTemplateImpl.Parser().parseExpression("{", 0));
    assertEquals(2, new UriTemplateImpl.Parser().parseExpression("{}", 0));
    assertEquals(3, new UriTemplateImpl.Parser().parseExpression("{A}", 0));
    assertEquals(5, new UriTemplateImpl.Parser().parseExpression("{A,B}", 0));
    assertEquals(0, new UriTemplateImpl.Parser().parseExpression("{A,}", 0));
    assertEquals(4, new UriTemplateImpl.Parser().parseExpression("{#A}", 0));
  }

  @Test
  public void testVariableList() {
    assertEquals(1, new UriTemplateImpl.Parser().parseVariableList("A", 0));
    assertEquals(2, new UriTemplateImpl.Parser().parseVariableList("AB", 0));
    assertEquals(4, new UriTemplateImpl.Parser().parseVariableList("AB,C", 0));
    assertEquals(4, new UriTemplateImpl.Parser().parseVariableList("AB,C,", 0));
    assertEquals(6, new UriTemplateImpl.Parser().parseVariableList("AB,C,D", 0));
    assertEquals(6, new UriTemplateImpl.Parser().parseVariableList("AB,C,D}", 0));
    assertEquals(8, new UriTemplateImpl.Parser().parseVariableList("AB,C,D:1.", 0));
  }

  @Test
  public void testParseVarspec() {
    UriTemplateImpl.Parser parser = new UriTemplateImpl.Parser();
    assertEquals(3, parser.parseVarspec("A:1", 0));
    assertEquals("A", parser.varspec.varname);
    assertEquals(1, parser.varspec.maxLength);
    assertEquals(4, parser.parseVarspec("A:12", 0));
    assertEquals("A", parser.varspec.varname);
    assertEquals(12, parser.varspec.maxLength);
    assertEquals(4, parser.parseVarspec("AB:1", 0));
    assertEquals("AB", parser.varspec.varname);
    assertEquals(1, parser.varspec.maxLength);
    assertEquals(5, parser.parseVarspec("A.B:1", 0));
    assertEquals("A.B", parser.varspec.varname);
    assertEquals(1, parser.varspec.maxLength);
    assertEquals(4, parser.parseVarspec("AB:1}", 0));
    assertEquals("AB", parser.varspec.varname);
    assertEquals(1, parser.varspec.maxLength);
    assertEquals(3, parser.parseVarspec("A:1.", 0));
    assertEquals("A", parser.varspec.varname);
    assertEquals(1, parser.varspec.maxLength);
    assertEquals(12, parser.parseVarspec("%2F%E2%82%AC", 0));
    assertEquals("%2F%E2%82%AC", parser.varspec.varname);
    assertEquals("/\u20AC", parser.varspec.decoded);
    assertEquals(-1, parser.varspec.maxLength);
  }

  @Test
  public void testParseVarchar() {
    assertEquals(1, new UriTemplateImpl.Parser().parseVarname("A", 0));
    assertEquals(2, new UriTemplateImpl.Parser().parseVarname("AB", 0));
    assertEquals(3, new UriTemplateImpl.Parser().parseVarname("A.B", 0));
    assertEquals(2, new UriTemplateImpl.Parser().parseVarname("AB}", 0));
    assertEquals(1, new UriTemplateImpl.Parser().parseVarname("A.", 0));
  }

  @Test
  public void testModifierLevel4() {
    assertEquals(0, new UriTemplateImpl.Parser().parseModifierLevel4(":0", 0));
    assertEquals(2, new UriTemplateImpl.Parser().parseModifierLevel4(":1", 0));
    assertEquals(3, new UriTemplateImpl.Parser().parseModifierLevel4(":12", 0));
    assertEquals(4, new UriTemplateImpl.Parser().parseModifierLevel4(":123", 0));
    assertEquals(5, new UriTemplateImpl.Parser().parseModifierLevel4(":1234", 0));
    assertEquals(5, new UriTemplateImpl.Parser().parseModifierLevel4(":12345", 0));
    assertEquals(1, new UriTemplateImpl.Parser().parseModifierLevel4("*", 0));
  }

  @Test
  public void testParsePrefix() {
    assertEquals(0, new UriTemplateImpl.Parser().parsePrefixModifier(":0", 0));
    assertEquals(2, new UriTemplateImpl.Parser().parsePrefixModifier(":1", 0));
    assertEquals(3, new UriTemplateImpl.Parser().parsePrefixModifier(":12", 0));
    assertEquals(4, new UriTemplateImpl.Parser().parsePrefixModifier(":123", 0));
    assertEquals(5, new UriTemplateImpl.Parser().parsePrefixModifier(":1234", 0));
    assertEquals(5, new UriTemplateImpl.Parser().parsePrefixModifier(":12345", 0));
  }

  @Test
  public void testParseMaxLength() {
    assertEquals(0, new UriTemplateImpl.Parser().parseMaxLength("0", 0));
    assertEquals(1, new UriTemplateImpl.Parser().parseMaxLength("1", 0));
    assertEquals(2, new UriTemplateImpl.Parser().parseMaxLength("12", 0));
    assertEquals(3, new UriTemplateImpl.Parser().parseMaxLength("123", 0));
    assertEquals(4, new UriTemplateImpl.Parser().parseMaxLength("1234", 0));
    assertEquals(4, new UriTemplateImpl.Parser().parseMaxLength("12345", 0));
  }

  @Test
  public void testOpReserved() {
    assertInvalidTemplate("{!test}");
  }

  private static void assertInvalidTemplate(String template) {
    try {
      UriTemplate.of(template);
      fail("Was expecting " + template + " to fail");
    } catch (Exception ignore) {
      // Expected
    }
  }
}

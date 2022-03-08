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

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ExpansionTest {

  private Variables variables;

  @Before
  public void setUp() throws Exception {
    variables = Variables.variables();
    variables.set("var1", "val1");
    variables.set("var2", "val2");
    variables.set("var3", "val3");
    variables.set("euro", "\u20AC");
    variables.set("slash", "/");
    variables.set("comma", ",");
    variables.set("empty", "");
    variables.set("empty_list", Collections.emptyList());
    variables.set("percent", "%E2%82%AC");
    variables.set("surrogate", "\ud83c\udf09");
    variables.set("list", Arrays.asList("one", "two", "three"));
    Map<String, String> map = new LinkedHashMap<>();
    map.put("one", "1");
    map.put("two", "2");
    map.put("three", "3");
    map.put("comma", ",");
    variables.set("map", map);
    variables.set("empty_map", Collections.emptyMap());
    variables.set("%2F", "/");
    variables.set("%2F_list", Arrays.asList("/", "/", "/"));
    variables.set("foo.bar", "foodotbar");
  }

  @Test
  public void testSimpleStringExpansion() {
    assertEquals("prefixsuffix", UriTemplate.of("prefix{undef}suffix").expandToString(variables));
    assertEquals("prefixsuffix", UriTemplate.of("prefix{empty}suffix").expandToString(variables));
    assertEquals("prefixsuffix", UriTemplate.of("prefix{empty}suffix").expandToString(variables));
    assertEquals("prefixval1suffix", UriTemplate.of("prefix{var1}suffix").expandToString(variables));
    assertEquals("prefixval1,val2suffix", UriTemplate.of("prefix{var1,var2}suffix").expandToString(variables));
    assertEquals("prefixval1suffix", UriTemplate.of("prefix{var1,undef}suffix").expandToString(variables));
    assertEquals("prefixval1,suffix", UriTemplate.of("prefix{var1,empty}suffix").expandToString(variables));
    assertEquals("prefixval2suffix", UriTemplate.of("prefix{undef,var2}suffix").expandToString(variables));
    assertEquals("prefix,val2suffix", UriTemplate.of("prefix{empty,var2}suffix").expandToString(variables));
    assertEquals("va", UriTemplate.of("{var1:2}").expandToString(variables));
    assertEquals("%E2%82%AC", UriTemplate.of("{euro}").expandToString(variables));
    assertEquals("%2F", UriTemplate.of("{slash}").expandToString(variables));
    assertEquals("%2C", UriTemplate.of("{comma}").expandToString(variables));
    assertEquals("%25E2%2582%25AC", UriTemplate.of("{percent}").expandToString(variables));
    assertEquals("%F0%9F%8C%89", UriTemplate.of("{surrogate}").expandToString(variables));
    assertEquals("one,two,three", UriTemplate.of("{list}").expandToString(variables));
    assertEquals("one,two,three", UriTemplate.of("{list*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{empty_list}").expandToString(variables));
    assertEquals("", UriTemplate.of("{empty_list*}").expandToString(variables));
    assertEquals("one,1,two,2,three,3,comma,%2C", UriTemplate.of("{map}").expandToString(variables));
    assertEquals("one=1,two=2,three=3,comma=%2C", UriTemplate.of("{map*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{empty_map}").expandToString(variables));
    assertEquals("", UriTemplate.of("{empty_map*}").expandToString(variables));
    assertExpansionFailure("{list:1}");
    assertExpansionFailure("{map:1}");
  }

  @Test
  public void testFormStyleQueryExpansion() {
    assertEquals("prefixsuffix", UriTemplate.of("prefix{?undef}suffix").expandToString(variables));
    assertEquals("prefix?empty=suffix", UriTemplate.of("prefix{?empty}suffix").expandToString(variables));
    assertEquals("prefix?var1=val1suffix", UriTemplate.of("prefix{?var1}suffix").expandToString(variables));
    assertEquals("prefix?var1=val1&var2=val2suffix", UriTemplate.of("prefix{?var1,var2}suffix").expandToString(variables));
    assertEquals("prefix?var1=val1suffix", UriTemplate.of("prefix{?var1,undef}suffix").expandToString(variables));
    assertEquals("prefix?var1=val1&empty=suffix", UriTemplate.of("prefix{?var1,empty}suffix").expandToString(variables));
    assertEquals("prefix?var2=val2suffix", UriTemplate.of("prefix{?undef,var2}suffix").expandToString(variables));
    assertEquals("prefix?empty=&var2=val2suffix", UriTemplate.of("prefix{?empty,var2}suffix").expandToString(variables));
    assertEquals("?foo.bar=foodotbar", UriTemplate.of("{?foo.bar}").expandToString(variables));
    assertEquals("?var1=va", UriTemplate.of("{?var1:2}").expandToString(variables));
    assertEquals("?euro=%E2%82%AC", UriTemplate.of("{?euro}").expandToString(variables));
    assertEquals("?slash=%2F", UriTemplate.of("{?slash}").expandToString(variables));
    assertEquals("?%2F=%2F", UriTemplate.of("{?%2F}").expandToString(variables));
    assertEquals("?comma=%2C", UriTemplate.of("{?comma}").expandToString(variables));
    assertEquals("?percent=%25E2%2582%25AC", UriTemplate.of("{?percent}").expandToString(variables));
    assertEquals("?surrogate=%F0%9F%8C%89", UriTemplate.of("{?surrogate}").expandToString(variables));
    assertEquals("?list=one,two,three", UriTemplate.of("{?list}").expandToString(variables));
    assertEquals("?list=one&list=two&list=three", UriTemplate.of("{?list*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{?empty_list}").expandToString(variables));
    assertEquals("", UriTemplate.of("{?empty_list*}").expandToString(variables));
    assertEquals("?%2F_list=%2F,%2F,%2F", UriTemplate.of("{?%2F_list}").expandToString(variables));
    assertEquals("?%2F_list=%2F&%2F_list=%2F&%2F_list=%2F", UriTemplate.of("{?%2F_list*}").expandToString(variables));
    assertEquals("?map=one,1,two,2,three,3,comma,%2C", UriTemplate.of("{?map}").expandToString(variables));
    assertEquals("?one=1&two=2&three=3&comma=%2C", UriTemplate.of("{?map*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{?empty_map}").expandToString(variables));
    assertEquals("", UriTemplate.of("{?empty_map*}").expandToString(variables));
  }

  @Test
  public void testFormStyleQueryContinuation() {
    assertEquals("prefixsuffix", UriTemplate.of("prefix{&undef}suffix").expandToString(variables));
    assertEquals("prefix&empty=suffix", UriTemplate.of("prefix{&empty}suffix").expandToString(variables));
    assertEquals("prefix&var1=val1suffix", UriTemplate.of("prefix{&var1}suffix").expandToString(variables));
    assertEquals("prefix&var1=val1&var2=val2suffix", UriTemplate.of("prefix{&var1,var2}suffix").expandToString(variables));
    assertEquals("prefix&var1=val1suffix", UriTemplate.of("prefix{&var1,undef}suffix").expandToString(variables));
    assertEquals("prefix&var1=val1&empty=suffix", UriTemplate.of("prefix{&var1,empty}suffix").expandToString(variables));
    assertEquals("prefix&var2=val2suffix", UriTemplate.of("prefix{&undef,var2}suffix").expandToString(variables));
    assertEquals("prefix&empty=&var2=val2suffix", UriTemplate.of("prefix{&empty,var2}suffix").expandToString(variables));
    assertEquals("&var1=va", UriTemplate.of("{&var1:2}").expandToString(variables));
    assertEquals("&euro=%E2%82%AC", UriTemplate.of("{&euro}").expandToString(variables));
    assertEquals("&slash=%2F", UriTemplate.of("{&slash}").expandToString(variables));
    assertEquals("&%2F=%2F", UriTemplate.of("{&%2F}").expandToString(variables));
    assertEquals("&comma=%2C", UriTemplate.of("{&comma}").expandToString(variables));
    assertEquals("&percent=%25E2%2582%25AC", UriTemplate.of("{&percent}").expandToString(variables));
    assertEquals("&surrogate=%F0%9F%8C%89", UriTemplate.of("{&surrogate}").expandToString(variables));
    assertEquals("&list=one,two,three", UriTemplate.of("{&list}").expandToString(variables));
    assertEquals("&list=one&list=two&list=three", UriTemplate.of("{&list*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{&empty_list}").expandToString(variables));
    assertEquals("", UriTemplate.of("{&empty_list*}").expandToString(variables));
    assertEquals("&%2F_list=%2F,%2F,%2F", UriTemplate.of("{&%2F_list}").expandToString(variables));
    assertEquals("&%2F_list=%2F&%2F_list=%2F&%2F_list=%2F", UriTemplate.of("{&%2F_list*}").expandToString(variables));
    assertEquals("&map=one,1,two,2,three,3,comma,%2C", UriTemplate.of("{&map}").expandToString(variables));
    assertEquals("&one=1&two=2&three=3&comma=%2C", UriTemplate.of("{&map*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{&empty_map}").expandToString(variables));
    assertEquals("", UriTemplate.of("{&empty_map*}").expandToString(variables));
  }

  @Test
  public void testPathSegmentExpansion() {
    assertEquals("prefixsuffix", UriTemplate.of("prefix{/undef}suffix").expandToString(variables));
    assertEquals("prefix/suffix", UriTemplate.of("prefix{/empty}suffix").expandToString(variables));
    assertEquals("prefix/val1suffix", UriTemplate.of("prefix{/var1}suffix").expandToString(variables));
    assertEquals("prefix/val1/val2suffix", UriTemplate.of("prefix{/var1,var2}suffix").expandToString(variables));
    assertEquals("prefix/val1suffix", UriTemplate.of("prefix{/var1,undef}suffix").expandToString(variables));
    assertEquals("prefix/val1/suffix", UriTemplate.of("prefix{/var1,empty}suffix").expandToString(variables));
    assertEquals("prefix/val2suffix", UriTemplate.of("prefix{/undef,var2}suffix").expandToString(variables));
    assertEquals("prefix//val2suffix", UriTemplate.of("prefix{/empty,var2}suffix").expandToString(variables));
    assertEquals("/va", UriTemplate.of("{/var1:2}").expandToString(variables));
    assertEquals("/%E2%82%AC", UriTemplate.of("{/euro}").expandToString(variables));
    assertEquals("/%2F", UriTemplate.of("{/slash}").expandToString(variables));
    assertEquals("/%2C", UriTemplate.of("{/comma}").expandToString(variables));
    assertEquals("/%25E2%2582%25AC", UriTemplate.of("{/percent}").expandToString(variables));
    assertEquals("/%F0%9F%8C%89", UriTemplate.of("{/surrogate}").expandToString(variables));
    assertEquals("/one,two,three", UriTemplate.of("{/list}").expandToString(variables));
    assertEquals("/one/two/three", UriTemplate.of("{/list*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{/empty_list}").expandToString(variables));
    assertEquals("", UriTemplate.of("{/empty_list*}").expandToString(variables));
    assertEquals("/one,1,two,2,three,3,comma,%2C", UriTemplate.of("{/map}").expandToString(variables));
    assertEquals("/one=1/two=2/three=3/comma=%2C", UriTemplate.of("{/map*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{/empty_map}").expandToString(variables));
    assertEquals("", UriTemplate.of("{/empty_map*}").expandToString(variables));
  }

  @Test
  public void testPathStyleParameterExpansion() {
    assertEquals("prefixsuffix", UriTemplate.of("prefix{;undef}suffix").expandToString(variables));
    assertEquals("prefix;emptysuffix", UriTemplate.of("prefix{;empty}suffix").expandToString(variables));
    assertEquals("prefix;var1=val1suffix", UriTemplate.of("prefix{;var1}suffix").expandToString(variables));
    assertEquals("prefix;var1=val1;var2=val2suffix", UriTemplate.of("prefix{;var1,var2}suffix").expandToString(variables));
    assertEquals("prefix;var1=val1suffix", UriTemplate.of("prefix{;var1,undef}suffix").expandToString(variables));
    assertEquals("prefix;var1=val1;emptysuffix", UriTemplate.of("prefix{;var1,empty}suffix").expandToString(variables));
    assertEquals("prefix;var2=val2suffix", UriTemplate.of("prefix{;undef,var2}suffix").expandToString(variables));
    assertEquals("prefix;empty;var2=val2suffix", UriTemplate.of("prefix{;empty,var2}suffix").expandToString(variables));
    assertEquals(";var1=va", UriTemplate.of("{;var1:2}").expandToString(variables));
    assertEquals(";euro=%E2%82%AC", UriTemplate.of("{;euro}").expandToString(variables));
    assertEquals(";slash=%2F", UriTemplate.of("{;slash}").expandToString(variables));
    assertEquals(";%2F=%2F", UriTemplate.of("{;%2F}").expandToString(variables));
    assertEquals(";comma=%2C", UriTemplate.of("{;comma}").expandToString(variables));
    assertEquals(";percent=%25E2%2582%25AC", UriTemplate.of("{;percent}").expandToString(variables));
    assertEquals(";surrogate=%F0%9F%8C%89", UriTemplate.of("{;surrogate}").expandToString(variables));
    assertEquals(";list=one,two,three", UriTemplate.of("{;list}").expandToString(variables));
    assertEquals(";list=one;list=two;list=three", UriTemplate.of("{;list*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{;empty_list}").expandToString(variables));
    assertEquals("", UriTemplate.of("{;empty_list*}").expandToString(variables));
    assertEquals(";%2F_list=%2F,%2F,%2F", UriTemplate.of("{;%2F_list}").expandToString(variables));
    assertEquals(";%2F_list=%2F;%2F_list=%2F;%2F_list=%2F", UriTemplate.of("{;%2F_list*}").expandToString(variables));
    assertEquals(";map=one,1,two,2,three,3,comma,%2C", UriTemplate.of("{;map}").expandToString(variables));
    assertEquals(";one=1;two=2;three=3;comma=%2C", UriTemplate.of("{;map*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{;empty_map}").expandToString(variables));
    assertEquals("", UriTemplate.of("{;empty_map*}").expandToString(variables));
  }

  @Test
  public void testReservedExpansion() {
    assertEquals("prefixsuffix", UriTemplate.of("prefix{+undef}suffix").expandToString(variables));
    assertEquals("prefixsuffix", UriTemplate.of("prefix{+empty}suffix").expandToString(variables));
    assertEquals("prefixval1suffix", UriTemplate.of("prefix{+var1}suffix").expandToString(variables));
    assertEquals("prefixval1,val2suffix", UriTemplate.of("prefix{+var1,var2}suffix").expandToString(variables));
    assertEquals("prefixval1suffix", UriTemplate.of("prefix{+var1,undef}suffix").expandToString(variables));
    assertEquals("prefixval1,suffix", UriTemplate.of("prefix{+var1,empty}suffix").expandToString(variables));
    assertEquals("prefixval2suffix", UriTemplate.of("prefix{+undef,var2}suffix").expandToString(variables));
    assertEquals("prefix,val2suffix", UriTemplate.of("prefix{+empty,var2}suffix").expandToString(variables));
    assertEquals("va", UriTemplate.of("{+var1:2}").expandToString(variables));
    assertEquals("%E2%82%AC", UriTemplate.of("{+euro}").expandToString(variables));
    assertEquals("/", UriTemplate.of("{+slash}").expandToString(variables));
    assertEquals(",", UriTemplate.of("{+comma}").expandToString(variables));
    assertEquals("%E2%82%AC", UriTemplate.of("{+percent}").expandToString(variables));
    assertEquals("%F0%9F%8C%89", UriTemplate.of("{+surrogate}").expandToString(variables));
    assertEquals("one,two,three", UriTemplate.of("{+list}").expandToString(variables));
    assertEquals("one,two,three", UriTemplate.of("{+list*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{+empty_list}").expandToString(variables));
    assertEquals("", UriTemplate.of("{+empty_list*}").expandToString(variables));
    assertEquals("one,1,two,2,three,3,comma,,", UriTemplate.of("{+map}").expandToString(variables));
    assertEquals("one=1,two=2,three=3,comma=,", UriTemplate.of("{+map*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{+empty_map}").expandToString(variables));
    assertEquals("", UriTemplate.of("{+empty_map*}").expandToString(variables));
  }

  @Test
  public void testFragmentExpansion() {
    assertEquals("prefixsuffix", UriTemplate.of("prefix{#undef}suffix").expandToString(variables));
    assertEquals("prefix#val1suffix", UriTemplate.of("prefix{#var1}suffix").expandToString(variables));
    assertEquals("prefix#val1,val2suffix", UriTemplate.of("prefix{#var1,var2}suffix").expandToString(variables));
    assertEquals("prefix#val1suffix", UriTemplate.of("prefix{#var1,undef}suffix").expandToString(variables));
    assertEquals("prefix#val2suffix", UriTemplate.of("prefix{#undef,var2}suffix").expandToString(variables));
    assertEquals("#va", UriTemplate.of("{#var1:2}").expandToString(variables));
    assertEquals("#%E2%82%AC", UriTemplate.of("{#euro}").expandToString(variables));
    assertEquals("#/", UriTemplate.of("{#slash}").expandToString(variables));
    assertEquals("#,", UriTemplate.of("{#comma}").expandToString(variables));
    assertEquals("#%E2%82%AC", UriTemplate.of("{#percent}").expandToString(variables));
    assertEquals("#%F0%9F%8C%89", UriTemplate.of("{#surrogate}").expandToString(variables));
    assertEquals("#one,two,three", UriTemplate.of("{#list}").expandToString(variables));
    assertEquals("#one,two,three", UriTemplate.of("{#list*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{#empty_list}").expandToString(variables));
    assertEquals("", UriTemplate.of("{#empty_list*}").expandToString(variables));
    assertEquals("#one,1,two,2,three,3,comma,,", UriTemplate.of("{#map}").expandToString(variables));
    assertEquals("#one=1,two=2,three=3,comma=,", UriTemplate.of("{#map*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{#empty_map}").expandToString(variables));
    assertEquals("", UriTemplate.of("{#empty_map*}").expandToString(variables));
  }

  @Test
  public void testLabelExpansionWithDotPrefix() {
    assertEquals("prefixsuffix", UriTemplate.of("prefix{.undef}suffix").expandToString(variables));
    assertEquals("prefix.val1suffix", UriTemplate.of("prefix{.var1}suffix").expandToString(variables));
    assertEquals("prefix.val1.val2suffix", UriTemplate.of("prefix{.var1,var2}suffix").expandToString(variables));
    assertEquals("prefix.val1suffix", UriTemplate.of("prefix{.var1,undef}suffix").expandToString(variables));
    assertEquals("prefix.val2suffix", UriTemplate.of("prefix{.undef,var2}suffix").expandToString(variables));
    assertEquals(".va", UriTemplate.of("{.var1:2}").expandToString(variables));
    assertEquals(".%E2%82%AC", UriTemplate.of("{.euro}").expandToString(variables));
    assertEquals(".%2F", UriTemplate.of("{.slash}").expandToString(variables));
    assertEquals(".%2C", UriTemplate.of("{.comma}").expandToString(variables));
    assertEquals(".%25E2%2582%25AC", UriTemplate.of("{.percent}").expandToString(variables));
    assertEquals(".%F0%9F%8C%89", UriTemplate.of("{.surrogate}").expandToString(variables));
    assertEquals(".one,two,three", UriTemplate.of("{.list}").expandToString(variables));
    assertEquals(".one.two.three", UriTemplate.of("{.list*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{.empty_list}").expandToString(variables));
    assertEquals("", UriTemplate.of("{.empty_list*}").expandToString(variables));
    assertEquals(".one,1,two,2,three,3,comma,%2C", UriTemplate.of("{.map}").expandToString(variables));
    assertEquals(".one=1.two=2.three=3.comma=%2C", UriTemplate.of("{.map*}").expandToString(variables));
    assertEquals("", UriTemplate.of("{.empty_map}").expandToString(variables));
    assertEquals("", UriTemplate.of("{empty_map*}").expandToString(variables));
  }

  @Test
  public void testMissingVariableExpansion() {
    assertEquals("", UriTemplate.of("{does_not_exist}").expandToString(variables));
    assertEquals("", UriTemplate.of("{does_not_exist}").expandToString(variables, new ExpandOptions()));
    assertEquals(NoSuchElementException.class, assertExpansionFailure("{does_not_exist}", new ExpandOptions().setAllowVariableMiss(false)).getClass());
  }

  private void assertExpansionFailure(String stringTemplate) {
    assertExpansionFailure(stringTemplate, new ExpandOptions());
  }

  private Throwable assertExpansionFailure(String stringTemplate, ExpandOptions options) {
    UriTemplate template = UriTemplate.of(stringTemplate);
    try {
      template.expandToString(variables, options);
      throw new AssertionError();
    } catch (Exception ignore) {
      // Expected
      return ignore;
    }
  }
}

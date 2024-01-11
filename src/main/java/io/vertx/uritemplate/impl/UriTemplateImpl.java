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

import io.github.stduritemplate.StdUriTemplate;
import io.vertx.uritemplate.ExpandOptions;
import io.vertx.uritemplate.UriTemplate;
import io.vertx.uritemplate.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class UriTemplateImpl implements UriTemplate {
  private String uri;

  public UriTemplateImpl(String uri) {
    this.uri = uri;
  }

  @Override
  public String expandToString(Variables variables) {
    Map<String, Object> substs = new HashMap<>();
    for (String key: variables.names()) {
      substs.put(key, variables.get(key));
    }
    return StdUriTemplate.expand(uri, substs);
  }

  @Override
  public String expandToString(Variables variables, ExpandOptions options) {
    if (!options.getAllowVariableMiss()) {
      for (String token: extractTokens()) {
        if (!variables.names().contains(token)) {
          throw new NoSuchElementException("Variable " + token + " is missing");
        }
      }
    }
    return expandToString(variables);
  }

  private List<String> extractTokens() {
    List<String> result = new ArrayList<>();
    boolean toToken = false;
    boolean maxChar = false;
    boolean operator = true;
    final StringBuilder token = new StringBuilder();

    for (int i = 0; i < uri.length(); i++) {
      char character = uri.charAt(i);
      switch (character) {
        case '{':
          toToken = true;
          operator = true;
          maxChar = false;
          token.setLength(0);
          break;
        case '}':
          if (toToken) {
            result.add(token.toString());
            toToken = false;
            token.setLength(0);
          }
          break;
        case ',':
          if (toToken) {
            result.add(token.toString());
            operator = true;
            token.setLength(0);
            break;
          }
          // Intentional fall-through for commas outside the {}
        default:
          if (toToken) {
            if (operator) {
              switch (character) {
                case '+':
                case '#':
                case '.':
                case '/':
                case ';':
                case '?':
                case '&':
                  break;
                default:
                  token.append(character);
                  break;
              }
              operator = false;
            } else if (maxChar) {
              // just consume
            } else {
              if (character == ':') {
                maxChar = true;
              } else if (character == '*') {
                // consume
              } else {
                token.append(character);
              }
            }
          }
          break;
      }
    }
    return result;
  }
}

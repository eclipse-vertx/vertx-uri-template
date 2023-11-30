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

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;

/**
 * Options to control template expansion.
 */
@DataObject
@JsonGen(publicConverter = false)
public class ExpandOptions {

  /**
   * Default value for {@link #allowVariableMiss} field = {@code true} as mandated by the RFC.
   */
  public static final boolean DEFAULT_ALLOW_VARIABLE_MISS = true;

  private boolean allowVariableMiss = DEFAULT_ALLOW_VARIABLE_MISS;

  public ExpandOptions() {
  }

  public ExpandOptions(JsonObject json) {
    ExpandOptionsConverter.fromJson(json, this);
  }

  public ExpandOptions(ExpandOptions that) {
    allowVariableMiss = that.allowVariableMiss;
  }

  /**
   * @return {@code false} to trigger a {@link java.util.NoSuchElementException} when a referenced variable is missing.
   */
  public boolean getAllowVariableMiss() {
    return allowVariableMiss;
  }

  /**
   * Configures whether a template missing variable is replaced by the empty string or triggers a {@link java.util.NoSuchElementException}
   * to be thrown.
   *
   * The default is {@code true} as specified by the RFC, setting {@code false} is a custom setting not compliant with the spec.
   *
   * @param allowVariableMiss {@code true} to accept missing variables.
   * @return a reference to this, so the API can be used fluently
   */
  public ExpandOptions setAllowVariableMiss(boolean allowVariableMiss) {
    this.allowVariableMiss = allowVariableMiss;
    return this;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ExpandOptionsConverter.toJson(this, json);
    return json;
  }
}

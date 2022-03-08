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

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.uritemplate.impl.UriTemplateImpl;

/**
 * A URI template that follows the <a href="https://datatracker.ietf.org/doc/html/rfc6570">rfc6570</a> level 4.
 *
 * <p> A template is immutable and thread safe, it can be safely shared between threads after its creation.
 * If you are sharing a template as a static variables, keep in mind that {@link #of(String)} can fail and create
 * a classloading issue.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@VertxGen
public interface UriTemplate {

  /**
   * Create a template from a string {@code uri}.
   *
   * <p> The string {@code uri} is validated and parsed, invalid inputs are rejected with a {@link IllegalArgumentException}.
   *
   * @param uri the template string
   * @return the template
   * @throws IllegalArgumentException when the template
   */
  static UriTemplate of(String uri) {
    return new UriTemplateImpl.Parser().parseURITemplate(uri);
  }

  /**
   * Expand this template to a string.
   *
   * @param variables the variables
   * @return the string expansion of this template with the {@code variables}
   */
  String expandToString(Variables variables);

  /**
   * Expand this template to a string.
   *
   * @param variables the variables
   * @param options the options to control template expansion
   * @return the string expansion of this template with the {@code variables}
   */
  String expandToString(Variables variables, ExpandOptions options);

}

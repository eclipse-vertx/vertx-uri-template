package io.vertx.uritemplate.it;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.uritemplate.UriTemplate;
import io.vertx.uritemplate.Variables;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.vertx.core.Vertx.vertx;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(VertxExtension.class)
public class UriTemplateIT {

  private final String abcUrl = "http://abc.com/";

  private HttpClient client = vertx().createHttpClient();

  @BeforeAll
  public static void deploy(VertxTestContext vertxTestContext) {
    Checkpoint checkpoint = vertxTestContext.checkpoint();
    vertxTestContext.verify(() -> {
      vertx().deployVerticle(UriTemplateVerticle.class.getName());
      checkpoint.flag();
    });
  }

  @Test
  @DisplayName("greetingFromUriTemplateTest")
  void greetingFromUriTemplateTest(VertxTestContext context) {
    client.request(HttpMethod.GET, 8080, "localhost", "/greeting")
      .compose(httpClientRequest -> httpClientRequest.send())
      .onComplete(context.succeeding(httpClientResponse -> {
        assertAll(
          () -> assertEquals(200, httpClientResponse.statusCode()),
          () -> assertEquals(HttpVersion.HTTP_1_1, httpClientResponse.version())
        );
        httpClientResponse.body().onComplete(context.succeeding(body -> {
          assertEquals("Hello from UriTemplateVerticle!", body.toString());
          context.completeNow();
        }));
      }))
      .onFailure(context::failNow);
  }

  @Test
  @DisplayName("invalidPortTest")
  void invalidCharacterInRequest(VertxTestContext vertxTestContext) {
    client.request(HttpMethod.GET, 8081, "localhost", "/greeting")
      .compose(httpClientRequest -> httpClientRequest.send())
      .onComplete(vertxTestContext.succeeding(httpClientResponse -> {
        assertAll(
          () -> assertEquals(404, httpClientResponse.statusCode()));
        vertxTestContext.completeNow();
      }));
  }

  @Test
  @DisplayName("getJsonResponseFromUriTemplateTest")
  void getJsonResponseFromUriTemplateTest(VertxTestContext context) {
    Checkpoint asynResponseCheck = context.checkpoint();
    context.verify(() -> {
      client.request(HttpMethod.GET, 8081, "localhost", "/person/12345")
        .compose(httpClientRequest -> httpClientRequest.send())
        .onComplete(context.succeeding(httpClientResponse -> {
          assertEquals(200, httpClientResponse.statusCode());
          httpClientResponse.body().onComplete(bufferAsyncResult -> {
            JsonObject jsonObject = bufferAsyncResult.result().toJsonObject();
            assertEquals(jsonObject.getString("name"), "John");
            assertEquals(jsonObject.getString("age"), "45");
          });
          asynResponseCheck.flag();
        }));
    });
  }

  @Test
  @DisplayName("invalidCharacterOnTemplateTest")
  void invalidCharacterOnTemplateTest(VertxTestContext vertxTestContext) {
    Checkpoint checkpoint = vertxTestContext.checkpoint();
    vertxTestContext.verify(() -> {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> UriTemplate.of(abcUrl + "{!data}"));
      assertEquals("Invalid reserved operator", e.getMessage());
      checkpoint.flag();
    });
  }

  @Test
  @DisplayName("expansionMultipleVariablesTest")
  void expansionMultipleVariablesTest(VertxTestContext testContext) {
    Checkpoint checkpoint = testContext.checkpoint();
    UriTemplate template = UriTemplate.of(abcUrl + "{first}/{second}/{third}/{ids}");
    Variables variables = Variables.variables();
    variables.set("first", "subpathA").set("second", "subpathB").set("third", "subpathC");
    variables.set("ids", Arrays.asList("123", "456"));
    String expectedUri = template.expandToString(variables);
    String actualUri = abcUrl + "subpathA/subpathB/subpathC/123,456";
    testContext.verify(() -> {
      assertEquals(expectedUri, actualUri);
      checkpoint.flag();
    });
  }

  @Test
  @DisplayName("expansionEmptyIdsTest")
  void expansionEmptyIdsTest(VertxTestContext testContext) {
    UriTemplate template = UriTemplate.of(abcUrl + "{first}/{second}/{third}/{ids}");
    Variables variables = Variables.variables();
    variables.set("first", "subpathA").set("second", "subpathB").set("third", "subpathC");
    variables.set("ids", Collections.emptyList());
    String expectedUri = template.expandToString(variables);
    String actualUri = abcUrl + "subpathA/subpathB/subpathC/";
    testContext.verify(() ->assertEquals(expectedUri, actualUri));
    testContext.completeNow();
  }

  @Test
  @DisplayName("uriTemplateWithExpansionStylesTest")
  void uriTemplateWithExpansionStylesTest(VertxTestContext testContext) {
    UriTemplate template = UriTemplate.of(abcUrl + "{first}/{second}/{?third}");
    Variables variables = Variables.variables();
    variables.set("first", "1").set("second", "2");
    String expectedUriWithoutThird = template.expandToString(variables);
    String actualUriWithoutThird = abcUrl + "1/2/";
    testContext.verify(() -> assertEquals(expectedUriWithoutThird, actualUriWithoutThird));
    variables.set("third", "3");
    String expectedUriWithThird = template.expandToString(variables);
    String actualUriWithThird = abcUrl + "1/2/?third=3";
    testContext.verify(() -> assertEquals(expectedUriWithThird, actualUriWithThird));
    testContext.completeNow();
  }

  @Test
  @DisplayName("queryVariablesTest")
  void queryVariablesTest(VertxTestContext vertxTestContext) {
    UriTemplate template = UriTemplate.of(abcUrl + "{query}");
    Variables variables = Variables.variables();
    Map<String, String> query = new HashMap<>();
    query.put("firstName", "Johny");
    query.put("lastName", "Deep");
    variables.set("query", query);
    String expected = template.expandToString(variables);
    String actual = abcUrl + "firstName,Johny,lastName,Deep";
    vertxTestContext.verify(() -> {
      assertEquals(expected, actual);
      vertxTestContext.completeNow();
    });
  }

  @Test
  @DisplayName("uriWithReservedExpansionTest")
  void uriWithReservedExpansionTest(VertxTestContext testContext) {
    UriTemplate template = UriTemplate.of(abcUrl + "{+param}");
    Variables variables = Variables.variables();
    variables.set("param", "value/with/slash");
    String expectedUri = template.expandToString(variables);
    String actualUri = abcUrl + "value/with/slash";
    testContext.verify(() -> assertEquals(expectedUri, actualUri));
    testContext.completeNow();
  }

  @Test
  @DisplayName("uriTemplateWithFragmentExpansionTest")
  void uriTemplateWithFragmentExpansionTest(VertxTestContext testContext) {
    UriTemplate template = UriTemplate.of(abcUrl + "{#param}");
    Variables variables = Variables.variables();
    variables.set("param", "value");
    String expectedUri = template.expandToString(variables);
    String actualUri = abcUrl + "#value";
    testContext.verify(() -> assertEquals(expectedUri, actualUri));
    testContext.completeNow();
  }
}

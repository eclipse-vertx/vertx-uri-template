package examples;

import io.vertx.docgen.Source;
import io.vertx.uritemplate.UriTemplate;
import io.vertx.uritemplate.Variables;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Source
public class UriTemplateExamples {

  public void exampleCreate() {
    UriTemplate template = UriTemplate.of("http://{host}/product/{id}{?sort}");
  }

  public void exampleInvalid() {
    UriTemplate template = UriTemplate.of("{!invalid}}"); // Will throw an exception
  }

  public void exampleExpansion() {
    UriTemplate template = UriTemplate.of("http://{host}/product/{id}{?sort}");
    String uri = template.expandToString(Variables
      .variables()
      .set("host", "localhost")
      .set("id", "12345")
      .set("sort", "price")
    );
  }

  public void exampleSingleValueVariable(Variables variables) {
    variables.set("server", "localhost");
    variables.set("port", "8080");
  }

  public void exampleListVariable(Variables variables) {
    variables.set("ids", Arrays.asList("123", "456"));
  }

  public void exampleMapVariable(Variables variables) {
    Map<String, String> query = new HashMap<>();
    query.put("firstName", "Dale");
    query.put("lastName", "Cooper");
    variables.set("query", query);
  }
}

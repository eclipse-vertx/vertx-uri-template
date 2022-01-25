package examples;

import io.vertx.uritemplate.UriTemplate;
import io.vertx.uritemplate.Variables;

public class UriTemplateExamples {

  public void simpleExample() {
    UriTemplate template = UriTemplate.of("http://{host}/product/{id}{?sort}");
    String uri = template.expandToString(Variables
      .variables()
      .set("host", "localhost")
      .set("id", "12345")
      .set("sort", "price")
    );
  }
}

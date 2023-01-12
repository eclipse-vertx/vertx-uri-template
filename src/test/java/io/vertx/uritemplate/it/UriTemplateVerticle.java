package io.vertx.uritemplate.it;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.uritemplate.UriTemplate;
import io.vertx.uritemplate.Variables;

public class UriTemplateVerticle extends AbstractVerticle {
  private UriTemplate template;

  private static final String GREETING = "Hello from UriTemplateVerticle!";

  private JsonObject jsonObject = new JsonObject();

  @Override
  public void start(Promise<Void> startPromise) {

    jsonObject.put("id", "12345");
    jsonObject.put("name", "John");
    jsonObject.put("age", 45);
    jsonObject.put("from", "New York");

    template = UriTemplate.of("/greeting");
    final String baseUri = template.expandToString(Variables
                                                     .variables()
                                                     .set("host", "localhost")
                                                     .set("port", "8080"));
    UriTemplate jsonTemplate = UriTemplate.of("/person/{id}");
    final String jsonUri = jsonTemplate.expandToString(Variables
                                                         .variables()
                                                         .set("host", "localhost")
                                                         .set("port", "8081")
                                                         .set("id", "12345")
    );

    Router routerGreeting = Router.router(vertx);
    routerGreeting.route(baseUri)
      .handler(routingContext -> {
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
          .end(GREETING);
      });
    Router routerJson = Router.router(vertx);
    routerJson.route(jsonUri).handler(routingContext -> {
      routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .end(jsonObject.encodePrettily());
    });
    Future<Void> port8080Future = Future.future(voidPromise -> listenOnPort(8080, startPromise, routerGreeting));
    Future<Void> port8081Future = Future.future(voidPromise -> listenOnPort(8081, startPromise, routerJson));
    CompositeFuture.all(port8080Future, port8081Future)
      .onComplete(compositeFutureAsyncResult -> {
        if (compositeFutureAsyncResult.succeeded()) {
          startPromise.complete();
        } else {
          startPromise.fail(compositeFutureAsyncResult.cause().getMessage());
        }
      });

  }

  private void listenOnPort(Integer port, Promise<Void> startPromise, Router router) {
    vertx.createHttpServer().requestHandler(router).listen(port, httpServerAsyncResult -> {
      if (httpServerAsyncResult.failed()) {
        startPromise.fail(httpServerAsyncResult.cause());
      }
    });
  }
}

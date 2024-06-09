package com.example.e;

import com.example.e.messaging.message.Json;
import com.example.e.messaging.message.Message;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.DecodeException;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


public class HttpVerticle extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(HttpVerticle.class);
  private final MessageService messageService;

  public HttpVerticle(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.post("/msg").handler(onMsg());

    vertx
      .createHttpServer()
      .requestHandler(router)
      .listen()
      .onSuccess(c -> startPromise.complete())
      .onFailure(startPromise::fail);
  }

  private Handler<RoutingContext> onMsg() {
    return ctx -> {
      log.info("/msg");
      String string = ctx.body().asString();
      try {
        log.info(string);
        var message = validateMsg(string);
        messageService.sendMessage(message);
        ctx
          .response()
          .end(string);
      } catch (DecodeException de) {
        String error = "%s got payload: %s".formatted(de.getMessage(), string);
        ctx.response().setStatusCode(400).end(error);
      }
    };
  }

  private Message validateMsg(String msg) {
    var message = Json.fromJson(msg, Message.class);
    var valid = Json.fromJson(Json.toJson(message.payload()), message.type().type);
    return new Message(message.type(), valid);
  }

}

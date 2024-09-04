package com.example.e;

import com.example.e.messaging.MessageSender;
import com.example.e.messaging.kafka.KafkaAdmin;
import com.example.e.messaging.kafka.KafkaTestConsumer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getenv;
import static java.util.Objects.requireNonNullElse;


public class MainVerticle extends AbstractVerticle {
  private static final boolean TEST_CONSUMER_ENABLED = parseBoolean(requireNonNullElse(getenv("TEST_CONSUMER_ENABLED"), "false"));
  private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new MainVerticle());
  }

  @Override
  public void start() {
    deployVerticles()
      .onSuccess(__ -> log.info("Server Started"))
      .onFailure(shutdown());
  }

  private Future<Void> deployVerticles() {
    KafkaAdmin kafkaAdmin = new KafkaAdmin();
    MessageSender producer = new MessageSender();
    HttpVerticle http = new HttpVerticle(producer);

    Future<String> kafkaAdminFuture = deployVerticle(kafkaAdmin, "KafkaAdmin");
    Future<String> producerFuture = kafkaAdminFuture.compose(id -> deployVerticle(producer, "MessageSender"));
    Future<String> httpFuture = producerFuture.compose(id -> deployVerticle(http, "HttpVerticle"));

    Future<String> testConsumerFuture = Future.succeededFuture();
    if (TEST_CONSUMER_ENABLED) {
      KafkaTestConsumer testConsumer = new KafkaTestConsumer();
      testConsumerFuture = kafkaAdminFuture.compose(id -> deployVerticle(testConsumer, "KafkaTestConsumer"));
    }

    return Future.all(httpFuture, testConsumerFuture).mapEmpty();
  }

  private Future<String> deployVerticle(Verticle verticle, String verticleName) {
    return vertx.deployVerticle(verticle)
      .onSuccess(id -> log.info(verticleName + " deployed successfully with deployment id " + id))
      .onFailure(err -> log.error("Failed to deploy " + verticleName + ": " + err.getMessage()));
  }

  public static Handler<Throwable> shutdown() {
    return f -> {
      log.error("Failed to bootstrap: " + f.getMessage() + ", is Kafka Running?");
      System.exit(1);
    };
  }
}

package com.example.e;

import com.example.e.messaging.kafka.KafkaAdmin;
import com.example.e.messaging.kafka.KafkaTestConsumer;
import com.example.e.messaging.MessageSender;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
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
    KafkaAdmin kafkaAdmin = new KafkaAdmin();
    MessageSender producer = new MessageSender();
    KafkaTestConsumer testConsumer = new KafkaTestConsumer();
    HttpVerticle http = new HttpVerticle(producer);

    log.info("Starting Admin Service..");
    vertx
      .deployVerticle(kafkaAdmin)
      .onFailure(shutdown())
      .onSuccess(admin -> {

        if (TEST_CONSUMER_ENABLED) {
          startTestConsumer(testConsumer);
        }

        log.info("Starting producer..");
        vertx
          .deployVerticle(producer)
          .onFailure(shutdown())
          .onSuccess(prod -> {

            log.info("Starting Http Server..");
            vertx
              .deployVerticle(http)
              .onSuccess(__ -> log.info("Server Started"))
              .onFailure(shutdown());
          });
      });
  }

  private void startTestConsumer(KafkaTestConsumer testConsumer) {
    log.info("Starting Test Consumer...");
    vertx
      .deployVerticle(testConsumer)
      .onFailure(shutdown());
  }

  public static Handler<Throwable> shutdown() {
    return f -> {
      log.error("Failed to bootstrap: " + f.getMessage() +", is Kafka Running?");
      System.exit(1);
    };
  }
}

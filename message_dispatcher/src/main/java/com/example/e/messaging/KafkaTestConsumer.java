package com.example.e.messaging;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.example.e.messaging.Configuration.CONSUMER_CONFIG;
import static com.example.e.messaging.KafkaAdmin.TEST_TOPIC;

public class KafkaTestConsumer extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(KafkaTestConsumer.class);
  KafkaConsumer<String, String> consumer;

  @Override
  public void start(Promise<Void> startPromise) {
    consumer = KafkaConsumer.create(vertx, CONSUMER_CONFIG);
    consumer
      .subscribe(TEST_TOPIC)
      .onSuccess(completeStartup(startPromise))
      .onFailure(startPromise::fail);

    consumer.handler(handleRecord());
  }

  private static Handler<KafkaConsumerRecord<String, String>> handleRecord() {
    return record -> {
      log.info("Processing key=" + record.key() + ",value=" + record.value() + ",partition=" + record.partition() + ",offset=" + record.offset());
    };
  }

  private Handler<Void> completeStartup(Promise<Void> startPromise) {
    return w -> {
      Runtime.getRuntime().addShutdownHook(new Thread(this::closeConsumer));
      startPromise.complete();
    };
  }

  private void closeConsumer() {
    log.info("Closing consumer..");
    var fut = new CompletableFuture<>();
    consumer.close().onComplete(f -> fut.complete(null));
    try {
      fut.get(1, TimeUnit.SECONDS);
    } catch (Exception e) {
      log.error("Failed to close consumer within the specified time.", e);
    }
  }
}

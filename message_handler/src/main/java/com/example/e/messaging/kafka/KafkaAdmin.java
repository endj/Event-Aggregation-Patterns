package com.example.e.messaging.kafka;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.kafka.admin.KafkaAdminClient;
import io.vertx.kafka.admin.NewTopic;

import java.util.List;

import static com.example.e.messaging.kafka.Configuration.ADMIN_CONFIG;
import static com.example.e.messaging.kafka.Configuration.PARTITIONS;

public class KafkaAdmin extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(KafkaAdmin.class);

  public static final String MESSAGE_TOPIC = "messages";

  KafkaAdminClient adminClient;

  @Override
  public void start(Promise<Void> startPromise) {
    adminClient = KafkaAdminClient.create(vertx, ADMIN_CONFIG);
    adminClient
      .listTopics()
      .onSuccess(topics -> {
        log.info("Fetched topics " + topics);
        if (topics.contains(MESSAGE_TOPIC)) {
          startPromise.complete();
        } else {
          setupDefaultTopic(startPromise);
        }
      }).onFailure(startPromise::fail);
  }

  private void setupDefaultTopic(Promise<Void> startPromise) {
    adminClient
      .createTopics(List.of(new NewTopic(MESSAGE_TOPIC, PARTITIONS, (short) 1)))
      .onSuccess(w -> startPromise.complete())
      .onFailure(startPromise::fail);
  }
}

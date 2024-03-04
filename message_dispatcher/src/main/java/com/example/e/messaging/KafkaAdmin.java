package com.example.e.messaging;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.kafka.admin.KafkaAdminClient;
import io.vertx.kafka.admin.NewTopic;

import java.util.List;

import static com.example.e.messaging.Configuration.ADMIN_CONFIG;
import static com.example.e.messaging.Configuration.PARTITIONS;

public class KafkaAdmin extends AbstractVerticle implements AdminService {
  private static final Logger log = LoggerFactory.getLogger(KafkaAdmin.class);

  public static final String TEST_TOPIC = "test";

  KafkaAdminClient adminClient;

  @Override
  public void start(Promise<Void> startPromise) {
    adminClient = KafkaAdminClient.create(vertx, ADMIN_CONFIG);
    adminClient
      .listTopics()
      .onSuccess(topics -> {
        log.info("Fetched topics " + topics);
        if (topics.contains(TEST_TOPIC)) {
          startPromise.complete();
        } else {
          setupDefaultTopic(startPromise);
        }
      }).onFailure(startPromise::fail);
  }

  private void setupDefaultTopic(Promise<Void> startPromise) {
    adminClient
      .createTopics(List.of(new NewTopic(TEST_TOPIC, PARTITIONS, (short) 1)))
      .onSuccess(w -> startPromise.complete())
      .onFailure(startPromise::fail);
  }
}

package com.example.e.messaging;

import com.example.e.MessageService;
import com.example.e.messaging.message.Json;
import com.example.e.messaging.message.Message;
import com.example.e.messaging.message.payloads.Partitionable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import static com.example.e.messaging.kafka.Configuration.PARTITIONS;
import static com.example.e.messaging.kafka.Configuration.PRODUCER_CONFIG;
import static com.example.e.messaging.kafka.KafkaAdmin.MESSAGE_TOPIC;

public class MessageSender extends AbstractVerticle implements MessageService {
  private static final Logger log = LoggerFactory.getLogger(MessageSender.class);
  KafkaProducer<String, String> producer;

  @Override
  public void start(Promise<Void> startPromise) {
    producer = KafkaProducer.create(vertx, PRODUCER_CONFIG);
    startPromise.complete();
  }

  @Override
  public void sendMessage(Message message) {
    String payload = Json.toJson(message);
    int partition = derivePartition(message);
    log.info("Sending message: [" + payload + "] to partition [" + partition + "]");
    producer.send(KafkaProducerRecord.create(MESSAGE_TOPIC, null, payload, partition), res -> {
      if (res.failed()) {
        log.error("Failed to send message %s to partition %s".formatted(message, partition));
      }
    });
  }

  private int derivePartition(Message message) {
    var id = ((Partitionable) message.payload()).id();
    return toPartition(id);
  }

  // Maps value to random partition in range [0, PARTITIONS - 1]
  private int toPartition(int id) {
    int m = id % PARTITIONS;
    return m + ((m >> 31) & PARTITIONS);
  }
}

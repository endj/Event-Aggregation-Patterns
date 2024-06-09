package com.example.e.messaging.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;

import static java.lang.Integer.parseUnsignedInt;
import static java.lang.System.getenv;
import static java.util.Objects.requireNonNullElse;

public class Configuration {
  public static final int PARTITIONS = parseUnsignedInt(requireNonNullElse(getenv("PARTITIONS"), "5"));
  public final static Map<String, String> CONSUMER_CONFIG = Map.of(
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
    ConsumerConfig.GROUP_ID_CONFIG, "my_group",
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"
  );
  public final static Map<String, String> PRODUCER_CONFIG = Map.of(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
    ProducerConfig.ACKS_CONFIG, "0"
  );
  public static final Map<String, String> ADMIN_CONFIG = Map.of(
    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
    AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "1000",
    AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "2000"
  );
}

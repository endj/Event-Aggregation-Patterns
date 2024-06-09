package com.example.e.messaging.message.payloads;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserCreated(
  UUID userId,
  OffsetDateTime createdAt,
  String email) implements Partitionable {
  @Override
  public int id() {
    return userId.hashCode();
  }
}

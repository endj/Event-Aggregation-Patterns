package com.example.e.messaging.message.payloads;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDeleted(UUID userId, OffsetDateTime deletedAt) implements Partitionable {
  @Override
  public int id() {
    return userId.hashCode();
  }
}

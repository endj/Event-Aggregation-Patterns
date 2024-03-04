package com.example.e.messaging.message.payloads;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserOrder(
  UUID userId,

  UUID orderId,
  List<Item> items,
  OffsetDateTime orderedAt) implements Partitionable {
  @Override
  public int id() {
    return userId.hashCode();
  }
}

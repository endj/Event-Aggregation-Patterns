package com.example.e.messaging.message.payloads;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentRefunded(UUID orderId, UUID userId, OffsetDateTime refundedAt,
                              BigDecimal refundAmount) implements Partitionable {
  @Override
  public int id() {
    return userId.hashCode();
  }
}

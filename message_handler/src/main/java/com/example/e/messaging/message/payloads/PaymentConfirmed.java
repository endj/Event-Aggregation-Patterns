package com.example.e.messaging.message.payloads;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentConfirmed(UUID userId, UUID orderId, BigDecimal amount,
                               OffsetDateTime payedAt) implements Partitionable {
  @Override
  public int id() {
    return userId.hashCode();
  }
}

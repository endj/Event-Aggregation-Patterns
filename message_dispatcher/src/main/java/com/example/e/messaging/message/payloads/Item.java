package com.example.e.messaging.message.payloads;

import java.math.BigDecimal;
import java.util.UUID;

public record Item(UUID id, BigDecimal cost) {
}

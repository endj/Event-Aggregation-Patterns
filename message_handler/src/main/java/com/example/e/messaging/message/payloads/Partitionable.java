package com.example.e.messaging.message.payloads;

import java.util.Objects;

public interface Partitionable {

  default int id() {
    return Objects.hash(this);
  }
}

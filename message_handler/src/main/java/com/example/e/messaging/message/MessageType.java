package com.example.e.messaging.message;

import com.example.e.messaging.message.payloads.PaymentConfirmed;
import com.example.e.messaging.message.payloads.PaymentRefunded;
import com.example.e.messaging.message.payloads.UserCreated;
import com.example.e.messaging.message.payloads.UserDeleted;
import com.example.e.messaging.message.payloads.UserOrder;

public enum MessageType {
  USER_CREATED(UserCreated.class),
  USER_ORDER(UserOrder.class),
  USER_PAYMENT(PaymentConfirmed.class),
  USER_REFUND(PaymentRefunded.class),
  USER_DELETED(UserDeleted.class);

  public final Class<?> type;

  MessageType(Class<?> type) {
    this.type = type;
  }
}

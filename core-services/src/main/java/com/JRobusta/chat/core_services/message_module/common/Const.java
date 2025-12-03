package com.JRobusta.chat.core_services.message_module.common;

import lombok.Getter;

@Getter
public enum Const {

  OUTBOX_REDIS_FALLBACK("outbox_redis_fallback_queue"), OUTBOX_REDIS_EVENT(
      "outbox_redis_event_queue"), OUTBOX_REDIS_PROCESSING("outbox_redis_processing_queue"),;

  private final String value;

  Const(String value) {
    this.value = value;
  }

}

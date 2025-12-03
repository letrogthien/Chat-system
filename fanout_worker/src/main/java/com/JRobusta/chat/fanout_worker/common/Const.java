package com.JRobusta.chat.fanout_worker.common;

import lombok.Getter;

@Getter
public enum Const {

  CHANNELS_GRPC_CONNECTION_MANAGEMENT("connection-management-service"), CHANNELS_GRPC_MESSAGE(
      "message-service"), CONNECTION_ACTIVE("connection-active"),
  CONNECTION_SET("connection-set:"),;

  private final String value;

  Const(String value) {
    this.value = value;
  }

}

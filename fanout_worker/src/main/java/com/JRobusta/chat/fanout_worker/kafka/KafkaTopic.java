package com.JRobusta.chat.fanout_worker.kafka;

import lombok.Getter;

@Getter
public enum KafkaTopic {
  MESSAGE_ALL("message.all"),
  CONNECTION_ADD("connection.add"),
  CONNECTION_REMOVE("connection.remove");

  private final String topicName;

  KafkaTopic(String topicName) {
    this.topicName = topicName;
  }

}

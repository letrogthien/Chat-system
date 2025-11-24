package com.JRobusta.chat.core_services.kafka;

import lombok.Getter;
@Getter
public enum KafkaTopic {
    MESSAGE_ALL("message.all"),
    OUTBOX_EVENT("outbox.event.raw"),;

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

}
package com.JRobusta.chat.core_services.message_module.kafka;

import lombok.Getter;
@Getter
public enum KafkaTopic {
    MESSAGE_ALL("message.all"),;

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

}
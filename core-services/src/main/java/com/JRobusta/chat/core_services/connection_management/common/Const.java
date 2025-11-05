package com.JRobusta.chat.core_services.connection_management.common;

import lombok.Getter;

@Getter
public enum Const {

    CONNECTION_VERIFIED_HASH("CONNECTIONS_HASH:"),
    CONNECTION_VERIFIED_SET("CONNECTIONS_SET:"),;

    private final String value;

    Const(String value) {
        this.value = value;
    }

}

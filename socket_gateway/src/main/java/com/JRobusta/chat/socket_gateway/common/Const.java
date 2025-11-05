package com.JRobusta.chat.socket_gateway.common;

import lombok.Getter;

@Getter
public enum Const {
    INSTANCE("default"),
    RSA_PUBLIC_KEY_REDIS("RSA_PUBLIC_KEY_ACCESS_TOKEN"),
    CHANNELS_GRPC_AUTH("auth-service"),
    CHANNELS_GRPC_CONNECTION_MANAGEMENT("connection-management-service"),
    CONNECTION_VERIFIED_REDIS("CONNECTION_VERIFIED:"),
    CLIENT_IP_ATTRIBUTE("CLIENT_IP"),;

    private final String value;

    Const(String value) {
        this.value = value;
    }

}

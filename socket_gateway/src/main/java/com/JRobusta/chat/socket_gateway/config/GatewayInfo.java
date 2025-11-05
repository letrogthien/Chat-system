package com.JRobusta.chat.socket_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
public class GatewayInfo {

    @Value("${gateway.id:${GATEWAY_ID:${HOSTNAME:gateway-unknown}}}")
    private String gatewayId;

    public String getId() {
        return gatewayId;
    }
}
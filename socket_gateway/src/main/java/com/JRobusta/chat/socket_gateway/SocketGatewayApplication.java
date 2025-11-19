package com.JRobusta.chat.socket_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SocketGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocketGatewayApplication.class, args);
	}

}

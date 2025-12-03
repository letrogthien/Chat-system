package com.JRobusta.chat.core_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoreServicesApplication {

  public static void main(String[] args) {
    SpringApplication.run(CoreServicesApplication.class, args);
  }

}

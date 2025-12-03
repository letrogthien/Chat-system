package com.JRobusta.chat.fanout_worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FanoutWorkerApplication {

  public static void main(String[] args) {
    SpringApplication.run(FanoutWorkerApplication.class, args);
  }

}

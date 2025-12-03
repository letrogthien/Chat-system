package com.JRobusta.chat.fanout_worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Configuration
public class VirtualThreadConfig {

  @Bean("ioExecutor")
  Executor ioExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

}

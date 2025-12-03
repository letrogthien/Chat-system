package com.JRobusta.chat.fanout_worker.services;


import com.JRobusta.chat.events.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {



  public void processMessage(MessageEvent message) {
    //XADD stream


  }

}

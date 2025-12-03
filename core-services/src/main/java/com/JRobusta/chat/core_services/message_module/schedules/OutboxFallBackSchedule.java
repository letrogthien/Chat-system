package com.JRobusta.chat.core_services.message_module.schedules;


import com.JRobusta.chat.core_services.kafka.KafkaTopic;
import com.JRobusta.chat.core_services.message_module.common.Const;
import com.JRobusta.chat.core_services.kafka.producer.SendEventService;
import com.JRobusta.chat.core_services.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxFallBackSchedule {
  private final RedisService redisService;
  private final SendEventService sendEventService;
  private final ObjectMapper objectMapper;

  @Value("${outbox.fallback.batch-size:100}")
  private int batchSize;

  @Value("${outbox.fallback.max-concurrent:10}")
  private int maxConcurrent;

  private Semaphore semaphore;

  @PostConstruct
  public void init() {
    semaphore = new Semaphore(maxConcurrent);
    log.info("OutboxFallBackSchedule initialized with batchSize={}, maxConcurrent={}", batchSize,
        maxConcurrent);
  }

  @Scheduled(fixedDelay = 2000)
  public void processOutboxFallBack() {
    log.debug("Running Outbox FallBack Schedule...");

    List<String> batch = new ArrayList<>();

    // 1. Batch processing - pop multiple items at once
    for (int i = 0; i < batchSize; i++) {
      String item = redisService.popFromFallbackToProcessing(Const.OUTBOX_REDIS_FALLBACK.getValue(),
          Const.OUTBOX_REDIS_PROCESSING.getValue());
      if (item == null)
        break;
      batch.add(item);
    }

    if (batch.isEmpty()) {
      return;
    }

    log.info("Processing {} fallback items", batch.size());

    // 2. Parallel processing with concurrency control
    List<CompletableFuture<Void>> futures = batch.stream().map(this::processItemAsync).toList();

    // 3. Wait for all to complete
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((v, ex) -> {
      if (ex != null) {
        log.error("Error in batch processing", ex);
      }
    });
  }

  private CompletableFuture<Void> processItemAsync(String fallbackItem) {
    return CompletableFuture.runAsync(() -> {
      try {
        semaphore.acquire();
        processItem(fallbackItem);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Thread interrupted while processing item", e);
      } finally {
        semaphore.release();
      }
    });
  }

  private void processItem(String fallbackItem) {
    String[] parts = fallbackItem.split("\\|", 2);
    if (parts.length < 2) {
      log.error("Invalid fallback item format: {}", fallbackItem);
      redisService.removeFromProcessing(Const.OUTBOX_REDIS_PROCESSING.getValue(), fallbackItem);
      return;
    }

    String uuid = parts[0];
    String payload = parts[1];

    try {
      JsonNode jsonNode = objectMapper.readTree(payload);
      String conversationId = jsonNode.get("conversationId").asText();

      sendEventService
          .sendEventOutbox(KafkaTopic.OUTBOX_EVENT.getTopicName(), fallbackItem, conversationId)
          .whenComplete((result, ex) -> handleCompletion(uuid, fallbackItem, ex));

    } catch (JsonProcessingException e) {
      log.error("Failed to parse payload for uuid {}: {}", uuid, payload, e);
      redisService.removeFromProcessing(Const.OUTBOX_REDIS_PROCESSING.getValue(), fallbackItem);
    }
  }

  private void handleCompletion(String uuid, String fallbackItem, Throwable ex) {
    if (ex != null) {
      redisService.pushToList(Const.OUTBOX_REDIS_FALLBACK.getValue(), fallbackItem);
      log.warn("Retry failed for outboxId {}. Re-queued to fallback list.", uuid);
    } else {
      log.info("Successfully resent fallback event for outboxId: {}", uuid);
    }
    redisService.removeFromProcessing(Const.OUTBOX_REDIS_PROCESSING.getValue(), fallbackItem);
  }



}

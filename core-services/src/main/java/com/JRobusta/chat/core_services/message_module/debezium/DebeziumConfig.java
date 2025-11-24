package com.JRobusta.chat.core_services.message_module.debezium;


import com.JRobusta.chat.core_services.kafka.KafkaTopic;
import com.JRobusta.chat.core_services.message_module.common.Const;
import com.JRobusta.chat.core_services.kafka.SendEventService;
import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import com.JRobusta.chat.core_services.message_module.services.MessageOutboxService;
import com.JRobusta.chat.core_services.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class DebeziumConfig {

    private final Configuration debeziumConfiguration;
    private DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final SendEventService sendEventService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final MessageOutboxService messageOutboxService;

    @Bean
    public static Configuration debeziumConnectorConfig(
            @Value("${spring.datasource.url}") String datasourceUrl,
            @Value("${spring.datasource.username}") String datasourceUsername,
            @Value("${spring.datasource.password}") String datasourcePassword) {

        String[] urlParts = datasourceUrl.split("/");
        String[] hostPort = urlParts[2].split(":");
        String hostname = hostPort[0];
        String port = hostPort.length > 1 ? hostPort[1] : "3306";
        String database = urlParts[3].split("\\?")[0];

        return Configuration.create()
                .with("name", "message-outbox-connector")
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "F:/chat-sys/tmp/offsets.dat")
                .with("offset.flush.interval.ms", "60000")
                .with("database.hostname", hostname)
                .with("database.port", port)
                .with("database.user", datasourceUsername)
                .with("database.password", datasourcePassword)
                .with("database.server.id", "85744")
                .with("topic.prefix", "dbserver1")
                .with("database.include.list", database)
                .with("table.include.list", database + ".message_producer_outbox")
                .with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
                .with("schema.history.internal.file.filename", "F:/chat-sys/tmp/schema-history.dat")
                .with("database.allowPublicKeyRetrieval", "true")
                .with("include.schema.changes", "false")
                .build();
    }

    @PostConstruct
    public void buildEngine() {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(debeziumConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startEngine() {
        executor.execute(debeziumEngine);
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> event) {
        try {
            Struct value = (Struct) event.record().value();
            if (value == null) {
                return;
            }

            String op = (String) value.get("op");
            if (!"c".equals(op)) {  // chỉ xử lý insert
                return;
            }

            Struct after = (Struct) value.get("after");
            if (after == null) {
                return;
            }
            String status = after.getString("status"); // lấy status
            if (!OutboxStatus.PENDING.toString().equalsIgnoreCase(status)) {  // chỉ xử lý PENDING
                return;
            }

            String payload = after.getString("payload");
            String uuid = after.getString("id");


            String conversationId = objectMapper.readTree(payload).get("conversationId").asText();
            sendEventService.sendEventOutbox(KafkaTopic.OUTBOX_EVENT.getTopicName() , uuid + "|" + payload, conversationId)
                    .whenComplete(
                            (result, ex) -> {
                                if (ex != null) {
                                    System.out.println("Failed to send outbox event to Kafka, falling back to Redis for outboxId: " + uuid);
                                    redisService.pushToList(Const.OUTBOX_REDIS_FALLBACK.getValue(), uuid + "|" + payload);
                                } else {
                                    messageOutboxService.markAsProcessed(uuid, OutboxStatus.PROCESSED);
                                }
                            }
                    );


        } catch (Exception e) {
            if (e instanceof JsonProcessingException) {
                log.error("Failed to parse outbox event payload", e);
            }
            log.error("Failed to send outbox event to Redis fallback queue", e);
        }
    }

    @PreDestroy
    public void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }
}
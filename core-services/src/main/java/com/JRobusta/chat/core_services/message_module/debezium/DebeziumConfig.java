package com.JRobusta.chat.core_services.message_module.debezium;


import com.JRobusta.chat.core_services.events.MessageEvent;
import com.JRobusta.chat.core_services.message_module.services.MessageOutboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class DebeziumConfig {

    private final Configuration debeziumConfiguration;
    private DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ObjectMapper objectMapper;

    private ObjectProvider<MessageOutboxService> messageOutboxService;




    @Autowired
    public void setMessageOutboxService(ObjectProvider<MessageOutboxService> messageOutboxService) {
        this.messageOutboxService = messageOutboxService;
    }

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
    public void start() {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(debeziumConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();

        executor.execute(debeziumEngine);
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> event) {
        try {
            SourceRecord record = event.record();
            Struct value = (Struct) record.value();
            if (value == null) {
                return;
            }

            String op = (String) value.get("op");
            if (!"c".equals(op) && !"u".equals(op)) {
                return;
            }

            Struct after = (Struct) value.get("after");
            if (after == null) {
                return;
            }

            String payload = after.getString("payload");
            MessageEvent messageEvent = objectMapper.readValue(payload, MessageEvent.class);
            String uuid = after.getString("id");

            MessageOutboxService service = messageOutboxService != null ? messageOutboxService.getIfAvailable() : null;
            if (service != null) {
                service.processOutboxEvent(messageEvent, uuid);
            } else {
                log.warn("MessageOutboxService not available when processing outbox event with id: {}", uuid);
            }
        } catch (Exception e) {

            throw new RuntimeException("Error processing change event", e);
        }
    }

    @PreDestroy
    public void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }
}
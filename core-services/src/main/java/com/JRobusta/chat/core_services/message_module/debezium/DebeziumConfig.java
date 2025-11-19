package com.JRobusta.chat.core_services.message_module.debezium;


import com.JRobusta.chat.core_services.events.MessageEvent;
import com.JRobusta.chat.core_services.message_module.services.MessageOutboxService;
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
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class DebeziumConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    private DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MessageOutboxService messageOutboxService;


    @Bean
    public Configuration debeziumConnectorConfig() {
        String[] urlParts = datasourceUrl.split("/");
        String[] hostPort = urlParts[2].split(":");
        String hostname = hostPort[0];
        String port = hostPort.length > 1 ? hostPort[1] : "3306";
        String database = urlParts[3].split("\\?")[0];

        return Configuration.create()
                .with("name", "message-outbox-connector")
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/tmp/offsets.dat")
                .with("offset.flush.interval.ms", "60000")
                .with("database.hostname", hostname)
                .with("database.port", port)
                .with("database.user", datasourceUsername)
                .with("database.password", datasourcePassword)
                .with("database.server.id", "85744")
                .with("topic.prefix", "dbserver1")
                .with("database.include.list", database)
                .with("table.include.list", database + ".message_producer_outbox")
                .with("schema.history.internal", "io.debezium.relational.history.MemorySchemaHistory")
                .with("database.allowPublicKeyRetrieval", "true")
                .with("include.schema.changes", "false")
                .build();
    }

    @PostConstruct
    public void start() {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(debeziumConnectorConfig().asProperties())
                .notifying(this::handleChangeEvent)
                .build();

        executor.execute(debeziumEngine);
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        try {
            SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
            Struct sourceRecordValue = (Struct) sourceRecord.value();

            if (sourceRecordValue != null) {
                String operation = (String) sourceRecordValue.get("op");

                if ("c".equals(operation) || "u".equals(operation)) {
                    Struct after = (Struct) sourceRecordValue.get("after");
                    if (after != null) {
                        String payload = after.getString("payload");
                        MessageEvent messageEvent = objectMapper.readValue(payload, MessageEvent.class);
                        UUID uuid = UUID.fromString(after.getString("id"));
                        messageOutboxService.processOutboxEvent(messageEvent, uuid);
                    }
                }
            }
        } catch (Exception e){
            log.error("Error processing change event: ", e);
        }
    }



    @PreDestroy
    public void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }
}

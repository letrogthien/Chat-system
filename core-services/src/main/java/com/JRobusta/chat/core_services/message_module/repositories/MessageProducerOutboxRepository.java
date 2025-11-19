package com.JRobusta.chat.core_services.message_module.repositories;

import com.JRobusta.chat.core_services.message_module.entities.MessageProducerOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageProducerOutboxRepository extends JpaRepository<MessageProducerOutbox, UUID> {
}

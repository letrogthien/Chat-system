package com.JRobusta.chat.core_services.message_module.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JRobusta.chat.core_services.message_module.entities.MessagePin;

@Repository
public interface MessagePinRepository extends JpaRepository<MessagePin, MessagePin.MessagePinId> {

}

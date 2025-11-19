package com.JRobusta.chat.core_services.message_module.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JRobusta.chat.core_services.message_module.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

}

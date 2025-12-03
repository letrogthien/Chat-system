package com.JRobusta.chat.core_services.message_module.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JRobusta.chat.core_services.message_module.entities.MessageAttachment;

@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, UUID> {

}

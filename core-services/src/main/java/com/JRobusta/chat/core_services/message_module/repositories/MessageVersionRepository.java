package com.JRobusta.chat.core_services.message_module.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JRobusta.chat.core_services.message_module.entities.MessageVersion;

import java.util.UUID;

@Repository
public interface MessageVersionRepository extends JpaRepository<MessageVersion, UUID> {

}

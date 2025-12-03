package com.JRobusta.chat.core_services.message_module.repositories;

import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import com.JRobusta.chat.core_services.message_module.entities.MessageProducerOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageProducerOutboxRepository
    extends JpaRepository<MessageProducerOutbox, String> {
  @Modifying
  @Transactional
  @Query("UPDATE MessageProducerOutbox o SET o.status = :status WHERE o.id = :id")
  int updateStatusById(@Param("id") String id, @Param("status") OutboxStatus status);
}

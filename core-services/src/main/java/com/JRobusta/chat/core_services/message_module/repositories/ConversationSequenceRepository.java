package com.JRobusta.chat.core_services.message_module.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.JRobusta.chat.core_services.message_module.entities.ConversationSequence;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ConversationSequenceRepository extends JpaRepository<ConversationSequence, UUID> {


    @Query("""
                    SELECT cs.lastSeq
                    FROM ConversationSequence cs
                    WHERE cs.conversationId = :conversationId
            
            """)
    Long getSequenceNumber(@Param("conversationId") UUID conversationId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE ConversationSequence cs
           SET cs.lastSeq = :sequenceNumber
           WHERE cs.conversationId = :conversationId
           """)
    void updateSequenceNumber(@Param("conversationId") UUID conversationId,
                              @Param("sequenceNumber") Long sequenceNumber);
}

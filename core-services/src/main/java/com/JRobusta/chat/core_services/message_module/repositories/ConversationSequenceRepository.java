package com.JRobusta.chat.core_services.message_module.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.JRobusta.chat.core_services.message_module.entities.ConversationSequence;

@Repository
public interface ConversationSequenceRepository extends JpaRepository<ConversationSequence, UUID> {


    @Query("""
                    SELECT cs.lastSeq
                    FROM ConversationSequence cs
                    WHERE cs.conversationId = :conversationId
            
            """)
    Long getSequenceNumber(@Param("conversationId") UUID conversationId);

    void updateSequenceNumber(UUID conversationId, Long sequenceNumber);
}

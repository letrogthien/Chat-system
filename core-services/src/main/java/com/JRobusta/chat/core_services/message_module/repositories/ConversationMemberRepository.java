package com.JRobusta.chat.core_services.message_module.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.JRobusta.chat.core_services.message_module.entities.ConversationMember;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationMemberRepository
    extends JpaRepository<ConversationMember, ConversationMember.ConversationMemberId> {


    @Query("""
    SELECT cm.userId
    FROM ConversationMember cm
    WHERE cm.conversationId = :conversationId
""")
    List<UUID> getListMemberIdByConversationId(@Param("conversationId") UUID conversationId);
}

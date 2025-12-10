package com.JRobusta.chat.core_services.mapper;


import com.JRobusta.chat.core_services.message_module.dtos.ConversationDto;
import com.JRobusta.chat.core_services.message_module.entities.Conversation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    Conversation toEntity(ConversationDto conversationDto);
    ConversationDto toDto(Conversation conversation);
}

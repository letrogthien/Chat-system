package com.JRobusta.chat.socket_gateway.mapper;

import com.JRobusta.chat.socket_gateway.dto.AckOffsetDTO;
import com.JRobusta.chat.socket_gateway.dto.CreateMessageResponseDTO;
import com.JRobusta.chat.socket_gateway.dto.SocketMessageDTO;
import com.google.protobuf.Timestamp;
import message.v1.MessageOuterClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;


import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    // Message mappings
    @Mapping(target = "messageId", source = "messageId", qualifiedByName = "stringToUUID")
    @Mapping(target = "conversationId", source = "conversationId", qualifiedByName = "stringToUUID")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "stringToUUID")
    @Mapping(target = "threadRootId", source = "threadRootId", qualifiedByName = "stringToUUID")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "timestampToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "timestampToInstant")
    SocketMessageDTO messageToSocketMessageDTO(MessageOuterClass.Message message);

    @Mapping(target = "messageId", source = "messageId", qualifiedByName = "uuidToString")
    @Mapping(target = "conversationId", source = "conversationId", qualifiedByName = "uuidToString")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "threadRootId", source = "threadRootId", qualifiedByName = "uuidToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToTimestamp")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToTimestamp")
    MessageOuterClass.Message socketMessageDTOToMessage(SocketMessageDTO socketMessageDTO);

    // AckOffset mappings
    @Mapping(target = "conversationId", source = "conversationId", qualifiedByName = "stringToUUID")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "stringToUUID")
    @Mapping(target = "ackedAt", source = "ackedAt", qualifiedByName = "timestampToInstant")
    AckOffsetDTO ackOffsetToAckOffsetDTO(MessageOuterClass.AckOffset ackOffset);

    @Mapping(target = "conversationId", source = "conversationId", qualifiedByName = "uuidToString")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "ackedAt", source = "ackedAt", qualifiedByName = "instantToTimestamp")
    MessageOuterClass.AckOffset ackOffsetDTOToAckOffset(AckOffsetDTO ackOffsetDTO);

    // CreateMessageResponse mappings
    CreateMessageResponseDTO createMessageResponseToDTO(MessageOuterClass.CreateMessageResponse response);

    MessageOuterClass.CreateMessageResponse dtoToCreateMessageResponse(CreateMessageResponseDTO dto);

    // Helper methods for type conversions
    @Named("stringToUUID")
    default UUID stringToUUID(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return UUID.fromString(value);
    }

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        if (uuid == null) {
            return "";
        }
        return uuid.toString();
    }

    @Named("timestampToInstant")
    default Instant timestampToInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    @Named("instantToTimestamp")
    default Timestamp instantToTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}

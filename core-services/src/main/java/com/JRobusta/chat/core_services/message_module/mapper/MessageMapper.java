package com.JRobusta.chat.core_services.message_module.mapper;


import com.JRobusta.chat.core_services.message_module.entities.Message;
import com.google.protobuf.Timestamp;
import message.v1.MessageOuterClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "messageId", source = "messageId", qualifiedByName = "stringToUUID")
    @Mapping(target = "conversationId", source = "conversationId", qualifiedByName = "stringToUUID")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "stringToUUID")
    @Mapping(target = "threadRootId", source = "threadRootId", qualifiedByName = "stringToUUIDNullable")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "timestampToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "timestampToInstant")
    Message toEntity(MessageOuterClass.Message protoMessage);

    @Mapping(target = "messageId", source = "messageId", qualifiedByName = "uuidToString")
    @Mapping(target = "conversationId", source = "conversationId", qualifiedByName = "uuidToString")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "threadRootId", source = "threadRootId", qualifiedByName = "uuidToStringNullable")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToTimestamp")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToTimestamp")
    MessageOuterClass.Message toProto(Message entity);

    @Named("stringToUUID")
    default UUID stringToUUID(String value) {
        return value != null && !value.isEmpty() ? UUID.fromString(value) : null;
    }

    @Named("stringToUUIDNullable")
    default UUID stringToUUIDNullable(String value) {
        return value != null && !value.isEmpty() ? UUID.fromString(value) : null;
    }

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : "";
    }

    @Named("uuidToStringNullable")
    default String uuidToStringNullable(UUID uuid) {
        return uuid != null ? uuid.toString() : "";
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
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}

package com.JRobusta.chat.core_services.mapper;

import com.JRobusta.chat.events.ConnectionEvent;
import com.google.protobuf.Timestamp;
import connection.v1.ConnectionManagerOuterClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface ConnectionMapper {

  @Mapping(target = "connectedAt", source = "connectedAt", qualifiedByName = "timestampToInstant")
  @Mapping(target = "lastPingAt", source = "lastPingAt", qualifiedByName = "timestampToInstant")
  @Mapping(target = "sessionState", source = "sessionState",
      qualifiedByName = "protoSessionStateToEventSessionState")
  ConnectionEvent toEvent(ConnectionManagerOuterClass.Connection protoConnection);

  @Mapping(target = "connectedAt", source = "connectedAt", qualifiedByName = "instantToTimestamp")
  @Mapping(target = "lastPingAt", source = "lastPingAt", qualifiedByName = "instantToTimestamp")
  @Mapping(target = "sessionState", source = "sessionState",
      qualifiedByName = "eventSessionStateToProtoSessionState")
  ConnectionManagerOuterClass.Connection toProto(ConnectionEvent event);

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
    return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano())
        .build();
  }

  @Named("protoSessionStateToEventSessionState")
  default ConnectionEvent.SessionState protoSessionStateToEventSessionState(
      connection.v1.ConnectionManagerOuterClass.SessionState protoState) {
    if (protoState == null) {
      return ConnectionEvent.SessionState.SESSION_STATE_UNKNOWN;
    }
    switch (protoState) {
      case CONNECTED:
        return ConnectionEvent.SessionState.CONNECTED;
      case DISCONNECTED:
        return ConnectionEvent.SessionState.DISCONNECTED;
      case EXPIRED:
        return ConnectionEvent.SessionState.EXPIRED;
      case SESSION_STATE_UNKNOWN:
      default:
        return ConnectionEvent.SessionState.SESSION_STATE_UNKNOWN;
    }
  }

  @Named("eventSessionStateToProtoSessionState")
  default connection.v1.ConnectionManagerOuterClass.SessionState eventSessionStateToProtoSessionState(
      ConnectionEvent.SessionState eventState) {
    if (eventState == null) {
      return connection.v1.ConnectionManagerOuterClass.SessionState.SESSION_STATE_UNKNOWN;
    }
    switch (eventState) {
      case CONNECTED:
        return connection.v1.ConnectionManagerOuterClass.SessionState.CONNECTED;
      case DISCONNECTED:
        return connection.v1.ConnectionManagerOuterClass.SessionState.DISCONNECTED;
      case EXPIRED:
        return connection.v1.ConnectionManagerOuterClass.SessionState.EXPIRED;
      case SESSION_STATE_UNKNOWN:
      default:
        return connection.v1.ConnectionManagerOuterClass.SessionState.SESSION_STATE_UNKNOWN;
    }
  }
}


package com.JRobusta.chat.core_services.message_module.grpc;


import com.JRobusta.chat.core_services.events.MessageEvent;
import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import com.JRobusta.chat.core_services.message_module.entities.Message;
import com.JRobusta.chat.core_services.message_module.entities.MessageProducerOutbox;
import com.JRobusta.chat.core_services.message_module.kafka.KafkaTopic;
import com.JRobusta.chat.core_services.message_module.mapper.MessageMapper;
import com.JRobusta.chat.core_services.message_module.repositories.ConversationSequenceRepository;
import com.JRobusta.chat.core_services.message_module.repositories.MessageProducerOutboxRepository;
import com.JRobusta.chat.core_services.message_module.repositories.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import message.v1.MessageOuterClass;
import message.v1.MessageServiceGrpc;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MessageService extends MessageServiceGrpc.MessageServiceImplBase {
    private final MessageMapper messageMapper;
    private final ConversationSequenceRepository conversationSequenceRepository;
    private final MessageRepository messageRepository;
    private  final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageProducerOutboxRepository messageProducerOutboxRepository;

    /*
        * Create a new message
        *
     */

    @Override
    public StreamObserver<MessageOuterClass.CreateMessageRequest> streamCreateMessages(StreamObserver<MessageOuterClass.CreateMessageResponse> responseObserver) {
        return new StreamObserver<MessageOuterClass.CreateMessageRequest>() {
            @Override
            public void onNext(MessageOuterClass.CreateMessageRequest request) {
                try {
                    Message messageEntity = messageMapper.toEntity(request.getMessage());

                    Long sequenceNumber = conversationSequenceRepository
                            .getSequenceNumber(messageEntity.getConversationId()) + 1;
                    Instant instant = Instant.now();


                    // Set additional fields
                    messageEntity.setMessageId(UUID.randomUUID());
                    messageEntity.setServerSeq(sequenceNumber);
                    messageEntity.setCreatedAt(instant);
                    messageEntity.setUpdatedAt(instant);
                    messageEntity.setDeleted(false);
                    messageEntity.setThreadRootId(null);
                    Message saveMessage = messageRepository.save(messageEntity);

                    // update sequence number
                    conversationSequenceRepository.updateSequenceNumber(
                            messageEntity.getConversationId(),
                            sequenceNumber
                    );

                    //build and send message event
                    MessageEvent event = MessageEvent.builder()
                            .messageId(saveMessage.getMessageId())
                            .conversationId(saveMessage.getConversationId())
                            .userId(saveMessage.getUserId())
                            .text(saveMessage.getText())
                            .serverSeq(saveMessage.getServerSeq())
                            .createdAt(saveMessage.getCreatedAt())
                            .updatedAt(saveMessage.getUpdatedAt())
                            .deleted(saveMessage.getDeleted())
                            .threadRootId(saveMessage.getThreadRootId())
                            .type(saveMessage.getType())
                            .build();
                    MessageProducerOutbox messageProducerOutbox = MessageProducerOutbox.builder()
                            .id(UUID.randomUUID())
                            .createdAt(Instant.now())
                            .topic(KafkaTopic.MESSAGE_ALL.getTopicName())
                            .payload(objectMapper.writeValueAsString(event))
                            .status(OutboxStatus.PENDING)
                            .conversationId(event.getConversationId())
                            .build();
                    messageProducerOutboxRepository.save(messageProducerOutbox);



                    MessageOuterClass.Message responseMessage = messageMapper.toProto(saveMessage);
                    MessageOuterClass.CreateMessageResponse response = MessageOuterClass.CreateMessageResponse
                            .newBuilder()
                            .setMessage(responseMessage)
                            .setSuccess(true)
                            .build();


                    responseObserver.onNext(response);
                } catch (Exception  e) {

                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }

    public boolean validateMessage(MessageOuterClass.Message message) {
        // Add validation logic here
        return true;
    }


}

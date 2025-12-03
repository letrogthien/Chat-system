package com.JRobusta.chat.socket_gateway.socket;

import com.JRobusta.chat.socket_gateway.dto.SocketMessageDTO;
import com.JRobusta.chat.socket_gateway.grpc.client.MessageGrpcClient;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import message.v1.MessageOuterClass;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.concurrent.ExecutorService;

@Controller
@RequiredArgsConstructor
public class SocketController {
    private final ExecutorService executor;
    private final MessageGrpcClient messageGrpcClient;

    @MessageMapping("/chat")
    public void receiveMessage(SocketMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        messageGrpcClient.send(
                MessageOuterClass.CreateMessageRequest.newBuilder()
                        .setMessage(toProto(message))
                        .setClientMsgId(message.getMessageId().toString())
                        .build()
        );
    }

//    @PostConstruct
//    public void startWorker() {
//        executor.submit(this::workerLoop);
//    }
//
//    private void workerLoop() {
//        while (!Thread.currentThread().isInterrupted()) {
//            try {
//                SocketMessageDTO message = messageQueue.take();
//                MessageOuterClass.Message protoMessage = toProto(message);
//                MessageOuterClass.CreateMessageRequest createMessageRequest = MessageOuterClass.CreateMessageRequest
//                        .newBuilder()
//                        .setMessage(protoMessage)
//                        .build();
//                MessageOuterClass.CreateMessageResponse createMessageResponse = messageGrpcClient.createMessage(createMessageRequest);
//                boolean isSuccess = createMessageResponse.getSuccess();
//                if (!isSuccess) {
//                    //dosthing
//                }
//
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//            catch (Exception ignored) {
//
//            }
//        }
//    }

    public MessageOuterClass.Message toProto(SocketMessageDTO dto) {
        MessageOuterClass.Message.Builder builder = MessageOuterClass.Message.newBuilder();

        // UUID → string
        if (dto.getMessageId() != null) {
            builder.setMessageId(dto.getMessageId().toString());
        }

        if (dto.getConversationId() != null) {
            builder.setConversationId(dto.getConversationId().toString());
        }

        if (dto.getUserId() != null) {
            builder.setUserId(dto.getUserId().toString());
        }

        if (dto.getServerSeq() != null) {
            builder.setServerSeq(dto.getServerSeq());
        }

        if (dto.getThreadRootId() != null) {
            builder.setThreadRootId(dto.getThreadRootId().toString());
        }

        if (dto.getText() != null) {
            builder.setText(dto.getText());
        }

        if (dto.getType() != null) {
            builder.setType(dto.getType());
        }

        // Instant → Protobuf Timestamp
        if (dto.getCreatedAt() != null) {
            builder.setCreatedAt(instantToTimestamp(dto.getCreatedAt()));
        }

        if (dto.getUpdatedAt() != null) {
            builder.setUpdatedAt(instantToTimestamp(dto.getUpdatedAt()));
        }

        if (dto.getEdited() != null) {
            builder.setEdited(dto.getEdited());
        }

        if (dto.getDeleted() != null) {
            builder.setDeleted(dto.getDeleted());
        }


        return builder.build();
    }

    private Timestamp instantToTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}

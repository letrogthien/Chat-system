package com.JRobusta.chat.socket_gateway.grpc.client;

import com.JRobusta.chat.socket_gateway.mapper.MessageMapper;
import com.JRobusta.chat.socket_gateway.socket.SocketSendMsgService;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import message.v1.MessageOuterClass;
import message.v1.MessageServiceGrpc;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageGrpcClient {
    private final MessageServiceGrpc.MessageServiceStub asyncStub;
    private StreamObserver<MessageOuterClass.CreateMessageRequest> requestStream;



    private StreamObserver<MessageOuterClass.CreateMessageRequest> createMessageRequestStreamObserver(
            StreamObserver<MessageOuterClass.CreateMessageResponse> responseObserver) {
        return asyncStub.streamCreateMessages(responseObserver);
    }


    @PostConstruct
    public void init() {
        StreamObserver<MessageOuterClass.CreateMessageResponse> responseObserver =
                new StreamObserver<MessageOuterClass.CreateMessageResponse>() {
                    @Override
                    public void onNext(MessageOuterClass.CreateMessageResponse createMessageResponse) {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // Handle errors
                    }

                    @Override
                    public void onCompleted() {

                    }
                };
        requestStream = this.createMessageRequestStreamObserver(responseObserver);
    }

    public void send(MessageOuterClass.CreateMessageRequest request) {
        requestStream.onNext(request);
    }
}

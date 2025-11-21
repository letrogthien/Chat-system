package com.JRobusta.chat.socket_gateway.grpc.client;

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
        createNewStream();
    }

    private void createNewStream() {
        StreamObserver<MessageOuterClass.CreateMessageResponse> responseObserver =
                new StreamObserver<MessageOuterClass.CreateMessageResponse>() {

                    @Override
                    public void onNext(MessageOuterClass.CreateMessageResponse response) {
                        // nhận message từ server nếu cần
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.err.println("gRPC stream error: " + throwable.getMessage());
                        reconnectWithBackoff();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("gRPC stream completed.");
                        reconnectWithBackoff();
                    }
                };

        this.requestStream = this.createMessageRequestStreamObserver(responseObserver);
        System.out.println("gRPC stream created.");
    }

    private void reconnectWithBackoff() {
        try {
            Thread.sleep(1000);  // backoff 1s
        } catch (InterruptedException ignored) {
        }

        System.out.println("Reconnecting gRPC stream...");
        createNewStream();
    }

    public synchronized void send(MessageOuterClass.CreateMessageRequest request) {
        try {
            requestStream.onNext(request);
        } catch (Exception e) {
            System.err.println("Send failed, reconnecting: " + e.getMessage());
            reconnectWithBackoff();
            requestStream.onNext(request);
        }
    }
}

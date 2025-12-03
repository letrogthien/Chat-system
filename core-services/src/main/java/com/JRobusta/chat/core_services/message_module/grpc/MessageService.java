package com.JRobusta.chat.core_services.message_module.grpc;


import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import message.v1.MessageOuterClass;
import message.v1.MessageServiceGrpc;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessageService extends MessageServiceGrpc.MessageServiceImplBase {
  private final MessageServiceHelper messageServiceHelper;

  /*
   * Create a new message
   *
   */

  @Override
  public StreamObserver<MessageOuterClass.CreateMessageRequest> streamCreateMessages(
      StreamObserver<MessageOuterClass.CreateMessageResponse> responseObserver) {
    return new StreamObserver<MessageOuterClass.CreateMessageRequest>() {
      @Override
      public void onNext(MessageOuterClass.CreateMessageRequest request) {
        try {
          MessageOuterClass.Message savedMessage =
              messageServiceHelper.handleMessage(request.getMessage());
          responseObserver.onNext(MessageOuterClass.CreateMessageResponse.newBuilder()
              .setMessage(savedMessage).setSuccess(true).build());
        } catch (Exception e) {
          responseObserver.onError(e);
        }
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("Error in gRPC CreateMessageRequest stream: " + throwable.getMessage());
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

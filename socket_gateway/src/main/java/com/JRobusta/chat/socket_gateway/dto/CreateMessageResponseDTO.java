package com.JRobusta.chat.socket_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMessageResponseDTO {

    private SocketMessageDTO message;

    private AckOffsetDTO ackOffset;

    private Boolean success;
}

package com.JRobusta.chat.core_services.message_module.services;

import com.JRobusta.chat.core_services.auth_module.response.ApiResponse;
import com.JRobusta.chat.core_services.message_module.dtos.ConversationDto;
import com.JRobusta.chat.core_services.message_module.repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;

}

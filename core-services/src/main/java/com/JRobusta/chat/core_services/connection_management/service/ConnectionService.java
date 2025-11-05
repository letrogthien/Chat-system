package com.JRobusta.chat.core_services.connection_management.service;


import com.JRobusta.chat.core_services.connection_management.repositories.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    private  final ConnectionRepository connectionRepository;




}

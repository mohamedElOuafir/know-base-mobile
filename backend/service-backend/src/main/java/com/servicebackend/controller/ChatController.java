package com.servicebackend.controller;


import com.servicebackend.dto.ChatDto;
import com.servicebackend.dto.MessageDto;
import com.servicebackend.service.ChatService;
import com.servicebackend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private ChatService chatService;

    @GetMapping("/{chatId}/messages")
    public List<MessageDto> getMessages(@PathVariable Long chatId){
        return messageService.getMessages(chatId);
    }

    @PostMapping("/{idCollection}")
    public ResponseEntity<ChatDto> createChat(
            Authentication authentication,
            @PathVariable Long idCollection,
            @Param("chatName") String chatName
    ) {
        String email = authentication.getName();
        ChatDto chat = chatService.createChat(email, idCollection, chatName);
        return ResponseEntity.ok(chat);
    }
}

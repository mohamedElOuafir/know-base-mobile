package com.servicebackend.controller;


import com.servicebackend.dto.MessageDto;
import com.servicebackend.dto.MessageRequestDto;
import com.servicebackend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDto> saveMessage(
            Authentication authentication,
            @RequestBody MessageRequestDto messageRequestDto) {

        String email = authentication.getName();
        MessageDto messageDto = messageService.addMessage(email, messageRequestDto);

        return ResponseEntity.ok().body(messageDto);
    }

}

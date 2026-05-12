package com.servicebackend.service;


import com.servicebackend.dto.ChatDto;
import com.servicebackend.dto.MessageDto;
import com.servicebackend.dto.MessageRequestDto;
import com.servicebackend.entity.Chat;
import com.servicebackend.entity.Message;
import com.servicebackend.repository.ChatRepository;
import com.servicebackend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private RagService ragService;


    public MessageDto addMessage(String email, MessageRequestDto messageRequestDto) {

        Chat chat = chatRepository.findById(messageRequestDto.getIdChat())
                .orElseThrow(() -> new RuntimeException("Chat not found"));


        if (!chat.getUserCollection().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        // Save user message
        Message userMessage = new Message();
        userMessage.setContent(messageRequestDto.getContent());
        userMessage.setSenderRole("user");
        userMessage.setCreatedAt(new Date());
        userMessage.setChat(chat);
        messageRepository.save(userMessage);


        String botResponse = ragService.generate(messageRequestDto.getContent());

        // Save bot response
        Message botMessage = new Message();
        botMessage.setContent(botResponse);
        botMessage.setSenderRole("bot");
        botMessage.setCreatedAt(new Date());
        botMessage.setChat(chat);
        messageRepository.save(botMessage);

        // Return bot message as Dto
        MessageDto dto = new MessageDto();
        dto.setIdMessage(botMessage.getIdMessage());
        dto.setContent(botMessage.getContent());
        dto.setSenderRole(botMessage.getSenderRole());
        dto.setCreatedAt(botMessage.getCreatedAt());

        return dto;
    }

    public List<MessageDto> getMessages(Long chatId){

        return messageRepository.findByChatIdChatOrderByCreatedAtAsc(chatId)
                .stream()
                .map(message -> {

                    MessageDto dto = new MessageDto();

                    dto.setIdMessage(message.getIdMessage());

                    dto.setContent(message.getContent());

                    dto.setCreatedAt(message.getCreatedAt());

                    dto.setSenderRole(message.getSenderRole());

                    return dto;

                }).toList();
    }

}

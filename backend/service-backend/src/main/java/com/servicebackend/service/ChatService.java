package com.servicebackend.service;

import com.servicebackend.dto.ChatDto;
import com.servicebackend.entity.Chat;
import com.servicebackend.entity.UserCollection;
import com.servicebackend.repository.ChatRepository;
import com.servicebackend.repository.UserCollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;


@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserCollectionRepository collectionRepository;

    public ChatDto createChat(String email, Long idCollection, String chatName) {
        UserCollection collection = collectionRepository.findById(idCollection)
                .orElseThrow(() -> new RuntimeException("Collection not found"));


        if (!collection.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        Chat chat = new Chat();
        chat.setChatName(chatName);
        chat.setCreatedAt(new Date());
        chat.setUserCollection(collection);

        Chat saved = chatRepository.save(chat);

        ChatDto dto = new ChatDto();
        dto.setIdChat(saved.getIdChat());
        dto.setChatName(saved.getChatName());
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setMessages(new ArrayList<>());

        return dto;
    }


}

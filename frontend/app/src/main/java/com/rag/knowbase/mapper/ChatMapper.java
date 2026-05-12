package com.rag.knowbase.mapper;

import com.rag.knowbase.data.dto.ChatDto;
import com.rag.knowbase.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatMapper {

    public static List<Chat> convertDtoToChatListModel(List<ChatDto> chatsDto) {
        List<Chat> chatList = new ArrayList<>();

        for(ChatDto chatDto: chatsDto){
            Chat chat = new Chat();

            chat.setIdChat(chatDto.getIdChat());
            chat.setName(chatDto.getChatName());
            chat.setCreateAt(chatDto.getCreatedAt());
            chat.setMessages(MessageMapper.convertDtoToMessageListModel(chatDto.getMessages()));

            chatList.add(chat);
        }

        return chatList;
    }


    public static Chat convertDtoToChatModel(ChatDto chatDto) {

        Chat chat = new Chat();

        chat.setIdChat(chatDto.getIdChat());
        chat.setName(chatDto.getChatName());
        chat.setCreateAt(chatDto.getCreatedAt());
        chat.setMessages(MessageMapper.convertDtoToMessageListModel(chatDto.getMessages()));

        return chat;
    }

}

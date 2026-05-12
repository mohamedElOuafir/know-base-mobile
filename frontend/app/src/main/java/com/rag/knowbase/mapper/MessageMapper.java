package com.rag.knowbase.mapper;

import com.rag.knowbase.data.dto.MessageDto;
import com.rag.knowbase.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageMapper {

    public static List<Message> convertDtoToMessageListModel(List<MessageDto> messagesDto) {
        List<Message> messagesList = new ArrayList<>();

        for(MessageDto messageDto : messagesDto){
            Message message = new Message();

            message.setIdMessage(messageDto.getIdMessage());
            message.setContent(messageDto.getContent());
            message.setCreatedAt(messageDto.getCreatedAt());
            message.setSenderRole(messageDto.getSenderRole());

            messagesList.add(message);
        }

        return messagesList;
    }


    public static Message convertDtoToMessageModel(MessageDto messageDto) {

        Message message = new Message();

        message.setIdMessage(messageDto.getIdMessage());
        message.setContent(messageDto.getContent());
        message.setCreatedAt(messageDto.getCreatedAt());
        message.setSenderRole(messageDto.getSenderRole());


        return message;
    }
}

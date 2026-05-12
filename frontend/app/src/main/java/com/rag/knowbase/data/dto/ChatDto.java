package com.rag.knowbase.data.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ChatDto implements Serializable {
    private Long idChat;
    private String chatName;
    private Date createdAt;
    private List<MessageDto> messages;

    public ChatDto(Long idChat, String chatName, Date createdAt, List<MessageDto> messages) {
        this.idChat = idChat;
        this.chatName = chatName;
        this.createdAt = createdAt;
        this.messages = messages;
    }


    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }
}
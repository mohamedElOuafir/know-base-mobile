package com.rag.knowbase.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private Long idMessage;
    private String content;
    private Date createdAt;
    private String senderRole;
    private Chat chat;

    public Message(){}
    public Message(Long idMessage, String content, Date createdAt, String senderRole, Chat chat) {
        this.idMessage = idMessage;
        this.content = content;
        this.createdAt = createdAt;
        this.senderRole = senderRole;
        this.chat = chat;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}

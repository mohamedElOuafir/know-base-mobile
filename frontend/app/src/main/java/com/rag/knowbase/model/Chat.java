package com.rag.knowbase.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Chat implements Serializable {

    private Long idChat;
    private String name;
    private Date createAt;
    private List<Message> messages;

    public Chat(){}
    public Chat(Long idChat, Date createAt, String name, List<Message> messages) {
        this.idChat = idChat;
        this.createAt = createAt;
        this.name = name;
        this.messages = messages;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }


    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}


package com.rag.knowbase.model;

public class Chat {
    private String message;
    private Boolean isUser;

    public Chat(String message, Boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getIsUser() {
        return isUser;
    }
}


package com.rag.knowbase.data.dto;

public class MessageRequestDto {

    private Long idChat;
    private String content;



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public MessageRequestDto(){}

    public MessageRequestDto(Long idChat, String content) {
        this.idChat = idChat;
        this.content = content;

    }
}

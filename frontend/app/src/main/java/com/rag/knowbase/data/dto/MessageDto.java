package com.rag.knowbase.data.dto;

import java.io.Serializable;
import java.util.Date;

public class MessageDto implements Serializable {
    private Long idMessage;
    private String content;
    private Date createdAt;
    private String senderRole;

    public MessageDto(Long idMessage, String content, Date createdAt, String senderRole) {
        this.idMessage = idMessage;
        this.content = content;
        this.createdAt = createdAt;
        this.senderRole = senderRole;
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
}

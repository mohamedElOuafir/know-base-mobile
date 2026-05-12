package com.rag.knowbase.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserCollection implements Serializable {

    private Long idCollection;
    private String nameCollection;
    private Date createdAt;
    private String description;
    private List<FileUploaded> fileUploadeds;
    private List<Chat> chats;

    public UserCollection(){}
    public UserCollection(Long idCollection, String nameCollection, Date createdAt, String description, List<FileUploaded> fileUploadeds, List<Chat> chats) {
        this.idCollection = idCollection;
        this.nameCollection = nameCollection;
        this.createdAt = createdAt;
        this.description = description;
        this.fileUploadeds = fileUploadeds;
        this.chats = chats;
    }


    public Long getIdCollection() {
        return idCollection;
    }

    public void setIdCollection(Long idCollection) {
        this.idCollection = idCollection;
    }

    public String getNameCollection() {
        return nameCollection;
    }

    public void setNameCollection(String nameCollection) {
        this.nameCollection = nameCollection;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FileUploaded> getFileUploadeds() {
        return fileUploadeds;
    }

    public void setFileUploadeds(List<FileUploaded> fileUploadeds) {
        this.fileUploadeds = fileUploadeds;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }
}

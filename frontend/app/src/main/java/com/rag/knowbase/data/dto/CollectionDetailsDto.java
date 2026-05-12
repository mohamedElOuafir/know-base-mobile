package com.rag.knowbase.data.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CollectionDetailsDto implements Serializable {
    private Long idCollection;
    private String title;
    private String description;
    private Date createdAt;
    private Integer totalChats;
    private Integer totalFiles;
    private List<ChatDto> chats;
    private List<FileUploadedDto> files;

    public CollectionDetailsDto(Long idCollection, String title, String description, Date createdAt, Integer totalChats, Integer totalFiles, List<ChatDto> chats, List<FileUploadedDto> files) {
        this.idCollection = idCollection;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.totalChats = totalChats;
        this.totalFiles = totalFiles;
        this.chats = chats;
        this.files = files;
    }

    public Long getIdCollection() {
        return idCollection;
    }

    public void setIdCollection(Long idCollection) {
        this.idCollection = idCollection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalChats() {
        return totalChats;
    }

    public void setTotalChats(Integer totalChats) {
        this.totalChats = totalChats;
    }

    public Integer getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Integer totalFiles) {
        this.totalFiles = totalFiles;
    }

    public List<ChatDto> getChats() {
        return chats;
    }

    public void setChats(List<ChatDto> chats) {
        this.chats = chats;
    }

    public List<FileUploadedDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileUploadedDto> files) {
        this.files = files;
    }
}

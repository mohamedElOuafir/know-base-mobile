package com.rag.knowbase.data.dto;

import java.io.Serializable;
import java.util.Date;

public class FileUploadedDto implements Serializable {
    private String path;
    private String type;
    private Long idFileUploaded;
    private String fileName;
    private Long size;
    private Date uploadedAt;

    public FileUploadedDto(String path, String type, String fileName, Long idFileUploaded, Long size, Date uploadedAt) {
        this.path = path;
        this.type = type;
        this.fileName = fileName;
        this.idFileUploaded = idFileUploaded;
        this.size = size;
        this.uploadedAt = uploadedAt;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getIdFileUploaded() {
        return idFileUploaded;
    }

    public void setIdFileUploaded(Long idFileUploaded) {
        this.idFileUploaded = idFileUploaded;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
package com.rag.knowbase.model;

import java.io.Serializable;
import java.util.Date;

public class FileUploaded implements Serializable {

    private Long idFileUploaded;
    private String fileName;
    private String type;
    private String path;
    private Long size;
    private Date uploadedAt;
    private Date deletedAt ;


    public FileUploaded(Long idFileUploaded, String fileName, String type, String path, Long size, Date uploadedAt, Date deletedAt) {
        this.idFileUploaded = idFileUploaded;
        this.fileName = fileName;
        this.type = type;
        this.path = path;
        this.size = size;
        this.uploadedAt = uploadedAt;
        this.deletedAt = deletedAt;
    }

    public FileUploaded(){}

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }


}

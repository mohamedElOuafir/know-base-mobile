package com.servicebackend.exceptions;



public class FileNotUploadedException extends RuntimeException{
    public FileNotUploadedException(String message) {
        super(message);
    }
}

package com.rag.knowbase.mapper;

import com.rag.knowbase.data.dto.FileUploadedDto;
import com.rag.knowbase.model.FileUploaded;

import java.util.ArrayList;
import java.util.List;

public class FileUploadedMapper {

    public static List<FileUploaded> convertDtoToFileUploadedListModel(List<FileUploadedDto> filesDto) {
        List<FileUploaded> filesList = new ArrayList<>();

        for(FileUploadedDto fileDto: filesDto){
            FileUploaded file = new FileUploaded();

            file.setIdFileUploaded(fileDto.getIdFileUploaded());
            file.setFileName(fileDto.getFileName());
            file.setUploadedAt(fileDto.getUploadedAt());
            file.setPath(fileDto.getPath());
            file.setSize(fileDto.getSize());
            file.setType(fileDto.getType());

            filesList.add(file);
        }

        return filesList;
    }


    public static FileUploaded convertDtoToFileUploadedModel(FileUploadedDto fileDto) {

        FileUploaded file = new FileUploaded();

        file.setIdFileUploaded(fileDto.getIdFileUploaded());
        file.setFileName(fileDto.getFileName());
        file.setUploadedAt(fileDto.getUploadedAt());
        file.setPath(fileDto.getPath());
        file.setSize(fileDto.getSize());
        file.setType(fileDto.getType());

        return file;
    }
}

package com.servicebackend.service;


import com.servicebackend.dto.FileUploadedDto;
import com.servicebackend.entity.FileUploaded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, FileUploadedDto> kafkaTemplateFileUploaded;

    public void sendUploadedFilesListToKafka(List<FileUploaded> listFileUploaded){
        for(FileUploaded file : listFileUploaded){
            FileUploadedDto fileUploadedDto =  new FileUploadedDto();

            fileUploadedDto.setPath(file.getPath());
            fileUploadedDto.setType(file.getType());
            fileUploadedDto.setIdFileUploaded(file.getIdFileUploaded());

            kafkaTemplateFileUploaded.send("file-uploaded-topic", fileUploadedDto);
        }

    }

    public void sendUploadedFileToKafka(FileUploaded file){

            FileUploadedDto fileUploadedDto =  new FileUploadedDto();

            fileUploadedDto.setPath(file.getPath());
            fileUploadedDto.setType(file.getType());
            fileUploadedDto.setIdFileUploaded(file.getIdFileUploaded());

            kafkaTemplateFileUploaded.send("file-uploaded-topic", fileUploadedDto);

    }
}

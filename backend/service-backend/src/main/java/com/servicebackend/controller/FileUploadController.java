package com.servicebackend.controller;

import com.servicebackend.dto.FileUploadedDto;
import com.servicebackend.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/file-uploaded")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/{idCollection}")
    public ResponseEntity<FileUploadedDto> upload(
            Authentication authentication,
            @PathVariable("idCollection") Long idCollection,
            @RequestParam("file") MultipartFile file
    ) {

        String email = authentication.getName();
        FileUploadedDto fileUploadedDto = fileUploadService.uploadFileByCollection(email, idCollection, file);
        return ResponseEntity.ok(fileUploadedDto);

    }


}

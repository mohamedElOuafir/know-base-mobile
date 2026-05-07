package com.servicebackend.controller;

import com.servicebackend.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin("*")
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();

        String fileUrl = fileUploadService.uploadToSupabase(file,fileName);

        return "";
    }


}

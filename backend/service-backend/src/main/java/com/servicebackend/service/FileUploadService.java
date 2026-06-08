package com.servicebackend.service;

import com.servicebackend.dto.FileUploadedDto;
import com.servicebackend.entity.FileUploaded;
import com.servicebackend.entity.UserCollection;
import com.servicebackend.exceptions.FileNotUploadedException;
import com.servicebackend.repository.FileUploadedRepository;
import com.servicebackend.repository.UserCollectionRepository;
import com.servicebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.*;
import java.util.Date;
import java.util.UUID;


@Service
public class FileUploadService {



    @Value("${supabase.url}")
    private String base_url;
    @Value("${supabase.api-key}")
    private String apiKey;


    @Autowired
    private UserCollectionRepository userCollectionRepository;
    @Autowired
    private FileUploadedRepository fileUploadedRepository;
    @Autowired
    private KafkaService kafkaService;


    public String uploadTextFileToSupabase(MultipartFile file) throws FileNotUploadedException {


        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        String url = base_url + "/uploaded-text-files/" + fileName;

        HttpClient client = HttpClient.newHttpClient();

        try {

            String contentType = file.getContentType();
            if (!isAllowedContentTypeTextFile(contentType)) {
                throw new FileNotUploadedException(
                        "Type de fichier non supporté. Utilisez PDF, DOC ou DOCX"
                );
            }


            if (file.getSize() > 20 * 1024 * 1024) {
                throw new FileNotUploadedException(
                        "Fichier trop volumineux. Maximum 20 MB"
                );
            }


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", contentType)
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200 || response.statusCode() == 201) {

                return url;
            } else {

                throw new FileNotUploadedException(
                        String.format("Upload échoué (HTTP %d): %s",
                                response.statusCode(), response.body())
                );
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FileNotUploadedException("Erreur réseau: " + e.getMessage());
        }
    }


    private boolean isAllowedContentTypeTextFile(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }





    public String uploadProfileImageToSupabase(MultipartFile file) throws FileNotUploadedException {


        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        String url = base_url + "/users-profile-images/" + fileName;

        HttpClient client = HttpClient.newHttpClient();

        try {

            String contentType = file.getContentType();
            if (!isAllowedContentTypeProfileImage(contentType)) {
                throw new FileNotUploadedException(
                        "Type de fichier non supporté. Utilisez PDF, DOC ou DOCX"
                );
            }


            if (file.getSize() > 20 * 1024 * 1024) {
                throw new FileNotUploadedException(
                        "Fichier trop volumineux. Maximum 20 MB"
                );
            }


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", contentType)
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200 || response.statusCode() == 201) {

                return url;
            } else {

                throw new FileNotUploadedException(
                        String.format("Upload échoué (HTTP %d): %s",
                                response.statusCode(), response.body())
                );
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FileNotUploadedException("Erreur réseau: " + e.getMessage());
        }
    }


    private boolean isAllowedContentTypeProfileImage(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/webp")
        );
    }


    public FileUploadedDto uploadFileByCollection(String email, Long idCollection, MultipartFile file){
        UserCollection collection = userCollectionRepository.findById(idCollection)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        if(!collection.getUser().getEmail().equals(email)){
            throw new RuntimeException("Unauthorized");
        }

        String path = uploadTextFileToSupabase(file);
        FileUploaded fileUploaded = new FileUploaded();
        fileUploaded.setPath(path);
        fileUploaded.setFileName(file.getOriginalFilename());
        fileUploaded.setSize(file.getSize());
        fileUploaded.setType(file.getContentType());
        fileUploaded.setUploadedAt(new Date());
        fileUploaded.setCollection(collection);

        FileUploaded fileUploadedSaved = fileUploadedRepository.save(fileUploaded);
        kafkaService.sendUploadedFileToKafka(fileUploadedSaved);

        FileUploadedDto fileUploadedDto = new FileUploadedDto();
        fileUploadedDto.setIdFileUploaded(fileUploadedSaved.getIdFileUploaded());
        fileUploadedDto.setUploadedAt(fileUploadedSaved.getUploadedAt());
        fileUploadedDto.setFileName(fileUploadedSaved.getFileName());
        fileUploadedDto.setPath(fileUploadedSaved.getPath());
        fileUploadedDto.setType(fileUploadedSaved.getType());
        fileUploadedDto.setSize(fileUploadedSaved.getSize());



        return fileUploadedDto;

    }
}

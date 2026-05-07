package com.servicebackend.service;

import com.servicebackend.exceptions.FileNotUploadedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.*;


@Service
public class FileUploadService {
    public String uploadToSupabase(MultipartFile file, String fileName)  {

        String url = System.getenv("SUPABASE_BUCKET_URL") + fileName;
        String apiKey = System.getenv("SUPABASE_API_KEY");

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", file.getContentType())
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }catch (IOException | InterruptedException e) {
            throw new FileNotUploadedException(" Error: File Not Uploaded");
        }


        return url;
    }

    public void insertFileUploaded() {}
}

package com.servicebackend.service;

import com.servicebackend.dto.CollectionDto;
import com.servicebackend.dto.FileUploadedDto;
import com.servicebackend.entity.FileUploaded;
import com.servicebackend.entity.User;
import com.servicebackend.entity.UserCollection;
import com.servicebackend.repository.UserCollectionRepository;
import com.servicebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CollectionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCollectionRepository userCollectionRepository;
    @Autowired
    private FileUploadService fileUploadService;



    @Autowired
    private KafkaTemplate<String, FileUploadedDto> kafkaTemplateFileUploaded;


    public void createUserCollection(String email, CollectionDto collection) {
        User user =  userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        List<FileUploaded> listFileUploaded  = new ArrayList<>();

        for(MultipartFile file : collection.getFiles()){

            String path = fileUploadService.uploadToSupabase(file,  file.getOriginalFilename());
            String name = file.getOriginalFilename();
            Long size = file.getSize();
            String type = file.getContentType();

            FileUploaded fileUploaded = new FileUploaded();
            fileUploaded.setFileName(name);
            fileUploaded.setSize(size);
            fileUploaded.setType(type);
            fileUploaded.setPath(path);
            listFileUploaded.add(fileUploaded);

        }
        UserCollection userCollection = new UserCollection();
        userCollection.setUser(user);
        userCollection.setNameCollection(collection.getNameCollection());
        userCollection.setDescription(collection.getDescription());
        userCollection.setCreatedAt(new Date());
        userCollection.setFileUploadeds(listFileUploaded);

        UserCollection collectionSaved = userCollectionRepository.save(userCollection);

        sendUploadedFilesToKafka(collectionSaved.getFileUploadeds());
    }

    public void sendUploadedFilesToKafka(List<FileUploaded> listFileUploaded){
        for(FileUploaded file : listFileUploaded){
            FileUploadedDto fileUploadedDto =  new FileUploadedDto(
                    file.getPath(),
                    file.getType(),
                    file.getIdFileUploaded()
            );
            kafkaTemplateFileUploaded.send("file-uploaded-topic", fileUploadedDto);
        }

    }

    public List<UserCollection> userCollectionlist(Long idUser){
        return userCollectionRepository.findByUserIdUser(idUser);
    }



    public List<UserCollection> getCollectionsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCollectionRepository.findByUserIdUser(user.getIdUser());
    }

    public void deleteUserCollection(Long id){
        userCollectionRepository.deleteById(id);
    }

}

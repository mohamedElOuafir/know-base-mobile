package com.servicebackend.service;


import com.servicebackend.dto.*;
import com.servicebackend.entity.*;
import com.servicebackend.repository.ChatRepository;
import com.servicebackend.repository.UserCollectionRepository;
import com.servicebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCollectionRepository userCollectionRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private KafkaService kafkaService;



    public CollectionDetailsDto createUserCollection(String email, CollectionRequestDto collection) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserCollection userCollection = new UserCollection();
        userCollection.setUser(user);
        userCollection.setNameCollection(collection.getNameCollection());
        userCollection.setDescription(collection.getDescription());
        userCollection.setCreatedAt(new Date());

        // Files
        List<FileUploaded> listFileUploaded = new ArrayList<>();
        if (collection.getFiles() != null) {
            for (MultipartFile file : collection.getFiles()) {
                String path = fileUploadService.uploadTextFileToSupabase(file);
                FileUploaded fileUploaded = new FileUploaded();
                fileUploaded.setFileName(file.getOriginalFilename());
                fileUploaded.setSize(file.getSize());
                fileUploaded.setType(file.getContentType());
                fileUploaded.setPath(path);
                fileUploaded.setUploadedAt(new Date());
                fileUploaded.setCollection(userCollection);
                listFileUploaded.add(fileUploaded);
            }
        }
        userCollection.setFileUploadeds(listFileUploaded);

        UserCollection collectionSaved = userCollectionRepository.save(userCollection);

        if (!listFileUploaded.isEmpty()) {
            kafkaService.sendUploadedFilesListToKafka(collectionSaved.getFileUploadeds());
        }

        // First chat
        Chat chat = new Chat();
        chat.setCreatedAt(new Date());
        chat.setChatName(collection.getChatName());
        chat.setUserCollection(collectionSaved);
        Chat chatSaved = chatRepository.save(chat);

        // Build ChatDto
        ChatDto chatDto = new ChatDto();
        chatDto.setIdChat(chatSaved.getIdChat());
        chatDto.setChatName(chatSaved.getChatName());
        chatDto.setCreatedAt(chatSaved.getCreatedAt());
        chatDto.setMessages(new ArrayList<>());

        // Build FileUploadedDto list
        List<FileUploadedDto> fileUploadedDtos = listFileUploaded.stream().map(f -> {
            FileUploadedDto dto = new FileUploadedDto();
            dto.setIdFileUploaded(f.getIdFileUploaded());
            dto.setFileName(f.getFileName());
            dto.setSize(f.getSize());
            dto.setType(f.getType());
            dto.setPath(f.getPath());
            dto.setUploadedAt(f.getUploadedAt());
            return dto;
        }).collect(Collectors.toList());

        // Build CollectionDetailsDto
        CollectionDetailsDto collectionDetailsDto = new CollectionDetailsDto();
        collectionDetailsDto.setIdCollection(collectionSaved.getIdCollection());
        collectionDetailsDto.setTitle(collectionSaved.getNameCollection());
        collectionDetailsDto.setDescription(collectionSaved.getDescription());
        collectionDetailsDto.setCreatedAt(collectionSaved.getCreatedAt());
        collectionDetailsDto.setChats(List.of(chatDto));
        collectionDetailsDto.setFiles(fileUploadedDtos);

        return collectionDetailsDto;
    }





    public List<UserCollection> userCollectionlist(Long idUser){
        return userCollectionRepository.findByUserIdUser(idUser);
    }

    public List<CollectionDetailsDto> getUserCollections(Long userId) {

        List<UserCollection> collections = userCollectionRepository.findByUserIdUser(userId);

        List<CollectionDetailsDto> listCollectionDetails = new ArrayList<>();
        for (UserCollection userCollection : collections){
            CollectionDetailsDto collectionDetailsDto = new CollectionDetailsDto();
            collectionDetailsDto.setIdCollection(userCollection.getIdCollection());
            collectionDetailsDto.setTitle(userCollection.getNameCollection());
            collectionDetailsDto.setDescription(userCollection.getDescription());
            collectionDetailsDto.setCreatedAt(userCollection.getCreatedAt());
            collectionDetailsDto.setTotalChats(userCollection.getChats().size());
            collectionDetailsDto.setTotalFiles(userCollection.getFileUploadeds().size());
            collectionDetailsDto.setChats(getChatsInfo(userCollection));
            collectionDetailsDto.setFiles(getFilesInfo(userCollection));
        }

        return listCollectionDetails;
    }

    public List<ChatDto> getChatsInfo(UserCollection userCollection){
        List<ChatDto> chats = new ArrayList<>();
        for(Chat chat : userCollection.getChats()){
            chats.add(
                    new ChatDto(
                            chat.getIdChat(),
                            chat.getChatName(),
                            chat.getCreatedAt(),
                            getMessageInfo(chat)
                    ));
        }

        return chats;
    }


    public List<MessageDto> getMessageInfo(Chat chat){
        List<MessageDto> messages = new ArrayList<>();
        for(Message message : chat.getMessages()){
            messages.add(
                    new MessageDto(
                            message.getIdMessage(),
                            message.getContent(),
                            message.getCreatedAt(),
                            message.getSenderRole()
                    ));
        }

        return messages;
    }


    public List<FileUploadedDto> getFilesInfo(UserCollection userCollection){
        List<FileUploadedDto> files = new ArrayList<>();
        for(FileUploaded fileUploaded : userCollection.getFileUploadeds()){
            files.add(
                    new FileUploadedDto(
                            fileUploaded.getPath(),
                            fileUploaded.getType(),
                            fileUploaded.getIdFileUploaded(),
                            fileUploaded.getFileName(),
                            fileUploaded.getSize(),
                            fileUploaded.getUploadedAt()
                    ));
        }

        return files;
    }

    public List<CollectionDetailsDto> getCollectionsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CollectionDetailsDto> collections = new ArrayList<>();
        List<UserCollection> userCollectionList = userCollectionRepository.findByUserIdUser(user.getIdUser());


        for(UserCollection userCollection : userCollectionList){
            collections.add(
                    new CollectionDetailsDto(
                            userCollection.getIdCollection(),
                            userCollection.getNameCollection(),
                            userCollection.getDescription(),
                            userCollection.getCreatedAt(),
                            userCollection.getChats().size(),
                            userCollection.getFileUploadeds().size(),
                            getChatsInfo(userCollection),
                            getFilesInfo(userCollection)
                    ));
        }

        return collections;
    }

    public void deleteUserCollection(Long id){
        userCollectionRepository.deleteById(id);
    }

}

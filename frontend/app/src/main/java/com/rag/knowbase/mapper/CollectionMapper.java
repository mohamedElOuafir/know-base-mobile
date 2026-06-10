package com.rag.knowbase.mapper;

import com.rag.knowbase.data.dto.ChatDto;
import com.rag.knowbase.data.dto.CollectionDetailsDto;
import com.rag.knowbase.data.dto.FileUploadedDto;
import com.rag.knowbase.data.dto.MessageDto;
import com.rag.knowbase.model.Chat;
import com.rag.knowbase.model.FileUploaded;
import com.rag.knowbase.model.Message;
import com.rag.knowbase.model.UserCollection;

import java.util.ArrayList;
import java.util.List;

public class CollectionMapper {
    public static List<UserCollection> convertDtoToCollectionListModel(List<CollectionDetailsDto> collectionDetailsDtoList){
        List<UserCollection> collectionList = new ArrayList<>();

        for(CollectionDetailsDto collectionDto: collectionDetailsDtoList){
            UserCollection userCollection = new UserCollection();

            userCollection.setIdCollection(collectionDto.getIdCollection());
            userCollection.setNameCollection(collectionDto.getTitle());
            userCollection.setDescription(collectionDto.getDescription());
            userCollection.setCreatedAt(collectionDto.getCreatedAt());
            userCollection.setChats(ChatMapper.convertDtoToChatListModel(collectionDto.getChats()));
            userCollection.setFileUploadeds(FileUploadedMapper.convertDtoToFileUploadedListModel(collectionDto.getFiles()));

            collectionList.add(userCollection);
        }

        return collectionList;
    }


    public static UserCollection convertDtoToCollectionModel(CollectionDetailsDto collectionDto){

        UserCollection userCollection = new UserCollection();

        userCollection.setIdCollection(collectionDto.getIdCollection());
        userCollection.setNameCollection(collectionDto.getTitle());
        userCollection.setDescription(collectionDto.getDescription());
        userCollection.setCreatedAt(collectionDto.getCreatedAt());
        userCollection.setChats(ChatMapper.convertDtoToChatListModel(collectionDto.getChats()));
        userCollection.setFileUploadeds(FileUploadedMapper.convertDtoToFileUploadedListModel(collectionDto.getFiles()));

        return userCollection;
    }


}

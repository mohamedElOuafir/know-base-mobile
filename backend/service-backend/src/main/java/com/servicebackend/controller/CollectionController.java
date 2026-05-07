package com.servicebackend.controller;



import com.servicebackend.dto.CollectionDto;
import com.servicebackend.entity.UserCollection;
import com.servicebackend.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/collections")
@CrossOrigin(origins = "*")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @PostMapping
    public ResponseEntity<?> addCollection(Authentication authentication,
                                           @ModelAttribute CollectionDto collection){

        String email = authentication.getName();

        collectionService.createUserCollection(email,collection);

        return  ResponseEntity.ok().body(Map.of("added",true));
    }

    @GetMapping
    public List<UserCollection> getAll(Authentication authentication) {

        String email = authentication.getName();

        return collectionService.getCollectionsByUser(email);
    }

}

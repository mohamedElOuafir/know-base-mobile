package com.servicebackend.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CollectionRequestDto {
    private String nameCollection ;
    private String description;
    private String chatName;
    private List<MultipartFile> files;
}

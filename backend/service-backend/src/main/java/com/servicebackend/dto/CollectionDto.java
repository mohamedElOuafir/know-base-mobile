package com.servicebackend.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDto {
    private String nameCollection ;
    private String description;
    private List<MultipartFile> files;

}

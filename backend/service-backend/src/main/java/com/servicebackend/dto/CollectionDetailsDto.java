package com.servicebackend.dto;

import lombok.*;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDetailsDto {
    private Long idCollection;
    private String title;
    private String description;
    private Date createdAt;
    private Integer totalChats;
    private Integer totalFiles;
    private List<ChatDto> chats;
    private List<FileUploadedDto> files;
}

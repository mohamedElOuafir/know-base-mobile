package com.servicebackend.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadedDto {
    private String path;
    private String type;
    private Long idFileUploaded;
}

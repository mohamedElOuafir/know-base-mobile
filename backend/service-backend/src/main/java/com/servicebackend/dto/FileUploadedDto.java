package com.servicebackend.dto;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadedDto {
    private String path;
    private String type;
    private Long idFileUploaded;
    private String fileName;
    private Long size;
    private Date uploadedAt;
}

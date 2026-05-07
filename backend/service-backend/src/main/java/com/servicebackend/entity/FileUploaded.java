package com.servicebackend.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class FileUploaded {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFileUploaded;

    private String fileName;
    private String type;

    private String path;

    private Long size;


    private Date uploadedAt;

    private Date deletedAt ;


    @ManyToOne
    @JoinColumn(name = "idCollection")
    private UserCollection collection;

    @OneToMany(mappedBy = "fileUploaded", cascade = CascadeType.ALL)
    private List<Chunk> chunks;
}

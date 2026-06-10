package com.servicebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class UserCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCollection;

    private String nameCollection;

    @Column(columnDefinition = "TEXT")
    private String description;


    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;

    @OneToMany(mappedBy = "collection",cascade = CascadeType.ALL)
    private List<FileUploaded> fileUploadeds;

    @OneToMany(mappedBy = "userCollection", cascade = CascadeType.ALL)
    private List<Chat> chats;
}

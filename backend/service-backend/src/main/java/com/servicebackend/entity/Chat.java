package com.servicebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idChat;

    private String chatName;

    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "idUserCollection")
    private UserCollection userCollection;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages;
}

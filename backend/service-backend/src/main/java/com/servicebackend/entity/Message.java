package com.servicebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMessage;

    private String content;
    private Date createdAt;

    private enum senderRole{
        user,
        boot
    };

    @ManyToOne
    @JoinColumn(name = "idChat")
    private Chat chat;
}

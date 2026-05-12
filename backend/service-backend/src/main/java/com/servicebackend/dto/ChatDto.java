package com.servicebackend.dto;

import lombok.*;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long idChat;
    private String chatName;
    private Date createdAt;
    private List<MessageDto> messages;
}

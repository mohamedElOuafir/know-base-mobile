package com.servicebackend.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {
    private Long idChat;
    private String content;
}

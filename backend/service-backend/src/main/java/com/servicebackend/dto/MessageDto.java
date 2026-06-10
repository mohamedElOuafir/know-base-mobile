package com.servicebackend.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long idMessage;
    private String content;
    private Date createdAt;
    private String senderRole;
}

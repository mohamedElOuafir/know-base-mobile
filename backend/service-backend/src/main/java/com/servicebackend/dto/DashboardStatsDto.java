package com.servicebackend.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private Long totalChats;
    private Long totalFiles;

}

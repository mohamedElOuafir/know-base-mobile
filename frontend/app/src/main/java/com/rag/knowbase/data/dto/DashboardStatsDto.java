package com.rag.knowbase.data.dto;

public class DashboardStatsDto {

    private Long totalChats;
    private Long totalFiles;


    public Long getTotalChats() {
        return totalChats;
    }

    public void setTotalChats(Long totalChats) {
        this.totalChats = totalChats;
    }

    public Long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Long totalFiles) {
        this.totalFiles = totalFiles;
    }

    public DashboardStatsDto(Long totalChats, Long totalFiles) {
        this.totalChats = totalChats;
        this.totalFiles = totalFiles;
    }
}
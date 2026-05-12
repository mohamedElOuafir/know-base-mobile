package com.servicebackend.service;

import com.servicebackend.dto.DashboardStatsDto;
import com.servicebackend.entity.User;
import com.servicebackend.repository.ChatRepository;
import com.servicebackend.repository.FileUploadedRepository;
import com.servicebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private FileUploadedRepository fileRepository;
    @Autowired
    private UserRepository userRepository;


    public DashboardStatsDto getStats(String email) {

        User user =  userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        DashboardStatsDto dto = new DashboardStatsDto();

        dto.setTotalChats(chatRepository.countByUserCollectionUserIdUser(user.getIdUser()));

        dto.setTotalFiles(fileRepository.countByCollectionUserIdUser(user.getIdUser()));

        return dto;
    }
}

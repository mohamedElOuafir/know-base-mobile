package com.servicebackend.controller;

import com.servicebackend.dto.DashboardStatsDto;
import com.servicebackend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;


    @GetMapping("/stats")
    public DashboardStatsDto getStats(Authentication authentication) {
        String email = authentication.getName();
        return dashboardService.getStats(email);
    }
}

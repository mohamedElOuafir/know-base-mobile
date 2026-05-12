package com.rag.knowbase.data.api;

import com.rag.knowbase.data.dto.DashboardStatsDto;
import com.rag.knowbase.data.dto.UserLoginRequest;
import com.rag.knowbase.data.dto.UserResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface DashboardApi {

    @GET("/dashboard/stats")
    Call<DashboardStatsDto> getStats(
            @Header("Authorization") String token
    );
}

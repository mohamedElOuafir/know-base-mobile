package com.rag.knowbase.data.api;

import com.rag.knowbase.data.dto.ChatDto;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApi {
    @POST("/chat/{idCollection}")
    Call<ChatDto> createChat(
            @Header("Authorization") String token,
            @Path("idCollection") Long idCollection,
            @Query("chatName") String chatName
    );
}

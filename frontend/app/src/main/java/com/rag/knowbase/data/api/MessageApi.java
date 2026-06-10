package com.rag.knowbase.data.api;

import com.rag.knowbase.data.dto.MessageDto;
import com.rag.knowbase.data.dto.MessageRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MessageApi {

    @POST("/messages")
    Call<MessageDto> sendMessage(
            @Header("Authorization") String token,
            @Body MessageRequestDto request
    );
}

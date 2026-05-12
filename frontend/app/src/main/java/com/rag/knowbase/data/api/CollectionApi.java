package com.rag.knowbase.data.api;

import com.rag.knowbase.data.dto.CollectionDetailsDto;
import com.rag.knowbase.data.dto.DashboardStatsDto;
import com.rag.knowbase.data.dto.FileUploadedDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CollectionApi {
    @GET("/collections")
    Call<List<CollectionDetailsDto>> getUserCollection(
            @Header("Authorization") String token
    );

    @Multipart
    @POST("/collections")
    Call<CollectionDetailsDto> addNewCollection(
            @Header("Authorization") String token,
            @Part("nameCollection") RequestBody name,
            @Part("description") RequestBody description,
            @Part("chatName") RequestBody chatName,
            @Part List<MultipartBody.Part> files
    );





}

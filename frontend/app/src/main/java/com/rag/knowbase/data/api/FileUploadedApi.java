package com.rag.knowbase.data.api;

import com.rag.knowbase.data.dto.FileUploadedDto;
import com.rag.knowbase.data.dto.UserResponseDto;


import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface FileUploadedApi {

    @Multipart
    @POST("/file-uploaded/{idCollection}")
    Call<FileUploadedDto> uploadFile(
            @Header("Authorization") String token,
            @Path("idCollection") Long idCollection,
            @Part MultipartBody.Part file
    );
}

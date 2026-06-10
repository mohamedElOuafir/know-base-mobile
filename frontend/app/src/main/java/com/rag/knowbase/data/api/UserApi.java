package com.rag.knowbase.data.api;

import com.rag.knowbase.data.dto.UserLoginRequest;
import com.rag.knowbase.data.dto.UserRegisterRequest;
import com.rag.knowbase.data.dto.UserResponseDto;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserApi {

    @Multipart
    @POST("/auth/register")
    Call<UserResponseDto> register(
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part profileImage
    );

    @POST("/auth/login")
    Call<UserResponseDto> login(
        @Body UserLoginRequest request
    );


    @Multipart
    @PUT("/auth/users/profile")
    Call<UserResponseDto> updateProfile(
            @Header("Authorization") String token,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part profileImage
    );
}

package com.rag.knowbase.data.retrofit;
import com.rag.knowbase.data.api.ChatApi;
import com.rag.knowbase.data.api.CollectionApi;
import com.rag.knowbase.data.api.DashboardApi;
import com.rag.knowbase.data.api.FileUploadedApi;
import com.rag.knowbase.data.api.MessageApi;
import com.rag.knowbase.data.api.UserApi;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBackend {


    private static final String url = "http://192.168.11.108:8080";
    private static Retrofit retrofit;

    public static Retrofit getClient(){
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
    public static UserApi getUserApi(){
        return getClient().create(UserApi.class);
    }
    public static DashboardApi getDashboardApi(){return getClient().create(DashboardApi.class);}
    public static CollectionApi getCollectionApi(){return getClient().create(CollectionApi.class);}
    public static FileUploadedApi getFileUploadedApi(){return getClient().create(FileUploadedApi.class);}
    public static ChatApi getChatApi(){return getClient().create(ChatApi.class);}
    public static MessageApi getMessageApi(){return getClient().create(MessageApi.class);}

}

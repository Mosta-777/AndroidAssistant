package com.example.mostafa.myapplication.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

/**
 * Created by Mostafa on 11/28/2017.
 */

public interface UserClient {

    @GET
    Call<ResponseBody> getSecret(
            @Url String url,
            @Header("Authorization") String authToken);
}

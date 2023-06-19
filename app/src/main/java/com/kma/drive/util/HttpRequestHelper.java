package com.kma.drive.util;


import com.kma.drive.dto.UserLoginDto;
import com.kma.drive.dto.UserRegisterDto;
import com.kma.drive.dto.VerifyCodeDto;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequestHelper {
    private static final String HTTP_PROTOCOL = "http";
    private static final String ADDRESS = "192.168.137.1"; // Thay dia chi ip o day khi thay server
    private static final int PORT = 8083;

    private Retrofit mRetrofit;
    private ApiService mApiService;

    public HttpRequestHelper() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Util.buildBaseUrl(HTTP_PROTOCOL, ADDRESS, PORT))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        mApiService = mRetrofit.create(ApiService.class);
    }

    public void register(UserRegisterDto user, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.postRegister(user);
        call.enqueue(callback);
    }

    public void verifyCodeRegister(VerifyCodeDto code, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.postVerifyCodeRegister(code);
        call.enqueue(callback);
    }

    public void login(UserLoginDto user, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.postLogin(user);
        call.enqueue(callback);
    }
}

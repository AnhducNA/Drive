package com.kma.drive.util;

import com.kma.drive.dto.UserLoginDto;
import com.kma.drive.dto.UserRegisterDto;
import com.kma.drive.dto.VerifyCodeDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/register")
    Call<ResponseBody> postRegister(@Body UserRegisterDto user);

    @POST("/api/verify_register_code")
    Call<ResponseBody> postVerifyCodeRegister(@Body VerifyCodeDto code);

    @POST("/api/authenticate")
    Call<ResponseBody> postLogin(@Body UserLoginDto user);
}

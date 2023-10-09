package com.kma.drive.util;

import com.kma.drive.common.Constant;
import com.kma.drive.dto.FileDto;
import com.kma.drive.dto.ResetPasswordDto;
import com.kma.drive.dto.UserDto;
import com.kma.drive.dto.UserLoginDto;
import com.kma.drive.dto.UserRegisterDto;
import com.kma.drive.dto.VerifyCodeDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/register")
    Call<ResponseBody> postRegister(@Body UserRegisterDto user);

    @POST("/api/verify_register_code")
    Call<ResponseBody> postVerifyCodeRegister(@Body VerifyCodeDto code);

    @POST("/api/authenticate")
    Call<UserDto> postLogin(@Body UserLoginDto user);

    @GET("/api/file/files")
    Call<List<FileDto>> getFilesByUserId(@Query(Constant.PARAM_USER_ID) String userId);

    @POST("/api/file/download")
    Call<ResponseBody> downloadFileById(@Body FileDto fileDto);

    @POST("/api/file/save")
    Call<List<FileDto>> saveFile(@Body FileDto fileDto);

    @DELETE("/api/file/delete")
    Call<ResponseBody> deleteFile(@Query(Constant.PARAM_FILE_ID) long fileId);

    @Multipart
    @POST("/api/file/upload")
    Call<ResponseBody> uploadFile(@Query(Constant.PARAM_FILE_ID) long fileId, @Part MultipartBody.Part file);

    @POST("/api/share")
    Call<ResponseBody> shareFile(@Query(Constant.PARAM_FILE_ID) long fileId,
                                 @Query(Constant.PARAM_SHARE_PERMISSION) int permission,
                                 @Query(Constant.PARAM_SHARE_EMAIL) String email);

    @GET("/api/login")
    Call<UserDto> login();

    @POST("api/get_reset_code")
    Call<ResponseBody> getResetCode(@Body String mail);

    @POST("api/reset_password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordDto resetPasswordDto);
}

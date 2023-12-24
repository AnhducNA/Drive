package com.kma.drive.util;


import com.kma.drive.dto.FileDto;
import com.kma.drive.dto.ResetPasswordDto;
import com.kma.drive.dto.UserDto;
import com.kma.drive.dto.UserLoginDto;
import com.kma.drive.dto.UserRegisterDto;
import com.kma.drive.dto.VerifyCodeDto;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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

    public HttpRequestHelper(String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new JwtInterceptor(token))
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

    public void login(UserLoginDto user, Callback<UserDto> callback) {
        Call<UserDto> call = mApiService.postLogin(user);
        call.enqueue(callback);
    }

    public void getAllFilesForUser(int userId, Callback<List<FileDto>> callback) {
        Call<List<FileDto>> call = mApiService.getFilesByUserId(String.valueOf(userId));
        call.enqueue(callback);
    }

    public void downloadFile(FileDto fileDto, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.downloadFileById(fileDto);
        call.enqueue(callback);
    }

    public void downloadDriveFile(Long fileId, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.downloadDriveFileById(fileId);
        call.enqueue(callback);
    }

    public void saveFile(FileDto fileDto, Callback<List<FileDto>> callback) {
        Call<List<FileDto>> call = mApiService.saveFile(fileDto);
        call.enqueue(callback);
    }

    public void saveDriveFile(FileDto fileDto, File file, Callback<List<FileDto>> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        Call<List<FileDto>> call = mApiService.saveDriveFile(fileDto.toString(), part);

        call.enqueue(callback);
    }

    public void deleteFile(long fileId, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.deleteFile(fileId);
        call.enqueue(callback);
    }

    public void deleteDriveFile(long fileId, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.deleteDriveFile(fileId);
        call.enqueue(callback);
    }

    public void uploadFile(long fileId, File file, Callback<ResponseBody> callback, String filename) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        Call<ResponseBody> call = mApiService.uploadFile(fileId, part);
        call.enqueue(callback);
    }

    public void shareFile(long fileId, int permission, String email, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.shareFile(fileId, permission, email);
        call.enqueue(callback);
    }

    public void login(Callback<UserDto> callback) {
        Call<UserDto> call = mApiService.login();
        call.enqueue(callback);
    }

    public void getResetCode(String mail, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.getResetCode(mail);
        call.enqueue(callback);
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = mApiService.resetPassword(resetPasswordDto);
        call.enqueue(callback);
    }
}

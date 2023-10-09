package com.kma.drive.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kma.drive.R;
import com.kma.drive.callback.Function;
import com.kma.drive.common.Constant;
import com.kma.drive.dto.UserDto;
import com.kma.drive.session.UserSession;
import com.kma.drive.util.HttpRequestHelper;
import com.kma.drive.util.Util;
import com.kma.drive.view.fragment.LoginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private HttpRequestHelper mRequestHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check jwt dang nhap xem da het han hay chua thi moi can dang nhap lai
        mPreferences = getSharedPreferences(Constant.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        String token = mPreferences.getString(Constant.JWT, null);

        // Check neu jwt khong con thi dua vao man login luon
        if (token == null) {
            startLoginActivity();
        } else {
            mRequestHelper = new HttpRequestHelper(token);
            mRequestHelper.login(new Callback<UserDto>() {

                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.isSuccessful()) {
                        UserSession userSession = UserSession.getInstance();
                        userSession.setUser(response.body());
                        // dang nhap xong tao thu muc local cho user
                        File folder = new File(getFilesDir(), String.valueOf(response.body().getId()));
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        startFileExploreActivity();
                    } else {
                        Util.getMessageDialog(SplashActivity.this, getString(R.string.message_session_out), new Function() {
                            @Override
                            public void execute() {
                                startLoginActivity();
                            }
                        }).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Util.getMessageDialog(SplashActivity.this, t.getMessage(), null).show();
                }
            });
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startFileExploreActivity() {
        Intent intent = new Intent(this, FileExploreActivity.class);
        startActivity(intent);
    }
}

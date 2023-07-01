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
        mRequestHelper = new HttpRequestHelper(token);

        mRequestHelper.login(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response.body().string());
                        UserSession userSession = UserSession.getInstance();
                        UserDto userDto = new UserDto(object.getString(UserDto.JWT),
                                object.getString(UserDto.USER_NAME),
                                new Date(10L),
                                object.getString(UserDto.EMAIL),
                                object.getString(UserDto.AVATAR),
                                object.getInt(UserDto.ID));
                        userSession.setUser(userDto);
                        // dang nhap xong tao thu muc local cho user
                        File folder = new File(getFilesDir(), String.valueOf(userDto.getId()));
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        startFileExploreActivity();
                    } catch (JSONException | IOException e) {
                        //
                    }
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.getMessageDialog(SplashActivity.this, t.getMessage(), null).show();
            }
        });
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

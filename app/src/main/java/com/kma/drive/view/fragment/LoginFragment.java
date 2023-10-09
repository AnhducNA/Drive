package com.kma.drive.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.kma.drive.R;
import com.kma.drive.common.Constant;
import com.kma.drive.dto.UserDto;
import com.kma.drive.dto.UserLoginDto;
import com.kma.drive.session.UserSession;
import com.kma.drive.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends BaseAbstractFragment{
    public static final String ORDER_REGISTER_ACCOUNT = "ORDER_REGISTER_ACCOUNT";
    public static final String ORDER_LOGIN_SUCCESS = "ORDER_LOGIN_SUCCESS";
    public static final String ORDER_FORGET_PASS = "ORDER_FORGET_PASS";
    private EditText mAccountEditText;
    private EditText mPasswordEditText;
    private AppCompatButton mLoginButton;
    private TextView mRegisterTextView;
    private TextView mForgetPasswordTextView;

    @Override
    protected int getLayout() {
        return R.layout.login_fragment;
    }

    @Override
    protected void doOnViewCreated(View view, Bundle bundle) {
        mAccountEditText = view.findViewById(R.id.et_account_login);
        mPasswordEditText = view.findViewById(R.id.et_password_login);
        mLoginButton = view.findViewById(R.id.bt_login);
        mRegisterTextView = view.findViewById(R.id.tv_register_account);
        mForgetPasswordTextView= view.findViewById(R.id.tv_forget_password);

        mRegisterTextView.setOnClickListener(view1 -> {
            mCallback.doAnOrder(ORDER_REGISTER_ACCOUNT);
        });

        mForgetPasswordTextView.setOnClickListener(view1 -> {
            mCallback.doAnOrder(ORDER_FORGET_PASS);
        });

        mLoginButton.setOnClickListener(view1 -> {
            String account = mAccountEditText.getText().toString().trim();
            String password = mPasswordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                return;
            }
            mLoginButton.setEnabled(false);
            UserLoginDto userLoginDto = new UserLoginDto(account, password);
            mRequestHelper.login(userLoginDto, new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.isSuccessful()) {
                        // Login thanh cong fetch data user thanh cong nay va luu lam session hien tai
                        UserSession userSession = UserSession.getInstance();
                        userSession.setUser(response.body());
                        // dang nhap xong tao thu muc local cho user
                        File folder = new File(mContext.get().getFilesDir(), String.valueOf(response.body().getId()));
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        mCallback.doAnOrder(ORDER_LOGIN_SUCCESS);
                    } else {
                        try {
                            JSONObject body = new JSONObject(response.errorBody().string());
                            String mess = body.getString(Constant.RESPONSE_MESSAGE);
                            Util.getMessageDialog(mContext.get(), mess, null).show();
                            mLoginButton.setEnabled(true);
                        } catch (IOException e) {

                        } catch (JSONException e) {

                        }
                    }
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Util.getMessageDialog(mContext.get(),
                            t.getMessage(), null).show();
                    mLoginButton.setEnabled(true);
                }
            });
        });
    }

}

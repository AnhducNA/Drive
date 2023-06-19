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
import com.kma.drive.dto.UserLoginDto;
import com.kma.drive.util.HttpRequestHelper;
import com.kma.drive.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends BaseAbstractFragment{
    public static final int ORDER_REGISTER_ACCOUNT = 1;
    public static final int ORDER_LOGIN_SUCCESS = 2;
    private EditText mAccountEditText;
    private EditText mPasswordEditText;
    private AppCompatButton mLoginButton;
    private TextView mRegisterTextView;

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

        mRegisterTextView.setOnClickListener(view1 -> {
            mCallback.doAnOrder(ORDER_REGISTER_ACCOUNT);
        });

        mLoginButton.setOnClickListener(view1 -> {
            String account = mAccountEditText.getText().toString().trim();
            String password = mPasswordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                return;
            }

            mLoginButton.setEnabled(false);

            UserLoginDto userLoginDto = new UserLoginDto(account, password);
            Log.d("MinhNTn", ":" + password + "," + userLoginDto.getPassword());
            mRequestHelper.login(userLoginDto, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            String token = object.getString(Constant.RESPONSE_TOKEN);
                            mCallback.doAnOrder(ORDER_LOGIN_SUCCESS);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            JSONObject body = new JSONObject(response.errorBody().string());
                            String mess = body.getString(Constant.RESPONSE_MESSAGE);
                            Util.getMessageDialog(mContext.get(), mess, null).show();
                            mLoginButton.setEnabled(true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Util.getMessageDialog(mContext.get(),
                            t.getMessage(), () -> {}).show();
                    mLoginButton.setEnabled(true);
                }
            });
        });
    }

}

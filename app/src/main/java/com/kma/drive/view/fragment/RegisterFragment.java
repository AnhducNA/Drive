package com.kma.drive.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.kma.drive.R;
import com.kma.drive.callback.Function;
import com.kma.drive.common.Constant;
import com.kma.drive.dto.UserRegisterDto;
import com.kma.drive.dto.VerifyCodeDto;
import com.kma.drive.util.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends BaseAbstractFragment{
    public static final String ORDER_REGISTER_DONE = "ORDER_REGISTER_DONE";
    public static final int GET_LOCAL_IMAGE = 1;
    public static final int GET_LOCAL_IMAGE_CODE = 11;

    private ImageView mBackImageView;
    private ImageView mAvatar;
    private EditText mUsernameRegister;
    private EditText mAccountRegister;
    private EditText mPasswordRegister;
    private EditText mPasswordConfirmRegister;
    private EditText mMailRegister;
    private AppCompatButton mRegisterButton;
    private String mAvatarBase64Temp;
    private UserRegisterDto mTempUserInfo;

    private boolean mRegisterSuccess;

    @Override
    protected int getLayout() {
        return R.layout.register_fragment;
    }

    @Override
    protected void doOnViewCreated(View view, Bundle bundle) {
        mBackImageView = view.findViewById(R.id.iv_back);
        mAvatar = view.findViewById(R.id.iv_avatar_register);
        mUsernameRegister = view.findViewById(R.id.et_username_register);
        mAccountRegister = view.findViewById(R.id.et_account_register);
        mPasswordRegister = view.findViewById(R.id.et_password_register);
        mPasswordConfirmRegister = view.findViewById(R.id.et_password_register_confirm);
        mMailRegister = view.findViewById(R.id.et_mail_register);
        mRegisterButton = view.findViewById(R.id.bt_register);

        mBackImageView.setOnClickListener(view1 -> {
            mCallback.back();
        });
        mAvatar.setOnClickListener(view1 -> getLocalImage());
        mRegisterButton.setOnClickListener(view1 -> register());
    }

    private void getLocalImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        mCallback.doAnOrderWithParams(GET_LOCAL_IMAGE, intent, GET_LOCAL_IMAGE_CODE);
    }

    private void register() {
        String username = mUsernameRegister.getText().toString().trim();
        String account = mAccountRegister.getText().toString().trim();
        String password = mPasswordRegister.getText().toString().trim();
        String confirmPassword = mPasswordConfirmRegister.getText().toString().trim();
        String mail = mMailRegister.getText().toString().trim();

        String errorMessage = mContext.get().getString(R.string.error_on_empty_input);
        boolean errorChecker = false;

        if (TextUtils.isEmpty(username)) {
            mUsernameRegister.setError(errorMessage);
            errorChecker = true;
        } else {
            if (username.length() > Constant.USERNAME_MAX_LENGTH) {
                mUsernameRegister.setError(mContext.get().getString(R.string.error_on_username_length));
                errorChecker = true;
            }
        }
        if (TextUtils.isEmpty(account)) {
            mAccountRegister.setError(errorMessage);
            errorChecker = true;
        } else {
            if (account.length() < Constant.ACCOUNT_MIN_LENGTH
                    || account.length() > Constant.ACCOUNT_MAX_LENGTH) {
                mAccountRegister.setError(mContext.get().getString(R.string.error_on_account_length));
                errorChecker = true;
            }
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordRegister.setError(errorMessage);
            errorChecker = true;
        } else {
            if (password.length() < Constant.PASSWORD_MIN_LENGTH
                    || password.length() > Constant.PASSWORD_MAX_LENGTH) {
                mPasswordRegister.setError(mContext.get().getString(R.string.error_on_password_length));
                errorChecker = true;
            }
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            mPasswordConfirmRegister.setError(errorMessage);
            errorChecker = true;
        } else {
            if (!confirmPassword.equals(password)) {
                mPasswordConfirmRegister.setError(mContext.get().getString(R.string.error_on_confirm_alike));
                errorChecker = true;
            }
        }
        if (TextUtils.isEmpty(mail)) {
            mMailRegister.setError(errorMessage);
            errorChecker = true;
        }

        if (!errorChecker) {
            mRegisterButton.setEnabled(false);
            password = new BCryptPasswordEncoder().encode(password);
            mTempUserInfo = new UserRegisterDto(account, password
                    , mAvatarBase64Temp, mail, username);
            // request dang ky tai khoan
            mRequestHelper.register(mTempUserInfo, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mRegisterButton.setEnabled(true);
                    if (response.isSuccessful()) {
                        getConfirmCodeDialog().show();
                    } else {
                        try {
                            Util.getMessageDialog(mContext.get(),
                                    response.errorBody().string(), new Function() {
                                        @Override
                                        public void execute() {
                                            if (mRegisterSuccess) {
                                                mCallback.back();
                                            }
                                        }
                                    }).show();
                        } catch (IOException e) {
//                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // TODO xu ly loi o day, tam thoi show dialog message o day
                    mRegisterButton.setEnabled(true);
                    Util.getMessageDialog(mContext.get(),
                            t.getMessage(), null).show();
                }
            });
        }
    }

    public void setAvatarImage(Uri data) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(mContext.get().getContentResolver().openInputStream(data));
            mAvatar.setImageBitmap(bitmap);

            new Handler().post(() -> {
                mAvatarBase64Temp = Util.convertBitmapToBase64(bitmap);
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Dialog getConfirmCodeDialog() {
        Dialog dialog = new Dialog(mContext.get());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_confirm_register_code);
        dialog.setCanceledOnTouchOutside(false);

        EditText codeEditText = dialog.findViewById(R.id.et_code_register);
        AppCompatButton confirmButton = dialog.findViewById(R.id.bt_confirm_code);
        AppCompatButton cancelButton = dialog.findViewById(R.id.bt_cancel_code);

        confirmButton.setOnClickListener(view -> {
            //TODO xac nhan code o day
            String code = codeEditText.getText().toString().trim();
            mRequestHelper.verifyCodeRegister(new VerifyCodeDto(mTempUserInfo.getAccount(), code)
                    , new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject object = new JSONObject(response.body().string());
                                    String mess = object.getString(Constant.RESPONSE_MESSAGE);
                                    mRegisterSuccess = (mess.equals(getString(R.string.message_register_success)));
                                    Util.getMessageDialog(mContext.get(), mess, new Function() {
                                        @Override
                                        public void execute() {
                                            if (mRegisterSuccess) {
                                                dialog.cancel();
                                                mCallback.doAnOrder(ORDER_REGISTER_DONE);
                                            }
                                        }
                                    }).show();
                                } catch (IOException | JSONException e) {
//                                    throw new RuntimeException(e);
                                }
                            } else {
                                //TODO trong truong hop fail
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // TODO xu ly loi o day, tam thoi show dialog message o day
                            Util.getMessageDialog(mContext.get(),
                                    t.getMessage(), null).show();
                        }
                    });
        });

        cancelButton.setOnClickListener(view -> {
            dialog.cancel();
        });

        return dialog;
    }

}

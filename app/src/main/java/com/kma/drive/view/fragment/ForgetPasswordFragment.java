package com.kma.drive.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.kma.drive.R;
import com.kma.drive.callback.Function;
import com.kma.drive.dto.ResetPasswordDto;
import com.kma.drive.util.Util;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordFragment extends BaseAbstractFragment{
    private EditText mMailEditText;
    private AppCompatButton mConfirmButton;

    @Override
    protected int getLayout() {
        return R.layout.fragment_forget_pass;
    }

    @Override
    protected void doOnViewCreated(View view, Bundle bundle) {
        mMailEditText = view.findViewById(R.id.et_mail_register);
        mConfirmButton = view.findViewById(R.id.bt_confirm);

        mConfirmButton.setOnClickListener(view1 -> {
            String mail = mMailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(mail)) {
                return;
            }
            mRequestHelper.getResetCode(mail, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        getResetPasswordDialog(mContext.get(), mail).show();
                    } else {
                        try {
                            String msg = response.errorBody().string();
                            Util.getMessageDialog(mContext.get(), msg, null).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("MinhNTn", "onFailure: " + t.getMessage());
                }
            });
        });
    }

    public Dialog getResetPasswordDialog(Context context, String mail) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_reset_password);
        dialog.setCanceledOnTouchOutside(false);

        EditText codeEditText = dialog.findViewById(R.id.et_code);
        EditText newPasswordEditText = dialog.findViewById(R.id.et_new_password);
        AppCompatButton buttonConfirm = dialog.findViewById(R.id.bt_confirm_dialog_option);
        AppCompatButton buttonCancel = dialog.findViewById(R.id.bt_cancel_dialog_option);

        buttonConfirm.setOnClickListener(view -> {
            String code = codeEditText.getText().toString().trim();
            String password = newPasswordEditText.getText().toString().trim();
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(password)) {
                return;
            }
            buttonConfirm.setEnabled(false);
            buttonCancel.setEnabled(false);

            ResetPasswordDto resetPasswordDto = new ResetPasswordDto(mail, code, password);
            mRequestHelper.resetPassword(resetPasswordDto, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        dialog.cancel();
                        Toast.makeText(context, "Reset mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        mCallback.doAnOrder(RegisterFragment.ORDER_REGISTER_DONE);
                    } else {
                        try {
                            String msg = response.errorBody().string();
                            Util.getMessageDialog(mContext.get(), msg, null).show();
                            buttonConfirm.setEnabled(true);
                            buttonCancel.setEnabled(true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    String msg = t.getMessage();
                    Util.getMessageDialog(mContext.get(), msg, null).show();
                }
            });
        });
        buttonCancel.setOnClickListener(view -> {
            dialog.cancel();
        });

        return dialog;
    }
}

package com.kma.drive.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.kma.drive.R;
import com.kma.drive.callback.Function;

import java.io.ByteArrayOutputStream;

public class Util {
    public static final String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static final Bitmap convertBase64ToBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String buildBaseUrl(String protocol, String address, int port) {
        return protocol + "://" + address + ((port != 0)? ":" + port: "") + "/";
    }

    public static Dialog getMessageDialog(Context context, String message, Function function) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_message);
        dialog.setCanceledOnTouchOutside(false);

        TextView textView = dialog.findViewById(R.id.tv_dialog_message);
        AppCompatButton button = dialog.findViewById(R.id.bt_confirm_message);

        textView.setText(message);
        button.setOnClickListener(view -> {
            dialog.cancel();
            if (function != null) {
                function.execute();
            }
        });

        return dialog;
    }

}

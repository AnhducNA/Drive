package com.kma.drive.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.kma.drive.R;
import com.kma.drive.callback.Function;
import com.kma.drive.common.Constant;
import com.kma.drive.dto.FileDto;
import com.kma.drive.model.FileModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {
    public static final String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        float originRatio = bitmap.getWidth() * 1.0f / bitmap.getHeight();
        int newWidth = Constant.AVATAR_WIDTH_R;
        int newHeight = (int) (newWidth / originRatio);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static final Bitmap convertBase64ToBitmap(String base64) {
        if (TextUtils.isEmpty(base64)) {
            return new BitmapDrawable().getBitmap();
        }
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String buildBaseUrl(String protocol, String address, int port) {
        return protocol + "://" + address + ((port != 0)? ":" + port: "") + "/";
    }

    public static Dialog getMessageDialog(Context context, String message, Function function) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

    public static Dialog getOptionInputTextDialog(Context context, String title, String additon, Function negative, Function positive) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_option_and_input_text);
        dialog.setCanceledOnTouchOutside(false);

        TextView textView = dialog.findViewById(R.id.tv_title_dialog_option_input);
        EditText editText = dialog.findViewById(R.id.et_input_text_option_dialog);
        AppCompatButton negativeButton = dialog.findViewById(R.id.bt_cancel_dialog_option);
        AppCompatButton positiveButton = dialog.findViewById(R.id.bt_confirm_dialog_option);

        textView.setText(title);
        if (!TextUtils.isEmpty(additon)) {
            editText.setText(additon);
        }
        negativeButton.setOnClickListener(view -> {
            dialog.cancel();
            if (negative != null) {
                negative.execute();
            }
        });
        positiveButton.setOnClickListener(view -> {
            dialog.cancel();
            if (positive != null) {
                positive.execute(editText.getText().toString());
            }
        });

        return dialog;
    }

    public static Dialog getOptionInputImageDialog(Context context, String title, Bitmap bitmap, Function negative, Function positive) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_option_and_input_image);
        dialog.setCanceledOnTouchOutside(false);

        TextView textView = dialog.findViewById(R.id.tv_title_dialog_option_input);
        ImageView imageView = dialog.findViewById(R.id.iv_option_dialog);
        AppCompatButton negativeButton = dialog.findViewById(R.id.bt_cancel_dialog_option);
        AppCompatButton positiveButton = dialog.findViewById(R.id.bt_confirm_dialog_option);

        textView.setText(title);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        negativeButton.setOnClickListener(view -> {
            dialog.cancel();
            if (negative != null) {
                negative.execute();
            }
        });
        positiveButton.setOnClickListener(view -> {
            dialog.cancel();
            if (positive != null) {
                positive.execute(bitmap);
            }
        });

        return dialog;
    }

    public static int getIconFileFromType(String type) {
        switch (type) {
            case Constant.FileType.PPTX: {
                return R.drawable.icon_pptx_file;
            }
            case Constant.FileType.TXT:
            case Constant.FileType.DOC: {
                return R.drawable.icon_txt_file;
            }
            case Constant.FileType.PDF: {
                return R.drawable.icon_pdf_file;
            }
            case Constant.FileType.FOLDER: {
                return R.drawable.ic_folder;
            }
            case Constant.FileType.JPG:
            case Constant.FileType.PNG:
            case Constant.FileType.JPEG: {
                return R.drawable.icon_image_file;
            }
            default: {
                return R.drawable.icon_unknown_file;
            }
        }
    }

    /**
     * Tra ve ten file meu doc file thanh cong tu input stream, null neu khong thanh cong
     * @param inputStream
     * @return
     */
    public static String getFileFromInputStream(InputStream inputStream, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            return null;
        }

        return file.getAbsolutePath();
    }

    public static String getMimeType(File file) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static String convertDateToString(Date date) {
        return date.toString();
    }

    public static Date convertStringToDate(String strDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return new Date(format.parse(strDate).getTime());
    }

    public static FileModel convertToFileModel(FileDto fileDto) throws ParseException {
        return new FileModel(fileDto.getId(),
                fileDto.getFileName(),
                convertStringToDate(fileDto.getDate()),
                fileDto.isFavorite(),
                fileDto.getType(),
                fileDto.getOwner(),
                fileDto.getLocation(),
                fileDto.getParentId());
    }

    public static FileDto convertToFileDto(FileModel fileModel) {
        return new FileDto(fileModel.getId(),
                fileModel.getFileName(),
                fileModel.getDate().toString(),
                fileModel.isFavorite(),
                fileModel.getType(),
                fileModel.getOwner(),
                fileModel.getLocation(),
                fileModel.getParentId());
    }

    public static FileDto convertFromJSON(JSONObject object)  {
        try {
            FileDto fileDto = new FileDto((long) object.getInt(FileDto.ID),
                    object.getString(FileDto.FILE_NAME),
                    object.getString(FileDto.DATE),
                    object.getBoolean(FileDto.FAVORITE),
                    object.getString(FileDto.TYPE),
                    object.getLong(FileDto.OWNER),
                    object.getString(FileDto.LOCATION),
                    object.getLong(FileDto.PARENT_ID));
            return fileDto;
        } catch (JSONException e) {
            return null;
        }
    }

    public static File convertBitmapToFile(Context context, Bitmap bitmap, String name) throws IOException {
        File image = new File(context.getFilesDir(), name);
        FileOutputStream fos = new FileOutputStream(image);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        return image;
    }
}

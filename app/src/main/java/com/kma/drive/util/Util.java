package com.kma.drive.util;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static Dialog getOptionShareFileDialog(Context context, String title, String string, Function negative, Function positive) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_option_share_file);
        dialog.setCanceledOnTouchOutside(false);

        TextView titleTV = dialog.findViewById(R.id.tv_title_dialog_option_input);
        EditText editText = dialog.findViewById(R.id.et_input_mail_to_share);
        Spinner spinner = dialog.findViewById(R.id.sn_permission);
        AppCompatButton negativeButton = dialog.findViewById(R.id.bt_cancel_dialog_option);
        AppCompatButton positiveButton = dialog.findViewById(R.id.bt_confirm_dialog_option);

        titleTV.setText(title);
        ArrayList<String> data = new ArrayList<>();
        data.add("Xem");
        data.add("Xóa");
        data.add("Chia sẻ");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);
        spinner.setAdapter(arrayAdapter);
        negativeButton.setOnClickListener(view -> {
            dialog.cancel();
            if (negative != null) {
                negative.execute();
            }
        });
        positiveButton.setOnClickListener(view -> {
            dialog.cancel();
            if (positive != null) {
                positive.execute(editText.getText().toString(), spinner.getSelectedItemPosition());
            }
        });

        return dialog;
    }

    public static int getIconFileFromType(String type) {
        switch (type) {
            case Constant.FileType.PPT:
            case Constant.FileType.POTM:
            case Constant.FileType.POTX:
            case Constant.FileType.PPAM:
            case Constant.FileType.PPSM:
            case Constant.FileType.PPSX:
            case Constant.FileType.PPTM:
            case Constant.FileType.PPTX: {
                return R.drawable.icon_pptx_file;
            }
            case Constant.FileType.DOC:
            case Constant.FileType.DOTX:
            case Constant.FileType.DOCM:
            case Constant.FileType.DOCX:
            case Constant.FileType.DOTM:
            case Constant.FileType.TXT: {
                return R.drawable.icon_txt_file;
            }
            case Constant.FileType.XLAM:
            case Constant.FileType.XLS:
            case Constant.FileType.XLSB:
            case Constant.FileType.XLSM:
            case Constant.FileType.XLSX:
            case Constant.FileType.XLTM:
            case Constant.FileType.XLTX: {
                return R.drawable.icon_excel;
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
                fileDto.getParentId(),
                false);
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

    public static String getPathFromUri(Context context, Uri uri) {
        String filePath = "";
        if (uri != null) {
            if (uri.getScheme().equals("content")) {
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                }
            } else if (uri.getScheme().equals("file")) {
                filePath = uri.getPath();
            }
        }
        return filePath;
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;

        if (uri != null) {
            Cursor cursor = null;
            try {
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return fileName;
    }

    public static File getFileFromUri(Context context, Uri uri) {
        String name = getFileNameFromUri(context, uri);
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            File file = new File(context.getFilesDir(), name);

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            return file;
        } catch (IOException e) {
            return new File(uri.getPath());
        }
    }
}

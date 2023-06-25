package com.kma.drive.view.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.kma.drive.R;
import com.kma.drive.common.Constant;
import com.kma.drive.dto.FileDto;
import com.kma.drive.model.FileModel;
import com.kma.drive.session.UserSession;
import com.kma.drive.util.HttpRequestHelper;
import com.kma.drive.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenFileActivity extends AppCompatActivity {
    public static final String EXTRA_FILE_OPEN = "EXTRA_FILE_OPEN";
    public static final int OPEN_REQUEST_CODE = 12;

    private ImageButton mCloseImageButton;
    private LinearLayout mEmptyFolderLinearLayout;
    private UserSession mUserSession;
    private HttpRequestHelper mRequestHelper;
    private FileModel mOpenFile;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        mCloseImageButton = findViewById(R.id.bt_close_file);
        mCloseImageButton.setOnClickListener(view -> finish());
        mEmptyFolderLinearLayout = findViewById(R.id.layout_empty_folder);

        mUserSession = UserSession.getInstance();
        mRequestHelper = new HttpRequestHelper(mUserSession.getUser().getJwt());

        Intent intent = getIntent();
        mOpenFile = intent.getParcelableExtra(EXTRA_FILE_OPEN, FileModel.class);
        if (mOpenFile.getType().equals(Constant.FileType.FOLDER)) {
            //TODO check neu la folder

        } else {
            download(mOpenFile.getId(), mOpenFile);
        }
    }

    private void download(long id, FileModel fileDto) {
        //TODO check file co trong ay khong thi moi tai, khong thi thoi
        //TODO download file nay chi tam thoi, nen xem xet xoa khi thoat app hoac dang xuat chang han
        mRequestHelper.downloadFile(id, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    InputStream inputStream = response.body().byteStream();
                    File file = new File(getFilesDir(), fileDto.getFileName());
                    String result = Util.getFileFromInputStream(inputStream, file);

                    if (result == null) {
                        //TODO tai file hoac doc file fail

                    } else {
                        Uri fileUri = FileProvider.getUriForFile(OpenFileActivity.this, Constant.AUTHORITY, file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(fileUri, Util.getMimeType(file));
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            startActivityForResult(intent, OPEN_REQUEST_CODE);
                        } catch (ActivityNotFoundException e) {
                            // TODO khong tim thay ung dung mo file thi hien thi text khong doc duoc...
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_REQUEST_CODE) {
            finish();
        }
    }
}

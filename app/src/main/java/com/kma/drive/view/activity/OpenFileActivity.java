package com.kma.drive.view.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kma.drive.R;
import com.kma.drive.adapter.FileAdapter;
import com.kma.drive.callback.FragmentCallback;
import com.kma.drive.callback.Function;
import com.kma.drive.callback.ItemFileClickListener;
import com.kma.drive.common.Constant;
import com.kma.drive.dto.FileDto;
import com.kma.drive.model.FileModel;
import com.kma.drive.session.UserSession;
import com.kma.drive.util.HttpRequestHelper;
import com.kma.drive.util.Util;

import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenFileActivity extends AppCompatActivity implements FragmentCallback, ItemFileClickListener {
    public static final String EXTRA_FILE_OPEN = "EXTRA_FILE_OPEN";
    public static final String EXTRA_FILE_MOVING = "EXTRA_FOLDER_OPEN"; // extra chi dung trong mode di chuyen file
    public static final String EXTRA_REASON_USE_ACTIVITY = "EXTRA_REASON_USE_ACTIVITY";
    public static final String EXTRA_NEW_LOCATION = "EXTRA_NEW_LOCATION";
    public static final String EXTRA_UPDATE_FILE_LOCATION = "EXTRA_UPDATE_FILE_LOCATION";

    public static final int REASON_OPEN_FILE = 0;
    public static final int REASON_MOVE_FILE = 1;
    public static final int OPEN_REQUEST_CODE = 12;

    // component
    private ImageButton mCloseImageButton;
    private LinearLayout mEmptyFolderLinearLayout;
    private RecyclerView mRecyclerView;
    private FileAdapter mFileAdapter;
    private ProgressBar mLoadingDataProgressBar;
    // component cua bottom dialog file - start
    private BottomSheetDialog mBottomSheetDialog;
    private TextView mTargetFileNameTextView;
    private TextView mFavoriteFunctionTextView;
    private TextView mChangeFileNameTextView;
    private TextView mDeleteFileTextView;
    private TextView mMoveFileTextView;
    // component cua bottom dialog - end
    // component thanh chuc nang di chuyen - start
    private LinearLayout mMoveFunctionLinearLayout;
    private Button mMoveCancelButton;
    private Button mMoveConfirmButton;
    // component thanh chuc nang di chuyen - end

    private UserSession mUserSession;
    private HttpRequestHelper mRequestHelper;
    private FileModel mOpenFile;
    private FileModel mMovingFile;
    private List<FileModel> mChildFiles;
    private int mCurrentReason;
    private String mNewLocation; // bien nay chi dung trong mode move file

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);

        Intent intent = getIntent();
        mOpenFile = intent.getParcelableExtra(EXTRA_FILE_OPEN, FileModel.class);
        mCurrentReason = intent.getIntExtra(EXTRA_REASON_USE_ACTIVITY, REASON_OPEN_FILE);
        mMovingFile = intent.getParcelableExtra(EXTRA_FILE_MOVING, FileModel.class);
        mNewLocation = intent.getStringExtra(EXTRA_NEW_LOCATION);
        Log.d("MinhNTn", "onCreate: " + mNewLocation);

        mUserSession = UserSession.getInstance();
        mRequestHelper = new HttpRequestHelper(mUserSession.getUser().getJwt());

        // init view - start
        mCloseImageButton = findViewById(R.id.bt_close_file);
        mCloseImageButton.setOnClickListener(view -> finish());
        mEmptyFolderLinearLayout = findViewById(R.id.layout_empty_folder);
        mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        TextView titleFileTextView = findViewById(R.id.tv_file_name_open);
        titleFileTextView.setText(mOpenFile.getFileName());
        mRecyclerView = findViewById(R.id.folderRecyclerView);
        mLoadingDataProgressBar = findViewById(R.id.pb_loading_data);
        mMoveFunctionLinearLayout = findViewById(R.id.ln_move_action);
        // init view - end

        // Doi voi activity nay thi se check neu dang dung de moi file thi cac view nay duoc cai dat,
        // nguoc lai thi khong can
        if (mCurrentReason == REASON_OPEN_FILE) {
            mMoveFunctionLinearLayout.setVisibility(View.INVISIBLE);
            // cai dat bottom sheet - start
            mBottomSheetDialog = new BottomSheetDialog(this);
            mBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
            mTargetFileNameTextView = mBottomSheetDialog.findViewById(R.id.tv_filename_current);
            mFavoriteFunctionTextView = mBottomSheetDialog.findViewById(R.id.tv_favorite);
            mChangeFileNameTextView = mBottomSheetDialog.findViewById(R.id.tv_rename);
            mDeleteFileTextView = mBottomSheetDialog.findViewById(R.id.tv_delete);
            mMoveFileTextView = mBottomSheetDialog.findViewById(R.id.tv_move);
            // cai dat bottom sheet - end
        } else {
            mMoveFunctionLinearLayout.setVisibility(View.VISIBLE);
            mMoveCancelButton = findViewById(R.id.bt_cancel_move);
            mMoveConfirmButton = findViewById(R.id.bt_move);
            mMoveCancelButton.setOnClickListener(view -> {
                Intent backIntent = new Intent(OpenFileActivity.this, FileExploreActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(backIntent);
            });
            mMoveConfirmButton.setOnClickListener(view -> {
                mMovingFile.setDate(new Date(Calendar.getInstance().getTimeInMillis()));
                mMovingFile.setParentId(mOpenFile.getId());
                mMovingFile.setLocation(mNewLocation);
                mRequestHelper.saveFile(Util.convertToFileDto(mMovingFile), new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Intent backIntent = new Intent(OpenFileActivity.this, FileExploreActivity.class);
                            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            backIntent.putExtra(EXTRA_UPDATE_FILE_LOCATION, mMovingFile);
                            startActivity(backIntent);
                        } else {
                            Log.d("MinhNTn", "onResponse: ");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            });
        }

        // recycler view part - start
        mChildFiles = new ArrayList<>();
        mFileAdapter = new FileAdapter(this, mChildFiles, this, this, mCurrentReason == REASON_OPEN_FILE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFileAdapter);
        // recycler view part - end

        if (mOpenFile.getType().equals(Constant.FileType.FOLDER) || mCurrentReason == REASON_MOVE_FILE) {
            //TODO check neu la folder
            getChildFiles();
            if (mChildFiles.isEmpty()) {
                mEmptyFolderLinearLayout.setVisibility(View.VISIBLE);
            } else {
                mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
            }
            mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        } else {
            download(mOpenFile.getId(), mOpenFile);
        }
    }

    private void getChildFiles() {
        mChildFiles.clear();
        mUserSession.getFileChildren(mOpenFile.getId(), mChildFiles, mCurrentReason == REASON_OPEN_FILE);
        if (mCurrentReason == REASON_MOVE_FILE) {
            mChildFiles.remove(mOpenFile);
        }
        if (mFileAdapter != null) {
            mFileAdapter.notifyDataSetChanged();
        }
    }

    private void setVisibleEmptyView() {
        if (mChildFiles.isEmpty()) {
            mEmptyFolderLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void onDataStateChanged() {
        if (mFileAdapter != null) {
            mFileAdapter.notifyDataSetChanged();
            setVisibleEmptyView();
        }
    }

    private void onDataDeleted(FileModel fileModel) {
        mChildFiles.remove(fileModel);
        onDataStateChanged();
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

    @Override
    public void doAnOrder(int order) {

    }

    @Override
    public void doAnOrderWithParams(int order, Object... objects) {
        //TODO thuc hien viec update xong thi can update ca date modify
        // Thuc hien chuc nang cua bottom sheet dialog
        FileModel currentFile = (FileModel) objects[0];
        mTargetFileNameTextView.setText(currentFile.getFileName());
        if (currentFile.isFavorite()) {
            mFavoriteFunctionTextView.setText(R.string.function_item_remove_favorite);
        } else {
            mFavoriteFunctionTextView.setText(R.string.function_item_add_favorite);
        }
        mFavoriteFunctionTextView.setOnClickListener(view -> {
            FileDto dtoFile = Util.convertToFileDto(currentFile);
            dtoFile.setFavorite(!dtoFile.isFavorite());
            mRequestHelper.saveFile(dtoFile, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        currentFile.setFavorite(!currentFile.isFavorite());
                        if (currentFile.isFavorite()) {
                            mFavoriteFunctionTextView.setText(R.string.function_item_remove_favorite);
                        } else {
                            mFavoriteFunctionTextView.setText(R.string.function_item_add_favorite);
                        }
                        onDataStateChanged();
                    } else {
                        Util.getMessageDialog(OpenFileActivity.this, response.body().toString(), null);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
            mBottomSheetDialog.hide();
        });
        mChangeFileNameTextView.setOnClickListener(view -> {
            FileDto dtoFile = Util.convertToFileDto(currentFile);
            Util.getOptionInputTextDialog(OpenFileActivity.this,
                    getString(R.string.title_dialog_change_name_file), dtoFile.getFileName(), null, new Function() {
                        @Override
                        public void execute(Object object) {
                            dtoFile.setFileName((String) object);
                            mRequestHelper.saveFile(dtoFile, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        currentFile.setFileName((String) object);
                                        mTargetFileNameTextView.setText(currentFile.getFileName());
                                        onDataStateChanged();
                                    } else {
                                        Util.getMessageDialog(OpenFileActivity.this, response.body().toString(), null);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    //TODO
                                }
                            });
                        }
                    }).show();
            mBottomSheetDialog.hide();
        });
        //TODO luc xoa file check xoa ca local nua
        mDeleteFileTextView.setOnClickListener(view -> {
            mRequestHelper.deleteFile(currentFile.getId(), new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        mUserSession.getFiles().remove(currentFile);
                        onDataDeleted(currentFile);
                    } else {
                        Util.getMessageDialog(OpenFileActivity.this, response.body().toString(), null);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
            mBottomSheetDialog.hide();
        });
        mMoveFileTextView.setOnClickListener(view -> {
            Intent intent = new Intent(OpenFileActivity.this, OpenFileActivity.class);
            intent.putExtra(OpenFileActivity.EXTRA_FILE_MOVING, currentFile);
            intent.putExtra(OpenFileActivity.EXTRA_FILE_OPEN, mUserSession.getRootFolder());
            intent.putExtra(OpenFileActivity.EXTRA_REASON_USE_ACTIVITY, OpenFileActivity.REASON_MOVE_FILE);
            intent.putExtra(OpenFileActivity.EXTRA_NEW_LOCATION, "");
            startActivity(intent);
            mBottomSheetDialog.hide();
        });
        mBottomSheetDialog.show();
    }

    @Override
    public void back() {

    }

    @Override
    public void open(long id) {
        final FileModel fileDto = getFileById(id);
        if (fileDto == null) {
            //TODO do something here
            return;
        }
        //TODO nen check cache xem file nay da duoc tai ve hay chua
        Intent intent = new Intent(this, OpenFileActivity.class);
        intent.putExtra(EXTRA_FILE_OPEN, fileDto);
        intent.putExtra(EXTRA_FILE_MOVING, mMovingFile);
        intent.putExtra(EXTRA_REASON_USE_ACTIVITY, mCurrentReason);
        intent.putExtra(EXTRA_NEW_LOCATION, mNewLocation + fileDto.getFileName() + "\\");
        startActivity(intent);
    }

    public FileModel getFileById(long id) {
        for (FileModel fileDto: mChildFiles) {
            if (fileDto.getId() == id) {
                return fileDto;
            }
        }

        return null;
    }
}

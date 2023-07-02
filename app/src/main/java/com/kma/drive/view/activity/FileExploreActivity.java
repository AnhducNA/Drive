package com.kma.drive.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kma.drive.R;
import com.kma.drive.callback.AwareDataStateChange;
import com.kma.drive.callback.FragmentCallback;
import com.kma.drive.callback.Function;
import com.kma.drive.common.Constant;
import com.kma.drive.dto.FileDto;
import com.kma.drive.model.FileModel;
import com.kma.drive.session.UserSession;
import com.kma.drive.util.FileUtil;
import com.kma.drive.util.HttpRequestHelper;
import com.kma.drive.util.Util;
import com.kma.drive.view.custom.CircularImageView;
import com.kma.drive.view.fragment.FavoriteFilesFragment;
import com.kma.drive.view.fragment.FilesFragment;
import com.kma.drive.view.fragment.HomeAppFragment;
import com.kma.drive.view.fragment.SharedFilesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileExploreActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FragmentCallback {
    public static final String HOME_APP_FRAG_NAME = "HOME APP FRAGMENT";
    public static final int REQUEST_IMAGE_CAPTURE_CODE = 101;
    public static final int REQUEST_FILE_UPLOAD_CODE = 102;
    // component
    private BottomNavigationView mBottomNavigationView;
    // component cua bottom dialog file - start
    private BottomSheetDialog mBottomSheetDialog;
    private TextView mTargetFileNameTextView;
    private TextView mFavoriteFunctionTextView;
    private TextView mChangeFileNameTextView;
    private TextView mDeleteFileTextView;
    private TextView mMoveFileTextView;
    private TextView mShareFileTextView;
    // component cua bottom dialog - end
    // component bottom dialog new file - start
    private BottomSheetDialog mBottomSheetNewFileDialog;
    // component bottom dialog new file - end
    private CircularImageView mUserAvatarImageView;
    private AutoCompleteTextView mSearchAutoCompleteTextView;
    private ArrayAdapter<String> mSearchAdapter;

    // etc
    private UserSession mUserSession;
    protected HttpRequestHelper mRequestHelper;
    private SharedPreferences mPreferences;

    private FloatingActionButton mAddFab;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserSession = UserSession.getInstance();
        mRequestHelper = new HttpRequestHelper((mUserSession.getUser() == null)? null: mUserSession.getUser().getJwt());
        mPreferences = getSharedPreferences(Constant.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constant.JWT, mUserSession.getUser().getJwt());
        editor.apply();

        // cai dat bottom sheet - start
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        mTargetFileNameTextView = mBottomSheetDialog.findViewById(R.id.tv_filename_current);
        mFavoriteFunctionTextView = mBottomSheetDialog.findViewById(R.id.tv_favorite);
        mChangeFileNameTextView = mBottomSheetDialog.findViewById(R.id.tv_rename);
        mDeleteFileTextView = mBottomSheetDialog.findViewById(R.id.tv_delete);
        mMoveFileTextView = mBottomSheetDialog.findViewById(R.id.tv_move);
        mShareFileTextView = mBottomSheetDialog.findViewById(R.id.tv_share_file);
        // cai dat bottom sheet - end

        //cai dat bottom sheet dialog new file - start
        mBottomSheetNewFileDialog = new BottomSheetDialog(this);
        mBottomSheetNewFileDialog.setContentView(R.layout.add_new_bottom_sheet_dialog);
        LinearLayout folder = mBottomSheetNewFileDialog.findViewById(R.id.add_new_folder_main);
        LinearLayout file = mBottomSheetNewFileDialog.findViewById(R.id.add_new_file_main);
        LinearLayout useCamera = mBottomSheetNewFileDialog.findViewById(R.id.use_camera_main);
        folder.setOnClickListener(view -> {
            Util.getOptionInputTextDialog(FileExploreActivity.this,
                    getString(R.string.title_dialog_new_folder), null, null, new Function() {
                        @Override
                        public void execute(Object object) {
                            String folder = (String) object;
                            // Kiem tra trung ten thu muc - check so voi cac thu muc nam trong cap root
                            if (FileUtil.checkFileNameExisted(folder, Constant.ID_PARENT_DEFAULT, mUserSession.getUser().getId(), mUserSession.getFiles())) {
                                Util.getMessageDialog(FileExploreActivity.this, "Có file với tên đã tồn tại", null).show();
                            }
                            FileModel temp = mUserSession.createNewFile(folder, Constant.FileType.FOLDER);
                            FileDto fileDto = Util.convertToFileDto(temp);
                            mRequestHelper.saveFile(fileDto, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONArray array = new JSONArray(response.body().string());
                                            JSONObject file = array.getJSONObject(0);
                                            FileDto tempDto = Util.convertFromJSON(file);
                                            if (tempDto != null) {
                                                temp.setId(tempDto.getId());
                                                mUserSession.getFiles().add(temp);
                                                ((AwareDataStateChange)getCurrentDisplayFragment()).onDataCreated(temp);
                                                notifySearchDataChanged(temp, Constant.ACTION_CREATE);
                                            }
                                        } catch (JSONException | IOException e) {
                                            //TODO
                                        }

                                    } else {
                                        try {
                                            if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                                doOnValidationExpired();
                                            }
                                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                                Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                                            }

                                        } catch (IOException e) {
                                            //
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        }
                    }).show();
        });
        useCamera.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_CODE);
            }
        });
        file.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, REQUEST_FILE_UPLOAD_CODE);
        });
        //cai dat bottom sheet dialog new file - end

        mBottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        // Mac dinh start activity nay la home_frag hien ra
        mBottomNavigationView.setSelectedItemId(R.id.footer_home);

        // cai dat search textview - start
        mSearchAutoCompleteTextView = findViewById(R.id.sv_item_bar);
        mSearchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(mUserSession.getStrFiles()));
        mSearchAutoCompleteTextView.setAdapter(mSearchAdapter);
        mSearchAutoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            String fileName = (String) adapterView.getItemAtPosition(i);
            final FileModel fileDto = mUserSession.getFileModelByFileName(fileName);
            if (fileDto == null) {
                //TODO do something here
                return;
            }
            Intent intent = new Intent(FileExploreActivity.this, OpenFileActivity.class);
            intent.putExtra(OpenFileActivity.EXTRA_FILE_OPEN, fileDto);
            FileExploreActivity.this.startActivity(intent);
            mSearchAutoCompleteTextView.setText(null);
        });
        // cai dat search textview - end

        // fetch files user tu server ve
        mUserSession.setDataFetching(true);
        Log.d("MinhNTn", "onCreate: " + mUserSession.getUser().getJwt());
        mRequestHelper.getAllFilesForUser(mUserSession.getUser().getId(), new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject file = array.getJSONObject(i);

                            FileDto fileDto = Util.convertFromJSON(file);

                            FileModel fileModel = Util.convertToFileModel(fileDto);
                            fileModel.setShared(fileModel.getOwner() != mUserSession.getUser().getId());
                            if (fileModel.isShared() ) {
                                fileModel.setParentId(Constant.ID_PARENT_DEFAULT);
                            }
                            mUserSession.getStrFiles().add(fileModel.getFileName());
                            mUserSession.getFiles().add(fileModel);
                        }
                        mUserSession.setDataFetching(false);
                        // Data fetch xong thi goi fragment dang hien thi load ra
                        notifyFragmentDataDone();
                    } catch (IOException e) {
                        //TODO handle
                    } catch (JSONException e) {
                        //TODO handle
                        Log.d("MinhNTn", "onResponse: JSONE");
                    } catch (ParseException e) {

                    }
                } else {
                    try {
                        if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                            doOnValidationExpired();
                        }
                        if (!TextUtils.isEmpty(response.errorBody().string())) {
                            Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                        }
                    } catch (IOException e) {
                        //
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("MinhNTn", "onFailure: ");
            }
        });

        mAddFab = findViewById(R.id.add_fab);
        mAddFab.setOnClickListener(view -> showBottomSheetDialog());

        // init avatar user
        //TODO event click vao avatar
        mUserAvatarImageView = findViewById(R.id.id_account);
        mUserAvatarImageView.setImageBitmap(Util.convertBase64ToBitmap(mUserSession.getUser().getAvatar()));
        mUserAvatarImageView.setOnClickListener(view -> {
            Dialog dialog = new Dialog(FileExploreActivity.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_info_user);

            CircularImageView avatar = dialog.findViewById(R.id.iv_info_avatar);
            TextView name = dialog.findViewById(R.id.tv_info_username);
            TextView mail = dialog.findViewById(R.id.tv_info_email);
            AppCompatButton logout = dialog.findViewById(R.id.bt_logout);

            avatar.setImageBitmap(Util.convertBase64ToBitmap(mUserSession.getUser().getAvatar()));
            name.setText(mUserSession.getUser().getUsername());
            mail.setText(mUserSession.getUser().getEmail());
            logout.setOnClickListener(view1 -> {
                //TODO clean all data when logout
                logout();
            });

            dialog.show();
        });

    }

    private void notifyFragmentDataDone() {
        Fragment fragment = getCurrentDisplayFragment();
        if (fragment != null ) {
            ((AwareDataStateChange) fragment).onDataLoadingFinished();
        }
        // Notify ca text search nua
        notifySearchDataChanged(null, 0);
    }

    private Fragment getCurrentDisplayFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_app_container);
        return fragment;
    }

    private void showBottomSheetDialog() {
        mBottomSheetNewFileDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        FileModel fileModel = intent.getParcelableExtra(OpenFileActivity.EXTRA_UPDATE_FILE_LOCATION, FileModel.class);
//        if (fileModel != null) {
//            for (int i = 0; i < mUserSession.getFiles().size(); i++) {
//                if (mUserSession.getFiles().get(i).getId() == fileModel.getId()) {
//                    mUserSession.getFiles().set(i, fileModel);
//                }
//            }
        ((AwareDataStateChange)getCurrentDisplayFragment()).onDataStateChanged();
//        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // TODO thuc hien hien fragment o day
        switch (item.getItemId()) {
            case R.id.footer_home: {
                HomeAppFragment homeAppFragment = new HomeAppFragment();
                homeAppFragment.setCallback(this);
                transactionFragment(R.id.main_app_container, homeAppFragment, true, true, HOME_APP_FRAG_NAME,
                        Constant.NO_ANIMATION, Constant.NO_ANIMATION, Constant.NO_ANIMATION, Constant.NO_ANIMATION);
                break;
            }
            case R.id.footer_favorite: {
                FavoriteFilesFragment favoriteFilesFragment = new FavoriteFilesFragment();
                favoriteFilesFragment.setCallback(this);
                transactionFragment(R.id.main_app_container, favoriteFilesFragment, true, true, HOME_APP_FRAG_NAME,
                        Constant.NO_ANIMATION, Constant.NO_ANIMATION, Constant.NO_ANIMATION, Constant.NO_ANIMATION);
                break;
            }
            case R.id.footer_folder: {
                FilesFragment filesFragment = new FilesFragment();
                filesFragment.setCallback(this);
                transactionFragment(R.id.main_app_container, filesFragment, true, true, HOME_APP_FRAG_NAME,
                        Constant.NO_ANIMATION, Constant.NO_ANIMATION, Constant.NO_ANIMATION, Constant.NO_ANIMATION);
                break;
            }
            case R.id.footer_share: {
                SharedFilesFragment sharedFilesFragment = new SharedFilesFragment();
                sharedFilesFragment.setCallback(this);
                transactionFragment(R.id.main_app_container, sharedFilesFragment, true, true, HOME_APP_FRAG_NAME,
                        Constant.NO_ANIMATION, Constant.NO_ANIMATION, Constant.NO_ANIMATION, Constant.NO_ANIMATION);
                break;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifySearchDataChanged(null, 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken()
                    , InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void transactionFragment(int layouID,
                                    Fragment fragment,
                                    boolean addToBackStack,
                                    boolean replaceAction,
                                    String nameBackStack,
                                    int animEnter, int animExit, int animPopEnter, int animPopExit){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(animEnter,animExit,animPopEnter,animPopExit);
        if(replaceAction){
            transaction.replace(layouID,fragment);
        } else {
            transaction.add(layouID,fragment);
        }
        if(addToBackStack) {
            transaction.addToBackStack(nameBackStack);
        }
        transaction.commit();
    }

    @Override
    public void doAnOrder(int order) {
        mBottomSheetDialog.show();
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
                        Fragment fragment = getCurrentDisplayFragment();
                        ((AwareDataStateChange) fragment).onDataStateChanged();
                    } else {
                        try {
                            if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                doOnValidationExpired();
                            }
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                            }
                        } catch (IOException e) {
                            //
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
            mBottomSheetDialog.hide();
        });
        mChangeFileNameTextView.setOnClickListener(view -> {
            if (currentFile.getOwner() != mUserSession.getUser().getId()) {
                Util.getMessageDialog(FileExploreActivity.this, getString(R.string.message_no_permission), null).show();
                return;
            }
            FileDto dtoFile = Util.convertToFileDto(currentFile);
            Util.getOptionInputTextDialog(FileExploreActivity.this,
                    getString(R.string.title_dialog_change_name_file), dtoFile.getFileName(), null, new Function() {
                        @Override
                        public void execute(Object object) {
                            String filename = (String) object;
                            // Check trung ten file
                            if (FileUtil.checkFileNameExisted(filename, dtoFile.getParentId(), dtoFile.getOwner(), mUserSession.getFiles())) {
                                Util.getMessageDialog(FileExploreActivity.this, "Có file với tên đã tồn tại", null).show();
                                return;
                            }
                            dtoFile.setFileName(filename);
                            Date date = new Date(Calendar.getInstance().getTimeInMillis());
                            dtoFile.setDate(date.toString());
                            mRequestHelper.saveFile(dtoFile, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        currentFile.setFileName((String) object);
                                        currentFile.setDate(date);
                                        mTargetFileNameTextView.setText(currentFile.getFileName());
                                        ((AwareDataStateChange)getCurrentDisplayFragment()).onDataStateChanged();
                                        //
                                        notifySearchDataChanged(currentFile, Constant.ACTION_CHANGE_NAME);
                                    } else {
                                        try {
                                            if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                                doOnValidationExpired();
                                            }
                                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                                Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                                            }

                                        } catch (IOException e) {
                                            //
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    //TODO
                                }
                            });
                        }
                    }).show();
        });
        //TODO luc xoa file check xoa ca local nua
        mDeleteFileTextView.setOnClickListener(view -> {
            if (currentFile.getOwner() != mUserSession.getUser().getId()) {
                Util.getMessageDialog(FileExploreActivity.this, getString(R.string.message_no_permission), null).show();
                return;
            }
            mRequestHelper.deleteFile(currentFile.getId(), new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        FileUtil.deleteFile(currentFile, mUserSession.getFiles());
                        ((AwareDataStateChange)getCurrentDisplayFragment()).onDataDeleted(currentFile);
                        notifySearchDataChanged(currentFile, Constant.ACTION_DELETE);
                    } else {
                        try {
                            if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                doOnValidationExpired();
                            }
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                            }

                        } catch (IOException e) {
                            //
                        }
                    }
                    mBottomSheetDialog.hide();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mBottomSheetDialog.hide();
                }
            });
        });
        mMoveFileTextView.setOnClickListener(view -> {
            if (currentFile.getOwner() != mUserSession.getUser().getId()) {
                Util.getMessageDialog(FileExploreActivity.this, getString(R.string.message_no_permission), null).show();
                return;
            }
            Intent intent = new Intent(FileExploreActivity.this, OpenFileActivity.class);
            intent.putExtra(OpenFileActivity.EXTRA_FILE_MOVING, currentFile);
            intent.putExtra(OpenFileActivity.EXTRA_FILE_OPEN, mUserSession.getRootFolder());
            intent.putExtra(OpenFileActivity.EXTRA_REASON_USE_ACTIVITY, OpenFileActivity.REASON_MOVE_FILE);
            intent.putExtra(OpenFileActivity.EXTRA_NEW_LOCATION, "");
            startActivity(intent);
            mBottomSheetDialog.hide();
        });
        mShareFileTextView.setOnClickListener(view -> {
            if (currentFile.getOwner() != mUserSession.getUser().getId()) {
                Util.getMessageDialog(FileExploreActivity.this, getString(R.string.message_no_permission), null).show();
                return;
            }
            Util.getOptionShareFileDialog(FileExploreActivity.this,
                    getString(R.string.title_dialog_share_file) + currentFile.getFileName(), null, null, new Function() {
                        @Override
                        public void execute(Object... objects) {
                            String email = (String) objects[0];
                            int permission = (int) objects[1];
                            mRequestHelper.shareFile(currentFile.getId(), permission, email, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Util.getMessageDialog(FileExploreActivity.this,
                                                getString(R.string.message_share_file_success), null).show();
                                    } else {
                                        try {
                                            if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                                doOnValidationExpired();
                                            }
                                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                                Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                                            }

                                        } catch (IOException e) {
                                            //
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("MinhNTn", "onFailure: ");
                                }
                            });
                        }
                    }).show();;
        });
        mBottomSheetDialog.show();
    }

    @Override
    public void back() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE_CODE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Util.getOptionInputImageDialog(this, getString(R.string.upload_image_file), imageBitmap, null, new Function() {
                    @Override
                    public void execute(Object object) {
                        try {
                            String name = Calendar.getInstance().getTimeInMillis() + ".png";
                            File image = Util.convertBitmapToFile(FileExploreActivity.this, imageBitmap, name);
                            FileModel temp = mUserSession.createNewFile(name, Constant.FileType.PNG);
                            mRequestHelper.saveFile(Util.convertToFileDto(temp), new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONArray array = new JSONArray(response.body().string());
                                            JSONObject object1 = array.getJSONObject(0);
                                            FileDto fileDto = Util.convertFromJSON(object1);
                                            temp.setId(fileDto.getId());
                                            mRequestHelper.uploadFile(temp.getId(), image, new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        mUserSession.getFiles().add(temp);
                                                        ((AwareDataStateChange)getCurrentDisplayFragment()).onDataCreated(temp);
                                                        notifySearchDataChanged(temp, Constant.ACTION_CREATE);
                                                    } else {
                                                        try {
                                                            if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                                                doOnValidationExpired();
                                                            }
                                                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                                                Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                                                            }

                                                        } catch (IOException e) {
                                                            //
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                }
                                            },image.getName());
                                        } catch (JSONException | IOException e) {
                                            //
                                        }
                                    } else {
                                        try {
                                            if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                                doOnValidationExpired();
                                            }
                                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                                Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                                            }

                                        } catch (IOException e) {
                                            //
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        } catch (IOException e) {
                            //TODO
                            e.printStackTrace();
                        }
                    }
                }).show();
            } else if (requestCode == REQUEST_FILE_UPLOAD_CODE) {
                Uri uri = data.getData();
                String name = FileUtil.getFileNameFromUri(this, uri);
                File file = FileUtil.getFileFromUri(this, uri);
                Util.getOptionInputTextDialog(this, getString(R.string.upload_file), name, null, new Function() {
                    @Override
                    public void execute(Object object) {
                        String fileName = (String) object;
                        // Check trung ten file
                        if (FileUtil.checkFileNameExisted(fileName, Constant.ID_PARENT_DEFAULT, mUserSession.getUser().getId(), mUserSession.getFiles())) {
                            Util.getMessageDialog(FileExploreActivity.this, "Có file với tên đã tồn tại", null).show();
                            return;
                        }
                        String type = getContentResolver().getType(uri);
                        FileModel temp = mUserSession.createNewFile(fileName, type);
                        mRequestHelper.saveFile(Util.convertToFileDto(temp), new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONArray array = new JSONArray(response.body().string());
                                        JSONObject object1 = array.getJSONObject(0);
                                        FileDto fileDto = Util.convertFromJSON(object1);
                                        temp.setId(fileDto.getId());
                                        mRequestHelper.uploadFile(temp.getId(), file, new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    mUserSession.getFiles().add(temp);
                                                    ((AwareDataStateChange)getCurrentDisplayFragment()).onDataCreated(temp);
                                                    notifySearchDataChanged(temp, Constant.ACTION_CREATE);
                                                } else {
                                                    try {
                                                        if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                                            doOnValidationExpired();
                                                        }
                                                    } catch (IOException e) {
                                                        //
                                                    }
                                                    mRequestHelper.deleteFile(temp.getId(), new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                            // Khong lam gi ca
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                            // Khong lam gi ca
                                                        }
                                                    });
                                                    try {
                                                        if (!TextUtils.isEmpty(response.errorBody().string())) {
                                                            Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                                                        }
                                                    } catch (IOException e) {
                                                        //
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        }, fileName);
                                    } catch (JSONException | IOException e) {
                                        //
                                    }
                                } else {
                                    try {
                                        if (response.errorBody().string().equals(Constant.MESSAGE_AUTHENTICATION_FAIL)) {
                                            doOnValidationExpired();
                                        }
                                        if (!TextUtils.isEmpty(response.errorBody().string())) {
                                            Util.getMessageDialog(FileExploreActivity.this, response.errorBody().string(), null).show();
                                        }
                                    } catch (IOException e) {
                                        //
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }).show();
            }
        }
    }

    private void notifySearchDataChanged(FileModel fileModel, int action) {
        if (fileModel != null) {
            mUserSession.updateStrFile(fileModel, action);
        }

        mSearchAdapter.clear();
        mSearchAdapter.addAll(new ArrayList<>(mUserSession.getStrFiles()));
        // temp list o day tranh tham chieu cua strFiles bi xoa sach
        mSearchAdapter.notifyDataSetChanged();
        Log.d("MinhNTn", "notifySearchDataChanged: " + mSearchAdapter);
    }

    private void logout() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(Constant.JWT);
        editor.apply();
        File storage = new File(getFilesDir() + "/" + mUserSession.getUser().getId());
        if (storage != null) {
            FileUtil.deleteFileOrFolder(storage);
        }
        mUserSession.clearSession();
        Intent logoutIntent = new Intent(FileExploreActivity.this, LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logoutIntent);
    }

    private void doOnValidationExpired() {
        Util.getMessageDialog(this, getString(R.string.message_session_out), new Function() {
            @Override
            public void execute() {
                logout();
            }
        }).show();
    }
}

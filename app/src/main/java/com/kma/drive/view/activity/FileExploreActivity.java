package com.kma.drive.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import java.text.ParseException;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileExploreActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FragmentCallback {
    public static final String HOME_APP_FRAG_NAME = "HOME APP FRAGMENT";
    public static final int REQUEST_IMAGE_CAPTURE_CODE = 101;
    // component
    private BottomNavigationView mBottomNavigationView;
    // component cua bottom dialog file - start
    private BottomSheetDialog mBottomSheetDialog;
    private TextView mTargetFileNameTextView;
    private TextView mFavoriteFunctionTextView;
    private TextView mChangeFileNameTextView;
    private TextView mDeleteFileTextView;
    private TextView mMoveFileTextView;
    // component cua bottom dialog - end
    // component bottom dialog new file - start
    private BottomSheetDialog mBottomSheetNewFileDialog;
    // component bottom dialog new file - end
    private CircularImageView mUserAvatarImageView;

    // etc
    private UserSession mUserSession;
    protected HttpRequestHelper mRequestHelper;

    private FloatingActionButton mAddFab;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserSession = UserSession.getInstance();
        mRequestHelper = new HttpRequestHelper((mUserSession.getUser() == null)? null: mUserSession.getUser().getJwt());

        // cai dat bottom sheet - start
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        mTargetFileNameTextView = mBottomSheetDialog.findViewById(R.id.tv_filename_current);
        mFavoriteFunctionTextView = mBottomSheetDialog.findViewById(R.id.tv_favorite);
        mChangeFileNameTextView = mBottomSheetDialog.findViewById(R.id.tv_rename);
        mDeleteFileTextView = mBottomSheetDialog.findViewById(R.id.tv_delete);
        mMoveFileTextView = mBottomSheetDialog.findViewById(R.id.tv_move);
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
                            FileModel temp = mUserSession.createNewFile(folder, Constant.FileType.FOLDER);
                            FileDto fileDto = Util.convertToFileDto(temp);
                            mRequestHelper.saveFile(fileDto, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject file = new JSONObject(response.body().string());
                                            FileDto tempDto = Util.convertFromJSON(file);
                                            if (tempDto != null) {
                                                temp.setId(tempDto.getId());
                                                mUserSession.getFiles().add(temp);
                                                ((AwareDataStateChange)getCurrentDisplayFragment()).onDataCreated(temp);
                                            }
                                        } catch (JSONException | IOException e) {
                                            //TODO
                                        }

                                    } else {
                                        Util.getMessageDialog(FileExploreActivity.this, response.body().toString(), null);
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
        //cai dat bottom sheet dialog new file - end

        mBottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        // Mac dinh start activity nay la home_frag hien ra
        mBottomNavigationView.setSelectedItemId(R.id.footer_home);

        // fetch files user tu server ve
        mUserSession.setDataFetching(true);
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
    }

    private void notifyFragmentDataDone() {
        Fragment fragment = getCurrentDisplayFragment();
        if (fragment != null ) {
            ((AwareDataStateChange) fragment).onDataLoadingFinished();
        }
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
        FileModel fileModel = intent.getParcelableExtra(OpenFileActivity.EXTRA_UPDATE_FILE_LOCATION, FileModel.class);
        if (fileModel != null) {
            for (int i = 0; i < mUserSession.getFiles().size(); i++) {
                if (mUserSession.getFiles().get(i).getId() == fileModel.getId()) {
                    mUserSession.getFiles().set(i, fileModel);
                }
            }
            ((AwareDataStateChange)getCurrentDisplayFragment()).onDataStateChanged(fileModel);
        }
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
                        Util.getMessageDialog(FileExploreActivity.this, response.body().toString(), null);
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
            Util.getOptionInputTextDialog(FileExploreActivity.this,
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
                                        ((AwareDataStateChange)getCurrentDisplayFragment()).onDataStateChanged();
                                    } else {
                                        Util.getMessageDialog(FileExploreActivity.this, response.body().toString(), null);
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
            mRequestHelper.deleteFile(currentFile.getId(), new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        mUserSession.getFiles().remove(currentFile);
                        ((AwareDataStateChange)getCurrentDisplayFragment()).onDataDeleted(currentFile);
                    } else {
                        Util.getMessageDialog(FileExploreActivity.this, response.body().toString(), null);
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
            Intent intent = new Intent(FileExploreActivity.this, OpenFileActivity.class);
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
                            String name = Calendar.getInstance().getTimeInMillis() + "." + Constant.FileType.PNG;
                            File image = Util.convertBitmapToFile(FileExploreActivity.this, imageBitmap, name);
                            FileModel temp = mUserSession.createNewFile(name, Constant.FileType.PNG);
                            mRequestHelper.saveFile(Util.convertToFileDto(temp), new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject object1 = new JSONObject(response.body().string());
                                            FileDto fileDto = Util.convertFromJSON(object1);
                                            temp.setId(fileDto.getId());
                                            mRequestHelper.uploadFile(temp.getId(), image, new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        mUserSession.getFiles().add(temp);
                                                        ((AwareDataStateChange)getCurrentDisplayFragment()).onDataCreated(temp);
                                                    } else {
                                                        Util.getMessageDialog(FileExploreActivity.this, response.body().toString(), null);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                }
                                            });
                                        } catch (JSONException | IOException e) {
                                            //
                                        }
                                    } else {
                                        Util.getMessageDialog(FileExploreActivity.this, response.body().toString(), null);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        } catch (IOException e) {
                            //TODO
                        }
                    }
                }).show();
            }
        }
    }
}

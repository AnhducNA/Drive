package com.kma.drive.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kma.drive.R;
import com.kma.drive.callback.AwareDataStateChange;
import com.kma.drive.callback.FragmentCallback;
import com.kma.drive.common.Constant;
import com.kma.drive.dto.FileDto;
import com.kma.drive.session.UserSession;
import com.kma.drive.util.HttpRequestHelper;
import com.kma.drive.view.fragment.FavoriteFilesFragment;
import com.kma.drive.view.fragment.FilesFragment;
import com.kma.drive.view.fragment.HomeAppFragment;
import com.kma.drive.view.fragment.SharedFilesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileExploreActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FragmentCallback {
    public static final String HOME_APP_FRAG_NAME = "HOME APP FRAGMENT";
    // component
    private BottomNavigationView mBottomNavigationView;
    private BottomSheetDialog mBottomSheetDialog;

    // etc
    private UserSession mUserSession;
    protected HttpRequestHelper mRequestHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserSession = UserSession.getInstance();

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        mBottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        // Mac dinh start activity nay la home_frag hien ra
        mBottomNavigationView.setSelectedItemId(R.id.footer_home);

        // fetch files user tu server ve
        mUserSession.setDataFetching(true);
        mRequestHelper = new HttpRequestHelper(mUserSession.getUser().getJwt());
        mRequestHelper.getAllFilesForUser(mUserSession.getUser().getId(), new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject file = array.getJSONObject(i);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                            FileDto fileDto = new FileDto((long) file.getInt(FileDto.ID),
                                    file.getString(FileDto.FILE_NAME),
                                    new Date(format.parse(file.getString(FileDto.DATE)).getTime()),
                                    file.getBoolean(FileDto.FAVORITE),
                                    file.getString(FileDto.TYPE),
                                    file.getLong(FileDto.OWNER));
                            mUserSession.getFiles().add(fileDto);
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

        // Bottom sheet dialog
    }

    private void notifyFragmentDataDone() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_app_container);
        if (fragment != null ) {
            ((AwareDataStateChange) fragment).onDataLoadingFinished();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // TODO thuc hien hien fragment o day
        switch (item.getItemId()) {
            case R.id.footer_home: {
                HomeAppFragment homeAppFragment = new HomeAppFragment();
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

    }

    @Override
    public void back() {

    }
}

package com.kma.drive.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kma.drive.R;
import com.kma.drive.common.Constant;
import com.kma.drive.view.fragment.FavoriteFilesFragment;
import com.kma.drive.view.fragment.FilesFragment;
import com.kma.drive.view.fragment.HomeAppFragment;
import com.kma.drive.view.fragment.SharedFilesFragment;

public class FileExploreActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static final String HOME_APP_FRAG_NAME = "HOME APP FRAGMENT";
    private BottomNavigationView mBottomNavigationView;

    private FloatingActionButton mAddFab;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        // Mac dinh start activity nay la home_frag hien ra
        mBottomNavigationView.setSelectedItemId(R.id.footer_home);

//        LinearLayout mAddNewModelMain = findViewById(R.id.add_new_model_main);
//        mAddNewModelMain.setVisibility(View.VISIBLE);

        mAddFab = findViewById(R.id.add_fab);
        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showBottomSheetDialog();
            }
        });
    }
    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.add_new_bottom_sheet_dialog);

        LinearLayout folder = bottomSheetDialog.findViewById(R.id.add_new_folder_main);
        LinearLayout file = bottomSheetDialog.findViewById(R.id.add_new_file_main);
        LinearLayout useCamera = bottomSheetDialog.findViewById(R.id.use_camera_main);
        bottomSheetDialog.show();
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
        }else {
            transaction.add(layouID,fragment);
        }
        if(addToBackStack) {
            transaction.addToBackStack(nameBackStack);
        }
        transaction.commit();
    }
}

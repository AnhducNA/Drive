package com.kma.drive.view.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kma.drive.R;
import com.kma.drive.common.Constant;
import com.kma.drive.view.fragment.HomeAppFragment;

public class FileExploreActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static final String HOME_APP_FRAG_NAME = "HOME APP FRAGMENT";
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        // Mac dinh start activity nay la home_frag hien ra
        mBottomNavigationView.setSelectedItemId(R.id.footer_home);
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
                break;
            }
            case R.id.footer_folder: {
                break;
            }
            case R.id.footer_share: {
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

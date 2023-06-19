package com.kma.drive.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import com.kma.drive.R;
import com.kma.drive.callback.FragmentCallback;
import com.kma.drive.common.Constant;
import com.kma.drive.view.fragment.BaseAbstractFragment;
import com.kma.drive.view.fragment.LoginFragment;
import com.kma.drive.view.fragment.RegisterFragment;

public class LoginActivity extends AppCompatActivity implements FragmentCallback {
    private BaseAbstractFragment mLoginFragment;
    private BaseAbstractFragment mRegisterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        //TODO: man dau tien nen tao them splash screen nhung tam thoi vao man login truoc
        mLoginFragment = new LoginFragment();
        mLoginFragment.setCallback(this);
        mRegisterFragment = new RegisterFragment();
        mRegisterFragment.setCallback(this);

        transactionFragment(R.id.main_container, mLoginFragment
                , false, true
                , Constant.NO_ANIMATION,Constant.NO_ANIMATION ,Constant.NO_ANIMATION ,Constant.NO_ANIMATION);
    }

    /**
     * Thuc hien viec transaction cho fragment
     * @param layouID: id cua vung thuc hien transaction cho fragment
     * @param fragment: fragment thuc hien
     * @param addToBackStack: co add vao backstack khong
     * @param replaceAction: thuc hien replace hay add
     * @param animEnter: animation luc fragment vao
     * @param animExit:
     * @param animPopEnter
     * @param animPopExit
     */
    public void transactionFragment(int layouID,
                             Fragment fragment,
                             boolean addToBackStack,
                             boolean replaceAction,
                             int animEnter, int animExit, int animPopEnter, int animPopExit){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(animEnter,animExit,animPopEnter,animPopExit);
        if(replaceAction){
            transaction.replace(layouID,fragment);
        }else {
            transaction.add(layouID,fragment);
        }
        if(addToBackStack) {
            transaction.addToBackStack("add");
        }
        transaction.commit();
    }

    @Override
    public void doAnOrder(int order) {
        switch (order) {
            case LoginFragment.ORDER_REGISTER_ACCOUNT: {
                transactionFragment(R.id.main_container,
                        mRegisterFragment, true, true,
                        R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            }
            case LoginFragment.ORDER_LOGIN_SUCCESS: {
                Intent intent = new Intent(this, FileExploreActivity.class);
                //TODO set user data can thiet o day truoc khi start activity
                startActivity(intent);
                // Sau khi start xong thi activity nay khong con nhiem vu nua, kill
                this.finish();
                break;
            }
            case RegisterFragment.ORDER_REGISTER_DONE: {
                getSupportFragmentManager().popBackStack();
                break;
            }
        }
    }

    @Override
    public void doAnOrderWithParams(int order, Object... objects) {
        switch (order) {
            case RegisterFragment.GET_LOCAL_IMAGE: {
                startActivityForResult((Intent)objects[0], (int)objects[1]);
                break;
            }
        }
    }

    @Override
    public void back() {
        getSupportFragmentManager().popBackStack();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case RegisterFragment.GET_LOCAL_IMAGE_CODE: {
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    if (mRegisterFragment != null) {
                        ((RegisterFragment) mRegisterFragment).setAvatarImage(imageUri);
                    }
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
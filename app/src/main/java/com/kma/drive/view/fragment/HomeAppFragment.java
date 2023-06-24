package com.kma.drive.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.kma.drive.R;
import com.kma.drive.callback.AwareDataStateChange;

public class HomeAppFragment extends BaseAbstractFragment implements AwareDataStateChange {

    @Override
    protected int getLayout() {
        return R.layout.home_app_fragment;
    }

    @Override
    protected void doOnViewCreated(View view, Bundle bundle) {

    }

    @Override
    public void onDataLoadingFinished() {

    }
}

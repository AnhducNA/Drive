package com.kma.drive.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kma.drive.callback.FragmentCallback;
import com.kma.drive.util.HttpRequestHelper;

import java.lang.ref.WeakReference;

public abstract class BaseAbstractFragment extends Fragment{
    protected WeakReference<Context> mContext;

    protected FragmentCallback mCallback;
    protected HttpRequestHelper mRequestHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = new WeakReference<>(context);
        mRequestHelper = new HttpRequestHelper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayout(), container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        doOnViewCreated(view, savedInstanceState);
    }

    public void setCallback(FragmentCallback callback) {
        mCallback = callback;
    }

    protected abstract int getLayout();

    protected abstract void doOnViewCreated(View view, Bundle bundle);
}

package com.kma.drive.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.drive.R;
import com.kma.drive.adapter.SanPhamAdapter;
import com.kma.drive.callback.FragmentCallback;
import com.kma.drive.model.SanPham;
import com.kma.drive.util.HttpRequestHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

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
        View rootView = inflater.inflate(getLayout(), container, false);
        // 1. get a reference to recyclerView
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.home_recyclerView);
        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // this is data from recycler view
        ArrayList<SanPham> listSanPham  = new ArrayList<>();
        listSanPham.add( new SanPham("Folder1", R.drawable.ic_folder));
        listSanPham.add( new SanPham("Folder1", R.drawable.ic_folder));
        listSanPham.add( new SanPham("Folder1", R.drawable.folder_shared));
        listSanPham.add( new SanPham("Folder1", R.drawable.folder_shared));

        // 3. create an adapter
        SanPhamAdapter mAdapter = new SanPhamAdapter(this.getContext(), listSanPham);
        // 4. set adapter
        recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
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

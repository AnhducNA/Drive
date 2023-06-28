package com.kma.drive.view.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.drive.R;
import com.kma.drive.adapter.FileAdapter;
import com.kma.drive.callback.AwareDataStateChange;
import com.kma.drive.callback.ItemFileClickListener;
import com.kma.drive.model.FileModel;
import com.kma.drive.view.activity.OpenFileActivity;

import java.util.ArrayList;
import java.util.List;

public class SharedFilesFragment extends BaseAbstractFragment implements AwareDataStateChange, ItemFileClickListener {
    private LinearLayout mEmptyFolderLinearLayout;
    private FileAdapter mFileAdapter;
    private RecyclerView mSharedFilesRecyclerView;
    private ProgressBar mLoadingDataProgressBar;
    private List<FileModel> mSharedFiles;

    @Override
    protected int getLayout() {
        return R.layout.shared_files_fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void doOnViewCreated(View view, Bundle bundle) {
        mEmptyFolderLinearLayout = view.findViewById(R.id.layout_empty_folder);
        mSharedFilesRecyclerView = view.findViewById(R.id.rv_shared_files);
        mLoadingDataProgressBar = view.findViewById(R.id.pb_loading_data);
        mSharedFiles = new ArrayList<>();

        if (mUserSession.isDataFetching()) {
            mLoadingDataProgressBar.setVisibility(View.VISIBLE);
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        } else {
            setVisibleEmptyView();
            mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        }

        mFileAdapter = new FileAdapter(mContext.get(), mSharedFiles, mCallback, this, true);
        mSharedFilesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext.get()));
        mSharedFilesRecyclerView.setAdapter(mFileAdapter);

        getSharedFiles();
        setVisibleEmptyView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getSharedFiles() {
        mSharedFiles.clear();
        mUserSession.getFiles().stream().forEach(fileDto -> {
            if (fileDto.isShared()) {
                mSharedFiles.add(fileDto);
            }
        });
        if (mFileAdapter != null) {
            mFileAdapter.notifyDataSetChanged();
        }
    }

    private void setVisibleEmptyView() {
        if (mSharedFiles.isEmpty()) {
            mEmptyFolderLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataLoadingFinished() {
        getSharedFiles();
        mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        setVisibleEmptyView();
    }

    @Override
    public void onDataStateChanged() {
        if (mFileAdapter != null) {
            mFileAdapter.notifyDataSetChanged();
            setVisibleEmptyView();
        }
    }

    @Override
    public void onDataStateChanged(FileModel fileModel) {

    }

    @Override
    public void onDataDeleted(FileModel fileModel) {
        mSharedFiles.remove(fileModel);
        onDataStateChanged();
    }

    @Override
    public void onDataCreated(FileModel fileModel) {
        // khong co file nao duoc tao va share de update
    }

    @Override
    public void open(long id) {
        final FileModel fileModel = getFileById(id);
        if (fileModel == null) {
            //TODO do something here
            return;
        }
        //TODO nen check cache xem file nay da duoc tai ve hay chua
        Intent intent = new Intent(mContext.get(), OpenFileActivity.class);
        intent.putExtra(OpenFileActivity.EXTRA_FILE_OPEN, fileModel);
        mContext.get().startActivity(intent);
    }

    public FileModel getFileById(long id) {
        for (FileModel fileDto: mSharedFiles) {
            if (fileDto.getId() == id) {
                return fileDto;
            }
        }

        return null;
    }
}

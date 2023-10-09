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
import com.kma.drive.common.Constant;
import com.kma.drive.model.FileModel;
import com.kma.drive.view.activity.OpenFileActivity;

import java.util.ArrayList;
import java.util.List;

public class FilesFragment extends BaseAbstractFragment implements AwareDataStateChange, ItemFileClickListener {
    private  RecyclerView mRecyclerView;
    private FileAdapter mFileAdapter;
    private ProgressBar mLoadingDataProgressBar;
    private LinearLayout mEmptyFolderLinearLayout;
    private List<FileModel> mFiles;

    @Override
    protected int getLayout() {
        return R.layout.files_fragment;
    }

    @Override
    protected void doOnViewCreated(View rootView, Bundle bundle) {
        mRecyclerView = rootView.findViewById(R.id.folderRecyclerView);
        mLoadingDataProgressBar = rootView.findViewById(R.id.pb_loading_data);
        mEmptyFolderLinearLayout = rootView.findViewById(R.id.layout_empty_folder);
        mFiles = new ArrayList<>();

        if (mUserSession.isDataFetching()) {
            mLoadingDataProgressBar.setVisibility(View.VISIBLE);
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        } else {
            getFiles();
            setVisibleEmptyView();
            mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        }

        mFileAdapter = new FileAdapter(mContext.get(), mFiles, mCallback, this, true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext.get()));
        mRecyclerView.setAdapter(mFileAdapter);
    }

    private void setVisibleEmptyView() {
        if (mFiles.isEmpty()) {
            mEmptyFolderLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDataLoadingFinished() {
        getFiles();
        mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        setVisibleEmptyView();
    }

    @Override
    public void onDataStateChanged() {
        if (mFileAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getFiles();
            }
            mFileAdapter.notifyDataSetChanged();
            setVisibleEmptyView();
        }
    }

    @Override
    public void onDataStateChanged(FileModel fileModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getFiles();
        }
        onDataStateChanged();
    }

    @Override
    public void onDataDeleted(FileModel fileModel) {
        mFiles.remove(fileModel);
        onDataStateChanged();
    }

    @Override
    public void onDataCreated(FileModel fileModel) {
        if (fileModel.getParentId() == Constant.ID_PARENT_DEFAULT) {
            mFiles.add(fileModel);
            onDataStateChanged();
        }
    }

    private void getFiles() {
        mFiles.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mUserSession.getFiles().stream().filter(fileModel -> (fileModel.getParentId() == Constant.ID_PARENT_DEFAULT))
                    .forEach(fileDto -> {
                        mFiles.add(fileDto);
                    });
        } else {
            for (FileModel fileModel: mUserSession.getFiles()) {
                if (fileModel.getParentId() == Constant.ID_PARENT_DEFAULT) {
                    mFiles.add(fileModel);
                }
            }
        }
        if (mFileAdapter != null) {
            mFileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void open(long id) {
        final FileModel fileDto = getFileById(id);
        if (fileDto == null) {
            //TODO do something here
            return;
        }
        //TODO nen check cache xem file nay da duoc tai ve hay chua
        Intent intent = new Intent(mContext.get(), OpenFileActivity.class);
        intent.putExtra(OpenFileActivity.EXTRA_FILE_OPEN, fileDto);
        mContext.get().startActivity(intent);
    }

    public FileModel getFileById(long id) {
        for (FileModel fileDto: mFiles) {
            if (fileDto.getId() == id) {
                return fileDto;
            }
        }

        return null;
    }
}

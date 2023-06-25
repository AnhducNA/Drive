package com.kma.drive.view.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.drive.R;
import com.kma.drive.adapter.FileAdapter;
import com.kma.drive.callback.AwareDataStateChange;
import com.kma.drive.adapter.FileFolderAdapter;
import com.kma.drive.callback.ItemFileClickListener;
import com.kma.drive.dto.FileDto;
import com.kma.drive.model.FileModel;
import com.kma.drive.model.SanPham;
import com.kma.drive.view.activity.OpenFileActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void doOnViewCreated(View rootView, Bundle bundle) {
        mRecyclerView = rootView.findViewById(R.id.folderRecyclerView);
        mLoadingDataProgressBar = rootView.findViewById(R.id.pb_loading_data);
        mEmptyFolderLinearLayout = rootView.findViewById(R.id.layout_empty_folder);
        mFiles = new ArrayList<>();

        if (mUserSession.isDataFetching()) {
            mLoadingDataProgressBar.setVisibility(View.VISIBLE);
        } else {
            getFiles();
            setVisibleEmptyView();
            mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        }

        mFileAdapter = new FileAdapter(mContext.get(), mFiles, mCallback, this);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataLoadingFinished() {
        getFiles();
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
    public void onDataDeleted(FileModel fileModel) {
        mFiles.remove(fileModel);
        onDataStateChanged();
    }

    @Override
    public void onDataCreated(FileModel fileModel) {
        mFiles.add(fileModel);
        onDataStateChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getFiles() {
        mUserSession.getFiles().stream().forEach(fileDto -> mFiles.add(fileDto));
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

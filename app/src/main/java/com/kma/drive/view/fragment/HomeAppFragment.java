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
import com.kma.drive.adapter.FileMainAdapter;
import com.kma.drive.callback.ItemFileClickListener;
import com.kma.drive.common.Constant;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Fragment nay chi hien thi file
public class HomeAppFragment extends BaseAbstractFragment implements AwareDataStateChange, ItemFileClickListener {
    private  RecyclerView mRecyclerView;
    private FileAdapter mFileAdapter;
    private ProgressBar mLoadingDataProgressBar;
    private LinearLayout mEmptyFolderLinearLayout;

    private List<FileModel> mRecentFiles;

    @Override
    protected int getLayout() {
        return R.layout.home_app_fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void doOnViewCreated(View rootView, Bundle bundle) {
        mRecyclerView = rootView.findViewById(R.id.home_recyclerView);
        mLoadingDataProgressBar = rootView.findViewById(R.id.pb_loading_data);
        mEmptyFolderLinearLayout = rootView.findViewById(R.id.layout_empty_folder);
        mRecentFiles = new ArrayList<>();

        if (mUserSession.isDataFetching()) {
            mLoadingDataProgressBar.setVisibility(View.VISIBLE);
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        } else {
            getRecentFiles();
            setVisibleEmptyView();
            mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        }

        mFileAdapter = new FileAdapter(mContext.get(), mRecentFiles, mCallback, this, true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext.get()));
        mRecyclerView.setAdapter(mFileAdapter);
    }

    private void setVisibleEmptyView() {
        if (mRecentFiles.isEmpty()) {
            mEmptyFolderLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataLoadingFinished() {
        getRecentFiles();
        mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        setVisibleEmptyView();
    }

    @Override
    public void onDataStateChanged() {
        if (mFileAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getRecentFiles();
            }
            mFileAdapter.notifyDataSetChanged();
            setVisibleEmptyView();
        }
    }

    @Override
    public void onDataStateChanged(FileModel fileModel) {

    }

    @Override
    public void onDataDeleted(FileModel fileModel) {
        mRecentFiles.remove(fileModel);
        onDataStateChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataCreated(FileModel fileModel) {
        mRecentFiles.add(fileModel);
        if (mRecentFiles.size() > Constant.MAX_RECENT_FILE_DISPLAY) {
            mRecentFiles.remove(4);
            mRecentFiles.stream().sorted(Comparator.comparing(FileModel::getDate).reversed());
        }
        onDataStateChanged();
    }

    @Override
    public void open(long id) {
        final FileModel fileDto = getFileById(id);
        if (fileDto == null) {
            //TODO do something here
            return;
        }
        Intent intent = new Intent(mContext.get(), OpenFileActivity.class);
        intent.putExtra(OpenFileActivity.EXTRA_FILE_OPEN, fileDto);
        mContext.get().startActivity(intent);
    }

    public FileModel getFileById(long id) {
        for (FileModel fileDto: mRecentFiles) {
            if (fileDto.getId() == id) {
                return fileDto;
            }
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getRecentFiles() {
        mRecentFiles.clear();
        List<FileModel> temp = mUserSession.getFiles().stream().filter(fileModel -> (!fileModel.getType().equals(Constant.FileType.FOLDER)))
                .sorted(Comparator.comparing(FileModel::getDate).reversed())
                        .collect(Collectors.toList());
        for (int i = 0; i < Constant.MAX_RECENT_FILE_DISPLAY; i++) {
            try {
                mRecentFiles.add(temp.get(i));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        if (mFileAdapter != null) {
            mFileAdapter.notifyDataSetChanged();
        }
    }
}

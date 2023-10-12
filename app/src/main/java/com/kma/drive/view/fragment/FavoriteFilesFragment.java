package com.kma.drive.view.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.drive.R;
import com.kma.drive.adapter.FileAdapter;
import com.kma.drive.callback.AwareDataStateChange;
import com.kma.drive.callback.ItemFileClickListener;
import com.kma.drive.dto.FileDto;
import com.kma.drive.model.FileModel;
import com.kma.drive.view.activity.OpenFileActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFilesFragment extends BaseAbstractFragment implements AwareDataStateChange, ItemFileClickListener {
    private RecyclerView mFavoriteFilesRecyclerView;
    private FileAdapter mFileAdapter;
    private ProgressBar mLoadingDataProgressBar;
    private LinearLayout mEmptyFolderLinearLayout;
    private List<FileModel> mFavoriteFiles;

    @Override
    protected int getLayout() {
        return R.layout.fav_files_fragment;
    }

    @Override
    protected void doOnViewCreated(View view, Bundle bundle) {
        mFavoriteFilesRecyclerView = view.findViewById(R.id.rv_favorite_files);
        mLoadingDataProgressBar = view.findViewById(R.id.pb_loading_data);
        mEmptyFolderLinearLayout = view.findViewById(R.id.layout_empty_folder);
        mFavoriteFiles = new ArrayList<>();

        if (mUserSession.isDataFetching()) {
            mLoadingDataProgressBar.setVisibility(View.VISIBLE);
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        } else {
            setVisibleEmptyView();
            mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        }

        mFileAdapter = new FileAdapter(mContext.get(), mFavoriteFiles, mCallback, this, true);
        mFavoriteFilesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext.get()));
        mFavoriteFilesRecyclerView.setAdapter(mFileAdapter);

        getFavoriteFiles();
        setVisibleEmptyView();
    }

    private void setVisibleEmptyView() {
        if (mFavoriteFiles.isEmpty()) {
            mEmptyFolderLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDataLoadingFinished() {
        getFavoriteFiles();
        mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        setVisibleEmptyView();
    }

    @Override
    public void onDataStateChanged() {
        if (mFileAdapter != null) {
            for (int i = 0; i < mFavoriteFiles.size(); i++) {
                if (!mFavoriteFiles.get(i).isFavorite()) {
                    mFavoriteFiles.remove(mFavoriteFiles.get(i));
                }
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
        mFavoriteFiles.remove(fileModel);
        onDataStateChanged();
    }

    @Override
    public void onDataCreated(FileModel fileModel) {
        // file moi them vao mac dinh khong phai favorite nen khong can notifydatachage()
    }

    private void getFavoriteFiles() {
        mFavoriteFiles.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mUserSession.getFiles().stream().forEach(fileModel -> {
                if (fileModel.isFavorite()) {
                    mFavoriteFiles.add(fileModel);
                }
            });
        } else {
            for (FileModel fileModel: mUserSession.getFiles()) {
                if (fileModel.isFavorite()) {
                    mFavoriteFiles.add(fileModel);
                }
            }
        }
        if (mFileAdapter != null) {
            mFileAdapter.notifyDataSetChanged();
        }
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
        for (FileModel fileDto: mFavoriteFiles) {
            if (fileDto.getId() == id) {
                return fileDto;
            }
        }

        return null;
    }
}

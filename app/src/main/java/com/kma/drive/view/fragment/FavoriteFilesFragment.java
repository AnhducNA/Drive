package com.kma.drive.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.drive.R;
import com.kma.drive.adapter.FileAdapter;
import com.kma.drive.callback.AwareDataStateChange;
import com.kma.drive.callback.ItemFileClickListener;
import com.kma.drive.dto.FileDto;

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

public class FavoriteFilesFragment extends BaseAbstractFragment implements AwareDataStateChange, ItemFileClickListener {
    private RecyclerView mFavoriteFilesRecyclerView;
    private FileAdapter mFileAdapter;
    private ProgressBar mLoadingDataProgressBar;
    private List<FileDto> mFavoriteFiles;

    @Override
    protected int getLayout() {
        return R.layout.fav_files_fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void doOnViewCreated(View view, Bundle bundle) {
        mFavoriteFilesRecyclerView = view.findViewById(R.id.rv_favorite_files);
        mLoadingDataProgressBar = view.findViewById(R.id.pb_loading_data);
        mFavoriteFiles = new ArrayList<>();

        if (mUserSession.isDataFetching()) {
            mLoadingDataProgressBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
        }

        mFileAdapter = new FileAdapter(mContext.get(), mFavoriteFiles, mCallback, this);
        mFavoriteFilesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext.get()));
        mFavoriteFilesRecyclerView.setAdapter(mFileAdapter);

        getFavoriteFiles();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataLoadingFinished() {
        getFavoriteFiles();
        mLoadingDataProgressBar.setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getFavoriteFiles() {
        mFavoriteFiles.clear();
        mUserSession.getFiles().stream().forEach(fileDto -> {
            if (fileDto.isFavorite()) {
                mFavoriteFiles.add(fileDto);
            }
        });
        if (mFileAdapter != null) {
            mFileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void open(long id) {
        final FileDto fileDto = getFileById(id);
        if (fileDto == null) {
            //TODO do something here
            return;
        }
        //TODO nen check cache xem file nay da duoc tai ve hay chua
        mRequestHelper.downloadFile(id, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    InputStream inputStream = response.body().byteStream();

                    File file = new File(mContext.get().getFilesDir(), fileDto.getEntireFileName());
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public FileDto getFileById(long id) {
        for (FileDto fileDto: mFavoriteFiles) {
            if (fileDto.getId() == id) {
                return fileDto;
            }
        }

        return null;
    }
}

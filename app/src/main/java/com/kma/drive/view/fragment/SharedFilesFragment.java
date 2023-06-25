package com.kma.drive.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.kma.drive.R;
import com.kma.drive.adapter.FileAdapter;
import com.kma.drive.callback.AwareDataStateChange;
import com.kma.drive.model.FileModel;

import java.util.ArrayList;
import java.util.List;

public class SharedFilesFragment extends BaseAbstractFragment implements AwareDataStateChange {
    private LinearLayout mEmptyFolderLinearLayout;
    private FileAdapter mFileAdapter;

    private List<FileModel> mSharedFiles;

    @Override
    protected int getLayout() {
        return R.layout.shared_files_fragment;
    }

    @Override
    protected void doOnViewCreated(View view, Bundle bundle) {
        mEmptyFolderLinearLayout = view.findViewById(R.id.layout_empty_folder);
        mSharedFiles = new ArrayList<>();
    }

    private void setVisibleEmptyView() {
        if (mSharedFiles.isEmpty()) {
            mEmptyFolderLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyFolderLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDataLoadingFinished() {

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

    }

    @Override
    public void onDataCreated(FileModel fileModel) {

    }
}

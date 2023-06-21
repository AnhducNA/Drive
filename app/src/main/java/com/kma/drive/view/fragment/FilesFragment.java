package com.kma.drive.view.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.drive.R;
import com.kma.drive.adapter.FileFolderAdapter;
import com.kma.drive.model.SanPham;

import java.util.ArrayList;

public class FilesFragment extends BaseAbstractFragment{
    @Override
    protected int getLayout() {
        return R.layout.files_fragment;
    }

    @Override
    protected void doOnViewCreated(View rootView, Bundle bundle) {
        // 1. get a reference to recyclerView
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.folderRecyclerView);
        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // this is data from recycler view
        ArrayList<SanPham> listSanPham  = new ArrayList<>();
        listSanPham.add( new SanPham("Folder1", R.drawable.ic_folder));
        listSanPham.add( new SanPham("Folder1", R.drawable.ic_folder));
        listSanPham.add( new SanPham("Folder1", R.drawable.folder_shared));
        listSanPham.add( new SanPham("Folder1", R.drawable.folder_shared));

        // 3. create an adapter
        FileFolderAdapter mAdapter = new FileFolderAdapter(this.getContext(), listSanPham);
        // 4. set adapter
        recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}

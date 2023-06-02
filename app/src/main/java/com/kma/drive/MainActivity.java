package com.kma.drive;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<SanPham> listSanPham;
    SanPhamAdapter sanPhamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set color for image
        ImageButton imageButton_menu = findViewById(R.id.id_menu);
        imageButton_menu.setColorFilter(Color.BLACK);

        // Recycle view
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listSanPham = new ArrayList<>();
        listSanPham.add(new SanPham("Folder 1", R.drawable.folder_shared));
        listSanPham.add(new SanPham("Folder 2", R.drawable.ic_folder));
        listSanPham.add(new SanPham("Folder 3", R.drawable.folder_shared));
        listSanPham.add(new SanPham("Folder 3", R.drawable.folder_shared));
        listSanPham.add(new SanPham("Folder 3", R.drawable.folder_shared));
        listSanPham.add(new SanPham("Folder 3", R.drawable.folder_shared));
        sanPhamAdapter = new SanPhamAdapter(getApplicationContext(), listSanPham);
        recyclerView.setAdapter(sanPhamAdapter);
//        hideNavigationBar();
    }

    private void hideNavigationBar() {
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
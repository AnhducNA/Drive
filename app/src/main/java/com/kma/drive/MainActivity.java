package com.kma.drive;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        ImageButton imageButton_menu = findViewById(R.id.btn_menu);
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

        // switch tab when click menu button
        ImageButton menuButton  = (ImageButton) findViewById(R.id.btn_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        // Footer main
        LinearLayout linearLayoutFooter = findViewById(R.id.footer);
        ImageView imageViewHome = new ImageView(this);
        imageViewHome.setImageResource(R.drawable.ic_home);
        TextView textViewHome = new TextView(this);
        textViewHome.setText("Trang chủ");
        imageViewHome.setMaxWidth(40);
        imageViewHome.setMaxHeight(40);
        textViewHome.setMaxWidth(40);
        textViewHome.setMaxHeight(40);
        ImageView imageViewFolder = new ImageView(this);
        imageViewFolder.setImageResource(R.drawable.ic_folder);
        TextView textViewFolder = new TextView(this);
        textViewFolder.setText("Tệp");
        imageViewFolder.setMaxWidth(40);
        imageViewFolder.setMaxHeight(40);
        textViewFolder.setMaxWidth(40);
        textViewFolder.setMaxHeight(40);
        CustomView customView = new CustomView(this, imageViewHome,textViewHome);
        customView.getLayoutParams().width = 60;
        customView.getLayoutParams().height = 60;
        CustomView customView1 = new CustomView(this, imageViewFolder,textViewFolder);
        customView1.getLayoutParams().width = 60;
        customView1.getLayoutParams().height = 60;
        linearLayoutFooter.addView(customView);
        linearLayoutFooter.addView(customView1);
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
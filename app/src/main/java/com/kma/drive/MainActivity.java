package com.kma.drive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

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
        ImageButton menuButton = (ImageButton) findViewById(R.id.btn_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        // bottom navigation
        BottomNavigationView bottomNavigationMenu = findViewById(R.id.bottomNavigationMenu);
        bottomNavigationMenu.setOnNavigationItemSelectedListener(this);
        bottomNavigationMenu.setSelectedItemId(R.id.footer_home);
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

    FirstFragment firstFragment = new FirstFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.footer_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, firstFragment).commit();
                return true;
        }
        return false;
    }
}
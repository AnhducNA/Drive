package com.kma.drive;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton imageButton_menu = (ImageButton) findViewById(R.id.id_menu);
        imageButton_menu.setColorFilter(Color.BLACK);

        String[] items = {"Folder 1", "Folder 2", "Folder 3"};
        ListView listView = (ListView) findViewById(R.id.list_view);

        DataAdapter adapter = new DataAdapter(this, items);
        listView.setAdapter(adapter);

    }
}
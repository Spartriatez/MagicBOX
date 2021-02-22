package com.example.magicbox;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.magicbox.databinding.ActivityGalleryBoxBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryBox extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityGalleryBoxBinding binding;

    private List<String> paths;
    private List<String> names;
    private String globalPathBox;
    String room="";
    String boxRoom="";
    String path = "";
    DataBase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGalleryBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        paths = new ArrayList<>();
        names = new ArrayList<>();
        database = new DataBase(this);

        GridView gridView = (GridView) findViewById(R.id.idImageView);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            path = intent.getStringExtra("route");
            room = intent.getStringExtra("roomBox");
            boxRoom = intent.getStringExtra("boxName");
        }
        Log.e("Dsddd",path+" "+room+" "+boxRoom);
        Cursor cur = database.getToolsPath(room,boxRoom);
        while (cur.moveToNext()) {
            names.add(cur.getString(0));
            paths.add(cur.getString(1));
        }


        if(paths.size()>0) {
            GridAdapter ga = new GridAdapter(paths, names, this);
            gridView.setAdapter(ga);
            gridView.setOnItemClickListener((parent, view, position, id) -> {
                String path1 = paths.get(position);
                String name = names.get(position);

                startActivity(new Intent(GalleryBox.this, ToolsDetail.class).putExtra("pathBox", globalPathBox).putExtra("nameImg", name).putExtra("pathImg", path1));
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery_box, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.idAddBoxTool:{
                Intent inten = new Intent(GalleryBox.this, Tools.class);
                inten.putExtra("route", path);
                inten.putExtra("roomBox",room);
                inten.putExtra("BoxList",boxRoom);
                startActivity(inten);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToBack(View view) {
        Intent inten = new Intent(GalleryBox.this, MainMenu.class);
        startActivity(inten);
    }
}
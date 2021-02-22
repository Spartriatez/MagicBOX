package com.example.magicbox;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.magicbox.databinding.ActivityToolsDetailBinding;

public class ToolsDetail extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityToolsDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityToolsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

    }
    public void goToBack(View view) {
        Intent inten = new Intent(ToolsDetail.this, MyBoxes.class);
        startActivity(inten);
    }
}
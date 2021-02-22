package com.example.magicbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;

public class MainMenu extends AppCompatActivity {
    DataBase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_magic_box_launcher_foreground);
        database = new DataBase(this);

    }

    public void Logout(View view) {
        boolean test = database.deleteUser();
        if (test) {
            Toast.makeText(MainMenu.this, "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(MainMenu.this, Login.class);
            startActivity(inten);
        } else
            Toast.makeText(MainMenu.this, "błąd wylogowania", Toast.LENGTH_SHORT).show();
    }

    public void EditUser(View view) {
        Intent inten = new Intent(MainMenu.this, EditUser.class);
        startActivity(inten);
    }

    public void ChangePasswd(View view) {
        Intent inten = new Intent(MainMenu.this, ChangePasswd.class);
        startActivity(inten);
    }

    public void goMyBox(View view) {
        Intent inten = new Intent(MainMenu.this, MyBoxes.class);
        startActivity(inten);
    }

    public void scanQRCode(View view) {
        scanCode();
    }

    private void scanCode() {
        IntentIntegrator inInteg = new IntentIntegrator(this);
        inInteg.setCaptureActivity(CaptureAct.class);
        inInteg.setOrientationLocked(false);
        inInteg.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        inInteg.setPrompt("Skanowanie");
        inInteg.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String qrcode = result.getContents();
                String boxPath = "";
                if (!qrcode.isEmpty()) {
                    Cursor res = database.getBoxInRoom3(qrcode);
                    if (res.getCount() != 0) {
                        while (res.moveToNext()) {
                            boxPath = res.getString(4);
                            Intent inten = new Intent(MainMenu.this, GalleryBox.class);
                            inten.putExtra("route", boxPath);
                            startActivity(inten);
                        }
                    }
                }
            } else {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
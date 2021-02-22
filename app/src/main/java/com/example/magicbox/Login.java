package com.example.magicbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {
    EditText ed1, ed2;
    DataBase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setContentView(R.layout.main_menu);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_magic_box_launcher_foreground);

        database = new DataBase(this);
        Cursor cur = database.showLogUser();
        String username = null;
        String password = null;
        if (cur.getCount() == 0) {
            setContentView(R.layout.login);
        } else {
            while (cur.moveToNext()) {
                username = cur.getString(0);
                password = cur.getString(1);
            }
            boolean test = database.autoLogin(username, password);
            if (test) {
                Toast.makeText(Login.this, "Błąd logowania", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.login);
            } else {
                Toast.makeText(Login.this, "zalogowano", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(Login.this, MainMenu.class);
                startActivity(inten);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Udzielono uprawnienia", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Nie udzielono uprawnień", Toast.LENGTH_SHORT).show();
                    finish();
                }

        }
    }

    public void GoRegistry(View view) {
        Intent inten = new Intent(Login.this, Registry.class);
        startActivity(inten);
    }

    public void Login(View view) throws NoSuchAlgorithmException {
        ed1 = (EditText) findViewById(R.id.idNameText);
        ed2 = (EditText) findViewById(R.id.idPasswordText);
        Cursor res = database.logged(ed1.getText().toString(), ed2.getText().toString());

        if (ed1.getText().toString().equals("") || ed2.getText().toString().equals(""))
            Toast.makeText(Login.this, "Wypelnij  poprawnie dane", Toast.LENGTH_SHORT).show();
        else if (res.getCount() == 0)
            Toast.makeText(Login.this, "Błąd logowania", Toast.LENGTH_SHORT).show();
        else {
            String autologin = "";
            while (res.moveToNext()) {
                autologin = res.getString(7);
            }

            boolean ins2 = database.insertUserLog2(ed1.getText().toString(), ed2.getText().toString());
            if (ins2)
                Toast.makeText(Login.this, "Zaladowalo", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(Login.this, "Nie załadwano", Toast.LENGTH_SHORT).show();

            if (autologin.equals("Y")) {
                boolean ins = database.insertUserLog(ed1.getText().toString(), ed2.getText().toString());
                if (ins) {
                    Toast.makeText(Login.this, "Zalogowano", Toast.LENGTH_SHORT).show();
                    Intent inten = new Intent(Login.this, MainMenu.class);
                    startActivity(inten);
                } else
                    Toast.makeText(Login.this, "Błąd logowania nie wiadomo", Toast.LENGTH_SHORT).show();
            } else if (autologin.equals("N")) {
                Toast.makeText(Login.this, "Zalogowano", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(Login.this, MainMenu.class);
                startActivity(inten);
            } else
                Toast.makeText(Login.this, "Błąd logowania nie wiadomo", Toast.LENGTH_SHORT).show();
        }
    }
}


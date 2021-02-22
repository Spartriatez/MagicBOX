package com.example.magicbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;

public class Registry extends AppCompatActivity {
    EditText tx1, tx2, tx3, tx4, tx5, tx6, tx7;
    DataBase dbsql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_magic_box_launcher_foreground);

        dbsql = new DataBase(this);
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
        if(externalStorageVolumes.length<2){
            RadioButton rb = (RadioButton)findViewById(R.id.idExternMem);
            rb.setEnabled(false);

            RadioButton rb2= (RadioButton)findViewById(R.id.idInterMem);
            rb2.setChecked(true);
            rb2.setEnabled(false);
        }else
        {
            RadioButton rb2= (RadioButton)findViewById(R.id.idInterMem);
            rb2.setChecked(true);
        }
    }

    public void GoBack(View view) {
        Intent inten = new Intent(Registry.this, Login.class);
        startActivity(inten);
    }

    public void Registry(View view) {
        String autologin = "";
        String mem="";
        tx1 = findViewById(R.id.idVname);
        tx2 = findViewById(R.id.idName);
        tx3 = findViewById(R.id.idPasswd);
        tx4 = findViewById(R.id.idConfPasswd);
        tx5 = findViewById(R.id.idEMail);
        tx6 = findViewById(R.id.idNrPhone);
        tx7 = findViewById(R.id.idLoginName);
        CheckBox views1 = findViewById(R.id.cbAutoLog);
        String result = dbsql.checkIfExistUser(tx7.getText().toString(), tx5.getText().toString(), tx6.getText().toString());
        if (tx1.getText().toString().equals("") || tx2.getText().toString().equals("") || tx3.getText().toString().equals("") || tx4.getText().toString().equals("") || tx5.getText().toString().equals("") || tx6.getText().toString().equals(""))
            Toast.makeText(Registry.this, "Wypełnij poprawnie wszystkie dane", Toast.LENGTH_SHORT).show();
        else if (result != null)
            Toast.makeText(Registry.this, "Użytkownik o takiej nazwie już istnieje", Toast.LENGTH_SHORT).show();
        else if (!tx3.getText().toString().equals(tx4.getText().toString()))
            Toast.makeText(Registry.this, "niepoprawne haslo", Toast.LENGTH_SHORT).show();
        else {
            if (views1.isChecked())
                autologin = "Y";
            else
                autologin = "N";


            RadioButton rb2= (RadioButton)findViewById(R.id.idExternMem);

            File[] externalStorageVolumes =
                    ContextCompat.getExternalFilesDirs(getApplicationContext(), null);

            if(rb2.isChecked())
                mem=externalStorageVolumes[1].toString();
            else
                mem=externalStorageVolumes[0].toString();


            boolean isInserted = dbsql.insertDataUsers(tx7.getText().toString(), tx1.getText().toString(), tx2.getText().toString(), tx3.getText().toString(), tx5.getText().toString(), tx6.getText().toString(), autologin,mem);
            if (isInserted) {
                Toast.makeText(Registry.this, "Dodano dane", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(Registry.this, Login.class);
                startActivity(inten);
            } else
                Toast.makeText(Registry.this, "Nie dodano danych", Toast.LENGTH_SHORT).show();
        }
    }
}
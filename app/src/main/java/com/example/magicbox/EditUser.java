package com.example.magicbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class EditUser extends AppCompatActivity {
    EditText tx1, tx2, tx3, tx4;
    CheckBox chb1;
    DataBase database;

    String imie = null;
    String nazwisko = null;
    String email = null;
    String nr_tel = null;
    String autolog = null;
    String uname = null;
    String passwd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        database = new DataBase(this);
        Cursor data = database.getInfoUser();

        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                uname = data.getString(1);
                imie = data.getString(2);
                nazwisko = data.getString(3);
                passwd = data.getString(4);
                email = data.getString(5);
                nr_tel = data.getString(6);
                autolog = data.getString(7);
            }
        }
        System.out.println(uname);
        System.out.println(imie);
        tx1 = findViewById(R.id.idVname2);
        tx2 = findViewById(R.id.idName2);
        tx3 = findViewById(R.id.idEMail2);
        tx4 = findViewById(R.id.idNrPhone2);
        chb1 = findViewById(R.id.cbAutoLog2);

        if (imie != null && nazwisko != null && email != null && nr_tel != null && autolog != null) {
            tx1.setText(imie);
            tx2.setText(nazwisko);
            tx3.setText(email);
            tx4.setText(nr_tel);
            System.out.println(autolog);
            if (autolog.equals("Y"))
                chb1.setChecked(true);

        }
    }

    public void goToBack(View view) {
        Intent inten = new Intent(EditUser.this, MainMenu.class);
        startActivity(inten);
    }

    public boolean autologin(String uname) {
        String tmp = "";
        if (chb1.isChecked())
            tmp = "Y";
        else
            tmp = "N";
        if (!autolog.equals(tmp)) {
            return database.changeAutoLog(tmp, uname);
        } else
            return false;

    }

    public void saveChanges(View view) {
        if (!tx1.getText().toString().equals(imie) || !tx2.getText().toString().equals(nazwisko) || !tx3.getText().toString().equals(email) || !tx4.getText().toString().equals(nr_tel)) {
            boolean res = database.changeInfo(tx1.getText().toString(), tx2.getText().toString(), tx3.getText().toString(), tx4.getText().toString(), uname);
            boolean res2 = autologin(uname);
            if (res || res2) {
                Toast.makeText(EditUser.this, "Dane zostały zmienione", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(EditUser.this, MainMenu.class);
                startActivity(inten);
            } else
                Toast.makeText(EditUser.this, "Błąd bazy danych", Toast.LENGTH_SHORT).show();
        } else {
            boolean res2 = autologin(uname);
            System.out.println(res2);
            if (res2) {
                Toast.makeText(EditUser.this, "Autolog został zmieniony", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(EditUser.this, MainMenu.class);
                startActivity(inten);
            } else
                Toast.makeText(EditUser.this, "Pola nie zostały zmienione", Toast.LENGTH_SHORT).show();
        }

    }
}
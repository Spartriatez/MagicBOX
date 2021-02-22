package com.example.magicbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

public class ChangePasswd extends AppCompatActivity {
    EditText tx1, tx2, tx3;
    DataBase dbsql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwd);
        dbsql = new DataBase(this);
    }

    public void goToBack(View view) {
        Intent inten = new Intent(ChangePasswd.this, MainMenu.class);
        startActivity(inten);
    }

    public void changePasswd(View view) throws NoSuchAlgorithmException {
        tx1 = findViewById(R.id.idOldPasswd);
        tx2 = findViewById(R.id.idPasswd2);
        tx3 = findViewById(R.id.idConfPasswd2);

        if (tx1.getText().toString().equals("") || tx2.getText().toString().equals("") || tx3.getText().toString().equals(""))
            Toast.makeText(ChangePasswd.this, "Wypelnij  poprawnie dane", Toast.LENGTH_SHORT).show();
        else if (tx1.getText().toString().equals(tx2.getText().toString()))
            Toast.makeText(ChangePasswd.this, "Nowe hasło jest podobne do starego", Toast.LENGTH_SHORT).show();
        else if (!tx2.getText().toString().equals(tx3.getText().toString())) {
            Toast.makeText(ChangePasswd.this, "Hasła się różnią", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ChangePasswd.this, "ok", Toast.LENGTH_SHORT).show();
            Cursor data = dbsql.checkIsTrueUser(tx1.getText().toString());
            if (data.getCount() == 0)
                Toast.makeText(ChangePasswd.this, "Błędne dane", Toast.LENGTH_SHORT).show();
            else {
                boolean res = dbsql.changePasswd(tx2.getText().toString(), data, tx1.getText().toString());
                if (res) {
                    Toast.makeText(ChangePasswd.this, "Zmieniono hasło", Toast.LENGTH_SHORT).show();

                    Intent inten = new Intent(ChangePasswd.this, Login.class);
                    startActivity(inten);
                } else
                    Toast.makeText(ChangePasswd.this, "Błąd zmiany", Toast.LENGTH_SHORT).show();

            }
        }

    }
}
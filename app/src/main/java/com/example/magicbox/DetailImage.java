package com.example.magicbox;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class DetailImage extends AppCompatActivity {
    private ImageView iv;
    private EditText tv;
    private String globalPath;
    private String globalName;
    private String globalPathBox;
    private int STORAGE_PERMISSION_CODE = 1;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

        iv = findViewById(R.id.idDetailView);
        tv = findViewById(R.id.idDetailTextImg);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String name = intent.getStringExtra("nameImg");
            String path = intent.getStringExtra("pathImg");
            String pathBox = intent.getStringExtra("pathBox");

            globalName = name;
            globalPath = path;
            globalPathBox = pathBox;

            tv.setText(name);
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            iv.setImageBitmap(bitmap);
        }
    }

    public void goToMenu(View view) {
        Intent inten = new Intent(DetailImage.this, MainMenu.class);
        startActivity(inten);
    }

    public void changeNamePhoto(View view) throws Exception {
        String imageName = tv.getText().toString();
        boolean isRename = false;

        if (!globalName.equals(imageName)) {
            File f1 = new File(globalPathBox, globalName);
            File f2 = new File(globalPathBox, imageName);

            if (f1.exists()) {
                isRename = f1.renameTo(f2);
            }
            if (isRename) {
                Toast.makeText(DetailImage.this, "Zmieniono nazwe pliku", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(DetailImage.this, MainMenu.class);
                startActivity(inten);
            } else
                Toast.makeText(DetailImage.this, "Błąd zmiany nazwy", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(DetailImage.this, "Nazwa jest taka sama", Toast.LENGTH_SHORT).show();
    }

    public void deleteImg(View view) {
        File f1 = new File(globalPathBox, globalName);
        boolean isRename = false;
        if (f1.exists()) {
            isRename = f1.delete();
        }
        if (isRename) {
            Toast.makeText(DetailImage.this, "Usunięto plik", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(DetailImage.this, MainMenu.class);
            startActivity(inten);
        } else
            Toast.makeText(DetailImage.this, "Błąd usuwania", Toast.LENGTH_SHORT).show();
    }
}
package com.example.magicbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Tools extends AppCompatActivity {
    DataBase database;
    String room="";
    String boxRoom="";
    String path = "";
    EditText tx1,tx2;
    List<String> boxTools;
    Bitmap qrcode;
    boolean isBitmap = false;
    ImageView iv;
    String username = "";
    String path_mem = "";
    String fl="";

    private static final int CONTENT_REQUEST=1337;
    private static final String AUTHORITY=
            BuildConfig.APPLICATION_ID+".provider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        database = new DataBase(this);
        tx1 = findViewById(R.id.idNameTool);
        tx2 = findViewById(R.id.idComment);
        iv = findViewById(R.id.idQRView4);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            path = intent.getStringExtra("route");
            room = intent.getStringExtra("roomBox");
            boxRoom = intent.getStringExtra("BoxList");
        }
        boxTools = new ArrayList<String>();

        Cursor cur = database.getToolsList(room,boxRoom);
        while (cur.moveToNext()) {
            boxTools.add(cur.getString(0));
        }

        Log.e("sdfdfsfsdf", String.valueOf(boxTools));
        Cursor cursor = database.getInfoUser();

        while (cursor.moveToNext()) {
            username = cursor.getString(1);
            path_mem = cursor.getString(8);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                File myFile = new File(path,tx1.getText().toString() + ".jpeg");
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, myFile);

                i.setDataAndType(outputUri, "image/jpeg");
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(i);

                Toast.makeText(Tools.this, "Zapisano dane", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(Tools.this, MainMenu.class);
                startActivity(inten);

                finish();


            }
        }
    }
    public void goToBack(View view) {
        Intent inten = new Intent(Tools.this, GalleryBox.class);
        startActivity(inten);
    }

    public void generateQRCode(View view) {
        if (!tx1.getText().toString().equals("")) {
            if (!boxTools.contains(tx1.getText().toString())) {
                QRGEncoder qrgEncoder = new QRGEncoder(tx1.getText().toString(), null, QRGContents.Type.TEXT, 2000);
                try {
                    qrcode = qrgEncoder.encodeAsBitmap();
                    iv.setImageBitmap(qrcode);
                    isBitmap = true;
                } catch (WriterException e) {
                    e.printStackTrace();
                    isBitmap = false;
                }
            } else {
                Toast.makeText(Tools.this, "Narzędzie już istnieje", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(Tools.this, "Wypełnij poprawnie wszystkie dane", Toast.LENGTH_SHORT).show();
    }

    public void getPhoto(View view) {
        if (!tx1.getText().toString().equals("") && isBitmap) {
            if (!boxTools.contains(tx1.getText().toString())) {
                String filename =  tx1.getText().toString() + ".png";
                String qrImages = "QrTools";
                String pathM = path_mem+ '/' +username +"/";

                String comment="";
                if(!tx2.getText().equals(""))
                    comment=tx2.getText().toString();
                boolean isSave = database.insertTool(tx1.getText().toString(),pathM+qrImages+"/"+filename,path+"/"+tx1.getText().toString() + ".jpeg",room,boxRoom,comment);
                if (isSave) {
                    File f = new File(pathM + File.separator + qrImages);
                    if (!f.exists()) {
                        f.mkdirs();
                    }

                    try {
                        File f3 = new File(pathM + qrImages, filename);
                        if (f3.getParentFile().exists()) {
                            f3.createNewFile();
                            FileOutputStream fos = new FileOutputStream(f3);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            qrcode.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("fdfdfd",path);
                    File myFile = new File(path,tx1.getText().toString() + ".jpeg");

                    Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri outputUri= FileProvider.getUriForFile(this, AUTHORITY, myFile);

                    i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    startActivityForResult(i, CONTENT_REQUEST);

                } else
                    Toast.makeText(Tools.this, "Błąd zapisu do bazy danych", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Tools.this, "Wypełnij poprawnie wszystkie dane i wygeneruj QR kod", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
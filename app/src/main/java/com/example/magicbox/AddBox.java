package com.example.magicbox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.R.layout.simple_spinner_item;

public class AddBox extends AppCompatActivity {
    DataBase database;
    Spinner sp1;
    String room = "";
    FloatingActionButton fab;
    EditText tx1;
    ImageView iv;
    Bitmap qrcode;
    List<String> globalRoomList;
    List<String> globalBoxRoom;
    boolean isBitmap = false;
    String username = "";
    String path_mem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_box);
        database = new DataBase(this);
        boolean is_save=false;
        Cursor cursor = database.getInfoUser();

        while (cursor.moveToNext()) {
            username = cursor.getString(1);
            path_mem = cursor.getString(8);
        }

        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
        if(externalStorageVolumes.length<2) {
            if(!externalStorageVolumes[0].toString().equals(path_mem))
                is_save=false;
            else
                is_save=true;
        }else
            is_save = true;
        if(is_save) {
            tx1 = findViewById(R.id.idNameBox);
            iv = findViewById(R.id.idQRView3);
            final List<String> list = new ArrayList<String>();
            globalBoxRoom = new ArrayList<String>();
            globalRoomList = new ArrayList<String>();

            Cursor get_room = database.getRoomData2();
            if (get_room.getCount() != 0) {
                while (get_room.moveToNext()) {
                    list.add(get_room.getString(0));
                }
            }

            Cursor get_room2 = database.getRoomData();
            if (get_room2.getCount() != 0) {
                while (get_room2.moveToNext()) {
                    globalBoxRoom.add(get_room2.getString(2));
                }
            }
            globalRoomList = list;
            sp1 = findViewById(R.id.idRoomName3);
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(dataAdapter);
            sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String dane = adapterView.getSelectedItem().toString();
                    room = dane;
                    sp1.setSelection(((ArrayAdapter<String>) sp1.getAdapter()).getPosition(dane));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }


            });

            fab = findViewById(R.id.idAddRoomName);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder addDialog = new AlertDialog.Builder(AddBox.this);
                    addDialog.setTitle("Podaj nazwę pokoju");

                    final EditText input_RN = new EditText(AddBox.this);
                    input_RN.setInputType(InputType.TYPE_CLASS_TEXT);
                    addDialog.setView(input_RN);

                    addDialog.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String myText = input_RN.getText().toString();
                            if (!list.equals(myText)) {
                                dataAdapter.add(myText);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp1.setAdapter(dataAdapter);
                            }
                        }
                    });
                    addDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    addDialog.show();
                }
            });
        }else{
            Toast.makeText(AddBox.this, "Brak karty zewnętrznej", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(AddBox.this, MainMenu.class);
            startActivity(inten);
        }
    }

    public void goToBack(View view) {
        Intent inten = new Intent(AddBox.this, MainMenu.class);
        startActivity(inten);
    }

    public void generateQRCOde(View view) {
        if (!room.equals("") && !tx1.getText().toString().equals("")) {
            Log.e("ggg", String.valueOf(globalBoxRoom));
            if (!globalBoxRoom.contains(tx1.getText().toString())) {
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
                Toast.makeText(AddBox.this, "Box już istnieje", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(AddBox.this, "Wypełnij poprawnie wszystkie dane", Toast.LENGTH_SHORT).show();
    }

    public void saveInfoBox(View view) {
        if (!room.equals("") && !tx1.getText().toString().equals("") && isBitmap) {
            if (!globalBoxRoom.contains(tx1.getText().toString())) {
                String qrImages = "QrImages";
                String path = path_mem+ '/' +username +"/";
                String filename = room + "-" + tx1.getText().toString() + ".png";

                boolean isSave = database.insertBox(room, tx1.getText().toString(), path + qrImages + '/' + filename, path + room + "-" + tx1.getText().toString());
                if (isSave) {
                    if (isExternalWritable()) {
                        File f = new File(path + File.separator + qrImages);
                        if (!f.exists()) {
                            f.mkdirs();
                        }

                        File f2 = new File(path + File.separator + room + "-" + tx1.getText().toString());
                        if (!f2.exists()) {
                            f2.mkdirs();
                        }

                        try {
                            File f3 = new File(path + qrImages, filename);
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
                        Toast.makeText(AddBox.this, "Zapisano dane", Toast.LENGTH_SHORT).show();
                        Intent inten = new Intent(AddBox.this, MainMenu.class);
                        startActivity(inten);
                    } else
                        Toast.makeText(AddBox.this, "Nie utworzono folderu", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(AddBox.this, "Błąd zapisu do bazy danych", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(AddBox.this, "Box już istnieje", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddBox.this, "Wypełnij poprawnie wszystkie dane i wygeneruj QR kod", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isExternalWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
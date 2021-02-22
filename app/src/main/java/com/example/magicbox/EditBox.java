package com.example.magicbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.R.layout.simple_spinner_item;

public class EditBox extends AppCompatActivity {
    DataBase database;
    Spinner sp1, sp2;
    String room = "";
    EditText tx1;
    ImageView iv;
    Bitmap qrcode;
    String boxName = "";
    List<String> globalRoomList;
    List<String> globalBoxRoom;
    List<String> globalQRPath;
    List<String> globalGalleryPath;
    int position = 0;
    int position2 = 0;
    boolean isBitmap = false;

    String username = "";
    String path_mem = "";

    private static final String AUTHORITY=
            BuildConfig.APPLICATION_ID+".provider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_box);

        sp2 = findViewById(R.id.idBoxName3);
        tx1 = findViewById(R.id.idChangeBox);
        iv = findViewById(R.id.idQRView3);
        final List<String> list = new ArrayList<String>();
        globalBoxRoom = new ArrayList<String>();
        globalRoomList = new ArrayList<String>();
        globalQRPath = new ArrayList<String>();
        globalGalleryPath = new ArrayList<String>();
        database = new DataBase(this);

        Cursor cursor = database.getInfoUser();

        while (cursor.moveToNext()) {
            username = cursor.getString(1);
            path_mem = cursor.getString(8);
        }

        Cursor get_room = database.getRoomData2();
        if (get_room.getCount() != 0) {
            while (get_room.moveToNext()) {
                list.add(get_room.getString(0));
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
                    position = i;
                    sp1.setSelection(((ArrayAdapter<String>) sp1.getAdapter()).getPosition(dane));
                    globalBoxRoom.clear();
                    globalQRPath.clear();
                    globalGalleryPath.clear();
                    Cursor cur = database.getBoxInRoom(sp1.getSelectedItem().toString());
                    if (cur.getCount() != 0) {
                        while (cur.moveToNext()) {
                            globalBoxRoom.add(cur.getString(2));
                            globalQRPath.add(cur.getString(3));
                            globalGalleryPath.add(cur.getString(4));
                        }
                    }
                    final ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(EditBox.this,
                            simple_spinner_item, globalBoxRoom);
                    dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp2.setAdapter(dataAdapter2);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (globalBoxRoom.size() == 0) {
                Cursor cur = database.getBoxInRoom(sp1.getSelectedItem().toString());
                if (cur.getCount() != 0) {
                    while (cur.moveToNext()) {
                        globalBoxRoom.add(cur.getString(2));
                        globalQRPath.add(cur.getString(3));
                        globalGalleryPath.add(cur.getString(4));
                    }
                }
                final ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                        simple_spinner_item, globalBoxRoom);
                dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp2.setAdapter(dataAdapter2);
            }
            sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String dane2 = adapterView.getSelectedItem().toString();
                    boxName = dane2;
                    sp2.setSelection(((ArrayAdapter<String>) sp2.getAdapter()).getPosition(dane2));
                    tx1.setText(dane2);
                    position2 = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            Toast.makeText(EditBox.this, "Brak utworzonych boxów", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(EditBox.this, MainMenu.class);
            startActivity(inten);

        }

    }

    public void goToBack(View view) {
        Intent inten = new Intent(EditBox.this, MainMenu.class);
        startActivity(inten);
    }

    public void newQRCode(View view) {
        if (!boxName.equals("") && !tx1.getText().toString().equals("")) {
            if (!boxName.equals(tx1.getText().toString())) {
                QRGEncoder qrgEncoder = new QRGEncoder(tx1.getText().toString(), null, QRGContents.Type.TEXT, 2000);
                try {
                    qrcode = qrgEncoder.encodeAsBitmap();
                    iv.setImageBitmap(qrcode);
                    isBitmap = true;
                } catch (WriterException e) {
                    e.printStackTrace();
                    isBitmap = false;
                }
            } else
                Toast.makeText(EditBox.this, "Box już istnieje", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(EditBox.this, "Wypełnij poprawnie wszystkie dane", Toast.LENGTH_SHORT).show();
    }

    public void changeBox(View view) {
        if (!boxName.equals("") && !tx1.getText().toString().equals("") && isBitmap) {
            if (!boxName.equals(tx1.getText().toString())) {
                String oldPathQr = globalQRPath.get(position2);
                String oldPathGallery = globalGalleryPath.get(position2);
                String oldBox = boxName;

                String qrImages = "QrImages";
                String path = path_mem+ '/' +username +"/";
                String filename = room + "-" + tx1.getText().toString() + ".png";

                Cursor cur = database.getToolsPath(room,boxName);
                List<String> tools = new ArrayList<String>();
                List<String> imgPaths = new ArrayList<String>();
                while (cur.moveToNext()) {
                    tools.add(cur.getString(0));
                    imgPaths.add(cur.getString(1));
                }

                List<String> dirs = new ArrayList<String>();
                List<String> filesPath = new ArrayList<String>();
                boolean renameFile = false;
                boolean isChange = database.changeBoxName(globalRoomList.get(position), oldBox, tx1.getText().toString(), path + qrImages + '/' + filename, path + room + "-" + tx1.getText().toString());
                if (isChange) {

                    File oldGallery = new File(oldPathGallery);
                    File newPathGallery = new File(path + File.separator + room + "-" + tx1.getText().toString());

                    if (oldGallery.exists() && !newPathGallery.exists()) {
                            boolean changeBase=false;
                            File directory = new File(oldPathGallery);
                            File[] files = directory.listFiles();
                            for (int i = 0; i < files.length; i++)
                            {
                                if(files[i].isFile())
                                    filesPath.add(files[i].getPath());
                                else
                                    dirs.add(files[i].getPath());
                            }

                            for(int i=0; i<filesPath.size();i++)
                                for(int j=0;j<tools.size();j++)
                                    if (filesPath.get(i).equals(imgPaths.get(j))) {
                                        changeBase=database.getToolsPath2(tools.get(j),globalRoomList.get(position),oldBox,tx1.getText().toString(),path + room + "-" + tx1.getText().toString()+File.separator + tools.get(j)+".jpeg");
                                        Log.e("dfsds", String.valueOf(changeBase));
                                    }
                            if(changeBase || filesPath.size()<1){
                                renameFile= oldGallery.renameTo(newPathGallery);
                                Log.e("dsds",oldGallery.getPath()+"\n"+newPathGallery.getPath());
                            }
                            else{
                                Toast.makeText(EditBox.this, "Nie zmieniono bazy", Toast.LENGTH_LONG).show();
                            }
                    }

                    File oldQr = new File(oldPathQr);

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
                    if (oldQr.delete() && renameFile) {
                        Toast.makeText(EditBox.this, "Zmieniono dane", Toast.LENGTH_SHORT).show();
                        Intent inten = new Intent(EditBox.this, MainMenu.class);
                        startActivity(inten);
                    } else if(!renameFile) {
                        Toast.makeText(EditBox.this, "Błąd modyfikacji pliku", Toast.LENGTH_LONG).show();
                        Intent inten = new Intent(EditBox.this, MainMenu.class);
                        startActivity(inten);
                    }
                        else{
                        Toast.makeText(EditBox.this, "Błąd usuwania starego QR kodu", Toast.LENGTH_LONG).show();
                        Intent inten = new Intent(EditBox.this, MainMenu.class);
                        startActivity(inten);
                    }
                } else
                    Toast.makeText(EditBox.this, "Błąd zmiany bazy danych", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(EditBox.this, "ta sama nazwa boxu", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(EditBox.this, "Wypełnij poprawnie wszystkie dane", Toast.LENGTH_SHORT).show();
    }

    /*public void deleteBox(View view) {
        if (!boxName.equals("") && !tx1.getText().toString().equals("")) {
            if (boxName.equals(tx1.getText().toString())) {
                boolean isDeleted = database.deleteBox(room, boxName);
                if (isDeleted) {
                    String pathQr = globalQRPath.get(position2);
                    String pathGallery = globalGalleryPath.get(position2);

                    File del1 = new File(pathQr);
                    File del2 = new File(pathGallery);
                    boolean delGallery = del2.delete();
                    boolean delQRCode = del1.delete();

                    if (delGallery && delQRCode) {
                        Toast.makeText(EditBox.this, "Usunięto Box", Toast.LENGTH_SHORT).show();
                        Intent inten = new Intent(EditBox.this, MainMenu.class);
                        startActivity(inten);
                    } else
                        Toast.makeText(EditBox.this, "Błąd usuwania", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(EditBox.this, "Błąd usuwania z bazy danych", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditBox.this, "Nie znaleziono boxu o takiej nazwie", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
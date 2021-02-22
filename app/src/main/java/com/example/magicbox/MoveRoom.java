package com.example.magicbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_item;

public class MoveRoom extends AppCompatActivity {
    DataBase database;
    Spinner sp1, sp2,sp3;
    String room = "";
    EditText tx1;
    String boxName = "";
    List<String> globalRoomList,globalRoomList2;
    List<String> globalBoxRoom;
    List<String> globalQRPath;
    List<String> globalGalleryPath;
    List<String> globalBoxTools;
    List<String> getToolsName;
    List<String> getToolsPath;

    int position = 0;
    int position2 = 0;
    int position3 = 0;

    String username = "";
    String path_mem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_room);

        sp2 = findViewById(R.id.idBox);
        sp3= findViewById(R.id.idAfterRoom);

        final List<String> list = new ArrayList<String>();
        globalBoxRoom = new ArrayList<String>();
        globalRoomList = new ArrayList<String>();
        globalRoomList2 = new ArrayList<String>();
        globalQRPath = new ArrayList<String>();
        globalGalleryPath = new ArrayList<String>();

        globalBoxTools= new ArrayList<>();
        getToolsName= new ArrayList<>();
        getToolsPath = new ArrayList<>();

        database = new DataBase(this);

        Cursor cursor = database.getInfoUser();

        while (cursor.moveToNext()) {
            username = cursor.getString(1);
            path_mem = cursor.getString(8);
        }

        Cursor get_room = database.getRoomData2();
        if (get_room.getCount() != 0 && get_room.getCount()>1) {
            while (get_room.moveToNext()) {
                list.add(get_room.getString(0));
            }

            globalRoomList = list;
            sp1 = findViewById(R.id.idBeforeRoomName);
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
                    globalRoomList2.clear();
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
                    final ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(MoveRoom.this,
                            simple_spinner_item, globalBoxRoom);
                    dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp2.setAdapter(dataAdapter2);


                    for(int k=0;k<globalRoomList.size();k++){
                        if(!globalRoomList.get(k).equals(globalRoomList.get(position))){
                            globalRoomList2.add(globalRoomList.get(k));
                        }
                    }
                    Log.e("zzzzzzz", String.valueOf(globalRoomList2));
                    final ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(MoveRoom.this,
                            simple_spinner_item, globalRoomList2);
                    dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp3.setAdapter(dataAdapter3);
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
                    position2 = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    position3 = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } else {
            Toast.makeText(MoveRoom.this, "Brak utworzonych boxów", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(MoveRoom.this, MainMenu.class);
            startActivity(inten);

        }
    }

    public void goToBack(View view) {
        Intent inten = new Intent(MoveRoom.this, MyBoxes.class);
        startActivity(inten);
    }

    public void changeRoom(View view) {
        Cursor cur =database.getToolsPath3(globalRoomList.get(position),globalBoxRoom.get(position2));
        if(cur.getCount()==0)
        {
            String path = path_mem + '/' + username + "/";
            String newPathTx = path + globalRoomList2.get(position3) + "-" + globalBoxRoom.get(position2);

            File oldPath = new File(globalGalleryPath.get(position2));
            File newPath = new File(newPathTx);

            oldPath.renameTo(newPath);
        }else{
            while (cur.moveToNext()) {
                getToolsName.add(cur.getString(0));
                getToolsPath.add(cur.getString(1));
                globalBoxTools.add(cur.getString(2));
            }

            for(int i=0;i<getToolsPath.size();i++) {
                String path = path_mem + '/' + username + "/";
                String newPathTx = path + globalRoomList2.get(position3)+ "-" + globalBoxTools.get(i);

                File oldPath = new File(globalGalleryPath.get(position2));
                File newPath = new File(newPathTx);

                oldPath.renameTo(newPath);
                File tmp = new File(newPathTx + "/" + getToolsName.get(i) + ".jpeg");
                boolean isSave = database.updateToolsPath(getToolsName.get(i), globalRoomList.get(position), globalBoxRoom.get(position2), globalRoomList2.get(position3), newPathTx + "/" + getToolsName.get(i) + ".jpeg");

            }
        }



        String path = path_mem + '/' + username + "/";
        String filename = globalRoomList2.get(position3) + "-" + globalBoxRoom.get(position2) + ".png";
        String qrImages = "QrImages";
        File oldQr = new File(globalQRPath.get(position2));
        File newQr = new File(path + qrImages + "/" + filename);

        oldQr.renameTo(newQr);
        boolean check=database.changeRoomName(globalBoxRoom.get(position2),globalRoomList.get(position), globalRoomList2.get(position3), path + globalRoomList2.get(position3) + "-" + globalBoxRoom.get(position2), path + qrImages + "/" + filename);

        int count=0;
        for(int i=0;i<getToolsName.size();i++){
            Cursor cur1 =database.checkToolsRoom(getToolsPath.get(i),globalBoxTools.get(i));
            if(cur1.getCount()>0){
                count=0;
            }else{
                count++;
            }
        }

        if(count == getToolsName.size() && check){
            Toast.makeText(MoveRoom.this, "Zmieniono dane", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(MoveRoom.this, MainMenu.class);
            startActivity(inten);
        }else{
            Toast.makeText(MoveRoom.this, "Błąd bazy lub pliku w drugiej bazie", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(MoveRoom.this, MainMenu.class);
            startActivity(inten);
        }




    }
}
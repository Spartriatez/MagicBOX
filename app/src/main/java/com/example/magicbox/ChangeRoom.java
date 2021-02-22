package com.example.magicbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_item;

public class ChangeRoom extends AppCompatActivity {
    DataBase database;
    Spinner sp1;
    String room = "";
    EditText tx1;
    List<String> globalRoomList;
      List<String> globalBoxRoom;
    List<String> globalBoxTools;
    List<String> globalQRPath;
    List<String> globalGalleryPath;
    List<String> getToolsName;
    List<String> getToolsPath;
    int position = 0;

    String username = "";
    String path_mem = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_room);

        final List<String> list = new ArrayList<>();
        globalBoxRoom = new ArrayList<>();
        globalBoxTools = new ArrayList<>();
        globalRoomList = new ArrayList<>();
        globalQRPath = new ArrayList<>();
        globalGalleryPath = new ArrayList<>();
        getToolsName= new ArrayList<>();
        getToolsPath = new ArrayList<>();
        database = new DataBase(this);

        sp1= findViewById(R.id.idSelRoom);
        tx1= findViewById(R.id.idNewRoomName);

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

            globalRoomList=list;

            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
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
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } else {
            Toast.makeText(ChangeRoom.this, "Brak utworzonych boxów", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(ChangeRoom.this, MainMenu.class);
            startActivity(inten);

        }
    }

    public void goToBack(View view) {
        Intent inten = new Intent(ChangeRoom.this, MyBoxes.class);
        startActivity(inten);
    }


    public void changeRoomName(View view) {
        Cursor cur =database.getToolsPath2(globalRoomList.get(position));
        while (cur.moveToNext()) {
            getToolsName.add(cur.getString(0));
            getToolsPath.add(cur.getString(1));
            globalBoxTools.add(cur.getString(2));
        }

        int[] counter= new int[globalBoxRoom.size()];
        for(int i=0;i<globalBoxRoom.size();i++){
            for(int j=0;j<globalBoxTools.size();j++){
                if(globalBoxRoom.get(i)==globalBoxTools.get(j)){
                    counter[j]++;
                }
            }
        }
        if (!globalRoomList.contains(tx1.getText().toString()) && !tx1.getText().toString().equals("")) {
            for(int i=0;i<getToolsPath.size();i++) {
                for(int j=0;j<globalBoxRoom.size();j++) {
                    if (globalBoxRoom.get(j).contains(globalBoxTools.get(i))) {
                        String path = path_mem + '/' + username + "/";
                        String newPathTx = path + tx1.getText().toString() + "-" + globalBoxTools.get(i);

                        File oldPath = new File(globalGalleryPath.get(j));
                        File newPath = new File(newPathTx);

                        oldPath.renameTo(newPath);
                        File tmp = new File(newPathTx + "/" + getToolsName.get(i) + ".jpeg");
                        boolean isSave = database.updateToolsPath(getToolsName.get(i), globalRoomList.get(position), globalBoxRoom.get(j), tx1.getText().toString(), newPathTx + "/" + getToolsName.get(i) + ".jpeg");
                    }else if(counter[j]==0){
                        String path = path_mem + '/' + username + "/";
                        String newPathTx = path + tx1.getText().toString() + "-" + globalBoxRoom.get(j);

                        File oldPath = new File(globalGalleryPath.get(j));
                        File newPath = new File(newPathTx);

                        oldPath.renameTo(newPath);

                    }
                }
            }

            for(int i=0;i<globalBoxRoom.size();i++){

                String path = path_mem + '/' + username + "/";
                String filename = tx1.getText().toString() + "-" + globalBoxRoom.get(i) + ".png";
                String qrImages = "QrImages";
                File oldQr = new File(globalQRPath.get(i));
                File newQr = new File(path + qrImages + "/" + filename);

                oldQr.renameTo(newQr);
                database.changeRoomName(globalBoxRoom.get(i),globalRoomList.get(position), tx1.getText().toString(), path + tx1.getText().toString() + "-" + globalBoxRoom.get(i), path + qrImages + "/" + filename);

            }

            int count=0;
            for(int i=0;i<getToolsName.size();i++){
                Cursor cur1 =database.checkToolsRoom(getToolsPath.get(i),globalBoxTools.get(i));
                if(cur1.getCount()>0){
                    count=0;
                }else{
                    count++;
                }
            }

            int count2=0;
            for(int i=0;i<globalBoxRoom.size();i++){
                Cursor cur2 = database.checkBoxRoom(globalBoxRoom.get(i),globalGalleryPath.get(i),globalQRPath.get(i));
                if(cur2.getCount()>0)
                    count2=0;
                else
                    count2++;
            }

            Log.e("dddd", String.valueOf(count)+" "+String.valueOf(count2));

            if(count == getToolsName.size() && count2 == globalBoxRoom.size() ){
                Toast.makeText(ChangeRoom.this, "Zmieniono dane", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(ChangeRoom.this, MainMenu.class);
                startActivity(inten);
            }else{
                Toast.makeText(ChangeRoom.this, "Błąd bazy lub pliku w drugiej bazie", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(ChangeRoom.this, MainMenu.class);
                startActivity(inten);
            }

            /*Log.e("msg", String.valueOf(globalRoomList)+"\n"+
                    String.valueOf(globalBoxRoom)+"\n"+
                    String.valueOf(globalQRPath)+"\n"+
                    String.valueOf(globalGalleryPath)+"\n"+
                    String.valueOf(getToolsName)+"\n"+
                    String.valueOf(getToolsPath)+"\n"+
                    String.valueOf(globalBoxTools)+"\n"
            );*/

        } else
            Toast.makeText(ChangeRoom.this, "Stara nazwa jest taka sama jak nowa lub puste pole", Toast.LENGTH_SHORT).show();

    }
}
package com.example.magicbox;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.magicbox.databinding.ActivityMyBoxesBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_item;

public class MyBoxes extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMyBoxesBinding binding;
    DataBase database;
    Spinner sp1;
    String room = "";
    String boxName = "";
    List<String> globalRoomList;
    List<String> globalBoxRoom;
    List<String> globalQRPath;
    List<String> globalGalleryPath;

    int pos = 0;
    int position=0;
    int position2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyBoxesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GridView gridView = (GridView) findViewById(R.id.idBoxDetail);
        setSupportActionBar(binding.toolbar);
        final List<String> list = new ArrayList<String>();
        globalBoxRoom = new ArrayList<String>();
        globalRoomList = new ArrayList<String>();
        globalQRPath = new ArrayList<String>();
        globalGalleryPath = new ArrayList<String>();

        database = new DataBase(this);
        Cursor get_room = database.getRoomData2();

        if (get_room.getCount() != 0) {
            while (get_room.moveToNext()) {
                list.add(get_room.getString(0));
            }

            globalRoomList = list;

            sp1 = findViewById(R.id.idRoomName4);
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MyBoxes.this,
                    simple_spinner_item, globalRoomList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(dataAdapter);
            sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String dane = adapterView.getSelectedItem().toString();
                    room = dane;
                    pos = i;
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
                    if (globalBoxRoom.size() > 0) {
                        BoxAdapter ba = new BoxAdapter(globalBoxRoom, view.getContext());
                        gridView.setAdapter(ba);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String boxName = globalBoxRoom.get(position);
                                Log.e("dfsdf",globalRoomList.get(pos));
                                String roomName = globalRoomList.get(pos);
                                String galleryPath = globalGalleryPath.get(position);

                                startActivity(new Intent(MyBoxes.this, GalleryBox.class).putExtra("route", galleryPath).putExtra("roomBox", roomName).putExtra("boxName", boxName));
                            }
                        });
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch(id){
            case R.id.add_box:{
                Intent inten = new Intent(MyBoxes.this, AddBox.class);
                startActivity(inten);
                break;
            }
            case R.id.edit_box:{
                Intent inten = new Intent(MyBoxes.this, EditBox.class);
                startActivity(inten);
            }
            case R.id.changeRoom:{
                Intent inten = new Intent(MyBoxes.this, ChangeRoom.class);
                startActivity(inten);
            }
            case R.id.moveBOX:{
                Intent inten = new Intent(MyBoxes.this, MoveRoom.class);
                startActivity(inten);
                break;
            }
            case R.id.deleteRoom:{
                final AlertDialog.Builder addDialog = new AlertDialog.Builder(MyBoxes.this);
                addDialog.setTitle("Usuń pomieszczenie");

                final TextView input_RN = new TextView(MyBoxes.this);
                input_RN.setText("Czy jesteś pewnien aby usunąć pomieszcenie?");
                input_RN.setGravity(Gravity.CENTER);
                addDialog.setView(input_RN);

                addDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        List<String> globalBoxTools= new ArrayList<>();
                        List<String> getToolsName= new ArrayList<>();
                        List<String> getToolsPath = new ArrayList<>();
                        for(int i=0; i<globalBoxRoom.size();i++){
                            globalBoxTools.clear();
                            getToolsName.clear();
                            getToolsPath.clear();

                            Cursor cur2 =database.getToolsPath3(globalRoomList.get(pos),globalBoxRoom.get(i));
                            if(cur2.getCount()>0)
                            {
                                while (cur2.moveToNext()) {
                                    getToolsName.add(cur2.getString(0));
                                    getToolsPath.add(cur2.getString(1));
                                    globalBoxTools.add(cur2.getString(2));
                                }

                                for(int j=0;j<getToolsName.size();j++){
                                    Cursor curx = database.checkIfQrCode(globalRoomList.get(pos),globalBoxRoom.get(i),getToolsName.get(j));
                                    if(curx.getCount()>0)
                                    {
                                        String pathQR = "";
                                        while (curx.moveToNext()) {
                                            pathQR = curx.getString(0);
                                        }
                                        File qrPath = new File(pathQR);
                                        qrPath.delete();
                                    }

                                    File delGallPath = new File(getToolsPath.get(j));
                                    delGallPath.delete();
                                    database.delTools(globalRoomList.get(pos),globalBoxRoom.get(i),getToolsName.get(j));
                                }
                            }
                        File delQr= new File(globalQRPath.get(i));
                        delQr.delete();
                        File delPathGall = new File(globalGalleryPath.get(i));
                        delPathGall.delete();

                        database.delBox(globalRoomList.get(pos),globalBoxRoom.get(i));

                        }
                        Cursor cury = database.getBoxInRoom(globalRoomList.get(pos));
                        Cursor curz = database.checkIfExistTools(globalRoomList.get(pos));

                        if(cury.getCount()==0 && curz.getCount()==0){
                            Toast.makeText(MyBoxes.this, "Usunieto pokoj", Toast.LENGTH_SHORT).show();
                            Intent inten = new Intent(MyBoxes.this, MainMenu.class);
                            startActivity(inten);
                        }else{
                            Toast.makeText(MyBoxes.this, "Nie usunieto pokoju", Toast.LENGTH_SHORT).show();
                            Intent inten = new Intent(MyBoxes.this, MainMenu.class);
                            startActivity(inten);
                        }


                    }
                });
                addDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                addDialog.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
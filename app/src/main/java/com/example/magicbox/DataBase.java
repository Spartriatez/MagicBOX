package com.example.magicbox;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class DataBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MagicBOX.db";
    public static final String TABLE_NAME1 = "UserProfile";
    public static final String TABLE_NAME2 = "IsSignIn";
    public static final String TABLE_NAME3 = "IsLogged";
    public static final String TABLE_NAME4 = "BoxTable";
    public static final String TABLE_NAME5 = "Tools";
    public static final String TABLE_NAME6 = "LikeBox";
    String sql = "create table IF NOT EXISTS " + TABLE_NAME1 + "(id_user INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,vName TEXT, name TEXT,password TEXT,mail TEXT,nr_tel TEXT,autologin TEXT,mem_path TEXT )";
    String sql2 = "create table IF NOT EXISTS " + TABLE_NAME2 + "(username TEXT,password TEXT)";
    String sql3 = "create table IF NOT EXISTS " + TABLE_NAME3 + "(id_user INTEGER, username TEXT,password TEXT, date TEXT, FOREIGN KEY (id_user) REFERENCES " + TABLE_NAME1 + "(id_user));";
    String sql4 = "create table IF NOT EXISTS " + TABLE_NAME4 + "(username TEXT,room_name TEXT, box_name TEXT, qr_code_path TEXT, gallery_path TEXT, image_path TEXT,root_box TEXT, subbox TEXT, CONSTRAINT fk_user\n" +
            "    FOREIGN KEY (username)\n" +
            "    REFERENCES TABLE_NAME1(username)\n" +
            "    ON DELETE CASCADE);";
    String sql5 = "create table IF NOT EXISTS " + TABLE_NAME5 + "(username TEXT,tool_name TEXT, qr_code_path TEXT, image_path TEXT, room_name TEXT, box_name TEXT, tag TEXT, comment TEXT, CONSTRAINT fk_user\n" +
            "    FOREIGN KEY (username)\n" +
            "    REFERENCES TABLE_NAME1(username)\n" +
            "    ON DELETE CASCADE);";
    String sql6 = "create table IF NOT EXISTS " + TABLE_NAME6 + "(id_user INTEGER,box_name TEXT, FOREIGN KEY (id_user) REFERENCES " + TABLE_NAME1 + "(id_user));";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.execute();
        stmt = db.compileStatement(sql2);
        stmt.execute();
        stmt = db.compileStatement(sql3);
        stmt.execute();
        stmt = db.compileStatement(sql4);
        stmt.execute();
        stmt = db.compileStatement(sql5);
        stmt.execute();
        stmt = db.compileStatement(sql6);
        stmt.execute();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sq = "Drop TABLE IF EXISTS " + TABLE_NAME1;
        String sq2 = "Drop TABLE IF EXISTS " + TABLE_NAME2;
        String sq3 = "Drop TABLE IF EXISTS " + TABLE_NAME3;
        String sq4 = "Drop TABLE IF EXISTS " + TABLE_NAME4;
        String sq5 = "Drop TABLE IF EXISTS " + TABLE_NAME5;
        String sq6 = "Drop TABLE IF EXISTS " + TABLE_NAME6;
        SQLiteStatement stmt = db.compileStatement(sq);
        stmt.execute();
        stmt = db.compileStatement(sq2);
        stmt.execute();
        stmt = db.compileStatement(sq3);
        stmt.execute();
        stmt = db.compileStatement(sq4);
        stmt.execute();
        stmt = db.compileStatement(sq5);
        stmt.execute();
        stmt = db.compileStatement(sq6);
        stmt.execute();
    }

    public String checkIfExistUser(String username, String eMail, String nrPhone) {
        String result;
        try {
            String sq = "Select username from " + TABLE_NAME1 + " where username=? and mail=? and nr_tel=?";
            SQLiteDatabase db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindString(1, username);
            statement.bindString(2, eMail);
            statement.bindString(3, nrPhone);
            result = statement.simpleQueryForString();
        } catch (Exception e) {
            Log.w("Exception:", e);
            return null;
        }
        return result;
    }

    public Cursor showLogUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data;
        String sql = "select * from " + TABLE_NAME2;
        data = db.rawQuery(sql, null);
        return data;
    }

    public boolean deleteUser() {
        try {
            String sq = "DELETE FROM " + TABLE_NAME2;
            SQLiteDatabase db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(sq);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }

    public boolean insertDataUsers(String username, String vName, String name, String password, String eMail, String nrPhone, String autologin,String mem) {
        String sq = "Insert into " + TABLE_NAME1 + "(username,vName,name,password,mail,nr_tel,autologin,mem_path) Values(?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            SQLiteStatement statement = db.compileStatement(sq);
            statement.clearBindings();
            statement.bindString(1, username);
            digest.update(password.getBytes(StandardCharsets.US_ASCII), 0, password.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            statement.bindString(4, hash);
            statement.bindString(2, vName);
            statement.bindString(3, name);
            statement.bindString(5, eMail);
            statement.bindString(6, nrPhone);
            statement.bindString(7, autologin);
            statement.bindString(8,mem);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }

    public boolean insertUserLog(String username, String password) {
        String sq = "Insert into " + TABLE_NAME2 + "(username,password) Values(?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            statement.clearBindings();
            digest.update(password.getBytes(StandardCharsets.US_ASCII), 0, password.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            statement.bindString(1, username);
            statement.bindString(2, hash);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }

    public Cursor logged(String username, String password) throws NoSuchAlgorithmException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(password.getBytes(StandardCharsets.US_ASCII), 0, password.length());
        byte[] magnitude = digest.digest();
        BigInteger bi = new BigInteger(1, magnitude);
        String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
        String sql = "select * from " + TABLE_NAME1 + " where username='" + username + "' and password='" + hash + "'";
        data = db.rawQuery(sql, null);
        return data;

    }

    public boolean autoLogin(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_NAME1 + " where username='" + username + "' and password='" + password + "'";
        Cursor res = db.rawQuery(sql, null);
        if (res.getCount() != 0)
            return false;
        else
            return true;
    }

    public int getUserName(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select id_user from " + TABLE_NAME1 + " where username='" + username + "'";
        Cursor res = db.rawQuery(sql, null);

        int id_u = 0;
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                id_u = res.getInt(0);
            }
        }
        return id_u;
    }

    public boolean insertUserLog2(String username, String password) {
        String sq = "Insert into " + TABLE_NAME3 + "(id_user,username,password,date) Values(?,?,?,datetime('now', 'localtime'))";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            statement.clearBindings();
            digest.update(password.getBytes(StandardCharsets.US_ASCII), 0, password.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            int id_user = getUserName(username);
            statement.bindDouble(1, id_user);
            statement.bindString(2, username);
            statement.bindString(3, hash);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }

    public Cursor getLastLogged() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor oldestDateCursor = db.query(TABLE_NAME3, null, null, null, null, null, "date DESC LIMIT 1");
        return oldestDateCursor;
    }

    public Cursor checkIsTrueUser(String oldPasswd) throws NoSuchAlgorithmException {
        Cursor lastLogged = getLastLogged();
        String userName = null;
        int id_user = 0;
        if (lastLogged.getCount() != 0) {
            while (lastLogged.moveToNext()) {
                id_user = lastLogged.getInt(0);
                userName = lastLogged.getString(1);
            }
        }
        SQLiteDatabase db = this.getWritableDatabase();

        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(oldPasswd.getBytes(StandardCharsets.US_ASCII), 0, oldPasswd.length());
        byte[] magnitude = digest.digest();
        BigInteger bi = new BigInteger(1, magnitude);
        String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
        String sql = "select * from " + TABLE_NAME1 + " where id_user=" + id_user + " and username='" + userName + "' and password='" + hash + "'";
        Cursor data = db.rawQuery(sql, null);
        return data;
    }

    public boolean changePasswd(String passwd, Cursor user, String oldPasswd) throws NoSuchAlgorithmException {
        SQLiteDatabase db = this.getWritableDatabase();
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(passwd.getBytes(StandardCharsets.US_ASCII), 0, passwd.length());
        byte[] magnitude = digest.digest();
        BigInteger bi = new BigInteger(1, magnitude);
        String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
        String userName = null;
        int id_user = 0;
        String autologin = null;
        if (user.getCount() != 0) {
            while (user.moveToNext()) {
                id_user = user.getInt(0);
                userName = user.getString(1);
                autologin = user.getString(7);
            }
        }

        String sql = "Update '" + TABLE_NAME1 + "' set password='" + hash + "' where id_user=" + id_user + " and username='" + userName + "';";
        Cursor res = db.rawQuery(sql, null);
        if (autologin.equals("Y")) {
            deleteUser();
        }

        MessageDigest digest2 = MessageDigest.getInstance("MD5");
        digest2.update(oldPasswd.getBytes(StandardCharsets.US_ASCII), 0, oldPasswd.length());
        byte[] magnitude2 = digest2.digest();
        BigInteger bi2 = new BigInteger(1, magnitude2);
        String hash2 = String.format("%0" + (magnitude2.length << 1) + "x", bi2);
        String sql2 = "select * from " + TABLE_NAME1 + " where id_user=" + id_user + " and username='" + userName + "' and password='" + hash2 + "'";
        Cursor res2 = db.rawQuery(sql2, null);

        if (res2.getCount() > 0) {
            res2.close();
            return false;
        }
        res2.close();
        return true;
    }

    public Cursor getInfoUser() {
        Cursor lastLogged = getLastLogged();
        String userName = null;
        String password = null;
        int id_user = 0;
        if (lastLogged.getCount() != 0) {
            while (lastLogged.moveToNext()) {
                id_user = lastLogged.getInt(0);
                userName = lastLogged.getString(1);
                password = lastLogged.getString(2);
            }
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String sql2 = "select * from " + TABLE_NAME1 + " where id_user=" + id_user + " and username='" + userName + "' and password='" + password + "';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

    public boolean changeInfo(String vName, String name, String email, String nrtel, String uname) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor check = getInfoUser();
        ContentValues newVal = new ContentValues();
        newVal.put("vName", vName);
        newVal.put("name", name);
        newVal.put("mail", email);
        newVal.put("nr_tel", nrtel);
        String[] args = new String[]{uname};
        db.update(TABLE_NAME1, newVal, "username=?", args);
        String n, v, e, p;
        n = v = e = p = null;
        if (check.getCount() != 0) {
            while (check.moveToNext()) {
                v = check.getString(2);
                n = check.getString(3);
                e = check.getString(5);
                p = check.getString(6);
            }
        }
        if (v.equals(vName) || n.equals(name) || e.equals(email) || p.equals(nrtel)) {
            return true;
        } else
            return false;
    }

    public boolean insertUserLog3(String username, String password) {
        String sq = "Insert into " + TABLE_NAME2 + "(username,password) Values(?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindString(1, username);
            statement.bindString(2, password);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }

    public boolean changeAutoLog(String autolog, String uname) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor check = getInfoUser();
        ContentValues newVal = new ContentValues();
        newVal.put("autologin", autolog);
        String[] args = new String[]{uname};
        db.update(TABLE_NAME1, newVal, "username=?", args);
        String a = null;
        if (check.getCount() != 0) {
            while (check.moveToNext()) {
                a = check.getString(7);
            }
        }

        Cursor check2 = getInfoUser();
        String a2 = null;
        String passwd = null;
        if (check2.getCount() != 0) {
            while (check2.moveToNext()) {
                passwd = check2.getString(4);
                a2 = check2.getString(7);
            }
        }

        if (a.equals(a2)) {
            if (a2.equals("N"))
                deleteUser();
            else
                insertUserLog3(uname, passwd);
            return true;
        } else
            return false;
    }

    public Cursor getRoomData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select * from " + TABLE_NAME4 + " where username is '" + username + "';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

    public Cursor getBoxInRoom(String room) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select * from " + TABLE_NAME4 + " where username is '" + username + "' and room_name is '" + room + "';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

    public Cursor getRoomData2() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select distinct room_name from " + TABLE_NAME4 + " where username is '" + username + "';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

    public boolean insertBox(String room, String box, String pathQr, String pathGallery) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }

        String sq = "Insert into " + TABLE_NAME4 + "(username,room_name,box_name,qr_code_path,gallery_path) Values(?,?,?,?,?)";
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindString(1, username);
            statement.bindString(2, room);
            statement.bindString(3, box);
            statement.bindString(4, pathQr);
            statement.bindString(5, pathGallery);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }

    public boolean changeBoxName(String room_name, String oldNameBox, String newName, String newPathQR, String newPathGallery) {
        //----------------------1------------------------------------
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        //---------------------2-------------------------------------------
        String sql2 = "select box_name,qr_code_path,gallery_path from " + TABLE_NAME4 + " where username is '" + username + "' and box_name is'" + oldNameBox + "';";
        Cursor data = db.rawQuery(sql2, null);

        String oldBox = "";
        String oldQRPath = "";
        String oldGalleryPath = "";
        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                oldBox = data.getString(0);
                oldQRPath = data.getString(1);
                oldGalleryPath = data.getString(2);
            }
        }
        // --------------------3-------------------------------------------
        ContentValues newVal = new ContentValues();
        newVal.put("box_name", newName);
        newVal.put("qr_code_path", newPathQR);
        newVal.put("gallery_path", newPathGallery);
        String[] args = new String[]{username, room_name, oldNameBox};
        db.update(TABLE_NAME4, newVal, "username=? AND room_name=? and box_name=? ", args);

        //---------------------4-----------------------------------------------------------
        String sql3 = "select box_name,qr_code_path,gallery_path from " + TABLE_NAME4 + " where username is '" + username + "' and box_name is'" + newName + "';";
        Cursor data2 = db.rawQuery(sql3, null);

        String newBox = "";
        String newQRPath = "";
        String newGalleryPath = "";
        if (data2.getCount() != 0) {
            while (data2.moveToNext()) {
                newBox = data2.getString(0);
                newQRPath = data2.getString(1);
                newGalleryPath = data2.getString(2);
            }
        }
        //---------------------5------------------------------------
        if (!oldBox.equals(newBox) && !oldQRPath.equals(newQRPath) && !oldGalleryPath.equals(newGalleryPath)) {
            return true;
        } else
            return false;
    }

    public boolean deleteBox(String roomName, String boxName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }

        String[] args = new String[]{username, roomName, boxName};
        return db.delete(TABLE_NAME4, "username=? AND room_name=? and box_name=? ", args) > 0;
    }

    public Cursor getBoxInRoom3(String box) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select * from " + TABLE_NAME4 + " where username is '" + username + "' and box_name is '" + box + "';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

    //username TEXT,tool_name TEXT, qr_code_path TEXT, image_path TEXT, room_name TEXT, box_name TEXT , comment TEXT
    public Cursor getToolsList(String room,String box) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select tool_name from " + TABLE_NAME5 + " where username is '" + username + "' and box_name is '" + box + "' and room_name is '"+room+"';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }
 //username TEXT,tool_name TEXT, qr_code_path TEXT, image_path TEXT, room_name TEXT, box_name TEXT, comment TEXT
    public boolean insertTool(String tool_name, String qr_code_path, String image_path, String room_name,String box_name, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }

        String sq = "Insert into " + TABLE_NAME5 + "(username,tool_name,qr_code_path,image_path,room_name,box_name,comment) Values(?,?,?,?,?,?,?)";
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindString(1, username);
            statement.bindString(2, tool_name);
            statement.bindString(3, qr_code_path);
            statement.bindString(4, image_path);
            statement.bindString(5, room_name);
            statement.bindString(6, box_name);
            statement.bindString(7, comment);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public Cursor getToolsPath(String room,String box) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select tool_name, image_path from " + TABLE_NAME5 + " where username is '" + username + "' and box_name is '" + box + "' and room_name is '"+room+"';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }
    public boolean getToolsPath2(String toolName,String RoomName, String oldBoxName, String newBoxName, String newPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        //---------------------2-------------------------------------------
        String sql2 = "select image_path,box_name from " + TABLE_NAME5 + " where username is '" + username + "' and box_name is '" + oldBoxName + "' and room_name is '"+ RoomName +"';";
        Cursor data = db.rawQuery(sql2, null);

        String oldBox = "";
        String oldGalleryPath = "";
        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                oldGalleryPath = data.getString(0);
                oldBox = data.getString(1);
            }
        }
        ContentValues newVal = new ContentValues();
        newVal.put("image_path", newPath);
        newVal.put("box_name", newBoxName);
        String[] args = new String[]{username, toolName, RoomName,oldBoxName};
        db.update(TABLE_NAME5, newVal, "username=? AND tool_name=? AND room_name=? and box_name=? ", args);

        String sql3 = "select image_path,room_name,box_name from " + TABLE_NAME5 + " where username is '" + username + "' and box_name is '" + oldBoxName + "' and room_name is '"+ RoomName +"';";
        Cursor data2 = db.rawQuery(sql3, null);

        String newBox = "";
        String newImgPath = "";
        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                newImgPath = data2.getString(0);
                newBox = data2.getString(1);
            }
        }

        return !oldBox.equals(newBox) && !oldGalleryPath.equals(newImgPath);
    }

    public Cursor getToolsPath2(String room) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select tool_name, image_path, box_name from " + TABLE_NAME5 + " where username is '" + username + "' and room_name is '"+room+"';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

    public boolean changeRoomName(String boxName,String oldName, String newName,String newGallPath, String newQr) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }

        Cursor room = getRoomData();
        List<String> rooms = new ArrayList<String>();

        if (room.getCount() != 0) {
            while (room.moveToNext()) {
                rooms.add(room.getString(1));
            }
        }
        ContentValues newVal = new ContentValues();
        newVal.put("room_name", newName);
        newVal.put("qr_code_path",newQr);
        newVal.put("gallery_path",newGallPath);
        String[] args = new String[]{username, oldName,boxName};
        db.update(TABLE_NAME4, newVal, "username=? AND room_name=? AND box_name=? ", args);

        Cursor room2 = getRoomData();
        List<String> rooms2 = new ArrayList<String>();

        if (room2.getCount() != 0) {
            while (room2.moveToNext()) {
                rooms2.add(room2.getString(1));
            }
        }
        return !rooms.contains(rooms2);
    }

    public boolean updateToolsPath(String toolName,String oldRoomName, String boxName, String newrRoomName, String newPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        //---------------------2-------------------------------------------
        String sql2 = "select image_path,box_name from " + TABLE_NAME5 + " where username is '" + username + "' and box_name is '" + boxName + "' and room_name is '"+ oldRoomName +"';";
        Cursor data = db.rawQuery(sql2, null);

        String oldBox = "";
        String oldGalleryPath = "";
        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                oldGalleryPath = data.getString(0);
                oldBox = data.getString(1);
            }
        }
        ContentValues newVal = new ContentValues();
        newVal.put("image_path", newPath);
        newVal.put("room_name", newrRoomName);
        String[] args = new String[]{username, toolName, oldRoomName,boxName};
        db.update(TABLE_NAME5, newVal, "username=? AND tool_name=? AND room_name=? and box_name=? ", args);

        String sql3 = "select image_path,room_name,box_name from " + TABLE_NAME5 + " where username is '" + username + "' and box_name is '" + boxName + "' and room_name is '"+ newrRoomName +"';";
        Cursor data2 = db.rawQuery(sql3, null);

        String newBox = "";
        String newImgPath = "";
        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                newImgPath = data2.getString(0);
                newBox = data2.getString(1);
            }
        }

        return !oldBox.equals(newBox) && !oldGalleryPath.equals(newImgPath);
    }

    public Cursor checkToolsRoom(String oldPath, String boxName ){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql3 = "select image_path,room_name,box_name from " + TABLE_NAME5 + " where username is '" + username + "' and box_name is '" + boxName + "' and image_path is '"+ oldPath +"';";
        Cursor data2 = db.rawQuery(sql3, null);
        return data2;

    }

    public Cursor checkBoxRoom(String box, String oldPath, String oldPathQr) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select * from " + TABLE_NAME4 + " where username is '" + username + "' and box_name is '" + box + "' and qr_code_path is '"+ oldPathQr+  "' or gallery_path is '"+ oldPath+ "';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

    public Cursor getToolsPath3(String room, String boxName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select tool_name, image_path, box_name from " + TABLE_NAME5 + " where username is '" + username + "' and room_name is '"+room+"' and box_name is '"+boxName+"';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

    public Cursor checkIfQrCode(String room , String boxName, String toolName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select qr_code_path from " + TABLE_NAME5 + " where username is '" + username + "' and room_name is '"+room+"' and box_name is '"+boxName+"' and tool_name is '"+toolName+"';";
        Cursor data = db.rawQuery(sql2, null);

        return data;
    }

    public boolean delTools(String room,String box,String tools){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String[] whereArgs = new String[]{username,room,box,tools};
        return db.delete(TABLE_NAME5,"username=? and room_name=? and box_name=? and tool_name=? ",whereArgs) >0;
    }

    public void delBox(String room,String box){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String[] whereArgs = new String[]{username,room,box};
        db.delete(TABLE_NAME4,"username=? and room_name=? and box_name=?",whereArgs);
    }

    public Cursor checkIfExistTools(String room){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor get_id_user = getInfoUser();
        String username = null;
        if (get_id_user.getCount() != 0) {
            while (get_id_user.moveToNext()) {
                username = get_id_user.getString(1);
            }
        }
        String sql2 = "select tool_name, image_path, box_name from " + TABLE_NAME5 + " where username is '" + username + "' and room_name is '"+room+"';";
        Cursor data = db.rawQuery(sql2, null);
        return data;
    }

}

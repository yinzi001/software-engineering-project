package com.programiner.gongdaquanzi.Adapter.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DButil extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="gongdaquanzi.db";
    private static final int DATABASE_VERSION=1;
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "password TEXT)";
    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE IF NOT EXISTS profile (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "grade TEXT," +
            "major TEXT," +
            "gender TEXT)";


    // 在DButil类中添加查询用户的方法
    public boolean checkUserExists(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    public DButil(@Nullable Context context) {//初始化数据库
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PROFILE);
       // Toast.makeText(Context, "Create successful")
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("Drop TABLE IF EXISTS profile");
        onCreate(db);
    }

    // 添加用户的方法
    public void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO users (username, password) VALUES (?, ?)", new String[]{username, password});
        db.close();
    }

}

package com.bobby.nesty.util.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by corous360 on 2016/6/1.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String name = "ZPDB"; //数据库名称

    private static final int version = 1;

    public DatabaseHelper(Context context) {
        this(context, name);
    }

    public DatabaseHelper(Context context, String dbname){
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS person (personid integer primary key autoincrement," +
                " name varchar(20), password varchar(20), age INTEGER)");

        db.execSQL("create table if not exists story (storyid integer primary key autoincrement," +
                "title varchar(30), content varchar(1000), date varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

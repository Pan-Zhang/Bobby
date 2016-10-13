package com.bobby.nesty.util.database;

import android.database.sqlite.SQLiteOpenHelper;

/**
 * Can't be used in thread , just use in main thread
 * Created by corous360 on 2016/7/18.
 */
public class DatabaseManager2 extends DaoImpl{

    private SQLiteOpenHelper mDatabaseHelper;

    public DatabaseManager2(SQLiteOpenHelper sqLiteOpenHelper){
        this.mDatabaseHelper = sqLiteOpenHelper;
    }

    @Override
    public void open() {
        if(!isOpen()){
            db = mDatabaseHelper.getWritableDatabase();
        }
    }

    @Override
    public void close(){
        if(isOpen()){
            db.close();
        }
    }
}

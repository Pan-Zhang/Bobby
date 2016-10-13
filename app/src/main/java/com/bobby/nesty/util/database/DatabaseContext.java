package com.bobby.nesty.util.database;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

/**
 * Created by corous360 on 2016/6/1.
 */
public class DatabaseContext extends ContextWrapper {

    private String path;

    public DatabaseContext(Context base, String path){
        super(base);
        this.path = path;
    }

    @Override
    public File getDatabasePath(String name) {
        //判断是否存在sd卡
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if(!sdExist){
            return null;
        }
        else{//如果存在
            //获取sd卡路径
            String dbDir=android.os.Environment.getExternalStorageDirectory().toString()+"/"+path;
            String dbPath = dbDir+"/"+name;//数据库路径
            //判断目录是否存在，不存在则创建该目录
            File dirFile = new File(dbDir);
            if(!dirFile.exists())
                dirFile.mkdirs();

            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            //判断文件是否存在，不存在则创建该文件
            File dbFile = new File(dbPath);
            if(!dbFile.exists())
            {
                try {
                    isFileCreateSuccess = dbFile.createNewFile();//创建文件
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else {
                isFileCreateSuccess = true;
            }

            //返回数据库文件对象
            if(isFileCreateSuccess)
                return dbFile;
            else
                return null;
        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

}

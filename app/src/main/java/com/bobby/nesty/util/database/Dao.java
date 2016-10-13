package com.bobby.nesty.util.database;

import android.content.ContentValues;

import java.util.List;
import java.util.Map;

/**
 * Created by corous360 on 2016/9/19.
 */
public interface Dao {
    boolean insert(String table, ContentValues values);
    boolean delete(String table, String whereClause, String[] whereArgs);
    boolean update(String table, ContentValues values, String whereClause, String[] whereArgs);
    Map<String, String> view(String table, String selection, String[] selectionArgs);
    List<Map<String, String>> listPersonMaps(String table, String selection, String[] selectionArgs, String orderBy);
    void InsertMaps(String sTableName, List<Map<String, Object>> ListOfMaps);
    void ExecNonQuery2(String Statement, List<Object> Args);
    void open();
    void close();
    boolean isOpen();
}

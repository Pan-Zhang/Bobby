package com.bobby.nesty.util.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by corous360 on 2016/9/19.
 */
public abstract class DaoImpl implements Dao {

    public SQLiteDatabase db;

    /**
     for example
     ContentValues values = new ContentValues();
     values1.put("name", "张龙");
     values1.put("address", "beijing");
     values1.put("sex", "male");
     insert("user", values);

     * @param table
     * @param values
     * @return
     */
    @Override
    public boolean insert(String table, ContentValues values) {
        open();
        boolean flag = false;
        long id = -1;
        try{
            id = db.insert(table, null, values);
            flag = id != -1;
        }catch (Exception e){
            e.printStackTrace();
        }
        close();
        return flag;
    }

    /**
     for example
     delete("user", " id =? ", new String[]{"1"});

     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public boolean delete(String table, String whereClause, String[] whereArgs) {
        open();
        boolean flag = false;
        int count;
        try {
            count = db.delete(table, whereClause, whereArgs);
            flag = count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
        return flag;
    }

    /**
     for example
     ContentValues values = new ContentValues();
     values.put("name", "张三");
     values.put("address", "上海");
     values.put("sex", "男");
     update("user", values, " id=? ", new String[]{"2"});

     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public boolean update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        open();
        boolean flag = false;
        int count;
        try {
            count = db.update(table, values, whereClause, whereArgs);
            flag = count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
        return flag;
    }

    /**
     for example
     view("user", " id=? ", new String[]{"2"});

     * @param table
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public Map<String, String> view(String table, String selection, String[] selectionArgs) {
        open();
        Cursor cursor = null;
        Map<String, String> map = new HashMap<String, String>();
        try {
            cursor = db.query(true, table, null, selection,selectionArgs, null, null, null, null);
            int cols_len = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                for (int i = 0; i < cols_len; i++) {
                    String cols_key = cursor.getColumnName(i);
                    String cols_value = cursor.getString(cursor.getColumnIndex(cols_key));
                    if (cols_value == null) {
                        cols_value = "";
                    }
                    map.put(cols_key, cols_value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(cursor != null){
            cursor.close();
        }
        close();
        return map;
    }

    /**
     for example
     listPersonMaps("user", null, null);

     * @param table
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public List<Map<String, String>> listPersonMaps(String table, String selection, String[] selectionArgs, String orderBy) {
        open();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Cursor cursor = null;
        try {
            cursor = db.query(false, table, null, selection,selectionArgs, null, null, orderBy, null);
            int cols_len = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < cols_len; i++) {
                    String cols_key = cursor.getColumnName(i);
                    String cols_value = cursor.getString(cursor.getColumnIndex(cols_key));
                    if (cols_value == null) {
                        cols_value = "";
                    }
                    map.put(cols_key, cols_value);
                }
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(cursor != null){
            cursor.close();
        }
        close();
        return list;
    }

    /**
     * 批量插入
     * @param sTableName
     * @param ListOfMaps
     */
    @Override
    public void InsertMaps(String sTableName, List<Map<String, Object>> ListOfMaps) {
        open();
        try
        {
            boolean bFirst=true;
            if(ListOfMaps==null || ListOfMaps.size()<1)
            {
                return;
            }

            Log.e("insertbegin", "begin");
            db.beginTransaction();
            for(int i=0;i<ListOfMaps.size();i++)
            {
                bFirst= true;
                StringBuilder sb=new StringBuilder();
                StringBuilder columns=new StringBuilder();
                StringBuilder values=new StringBuilder();
                sb = sb.append("INSERT INTO [" + sTableName + "] (");

                ArrayList<Object> listOfValues = new ArrayList<Object>();
                Map<String, Object> map = ListOfMaps.get(i);

                Iterator<Map.Entry<String, Object>> iterator =map.entrySet().iterator();
                while(iterator.hasNext())
                {
                    Map.Entry<String, Object> next = iterator.next();
                    if(!bFirst)
                    {
                        columns = columns.append(", ");
                        values = values.append(", ");
                    }
                    columns.append("[").append(next.getKey()).append("]");
                    values.append("?");
                    listOfValues.add(next.getValue());
                    bFirst = false;
                }
                sb = sb.append(columns.toString()).append(") VALUES (").append(values.toString()).append(")");
                ExecNonQuery2(sb.toString(), listOfValues);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            Log.e("insertend", "end");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        close();
    }

    @Override
    public void ExecNonQuery2(String Statement, List<Object> Args) {
        SQLiteStatement s = this.db.compileStatement(Statement);
        try
        {
            int numArgs = 0;
            if (Args != null)
            {
                numArgs = Args.size();
            }
            for (int i = 0; i < numArgs; i++)
            {
                DatabaseUtils.bindObjectToProgram(s, i + 1, Args.get(i));
            }
            s.execute();
        } finally
        {
            s.close();
        }
    }

    @Override
    public boolean isOpen() {
        if(db != null){
            return db.isOpen();
        }
        return false;
    }
}

package com.example.browser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.browser.other.FavAndHisManager;

/**
 * Created by Young on 2016/11/29.
 */

public class SQLManager extends SQLiteOpenHelper implements  IDatabase{

    private static final String DEG_TAG = "webBrowser_SQLManager";

    public SQLManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL(SQLStr.CREATE_TABLE_FAVORITES);
        db.execSQL(SQLStr.CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //更新
        db.execSQL(SQLStr.CREATE_TABLE_FAVORITES);
        db.execSQL(SQLStr.CREATE_TABLE_HISTORY);
    }

    @Override
    public boolean add(SQLiteDatabase sqLiteDatabase, String tableName, String name, String url, long date) throws SQLException {
        //添加数据
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("url", url);
        if (tableName.equals(FavAndHisManager.TABLE_NAME_Favorite)) {
        } else {
            values.put("date", date);
        }
        long id = sqLiteDatabase.insert(tableName, null, values);
        if (id != -1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(SQLiteDatabase sqLiteDatabase, String tableName, String id) {
        int number = sqLiteDatabase.delete(tableName, "id=?", new String[]{id});
        if (number != 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteAll(SQLiteDatabase sqLiteDatabase, String tableName) {
        int number = sqLiteDatabase.delete(tableName, null, null);
        if (number != 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean modify(SQLiteDatabase sqLiteDatabase, String tableName, String id, String name, String url) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("url", url);
        int number = sqLiteDatabase.update(tableName, values, "id=?", new String[]{id});
        if (number != 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Cursor getAll(SQLiteDatabase sqLiteDatabase, String tableName) {
        String[] returnColumns = null;
        if (tableName.equals(FavAndHisManager.TABLE_NAME_Favorite)) {
            returnColumns = new String[]{
                    "id as _id",
                    "name",
                    "url"
            };
        } else {
            returnColumns = new String[]{
                    "id as _id",
                    "name",
                    "url",
                    "date"
            };
        }
        Cursor result = sqLiteDatabase.query(tableName, returnColumns, null, null, null, null, null);
        while (result.moveToNext()) {
            String id = String.valueOf(result.getInt(result.getColumnIndex("_id")));
            String name = result.getString(result.getColumnIndex("name"));
            String url = result.getString(result.getColumnIndex("url"));
            if(tableName.equals(FavAndHisManager.TABLE_NAME_Favorite)) {
            } else {
                String date = String.valueOf(result.getLong(result.getColumnIndex("date")));
            }
        }
        return result;
    }

    @Override
    public boolean multiply(SQLiteDatabase sqLiteDatabase, String tableName, String url) {
        Cursor result = sqLiteDatabase.query(tableName, null, "url=?", new String[]{url}, null, null, null);
        if (result.getCount() > 0) {
            result.close();
            return true;
        } else {
            result.close();
            return false;
        }
     }

    @Override
    public void transactionAround(boolean readOnly, CallBack callback) {
        SQLiteDatabase sqLiteDatabase = null;
        if (readOnly) {
            sqLiteDatabase = this.getReadableDatabase();
        } else {
            sqLiteDatabase = this.getWritableDatabase();
        }
        sqLiteDatabase.beginTransaction();
        callback.doSomething(sqLiteDatabase);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }
}

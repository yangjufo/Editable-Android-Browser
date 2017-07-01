package com.example.browser.other;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.browser.database.CallBack;
import com.example.browser.database.IDatabase;
import com.example.browser.database.SQLManager;

/**
 * Created by Young on 2016/11/29.
 */

public class FavAndHisManager {

    private static final String DEG_TAG = "webBrowser_FavoritesAndHistoryManager";
    public static final String TABLE_NAME_Favorite = "favorite";
    public static final String TABLE_NAME_History = "history";

    private IDatabase database;
    private boolean flag = false;
    private Cursor resultMap;

    public FavAndHisManager(Context context) {
        this.database = new SQLManager(context, "com_webBrowser_Data", null, 1);
    }

    //添加书签
    public boolean addFavorite(final String name, final String url) {
        flag = false;
        this.database.transactionAround(false, new CallBack() {
            @Override
            public void doSomething(SQLiteDatabase sqLiteDatabase) {
                boolean ifmultiply = database.multiply(sqLiteDatabase, TABLE_NAME_Favorite, url);
                if (!ifmultiply) {
                    flag = database.add(sqLiteDatabase, TABLE_NAME_Favorite, name, url, -1);
                } else {
                    flag = false;
                }
            }
        });
        return flag;
    }

    //删除书签
    public boolean deleteFavorite(final  String id) {
        flag = false;
        this.database.transactionAround(false, new CallBack() {
            @Override
            public void doSomething(SQLiteDatabase sqLiteDatabase) {
                flag = database.delete(sqLiteDatabase, TABLE_NAME_Favorite, id);
            }
        });
        return flag;
    }

    //修改书签
    public boolean modifyFavorite(final String id, final String name, final String url) {
        flag = false;
        this.database.transactionAround(false, new CallBack() {
            @Override
            public void doSomething(SQLiteDatabase sqLiteDatabase) {
                flag = database.modify(sqLiteDatabase, TABLE_NAME_Favorite, id, name, url);
            }
        });
        return flag;
    }

    //获取所有书签
    public Cursor getAllFavorites() {
        this.database.transactionAround(true, new CallBack() {
            @Override
            public void doSomething(SQLiteDatabase sqLiteDatabase) {
                resultMap = database.getAll(sqLiteDatabase, TABLE_NAME_Favorite);
            }
        });
        return resultMap;
    }

    //添加历史
    public boolean addHistory(final String name, final String url, final long date) {
        flag = false;
        this.database.transactionAround(false, new CallBack() {
            @Override
            public void doSomething(SQLiteDatabase sqLiteDatabase) {
                //历史记录可以重复，id和date不同
                flag = database.add(sqLiteDatabase, TABLE_NAME_History, name, url, date);
            }
        });
        return flag;
    }

    //删除历史
    public boolean deleteHistory(final String id) {
        flag = false;
        this.database.transactionAround(false, new CallBack() {
            @Override
            public void doSomething(SQLiteDatabase sqLiteDatabase) {
                flag = database.delete(sqLiteDatabase, TABLE_NAME_History, id);
            }
        });
        return flag;
    }

    //删除所有历史
    public boolean deleteAllHistories() {
        flag = false;
        this.database.transactionAround(false, new CallBack() {
            @Override
            public void doSomething(SQLiteDatabase sqLiteDatabase) {
                flag = database.deleteAll(sqLiteDatabase, TABLE_NAME_History);
            }
        });
        return flag;
    }

    //获取所有历史
    public Cursor getAllHistory() {
        this.database.transactionAround(true, new CallBack() {
            @Override
            public void doSomething(SQLiteDatabase sqLiteDatabase) {
                resultMap = database.getAll(sqLiteDatabase, TABLE_NAME_History);
            }
        });
        return resultMap;
    }
}

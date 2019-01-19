package com.xzl.project.minizhihu.view.adapter;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SearchListSqLiteOpenHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "keep.db";
    private final static int DB_VERSION = 1;

    public SearchListSqLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlStr = "CREATE TABLE IF NOT EXISTS building (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);";//创建存储搜索楼盘的表
        db.execSQL(sqlStr);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

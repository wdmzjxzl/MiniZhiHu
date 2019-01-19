package com.xzl.project.minizhihu.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xzl.project.minizhihu.view.adapter.SearchListSqLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchListDBOperation {
    SearchListSqLiteOpenHelper searchListSqLiteOpenHelper;
    SQLiteDatabase recordsDb;
    String tableName;

    public SearchListDBOperation(Context context,String tableName){
        searchListSqLiteOpenHelper = new SearchListSqLiteOpenHelper(context);
        this.tableName = tableName;
    }

    //添加搜索记录
    public void addRecords(String record){
        if (!isHasRecord(record)){
            recordsDb = searchListSqLiteOpenHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("name",record);
            //添加
            recordsDb.insert(tableName,null,values);
            //关闭
            recordsDb.close();
        }
    }

    /**
     * 判断是否含有该搜索记录
     * @param record
     * @return
     */
    public boolean isHasRecord(String record) {
        boolean isHasRecord = false;
        recordsDb = searchListSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = recordsDb.query(tableName,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            if (record.equals(cursor.getString(cursor.getColumnIndexOrThrow("name")))){
                isHasRecord = true;
            }
        }
        //关闭数据库
        recordsDb.close();
        return isHasRecord;
    }

    /**
     * 获取全部搜索记录
     */
    public List<String> getRecordsList(){
        List<String> recordsList = new ArrayList<>();
        recordsDb = searchListSqLiteOpenHelper.getReadableDatabase();
        Cursor curso = recordsDb.query(tableName,null,null,null,null,null,null);
        while (curso.moveToNext()){
            String name = curso.getString(curso.getColumnIndexOrThrow("name"));
            recordsList.add(name);
        }
        //关闭数据库
        recordsDb.close();
        return recordsList;
    }

    /**
     * 清空搜索记录
     */
    public void deleteAllRecords(){
        recordsDb = searchListSqLiteOpenHelper.getWritableDatabase();
        recordsDb.execSQL("delete from "+tableName);

        recordsDb.close();
    }
}

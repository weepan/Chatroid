package com.jtong.chatroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库sql语句并执行
        String sql="create table setting(id INTEGER PRIMARY KEY,apikey varchar(256),server varchar(256))";
        db.execSQL(sql);
        sql="create table messages(topic varchar(256) PRIMARY KEY,json text)";
        db.execSQL(sql);
        sql="create table topics(topic varchar(256) PRIMARY KEY)";
        db.execSQL(sql);
        sql="insert into topics(topic) values('翻译')";
        db.execSQL(sql);
        sql="insert into topics(topic) values('编程')";
        db.execSQL(sql);
        sql="insert into topics(topic) values('聊天')";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="drop table IF EXISTS setting";
        db.execSQL(sql);
        sql="drop table IF EXISTS messages";
        db.execSQL(sql);
        sql="drop table IF EXISTS topics";
        db.execSQL(sql);
        onCreate(db);
    }
}

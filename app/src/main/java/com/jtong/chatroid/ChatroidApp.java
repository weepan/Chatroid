package com.jtong.chatroid;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChatroidApp extends Application {
    private static Config globalConfig =new Config();

    public static Config getGlobalConfig() {
        return globalConfig;
    }

    public static void setGlobalConfig(Config value) {
        globalConfig = value;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化数据库
        DBHelper databaseHelper = new DBHelper(this, "setting.db", null, 2);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //创建游标对象
        Cursor cursor = db.query("setting", new String[]{"id","apikey","server"}, "id=?",
                new String[]{"1"}, null, null, null);
        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String apikey = cursor.getString(cursor.getColumnIndex("apikey"));
            @SuppressLint("Range") String server = cursor.getString(cursor.getColumnIndex("server"));
            globalConfig.server = server;
            globalConfig.api_key = apikey;
            Log.i("ChatroidApp","result: id="  + id +" apikey: " + apikey +"  server:" + server);
        }
        // 关闭游标，释放资源
        cursor.close();

    }
}

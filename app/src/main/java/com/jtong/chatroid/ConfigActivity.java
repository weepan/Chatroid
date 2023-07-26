package com.jtong.chatroid;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        // 设置自定义标题栏布局
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.config_title_bar);

        EditText editTextKey = findViewById(R.id.editTextKey);
        editTextKey.setText(ChatroidApp.getGlobalConfig().api_key);

        EditText editTextServer = findViewById(R.id.editTextServer);
        editTextServer.setText(ChatroidApp.getGlobalConfig().server);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在此处将输入配置的值保存到数据库、文件或其他存储位置
                String key = editTextKey.getText().toString();
                String server = editTextServer.getText().toString();
                ChatroidApp.getGlobalConfig().api_key = key;
                ChatroidApp.getGlobalConfig().server = server;
                saveConfigToDatabase(key, server);
                finish();
            }
        });
    }

    private void saveConfigToDatabase(String key, String server) {
        DBHelper mdbHelper = new DBHelper(ConfigActivity.this, "setting.db", null, 2);
        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("apikey", key);
        values.put("server", server);
        //删除后插入
        db.delete("setting", "id=?", new String[]{"1"});
        long newRowId =db.insert("setting", null, values);
        if (newRowId != -1) {
            Toast.makeText(ConfigActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            Log.d("DB", "Data saved successfully");
        } else {
            Toast.makeText(ConfigActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
            Log.d("DB", "Failed to save data");
        }
    }
}
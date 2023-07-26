package com.jtong.chatroid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TopicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatTopicAdapter chatTopicAdapter;
    private List<ChatTopic> topicList;
    private TextView inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        // 设置自定义标题栏布局
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.topic_title_bar);


        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        topicList = new ArrayList<ChatTopic>();
        // 创建聊天人员列表数据
        try {
            topicList = loadTopicsFromDatabase();
        } catch (IOException e) {
            Log.e("TopicActivity", e.toString());
        }
        // 添加更多聊天人员数据...

        // 创建适配器并设置给 RecyclerView
        chatTopicAdapter = new ChatTopicAdapter(this,topicList);
        recyclerView.setAdapter(chatTopicAdapter);

        // 创建 ItemTouchHelper 实例，并附加到 RecyclerView
        ItemTouchHelper.Callback callback = new CustomItemTouchHelperCallback(chatTopicAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ImageButton settingButton = findViewById(R.id.settingButton);
        settingButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TopicActivity.this,
                                ConfigActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Button addButton = findViewById(R.id.addButton);
        inputEditText = findViewById(R.id.inputEditText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicText = inputEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(topicText)) {
                    // 创建消息对象并添加到消息列表
                    ChatTopic topic = new ChatTopic(topicText);
                    topicList.add(topic);
                    chatTopicAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(topicList.size() - 1);
                    // 保存到数据库
                    saveTopicToDatabase(topic);
                    // 清空输入框
                    inputEditText.setText("");
                }
            }
        });
    }



    public void saveTopicToDatabase(ChatTopic topic){

        DBHelper mdbHelper = new DBHelper(TopicActivity.this, "setting.db", null, 2);
        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        values.put("topic", topic.getTopic());

        //删除后插入
        db.delete("topics", "topic=?", new String[]{topic.getTopic()});
        long newRowId =db.insert("topics", null, values);
        if (newRowId != -1) {
            //Toast.makeText(TopicActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            Log.d("TopicActivity", "Data saved successfully");
        } else {
            //Toast.makeText(TopicActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
            Log.d("TopicActivity", "Failed to save data");
        }
    }

    @SuppressLint("Range")
    public List<ChatTopic> loadTopicsFromDatabase() throws IOException {
        List<ChatTopic> topics = new ArrayList<ChatTopic>();
        // 初始化数据库
        DBHelper databaseHelper = new DBHelper(this, "setting.db", null, 2);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //创建游标对象
        Cursor cursor = db.query("topics", new String[]{"topic"}, null,
                null, null, null, null);
        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            String topic = cursor.getString(cursor.getColumnIndex("topic"));
            topics.add(new ChatTopic(topic));
            Log.i("TopicActivity"," topic: " + topic );
        }
        // 关闭游标，释放资源
        cursor.close();
        return topics;
    }
}
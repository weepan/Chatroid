package com.jtong.chatroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatTopicAdapter chatTopicAdapter;
    private List<ChatTopic> chatTopics;

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

        // 创建聊天人员列表数据
        chatTopics = new ArrayList<ChatTopic>();
        chatTopics.add(new ChatTopic("聊天"));
        chatTopics.add(new ChatTopic("编程"));
        chatTopics.add(new ChatTopic("翻译"));
        // 添加更多聊天人员数据...

        // 创建适配器并设置给 RecyclerView
        chatTopicAdapter = new ChatTopicAdapter(this,chatTopics);
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
    }
}
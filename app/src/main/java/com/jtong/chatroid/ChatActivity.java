package com.jtong.chatroid;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText inputEditText;
    private Button sendButton;
    private ImageButton cleanButton;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private Chatgpt chatgpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // 设置自定义标题栏布局
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.chat_title_bar);

        recyclerView = findViewById(R.id.recyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        cleanButton = findViewById(R.id.cleanButton);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        chatgpt = new Chatgpt();
        chatgpt.setOnResponseListener(new Chatgpt.OnResponseListener() {
            @Override
            public void OnResponse(String response) {
                // 创建消息对象并添加到消息列表
                Message message = new Message(response, System.currentTimeMillis(), false);
                messageList.add(message);
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        //更新UI
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }

                });


            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = inputEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(messageText)) {
                    // 创建消息对象并添加到消息列表
                    Message message = new Message(messageText, System.currentTimeMillis(), true);
                    messageList.add(message);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    chatgpt.completion(messageList);

                    // 清空输入框
                    inputEditText.setText("");
                }
            }
        });

        cleanButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageList.clear();
                        messageAdapter.notifyDataSetChanged();
                    }
                });

    }
}

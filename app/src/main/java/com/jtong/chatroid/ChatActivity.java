package com.jtong.chatroid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.moshi.Moshi;

import java.io.IOException;
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
    private String system;

    private final Moshi moshi = new Moshi.Builder().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //获取系统值
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            system = bundle.getString("system");
        }


        // 设置自定义标题栏布局
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.chat_title_bar);

        recyclerView = findViewById(R.id.recyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        cleanButton = findViewById(R.id.cleanButton);
        TextView  tvTitle=findViewById(R.id.tv_title);
        if (system!=null && !system.isEmpty())
            tvTitle.setText(system);

        try {
            messageList = loadMessageFromDatabase(system);
        } catch (IOException e) {
            Log.e("ChatActivity",e.toString());
        }
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
                    if(TextUtils.isEmpty(ChatroidApp.getGlobalConfig().api_key)){
                        Toast.makeText(ChatActivity.this, "Please set the API KEY", Toast.LENGTH_LONG).show();
                    }else {
                        chatgpt.completion(system, messageList);

                        // 清空输入框
                        inputEditText.setText("");
                    }
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
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在此处将聊天记录保存到数据库
                saveMessageToDatabase(messageList);
                finish();
            }
        });

    }

    private static class Messages{
        public List<Message> data = new ArrayList<>();
    }

    public void saveMessageToDatabase(List<Message> messageList){
        Messages msgs = new Messages();
        msgs.data = messageList;
        String msgsJson = moshi.adapter(Messages.class).toJson(msgs);
        DBHelper mdbHelper = new DBHelper(ChatActivity.this, "setting.db", null, 2);
        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        values.put("topic", system);
        values.put("json", msgsJson);
         //删除后插入
        db.delete("messages", "topic=?", new String[]{system});
        long newRowId =db.insert("messages", null, values);
        if (newRowId != -1) {
            Toast.makeText(ChatActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            Log.d("DB", "Data saved successfully");
        } else {
            Toast.makeText(ChatActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
            Log.d("DB", "Failed to save data");
        }
    }

    @SuppressLint("Range")
    public List<Message> loadMessageFromDatabase(String topic) throws IOException {
        String msgsJson=null;

        // 初始化数据库
        DBHelper databaseHelper = new DBHelper(this, "setting.db", null, 2);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //创建游标对象
        Cursor cursor = db.query("messages", new String[]{"topic","json"}, "topic=?",
                new String[]{system}, null, null, null);
        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            msgsJson = cursor.getString(cursor.getColumnIndex("json"));
            Log.i("ChatActivity","result: topic="  + topic +" json: " + msgsJson );
        }
        // 关闭游标，释放资源
        cursor.close();
        Messages msgs = new Messages();
        if (msgsJson!=null)
             msgs = moshi.adapter(Messages.class).fromJson(msgsJson);
        return msgs.data;
    }
}

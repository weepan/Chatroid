package com.jtong.chatroid;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ChatTopicAdapter extends RecyclerView.Adapter<ChatTopicAdapter.ChatTopicViewHolder> {

    private List<ChatTopic> topicList;
    private Context context;

    public ChatTopicAdapter(Context context,List<ChatTopic> chatTopics) {
        this.topicList = chatTopics;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatTopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_topic, parent, false);
        return new ChatTopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatTopicViewHolder holder, int position) {
        ChatTopic chatTopic = topicList.get(position);
        holder.nameTextView.setText(chatTopic.getTopic());
        // 设置头像等其他数据
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public void deleteTopicInDatabase(ChatTopic topic){

        DBHelper mdbHelper = new DBHelper(context, "setting.db", null, 2);
        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        //创建存放数据的ContentValues对象
        //删除
        db.delete("topics", "topic=?", new String[]{topic.getTopic()});

        //删除
        db.delete("messages", "topic=?", new String[]{topic.getTopic()});

    }

    public void deleteItem(int position) {
        // 执行删除逻辑
        // 数据库删除
        deleteTopicInDatabase(topicList.get(position));
        topicList.remove(position);
        // 更新 RecyclerView
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    class ChatTopicViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView nameTextView;

        ChatTopicViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);

            // 注册长按事件监听器
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 在此处理打开聊天页面
                        Intent intent = new Intent(context,
                                ChatActivity.class);
                        intent.putExtra("system",nameTextView.getText().toString());
                        context.startActivity(intent);
                    }
                }
            } );

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 显示删除菜单
                        showDeleteMenu(v, position);
                        return true;
                    }
                    return false;
                }
            });


        }
        private void showDeleteMenu(View view, final int position) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.delete_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.delete_item) {
                        // 执行删除操作
                        deleteItem(position);
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }



    }
}

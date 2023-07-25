package com.jtong.chatroid;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final List<Message> messageList;
    private Context context;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageTextView.setText(message.getContent());
        holder.timestampTextView.setText(formatTimestamp(message.getTimestamp()));

        // 根据消息发送者设置不同的样式
        if (message.isSentByUser()) {
            holder.messageTextView.setBackgroundResource(R.drawable.message_sent_background);
            holder.messageTextView.setTextColor(Color.WHITE);
        } else {
            holder.messageTextView.setBackgroundResource(R.drawable.message_received_background);
            holder.messageTextView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    private void copyText(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView timestampTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            // 注册长按事件监听器
            messageTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 在此处理拷贝逻辑
                        copyText(messageTextView.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}

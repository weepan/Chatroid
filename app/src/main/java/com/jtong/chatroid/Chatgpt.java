package com.jtong.chatroid;

import android.util.Log;

import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chatgpt {
    private final static String API_KEY = "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";
    private final static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    private OnResponseListener onResponseListener;


    public interface OnResponseListener {
        void OnResponse(String response);
    }

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().
            connectTimeout(1, TimeUnit.SECONDS).
            writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).
            callTimeout(69, TimeUnit.SECONDS).
            build();

    private final Moshi moshi = new Moshi.Builder().build();

    private static class ChatMessage{
        public  ChatMessage(String role, String content){
            this.role = role;
            this.content = content;
        }
        private final String role;
        private final String content;

    }

    private static class CompletionRequest {
        private final String model = "gpt-3.5-turbo";
        private final boolean stream = false;
        private ChatMessage[] messages;
        public void setMessages(ChatMessage[] messages) {
            this.messages = messages;
        }

    }

    private static class CompletionResponse {
        private String id;
        private String object;
        private int created;
        private String model;
        private Choice[] choices;
        private Usage usage;
    }

    private static class Choice {
        private int index;
        private ChatMessage message;
        private String finish_reason;
    }

    private static class Usage{
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

    }

    public void completion(List<Message> messages) {
        CompletionRequest completionRequest = new CompletionRequest();
        Message lastMsg = messages.get(messages.size()-1);
        ChatMessage[] msgs = new ChatMessage[]{new ChatMessage(lastMsg.isSentByUser()?"user":"assistant",lastMsg.getContent())};
        completionRequest.setMessages(msgs);
        String reqJson = moshi.adapter(CompletionRequest.class).toJson(completionRequest);
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "Bearer "+API_KEY;
        RequestBody body = RequestBody.create(reqJson,MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url)
                .header("Authorization", apiKey)
                .post(body)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    CompletionResponse resp= moshi.adapter(CompletionResponse.class).fromJson(responseBody);
                    onResponseListener.OnResponse(resp.choices[0].message.content);// 解析响应数据并处理 ChatGPT 的回复
                } else {
                    // 处理错误情况
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 处理网络请求失败
                Log.e("Chatgpt",e.toString());
            }
        });


    }
}

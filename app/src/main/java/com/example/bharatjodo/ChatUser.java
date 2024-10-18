package com.example.bharatjodo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatUser extends AppCompatActivity {

    SessionManagement sessionManagement;
    private EditText chatMessageInput;
    private Button backButton_chatpage;
    private ImageButton sendButton, micButton;
    private RecyclerView recyclerViewChat;
    private ChatMessageAdaptor chatMessageAdaptor;
    private TextView usernameTextView;
    private String senderId;
    private String receiverId;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private static final int REQUEST_MIC_PERMISSION = 200;
    private WebSocket webSocket;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        sessionManagement = new SessionManagement(this);

        senderId = sessionManagement.getUserId();
        receiverId = sessionManagement.getFriendId();

        chatMessageInput = findViewById(R.id.chat_message_input);
        backButton_chatpage = findViewById(R.id.back_button_chatpage);
        sendButton = findViewById(R.id.message_send_btn);
        micButton = findViewById(R.id.speech_button);
        recyclerViewChat = findViewById(R.id.recyclerView_chat);
        usernameTextView = findViewById(R.id.username_textview_chatpage);

        chatMessageAdaptor = new ChatMessageAdaptor(new ArrayList<>());
        recyclerViewChat.setAdapter(chatMessageAdaptor);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));

        retrieveReceiverUsername(receiverId);

        client = new OkHttpClient();

        backButton_chatpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        micButton.setOnClickListener(v -> {
            if (checkMicPermission()) {
                startSpeechToText();
            } else {
                requestMicPermission();
            }
        });

        sendButton.setOnClickListener(v -> {
            String message = chatMessageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                chatMessageInput.setText("");
            } else {
                Toast.makeText(ChatUser.this, "Enter a message", Toast.LENGTH_SHORT).show();
            }
        });

        connectWebSocket();
    }

    private boolean checkMicPermission() {
        return ContextCompat.checkSelfPermission(ChatUser.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMicPermission() {
        ActivityCompat.requestPermissions(ChatUser.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MIC_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MIC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechToText();
            } else {
                Toast.makeText(ChatUser.this, "Microphone permission is required to use speech recognition", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(ChatUser.this, "Speech recognition not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                chatMessageInput.setText(spokenText);
            }
        }
    }

    private void retrieveReceiverUsername(String receiverId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.getUsernameOfFriend_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int jsonStartIndex = response.indexOf("{");
                    if (jsonStartIndex != -1) {
                        response = response.substring(jsonStartIndex);
                    } else {
                        throw new JSONException("Invalid response, no JSON found");
                    }

                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("RetrievedUsernameResponse", "Retrieved Username: " + jsonObject.toString());

                    if (jsonObject.getString("status").equals("success")) {
                        String receiverUsername = jsonObject.getString("username");
                        Log.d("RetrievedUsernameResponse", "Retrieved Username of friend is: " + receiverUsername);
                        usernameTextView.setText(receiverUsername);
                    } else {
                        Toast.makeText(ChatUser.this, "Failed to retrieve friend details", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChatUser.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatUser.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", receiverId);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void connectWebSocket() {
        okhttp3.Request request = new okhttp3.Request.Builder().url(ApiEndpoints.websocketServer_url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocket", "Connection opened");
                Log.i("WebSocket", "Successfully connected to WebSocket server at: " + ApiEndpoints.websocketServer_url);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        String incomingMessage = jsonObject.getString("message_content");
                        addMessageToChat(incomingMessage, false); // Received message
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // Handle binary messages if needed
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable okhttp3.Response response) {
                Log.e("WebSocket", "Error: " + t.getMessage());
            }
        });
    }

    private void sendMessage(final String messageContent) {
        if (webSocket != null) {
            try {
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("sender_id", senderId);
                jsonMessage.put("receiver_id", receiverId);
                jsonMessage.put("message_content", messageContent);

                // Send message
                webSocket.send(jsonMessage.toString());
                addMessageToChat(messageContent, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("WebSocket", "WebSocket not initialized.");
            Toast.makeText(this, "Unable to send message. WebSocket not initialized.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMessageToChat(String messageContent, boolean isSentByMe) {
        chatMessageAdaptor.addMessage(new ChatMessageModel(messageContent, isSentByMe));
        recyclerViewChat.scrollToPosition(chatMessageAdaptor.getItemCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Closing connection");
        }
    }
}

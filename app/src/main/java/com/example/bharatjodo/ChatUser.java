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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                    // Check if the response contains a JSON object
                    int jsonStartIndex = response.indexOf("{");
                    if (jsonStartIndex != -1) {
                        response = response.substring(jsonStartIndex);  // Remove any content before the JSON
                    } else {
                        throw new JSONException("Invalid response, no JSON found");
                    }

                    // Parse the actual JSON response
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



    private void sendMessage(final String messageContent) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.sendMessage_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Check if the response contains a JSON object
                    int jsonStartIndex = response.indexOf("{");
                    if (jsonStartIndex != -1) {
                        response = response.substring(jsonStartIndex);  // Remove any content before the JSON
                    } else {
                        throw new JSONException("Invalid response, no JSON found");
                    }

                    // Parse the actual JSON response
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        addMessageToChat(messageContent, true);
                    } else {
                        Toast.makeText(ChatUser.this, "Failed to send message", Toast.LENGTH_SHORT).show();
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
                params.put("sender_id", senderId);
                params.put("receiver_id", receiverId);
                params.put("message_content", messageContent);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }



    private void addMessageToChat(String message, boolean isSender) {
        ChatMessageModel chatMessage = new ChatMessageModel(message, isSender);
        chatMessageAdaptor.addMessage(chatMessage);
        recyclerViewChat.scrollToPosition(chatMessageAdaptor.getItemCount() - 1);
    }
}

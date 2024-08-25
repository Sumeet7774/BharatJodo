package com.example.bharatjodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingFriendRequestPage extends AppCompatActivity {

    private SessionManagement sessionManagement;
    private RecyclerView recyclerView;
    private PendingFriendRequestAdapter adapter;
    private List<PendingFriendRequestModel> pendingFriendRequestList;
    private ProgressBar progressBar;
    private TextView noPendingRequestsTextView;
    private Button backButton_pendingfriendrequestpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friend_request_page);

        sessionManagement = new SessionManagement(this);

        recyclerView = findViewById(R.id.pending_friend_request_recycler_view);
        progressBar = findViewById(R.id.pendingfriendrequest_progress_bar);
        backButton_pendingfriendrequestpage = findViewById(R.id.back_button_pendingfriendrequestpage);
        noPendingRequestsTextView = findViewById(R.id.nopendingrequestsfound_textview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingFriendRequestList = new ArrayList<>();
        adapter = new PendingFriendRequestAdapter(pendingFriendRequestList);
        recyclerView.setAdapter(adapter);

        loadPendingFriendRequests();

        backButton_pendingfriendrequestpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Navigate back to the previous fragment/activity
            }
        });
    }

    private void loadPendingFriendRequests() {
        progressBar.setVisibility(View.VISIBLE);

        String userId = sessionManagement.getUserId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrievePendingFriendRequests_url,
                response -> {
                    progressBar.setVisibility(View.GONE);

                    Log.d("PendingFriendRequestResponse", response);

                    try
                    {
                        int startIndex = response.indexOf("[");
                        int endIndex = response.lastIndexOf("]") + 1;

                        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex)
                        {
                            String jsonResponse = response.substring(startIndex, endIndex);
                            Log.d("PendingFriendRequest", "Cleaned Response: " + jsonResponse);

                            JSONArray jsonArray = new JSONArray(jsonResponse);

                            if (jsonArray.length() > 0)
                            {
                                pendingFriendRequestList.clear();

                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String username = jsonObject.getString("username");
                                    String phoneNumber = jsonObject.getString("phone_number");

                                    pendingFriendRequestList.add(new PendingFriendRequestModel(username, phoneNumber));
                                }
                                adapter.notifyDataSetChanged();
                                noPendingRequestsTextView.setVisibility(View.GONE);
                            }
                            else
                            {
                                noPendingRequestsTextView.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                        {
                            noPendingRequestsTextView.setVisibility(View.VISIBLE);
                            Log.e("PendingFriendRequest", "Invalid response format");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        noPendingRequestsTextView.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    noPendingRequestsTextView.setVisibility(View.VISIBLE);
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
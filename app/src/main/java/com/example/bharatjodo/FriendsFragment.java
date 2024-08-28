package com.example.bharatjodo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment {

    private SessionManagement sessionManagement;
    private RecyclerView friendsRecyclerview;
    private TextView no_friendsFound_textview;
    private ProgressBar progressBar_friends;
    private FriendsAdapter friendsAdapter;
    private List<FriendsModel> friendList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        sessionManagement = new SessionManagement(getContext());
        no_friendsFound_textview = view.findViewById(R.id.nofriendsfound_textview);
        progressBar_friends = view.findViewById(R.id.allfriends_progress_bar);
        friendsRecyclerview = view.findViewById(R.id.friends_recycler_view);

        friendList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(friendList);
        friendsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerview.setAdapter(friendsAdapter);

        retrieveFriends();

        return view;
    }

    private void retrieveFriends() {
        progressBar_friends.setVisibility(View.VISIBLE);

        String user_id = sessionManagement.getUserId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrieveFriends_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar_friends.setVisibility(View.GONE);

                        Log.d("Retrieved Friends", response);

                        try
                        {
                            int startIndex = response.indexOf("{");
                            int endIndex = response.lastIndexOf("}") + 1;

                            String jsonResponse = response.substring(startIndex, endIndex);

                            Log.d("FriendsFragment", "Cleaned Response: " + jsonResponse);

                            JSONObject jsonObject = new JSONObject(jsonResponse);

                            if (jsonObject.has("message"))
                            {
                                no_friendsFound_textview.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                no_friendsFound_textview.setVisibility(View.GONE);
                                friendList.clear();

                                JSONArray jsonArray = jsonObject.getJSONArray("friends");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject friendObject = jsonArray.getJSONObject(i);
                                    String friendshipId = friendObject.getString("friendship_id");
                                    String username = friendObject.getString("username");
                                    String phoneNumber = friendObject.getString("phone_number");

                                    friendList.add(new FriendsModel(friendshipId,username, phoneNumber));
                                }

                                friendsAdapter.notifyDataSetChanged();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e("FriendsFragment", "JSON Parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar_friends.setVisibility(View.GONE);
                        Log.e("RetrieveFriends", "Volley error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}
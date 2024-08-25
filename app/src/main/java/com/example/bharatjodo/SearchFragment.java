package com.example.bharatjodo;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SearchFragment extends Fragment {

    private EditText searchUsernameEditText;
    private ImageButton searchUserButton;
    private RecyclerView searchUserRecyclerView;
    private TextView noUsersFoundTextView;
    private ProgressBar searchprogressBar;
    private UserAdaptor adapter;
    private ArrayList<UserModel> userList;
    private SessionManagement sessionManagement;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchUsernameEditText = view.findViewById(R.id.search_username_edittext);
        searchUserButton = view.findViewById(R.id.search_user_button);
        searchUserRecyclerView = view.findViewById(R.id.search_user_recycler_view);
        noUsersFoundTextView = view.findViewById(R.id.norusersfoundinsearch_textview);
        searchprogressBar = view.findViewById(R.id.search_progress_bar);

        // Input filter for only lowercase letters and no spaces
        InputFilter noSpacesAndLowercaseFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null) {
                    if (source.toString().matches(".*[A-Z].*")) {
                        showToast("Only lowercase letters are allowed.");
                        return "";
                    }
                    if (source.toString().contains(" ")) {
                        showToast("Spaces are not allowed.");
                        return source.toString().replace(" ", "");
                    }
                }
                return null;
            }
        };
        searchUsernameEditText.setFilters(new InputFilter[]{noSpacesAndLowercaseFilter, new InputFilter.LengthFilter(12)});

        userList = new ArrayList<>();
        adapter = new UserAdaptor(getContext(), userList, this::onUserClick);
        searchUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchUserRecyclerView.setAdapter(adapter);

        sessionManagement = new SessionManagement(getContext());
        userId = sessionManagement.getUserId();

        searchUserButton.setOnClickListener(v -> {
            String query = searchUsernameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(query)) {
                Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
            } else if (!isValidUsername(query)) {
                showToast("Username must contain only letters.");
            } else {
                String username = sessionManagement.getUsername();
                if (query.equals(username)) {
                    showToast("Can't search for your own username");
                } else {
                    searchUser(query);
                }
            }
        });

        return view;
    }

    private void showToast(String message) {
        MotionToast.Companion.createColorToast(getActivity(),
                "Error", message,
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(getContext(), R.font.montserrat_semibold));
    }

    private void searchUser(String query) {
        searchprogressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.search_user_url,
                response -> {
                    searchprogressBar.setVisibility(View.GONE);

                    Log.d("SearchResponse", response);
                    try {
                        int startIndex = response.indexOf("{");
                        int endIndex = response.lastIndexOf("}") + 1;
                        String jsonResponse = response.substring(startIndex, endIndex);
                        Log.d("SearchUser", "Cleaned Response: " + jsonResponse);
                        JSONObject jsonObject = new JSONObject(jsonResponse);

                        if (jsonObject.getString("status").equals("found")) {
                            userList.clear();
                            String receiverUserId = jsonObject.getString("user_id");
                            String username = jsonObject.getString("username");
                            String phoneNumber = jsonObject.getString("phone_number");

                            checkFriendshipStatus(receiverUserId, username, phoneNumber);
                        } else {
                            noUsersFoundTextView.setVisibility(View.VISIBLE);
                            noUsersFoundTextView.setText("No user found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    searchprogressBar.setVisibility(View.GONE);
                    Log.e("SearchUser", "Error: " + error.toString());
                    Toast.makeText(getContext(), "Error fetching users", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", query);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void checkFriendshipStatus(String receiverUserId, String username, String phoneNumber) {
        searchprogressBar.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.checkfriendhip_url,
                response -> {
                    searchprogressBar.setVisibility(View.GONE);

                    Log.d("FriendshipStatus", response);

                    try {
                        int startIndex = response.indexOf("{");

                        if (startIndex != -1)
                        {
                            String jsonResponse = response.substring(startIndex);
                            JSONObject jsonObject = new JSONObject(jsonResponse);

                            String status = jsonObject.getString("status");
                            userList.add(new UserModel(receiverUserId, username, phoneNumber, status));
                            adapter.notifyDataSetChanged();
                            noUsersFoundTextView.setVisibility(userList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    searchprogressBar.setVisibility(View.GONE);

                    Log.e("CheckFriendshipStatus", "Error: " + error.toString());
                    Toast.makeText(getContext(), "Error checking friendship status", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_user_id", userId);
                params.put("receiver_user_id", receiverUserId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void onUserClick(UserModel userModel) {
        Toast.makeText(getContext(), "Clicked on: " + userModel.getUsername(), Toast.LENGTH_SHORT).show();
    }

    private boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z]+");
    }
}

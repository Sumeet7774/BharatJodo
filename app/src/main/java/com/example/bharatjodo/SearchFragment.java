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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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


        InputFilter noSpacesandlowercaseFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null) {
                    if (source.toString().matches(".*[A-Z].*"))
                    {
                        MotionToast.Companion.createColorToast(getActivity(),
                                "Error", "Only lowercase letters are allowed.",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(getContext(), R.font.montserrat_semibold));
                        return "";
                    }
                    if (source.toString().contains(" "))
                    {
                        MotionToast.Companion.createColorToast(getActivity(),
                                "Error", "Spaces are not allowed.",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(getContext(), R.font.montserrat_semibold));
                        return source.toString().replace(" ", "");
                    }
                }
                return null;
            }
        };

        searchUsernameEditText.setFilters(new InputFilter[]{noSpacesandlowercaseFilter, new InputFilter.LengthFilter(12)});

        userList = new ArrayList<>();
        adapter = new UserAdaptor(getContext(), userList, this::onUserClick);
        searchUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchUserRecyclerView.setAdapter(adapter);

        sessionManagement = new SessionManagement(getContext());
        userId = sessionManagement.getUserId();

        searchUserButton.setOnClickListener(v -> {
            String query = searchUsernameEditText.getText().toString().trim();

            if (TextUtils.isEmpty(query))
            {
                Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
            }
            else if (!isValidUsername(query))
            {
                MotionToast.Companion.createColorToast(getActivity(),
                        "Error", "Username must contain only letters.",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getContext(), R.font.montserrat_semibold));
            }
            else
            {
                String username = sessionManagement.getUsername();

                if (query.equals(username))
                {
                    MotionToast.Companion.createColorToast(getActivity(),
                            "Error", "Can't search for your own username",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(getContext(), R.font.montserrat_semibold));
                }
                else
                {
                    searchUser(query);
                }
            }
        });

        return view;
    }

    private void searchUser(String query) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.search_user_url,
                response -> {
                    Log.d("SearchResponse", response);

                    try {
                        int startIndex = response.indexOf("{");
                        int endIndex = response.lastIndexOf("}") + 1;
                        String jsonResponse = response.substring(startIndex, endIndex);

                        Log.d("SearchUser", "Cleaned Response: " + jsonResponse);

                        JSONObject jsonObject = new JSONObject(jsonResponse);

                        if (jsonObject.getString("status").equals("found")) {
                            userList.clear();

                            String userId = jsonObject.getString("user_id");
                            String username = jsonObject.getString("username");
                            String phoneNumber = jsonObject.getString("phone_number");

                            userList.add(new UserModel(userId, username, phoneNumber));
                            adapter.notifyDataSetChanged();

                            if (userList.isEmpty())
                            {
                                noUsersFoundTextView.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                noUsersFoundTextView.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            noUsersFoundTextView.setVisibility(View.VISIBLE);
                            noUsersFoundTextView.setText("No user found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
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


    private void onUserClick(UserModel userModel) {
        // Handle user click event here (view details, edit, delete, etc.)
        Toast.makeText(getContext(), "Clicked on: " + userModel.getUsername(), Toast.LENGTH_SHORT).show();
    }

    private boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z]+");
    }
}

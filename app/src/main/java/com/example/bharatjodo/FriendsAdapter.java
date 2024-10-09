package com.example.bharatjodo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private SessionManagement sessionManagement;
    private List<FriendsModel> friendList;
    private Context context;

    public FriendsAdapter(List<FriendsModel> friendList, Context context) {
        this.friendList = friendList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_friends_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendsModel friend = friendList.get(position);
        holder.usernameTextView.setText(friend.get_username());
        holder.phoneNumberTextView.setText(friend.get_phonenumber());

        sessionManagement = new SessionManagement(context);

        holder.chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = friend.get_username();
                retrieveFriendId(username, new FriendIdCallback() {
                    @Override
                    public void onFriendIdReceived(String friendUserId) {
                        checkFriendshipStatus(friendUserId, new FriendshipStatusCallback() {
                            @Override
                            public void onStatusReceived(String status) {
                                handleChatClickResponse(status, friendUserId);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView phoneNumberTextView;
        ImageButton chat_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.friendcardviewUsername_textview);
            phoneNumberTextView = itemView.findViewById(R.id.friendcardviewPhoneNumber_textview);
            chat_button = itemView.findViewById(R.id.cardview_chat_button);
        }
    }

    private void retrieveFriendId(final String username, final FriendIdCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.getFriendId_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("retrieveFriendId", "Response: " + response);

                        try {
                            int startIndex = response.indexOf("{");
                            int endIndex = response.lastIndexOf("}") + 1;

                            if (startIndex != -1 && endIndex != -1) {
                                String jsonResponse = response.substring(startIndex, endIndex);
                                Log.d("retrieveFriendId", "Extracted JSON: " + jsonResponse);

                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                String status = jsonObject.getString("status");

                                if ("success".equals(status)) {
                                    String friendUserId = jsonObject.getString("user_id");
                                    callback.onFriendIdReceived(friendUserId);
                                } else {
                                    String message = jsonObject.getString("message");
                                    Log.d("retrieveFriendId", "Failed: " + message);
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("retrieveFriendId", "Failed to extract JSON from response: " + response);
                                Toast.makeText(context, "Invalid response from server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("retrieveFriendId", "JSON Parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error retrieving friend ID", Toast.LENGTH_SHORT).show();
                        Log.d("retrieveFriendId", "Volley error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void checkFriendshipStatus(String friendUserId, FriendshipStatusCallback callback) {
        SessionManagement sessionManagement = new SessionManagement(context.getApplicationContext());
        String senderUserId = sessionManagement.getUserId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.checkFriendshipForChat_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            int startIndex = response.indexOf("{");
                            int endIndex = response.lastIndexOf("}") + 1;

                            if (startIndex != -1 && endIndex != -1) {
                                String jsonResponse = response.substring(startIndex, endIndex);
                                Log.d("checkFriendshipStatus", "Extracted JSON: " + jsonResponse);

                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                String status = jsonObject.getString("status");
                                callback.onStatusReceived(status);
                            } else {
                                Log.e("checkFriendshipStatus", "Failed to extract JSON from response: " + response);
                                Toast.makeText(context, "Invalid response from server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("checkFriendshipStatus", "JSON Parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("checkFriendshipStatus", "Error: " + error.getMessage());
                        Toast.makeText(context, "Error checking friendship status", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_user_id", senderUserId);
                params.put("receiver_user_id", friendUserId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void handleChatClickResponse(String status, String friendUserId) {
        if ("accepted".equals(status)) {
            MotionToast.Companion.createColorToast((Activity) context,
                    "Success", "You can chat now",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(context, R.font.montserrat_semibold));

            Intent intent = new Intent(context, ChatUser.class);
            context.startActivity(intent);
            sessionManagement.setFriendId(friendUserId);
            Log.d("SessionFriendId", "Session Friend Id: " + sessionManagement.getFriendId());
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if ("pending".equals(status)) {
            MotionToast.Companion.createColorToast((Activity) context,
                    "Friend request pending", "The friend request is still pending, either from your side or the other user's.",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(context, R.font.montserrat_semibold));
        } else {
            MotionToast.Companion.createColorToast((Activity) context,
                    "Friend request rejected", "The friend request was rejected, either by you or him.",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(context, R.font.montserrat_semibold));
        }
    }

    public interface FriendshipStatusCallback {
        void onStatusReceived(String status);
    }

    public interface FriendIdCallback {
        void onFriendIdReceived(String friendUserId);
    }
}

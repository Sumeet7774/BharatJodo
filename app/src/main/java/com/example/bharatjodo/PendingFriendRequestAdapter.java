package com.example.bharatjodo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class PendingFriendRequestAdapter extends RecyclerView.Adapter<PendingFriendRequestAdapter.ViewHolder> {
    private List<PendingFriendRequestModel> pendingFriendRequestList;
    private OnDataChangedListener onDataChangedListener;

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    public PendingFriendRequestAdapter(List<PendingFriendRequestModel> pendingFriendRequestList, OnDataChangedListener onDataChangedListener) {
        this.pendingFriendRequestList = pendingFriendRequestList;
        this.onDataChangedListener = onDataChangedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pending_friendrequest_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingFriendRequestModel pendingFriendRequest = pendingFriendRequestList.get(position);
        holder.usernameTextView.setText(pendingFriendRequest.getUsernameofFriend());
        holder.phoneNumberTextView.setText(pendingFriendRequest.getPhoneNumberofFriend());

        holder.acceptButton.setOnClickListener(v -> updateFriendshipStatus(holder, pendingFriendRequest, "accepted", position));
        holder.rejectButton.setOnClickListener(v -> updateFriendshipStatus(holder, pendingFriendRequest, "rejected", position));
    }

    @Override
    public int getItemCount() {
        return pendingFriendRequestList.size();
    }

    private void updateFriendshipStatus(ViewHolder holder, PendingFriendRequestModel pendingFriendRequest, String status, int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.updatefriendship_url,
                response -> {
                    try {
                        Log.d("FriendshipStatus", response);

                        int jsonStartIndex = response.indexOf("{");
                        if (jsonStartIndex != -1) {
                            String jsonResponseString = response.substring(jsonStartIndex);
                            Log.d("CleanedFriendshipStatus", jsonResponseString);

                            JSONObject jsonResponse = new JSONObject(jsonResponseString);
                            String serverStatus = jsonResponse.getString("status");

                            if ("success".equals(serverStatus)) {
                                if (position >= 0 && position < pendingFriendRequestList.size()) {
                                    pendingFriendRequestList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, pendingFriendRequestList.size());

                                    if (pendingFriendRequestList.isEmpty() && onDataChangedListener != null) {
                                        onDataChangedListener.onDataChanged();
                                    }
                                }

                                if ("accepted".equals(status)) {
                                    showMotionToast(holder.itemView.getContext(), "Friend Request Accepted", "You added that user as friend", MotionToastStyle.SUCCESS);
                                } else if ("rejected".equals(status)) {
                                    showMotionToast(holder.itemView.getContext(), "Friend Request Rejected", "You rejected the friend request", MotionToastStyle.ERROR);
                                }
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Failed to update friendship status", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(holder.itemView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(holder.itemView.getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("friendship_id", String.valueOf(pendingFriendRequest.getFriendshipId()));
                params.put("status", status);
                return params;
            }
        };

        VolleySingleton.getInstance(holder.itemView.getContext()).addToRequestQueue(stringRequest);
    }

    private void showMotionToast(Context context, String title, String message, MotionToastStyle style) {
        MotionToast.Companion.createColorToast((Activity) context,
                title, message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(context, R.font.montserrat_semibold));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, phoneNumberTextView;
        Button acceptButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.pendingfriendrequest_cardviewUsername_textview);
            phoneNumberTextView = itemView.findViewById(R.id.pendingfriendrequest_cardviewPhoneNumber_textview);
            acceptButton = itemView.findViewById(R.id.pendingfriendrequest_accept_button);
            rejectButton = itemView.findViewById(R.id.pendingfriendrequest_reject_button);
        }
    }
}

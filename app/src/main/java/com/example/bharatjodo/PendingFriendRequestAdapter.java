package com.example.bharatjodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PendingFriendRequestAdapter extends RecyclerView.Adapter<PendingFriendRequestAdapter.ViewHolder> {
    private List<PendingFriendRequestModel> pendingFriendRequestList;

    public PendingFriendRequestAdapter(List<PendingFriendRequestModel> pendingFriendRequestList) {
        this.pendingFriendRequestList = pendingFriendRequestList;
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

        // Handle accept button click
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle accept friend request logic
            }
        });

        // Handle reject button click
        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle reject friend request logic
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingFriendRequestList.size();
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

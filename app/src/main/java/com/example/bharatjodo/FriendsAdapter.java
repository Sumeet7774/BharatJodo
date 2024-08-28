package com.example.bharatjodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>
{
    private List<FriendsModel> friendList;

    public FriendsAdapter(List<FriendsModel> friendList) {
        this.friendList = friendList;
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
}
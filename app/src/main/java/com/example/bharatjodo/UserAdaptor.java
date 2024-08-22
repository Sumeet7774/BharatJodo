package com.example.bharatjodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.UserViewHolder> {

    private Context context;
    private ArrayList<UserModel> userList;
    private OnAddFriendClickListener addFriendClickListener;

    public UserAdaptor(Context context, ArrayList<UserModel> userList, OnAddFriendClickListener addFriendClickListener) {
        this.context = context;
        this.userList = userList;
        this.addFriendClickListener = addFriendClickListener;
    }

    public interface OnAddFriendClickListener {
        void onAddFriendClick(UserModel userModel);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_add_friend_cardview, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel userModel = userList.get(position);

        holder.usernameTextView.setText(userModel.getUsername());
        holder.phoneNumberTextView.setText(userModel.getPhoneNumber());

        holder.addFriendButton.setOnClickListener(v -> addFriendClickListener.onAddFriendClick(userModel));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        TextView phoneNumberTextView;
        Button addFriendButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.addfriend_cardviewUsername_textview);
            phoneNumberTextView = itemView.findViewById(R.id.addfriend_cardviewPhoneNumber_textview);
            addFriendButton = itemView.findViewById(R.id.addfriend_button);
        }
    }
}

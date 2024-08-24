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
    private OnUserClickListener onUserClickListener;

    public UserAdaptor(Context context, ArrayList<UserModel> userList, OnUserClickListener onUserClickListener) {
        this.context = context;
        this.userList = userList;
        this.onUserClickListener = onUserClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        String status = userList.get(position).getFriendshipStatus();
        if ("pending".equals(status)) {
            return R.layout.custom_friendrequest_sent_cardview;
        } else if ("accepted".equals(status)) {
            return R.layout.custom_acceptedfriend_cardview;
        } else {
            return R.layout.custom_add_friend_cardview;
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel userModel = userList.get(position);
        holder.usernameTextView.setText(userModel.getUsername());
        holder.phoneNumberTextView.setText(userModel.getPhoneNumber());

        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(userModel));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserClickListener {
        void onUserClick(UserModel userModel);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView phoneNumberTextView;
        Button actionButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.cardviewUsername_textview);
            phoneNumberTextView = itemView.findViewById(R.id.cardviewPhoneNumber_textview);
            actionButton = itemView.findViewById(R.id.cardview_action_button);
        }
    }
}

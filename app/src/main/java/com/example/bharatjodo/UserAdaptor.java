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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.UserViewHolder> {

    private Context context;
    private ArrayList<UserModel> userList;
    private OnUserClickListener onUserClickListener;
    SessionManagement sessionManagement;

    public UserAdaptor(Context context, ArrayList<UserModel> userList, OnUserClickListener onUserClickListener) {
        this.context = context;
        this.userList = userList;
        this.onUserClickListener = onUserClickListener;
        this.sessionManagement = new SessionManagement(context);
    }

    @Override
    public int getItemViewType(int position) {
        String status = userList.get(position).getFriendshipStatus();

        if ("pending".equals(status))
        {
            return R.layout.custom_friendrequest_sent_cardview;
        }
        else if ("accepted".equals(status))
        {
            return R.layout.custom_acceptedfriend_cardview;
        }
        else
        {
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

        if ("pending".equals(userModel.getFriendshipStatus()))
        {
            holder.actionButton.setText("Sent");
            holder.actionButton.setEnabled(false);
        }
        else if ("accepted".equals(userModel.getFriendshipStatus()))
        {
            holder.actionButton.setText("Friends");
            holder.actionButton.setEnabled(false);
        }
        else
        {
            holder.actionButton.setText("Add Friend");
            holder.actionButton.setOnClickListener(v -> sendFriendRequest(userModel, holder));
        }

        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(userModel));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void sendFriendRequest(UserModel userModel, UserViewHolder holder) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.sendfriendrequest_url,
                response -> {
                    Log.d("FriendRequestResponse", response);

                    try
                    {
                        int startIndex = response.indexOf("{");
                        int endIndex = response.lastIndexOf("}") + 1;

                        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex)
                        {
                            String jsonResponse = response.substring(startIndex, endIndex);
                            Log.d("FriendRequest", "Cleaned Response: " + jsonResponse);

                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if("successfull".equals(status) && "Friend request sent".equals(message))
                            {
                                MotionToast.Companion.createColorToast((Activity) context,
                                        "Sent Successfully", "Friend request sent successfully.",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(context, R.font.montserrat_semibold));
                                userModel.setFriendshipStatus("pending");
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                            else if("unsuccessfull".equals(status) && "Friend request already exists".equals(message))
                            {
                                MotionToast.Companion.createColorToast((Activity) context,
                                        "Already Sent", "Friend request already sent ",
                                        MotionToastStyle.INFO,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(context, R.font.montserrat_semibold));
                                userModel.setFriendshipStatus("pending");
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                            else
                            {
                                MotionToast.Companion.createColorToast((Activity) context,
                                        "Error", "Failed to send friend request.",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(context, R.font.montserrat_semibold));
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Invalid response format", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(context, "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_user_id", sessionManagement.getUserId());
                params.put("receiver_user_id", userModel.getUserId());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
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

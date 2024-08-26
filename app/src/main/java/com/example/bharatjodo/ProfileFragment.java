package com.example.bharatjodo;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {
    SessionManagement sessionManagement;
    TextView username_profile_textview;
    CardView update_profile_cardview, pending_friend_request_cardview, about_us_cardview, contact_us_cardview;
    Button deleteuserButton, logoutButton;
    ImageButton updateprofileCardview_opener_button, pendingfriendrequestCardview_opener_button, aboutusCardview_opener_button, contactusCardview_opener_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManagement = new SessionManagement(getContext());

        username_profile_textview = view.findViewById(R.id.username_textview_profile);
        update_profile_cardview = view.findViewById(R.id.cardUpdateProfile);
        pending_friend_request_cardview = view.findViewById(R.id.cardPendingFriendRequests);
        about_us_cardview = view.findViewById(R.id.cardAboutUs);
        contact_us_cardview = view.findViewById(R.id.cardContactUs);
        deleteuserButton = view.findViewById(R.id.delete_user_button);
        logoutButton = view.findViewById(R.id.logout_button);
        updateprofileCardview_opener_button = view.findViewById(R.id.updateprofile_rightbutton);
        pendingfriendrequestCardview_opener_button = view.findViewById(R.id.friend_requests_rightbutton);
        aboutusCardview_opener_button = view.findViewById(R.id.aboutus_rightbutton);
        contactusCardview_opener_button = view.findViewById(R.id.contactus_rightbutton);

        String profileUsername = sessionManagement.getUsername();
        username_profile_textview.setText(profileUsername);

        updateprofileCardview_opener_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        pendingfriendrequestCardview_opener_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), PendingFriendRequestPage.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        aboutusCardview_opener_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        contactusCardview_opener_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        deleteuserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}
package com.example.bharatjodo;

public class ApiEndpoints {
    private static final String base_url = "https://c0c4-2405-201-201f-609b-6199-2913-4f97-219.ngrok-free.app/BharatJodoApi/";
    public static final String register_url = base_url + "register.php";
    public static final String login_url = base_url + "login.php";
    public static final String getUserid_url = base_url + "get_user_id.php";
    public static final String getUsername_url = base_url + "get_username.php";
    public static final String getEmailId_url = base_url + "get_email_id.php";
    public static final String search_user_url = base_url + "search_user.php";
    public static final String checkfriendhip_url = base_url + "checking_friendship.php";
    public static final String sendfriendrequest_url = base_url + "add_friend.php";
    public static final String retrievePendingFriendRequests_url = base_url + "retrieve_pending_friendrequest.php";
    public static final String updatefriendship_url = base_url + "update_friendship.php";
    public static final String retrieveFriends_url = base_url + "retrieve_friends.php";
    public static final String checkFriendshipForChat_url = base_url + "check_friendship_for_chat.php";
    public static final String getFriendId_url = base_url + "get_friend_id.php";
    public static final String getUsernameOfFriend_url = base_url + "get_username_of_friend.php";
    public static final String sendMessage_url = base_url + "send_message.php";
}

package com.example.bharatjodo;

public class FriendshipModel {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_ACCEPTED = "Accepted";
    public static final String STATUS_REJECTED = "Rejected";

    private String friendshipId;
    private String userId;
    private String friendId;
    private String status;

    public FriendshipModel(String friendshipId, String userId, String friendId, String status) {
        this.friendshipId = friendshipId;
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }

    public String getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(String friendshipId) {
        this.friendshipId = friendshipId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status.equals(STATUS_PENDING) || status.equals(STATUS_ACCEPTED) || status.equals(STATUS_REJECTED)) {
            this.status = status;
        }
        else {
            throw new IllegalArgumentException("Invalid status value");
        }
    }
}
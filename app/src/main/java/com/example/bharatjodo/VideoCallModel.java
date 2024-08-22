package com.example.bharatjodo;

public class VideoCallModel {
    public static final String CALL_TYPE_ONE_TO_ONE = "one-to-one";
    public static final String CALL_TYPE_GROUP = "group";

    private String callId;
    private String callerId;
    private String callType;
    private String createdAt;
    private String endedAt;

    public VideoCallModel(String callId, String callerId, String callType, String createdAt, String endedAt) {
        this.callId = callId;
        this.callerId = callerId;
        setCallType(callType);
        this.createdAt = createdAt;
        this.endedAt = endedAt;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        if (callType.equals(CALL_TYPE_ONE_TO_ONE) || callType.equals(CALL_TYPE_GROUP)) {
            this.callType = callType;
        }
        else {
            throw new IllegalArgumentException("Invalid call type value");
        }
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(String endedAt) {
        this.endedAt = endedAt;
    }
}
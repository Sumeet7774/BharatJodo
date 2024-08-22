package com.example.bharatjodo;

public class CallParticipantsModel {
    private String participantId;
    private String callId;
    private String userId;
    private String joinedAt;
    private String leftAt;

    public CallParticipantsModel(String participantId, String callId, String userId, String joinedAt, String leftAt) {
        this.participantId = participantId;
        this.callId = callId;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(String leftAt) {
        this.leftAt = leftAt;
    }
}

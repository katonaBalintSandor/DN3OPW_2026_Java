package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Topic;

public class TopicDetailResponse {
    private boolean success;
    private String message;
    private Topic topic;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Topic getTopic() {
        return topic;
    }
}
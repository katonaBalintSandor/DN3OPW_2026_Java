package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.AdminCommunityTopic;
import java.util.List;

public class AdminCommunityTopicsResponse {
    private boolean success;
    private String message;
    private List<AdminCommunityTopic> topics;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<AdminCommunityTopic> getTopics() {
        return topics;
    }
}
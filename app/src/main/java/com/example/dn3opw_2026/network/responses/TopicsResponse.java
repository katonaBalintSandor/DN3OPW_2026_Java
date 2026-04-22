package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Topic;
import java.util.List;

public class TopicsResponse {
    private boolean success;
    private String message;
    private List<Topic> topics;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Topic> getTopics() {
        return topics;
    }


}
package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Comment;
import com.example.dn3opw_2026.model.Topic;
import java.util.List;

public class AdminTopicDetailsResponse {

    private boolean success;
    private String message;
    private Topic topic;
    private List<Comment> comments;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Topic getTopic() {
        return topic;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
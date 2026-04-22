package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Comment;
import java.util.List;

public class CommentsResponse {
    private boolean success;
    private String message;
    private List<Comment> comments;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
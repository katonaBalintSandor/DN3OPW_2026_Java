package com.example.dn3opw_2026.repository;

import com.example.dn3opw_2026.network.ApiClient;
import com.example.dn3opw_2026.network.ApiService;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.CommentsResponse;
import retrofit2.Call;

public class CommentRepository {

    private final ApiService apiService;

    public CommentRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<CommentsResponse> getCommentsForTopic(int topicId) {
        return apiService.getCommentsForTopic(topicId);
    }

    public Call<BaseResponse> addComment(int topicId, int userId, String comment) {
        return apiService.addComment(topicId, userId, comment);
    }

    public Call<BaseResponse> deleteComment(int commentId, int userId) {
        return apiService.deleteComment(commentId, userId);
    }
}
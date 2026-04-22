package com.example.dn3opw_2026.repository;

import com.example.dn3opw_2026.network.ApiClient;
import com.example.dn3opw_2026.network.ApiService;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.TopicDetailResponse;
import com.example.dn3opw_2026.network.responses.TopicsResponse;
import retrofit2.Call;

public class TopicRepository {

    private final ApiService apiService;

    public TopicRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<TopicsResponse> getAllTopics() {
        return apiService.getAllTopics();
    }

    public Call<TopicDetailResponse> getTopicById(int topicId) {
        return apiService.getTopicById(topicId);
    }

    public Call<BaseResponse> deleteTopic(int topicId) {
        return apiService.deleteTopic(topicId);
    }

    public Call<BaseResponse> addTopic(String topic, String description, int rating, int bookId, int userId) {
        return apiService.addTopic(topic, description, rating, bookId, userId);
    }
}
package com.example.dn3opw_2026.repository;

import com.example.dn3opw_2026.network.ApiClient;
import com.example.dn3opw_2026.network.ApiService;
import com.example.dn3opw_2026.network.responses.RecommendationsResponse;
import retrofit2.Call;

public class RecommendationRepository {

    private final ApiService apiService;

    public RecommendationRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<RecommendationsResponse> getRecommendations(int userId, int limit) {
        return apiService.getRecommendations(userId, limit);
    }
}
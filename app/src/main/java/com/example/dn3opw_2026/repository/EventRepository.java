package com.example.dn3opw_2026.repository;

import com.example.dn3opw_2026.network.ApiClient;
import com.example.dn3opw_2026.network.ApiService;
import com.example.dn3opw_2026.network.responses.EventsResponse;
import retrofit2.Call;

public class EventRepository {

    private final ApiService apiService;

    public EventRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<EventsResponse> getAllEvents() {
        return apiService.getAllEvents();
    }
}
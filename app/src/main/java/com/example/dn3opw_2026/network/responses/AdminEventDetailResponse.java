package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Event;

public class AdminEventDetailResponse {
    private boolean success;
    private String message;
    private Event event;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Event getEvent() {
        return event;
    }
}
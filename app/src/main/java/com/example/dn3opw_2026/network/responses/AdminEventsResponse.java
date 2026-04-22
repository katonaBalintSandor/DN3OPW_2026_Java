package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Event;
import java.util.List;

public class AdminEventsResponse {
    private boolean success;
    private String message;
    private List<Event> events;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Event> getEvents() {
        return events;
    }
}
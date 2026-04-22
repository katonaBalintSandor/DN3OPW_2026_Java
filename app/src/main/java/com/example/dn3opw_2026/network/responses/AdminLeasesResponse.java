package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.AdminLease;
import java.util.List;

public class AdminLeasesResponse {
    private boolean success;
    private String message;
    private List<AdminLease> leases;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<AdminLease> getLeases() {
        return leases;
    }
}
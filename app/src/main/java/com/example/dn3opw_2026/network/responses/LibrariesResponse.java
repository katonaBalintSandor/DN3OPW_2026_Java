package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Library;
import java.util.List;

public class LibrariesResponse {
    private boolean success;
    private List<Library> libraries;

    public boolean isSuccess() {
        return success;
    }

    public List<Library> getLibraries() {
        return libraries;
    }
}
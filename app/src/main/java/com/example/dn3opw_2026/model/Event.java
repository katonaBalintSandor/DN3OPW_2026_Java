package com.example.dn3opw_2026.model;

import java.io.Serializable;

public class Event implements Serializable {

    private int id;
    private String title;
    private String header;
    private String date;
    private String description;
    private String picture;
    private int admin_id;
    private int library_id;
    private String library_name;

    public Event() {
    }

    public Event(int id, String title, String header, String date, String description,
                 String picture, int admin_id, int library_id, String library_name) {
        this.id = id;
        this.title = title;
        this.header = header;
        this.date = date;
        this.description = description;
        this.picture = picture;
        this.admin_id = admin_id;
        this.library_id = library_id;
        this.library_name = library_name;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getHeader() {
        return header;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public int getLibrary_id() {
        return library_id;
    }

    public String getLibrary_name() {
        return library_name;
    }
}
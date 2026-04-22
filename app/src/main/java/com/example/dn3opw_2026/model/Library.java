package com.example.dn3opw_2026.model;

import java.io.Serializable;

public class Library implements Serializable {
    private int id;
    private String name;
    private String city;
    private String picture;
    private int total_books;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getPicture() {
        return picture;
    }

    public int getTotal_books() {
        return total_books;
    }
}
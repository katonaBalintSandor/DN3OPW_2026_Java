package com.example.dn3opw_2026.model;

import com.google.gson.annotations.SerializedName;

public class Book {

    private int id;
    private String title;
    private String author;
    private String picture;
    private String category;
    private int quantity;
    private int library_id;
    private String release_date;
    private String description;

    private int lease_id;
    private String library_name;
    private String leased_date;
    private String returned_date;

    @SerializedName("book_id")
    private int book_id;

    public int getId() {
        return id != 0 ? id : book_id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPicture() {
        return picture;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getLibrary_id() {
        return library_id;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getDescription() {
        return description;
    }

    public int getLease_id() {
        return lease_id;
    }

    public String getLibrary_name() {
        return library_name;
    }

    public String getLeased_date() {
        return leased_date;
    }

    public String getReturned_date() {
        return returned_date;
    }

    public boolean hasQuantity() {
        return quantity >= 0;
    }
}
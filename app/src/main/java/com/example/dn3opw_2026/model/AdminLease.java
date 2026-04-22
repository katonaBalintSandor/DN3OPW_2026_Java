package com.example.dn3opw_2026.model;

public class AdminLease {

    private int lease_id;
    private int book_id;
    private String title;
    private String author;
    private String category;
    private String picture;
    private String username;
    private String leased_date;
    private String returned_date;

    public int getLease_id() {
        return lease_id;
    }

    public int getBook_id() {
        return book_id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getPicture() {
        return picture;
    }

    public String getUsername() {
        return username;
    }

    public String getLeased_date() {
        return leased_date;
    }

    public String getReturned_date() {
        return returned_date;
    }

    public boolean isActiveLease() {
        return returned_date == null || returned_date.trim().isEmpty();
    }
}
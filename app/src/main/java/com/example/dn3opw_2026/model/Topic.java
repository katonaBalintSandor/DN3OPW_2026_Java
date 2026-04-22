package com.example.dn3opw_2026.model;

import java.io.Serializable;

public class Topic implements Serializable {
    private int id;
    private int user_id;
    private int book_id;
    private int library_id;
    private String username;
    private String topic;
    private int rating;
    private String book_title;
    private String book_author;
    private String book_picture;
    private String user_firstname;
    private String user_lastname;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getBook_id() {
        return book_id;
    }

    public int getLibrary_id() {
        return library_id;
    }

    public String getUsername() {
        return username;
    }

    public String getTopic() {
        return topic;
    }

    public int getRating() {
        return rating;
    }

    public String getBook_title() {
        return book_title;
    }

    public String getBook_author() {
        return book_author;
    }

    public String getBook_picture() {
        return book_picture;
    }

    public String getUser_firstname() {
        return user_firstname;
    }

    public String getUser_lastname() {
        return user_lastname;
    }
}
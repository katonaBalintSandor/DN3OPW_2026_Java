package com.example.dn3opw_2026.model;

public class Comment {

    private int id;
    private int user_id;
    private String username;
    private String firstname;
    private String lastname;
    private String comment;
    private String created_at;

    public int getId() {
        return id;
    }
    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getComment() {
        return comment;
    }

    public String getCreated_at() {
        return created_at;
    }
}
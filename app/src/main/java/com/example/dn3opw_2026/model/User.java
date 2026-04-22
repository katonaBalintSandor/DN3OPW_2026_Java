package com.example.dn3opw_2026.model;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    public User() {
    }

    public User(int id, String firstname, String lastname, String username, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
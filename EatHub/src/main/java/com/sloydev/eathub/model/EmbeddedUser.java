package com.sloydev.eathub.model;


import java.io.Serializable;

public class EmbeddedUser implements Serializable {

    String username;
    String display_name;
    String avatar;


    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        return "EmbeddedUser{" +
                "username='" + username + '\'' +
                '}';
    }
}

package com.hihi.mybad.chatapp;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Long on 14/4/2017.
 */

public class User implements Serializable {
    private String id;
    private String image;
    private String email;
    private String name;
    private String password;

    public User(){

    }

    public User(String id,String image, String email, String name, String password) {
        this.id = id;
        this.image = image;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

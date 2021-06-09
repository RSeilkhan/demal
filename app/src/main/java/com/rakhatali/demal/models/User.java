package com.rakhatali.demal.models;

public class User {
    private String id, email, userName, password, imageUrl;
    private int listenedCount, listenedMinutes ;

    public User() {
    }

    public User(String id, String email, String userName, String password, String imageUrl, int listenedCount, int listenedMinutes) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.imageUrl = imageUrl;
        this.listenedCount = listenedCount;
        this.listenedMinutes = listenedMinutes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getListenedCount() {
        return listenedCount;
    }

    public void setListenedCount(int listenedCount) {
        this.listenedCount = listenedCount;
    }

    public int getListenedMinutes() {
        return listenedMinutes;
    }

    public void setListenedMinutes(int listenedMinutes) {
        this.listenedMinutes = listenedMinutes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
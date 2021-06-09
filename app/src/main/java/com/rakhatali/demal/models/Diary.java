package com.rakhatali.demal.models;

import java.util.Date;
import java.util.Date;

public class Diary {
    private String id, userId, title, content;
    private long addedDate;
    private int imageName;
    public Diary() {
    }

    public Diary(String id, String userId, String title, String content, long addedDate, int imageName) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.addedDate = addedDate;
        this.imageName = imageName;
    }

    public int getImageName() {
        return imageName;
    }

    public void setImageName(int imageName) {
        this.imageName = imageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }
}

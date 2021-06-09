package com.rakhatali.demal.models;

import java.util.List;

public class AudioFile {
    String id, name, description, audioUrl, imageUrl;
    int listenCount;
    List<String> categoryId;

    public AudioFile() {
    }

    public AudioFile(String id, String name, String description, String audioUrl, String imageUrl, int listenCount, List<String> categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
        this.listenCount = listenCount;
        this.categoryId = categoryId;
    }



    public int getListenCount() {
        return listenCount;
    }

    public void setListenCount(int listenCount) {
        this.listenCount = listenCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<String> categoryId) {
        this.categoryId = categoryId;
    }
}

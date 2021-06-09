package com.rakhatali.demal.models;

public class Background {
    private String backgroundId, imageUrl, nameBack;

    public Background() {
    }

    public Background(String backgroundId, String imageUrl, String nameBack) {
        this.backgroundId = backgroundId;
        this.imageUrl = imageUrl;
        this.nameBack = nameBack;
    }

    public String getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(String backgroundId) {
        this.backgroundId = backgroundId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNameBack() {
        return nameBack;
    }

    public void setName(String nameBack) {
        this.nameBack = nameBack;
    }
}

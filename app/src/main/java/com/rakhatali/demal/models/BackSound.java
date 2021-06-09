package com.rakhatali.demal.models;

public class BackSound {
    private String backSoundId, musicUrl, name;

    public BackSound() {
    }

    public BackSound(String backSoundId, String musicUrl, String name) {
        this.backSoundId = backSoundId;
        this.musicUrl = musicUrl;
        this.name = name;
    }

    public String getBackSoundId() {
        return backSoundId;
    }

    public void setBackSoundId(String backSoundId) {
        this.backSoundId = backSoundId;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

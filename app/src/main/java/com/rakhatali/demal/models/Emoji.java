package com.rakhatali.demal.models;

public class Emoji {

    private String name;
    private int emojiResource;

    public Emoji(String name, int emojiResource) {
        this.name = name;
        this.emojiResource = emojiResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEmojiResource() {
        return emojiResource;
    }

    public void setEmojiResource(int emojiResource) {
        this.emojiResource = emojiResource;
    }
}

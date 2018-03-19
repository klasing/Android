package com.example.fir_sqlite;

public class FirebaseMessage {
    private String photoUrl;
    private String text;
    private String name;
    private String imageUrl;

    public FirebaseMessage() {
    }

    public FirebaseMessage(String photoUrl, String text, String name, String imageUrl) {
        this.photoUrl = photoUrl;
        this.text = text;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
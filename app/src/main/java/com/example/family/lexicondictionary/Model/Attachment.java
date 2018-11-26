package com.example.family.lexicondictionary.Model;

import java.sql.Blob;

public class Attachment {
    private int id;
    private Blob photo, pronunciation;
    private int wordID;

    public Attachment() {
    }

    public Attachment(int id, Blob photo, Blob pronunciation, int wordID) {
        this.id = id;
        this.photo = photo;
        this.pronunciation = pronunciation;
        this.wordID = wordID;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", photo=" + photo +
                ", pronunciation=" + pronunciation +
                ", wordID=" + wordID +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Blob getPhoto() {
        return photo;
    }

    public void setPhoto(Blob photo) {
        this.photo = photo;
    }

    public Blob getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(Blob pronunciation) {
        this.pronunciation = pronunciation;
    }

    public int getWordID() {
        return wordID;
    }

    public void setWordID(int wordID) {
        this.wordID = wordID;
    }
}

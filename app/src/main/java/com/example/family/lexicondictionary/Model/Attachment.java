package com.example.family.lexicondictionary.Model;

public class Attachment {
    private int id;
    private String photo, pronunciation;
    private int wordID;

    public Attachment() {
    }

    public Attachment(int id, String photo, String pronunciation, int wordID) {
        this.id = id;
        this.photo = photo;
        this.pronunciation = pronunciation;
        this.wordID = wordID;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", photo='" + photo + '\'' +
                ", pronunciation='" + pronunciation + '\'' +
                ", wordID=" + wordID +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public int getWordID() {
        return wordID;
    }

    public void setWordID(int wordID) {
        this.wordID = wordID;
    }
}

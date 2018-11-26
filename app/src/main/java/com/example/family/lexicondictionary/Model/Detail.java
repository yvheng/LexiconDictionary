package com.example.family.lexicondictionary.Model;

public class Detail {
    private int id;
    private double sentiment;
    private String emotion;
    private double accuracy;
    private int wordID;

    public Detail() {
    }

    public Detail(int id, double sentiment, String emotion, double accuracy, int wordID) {
        this.id = id;
        this.sentiment = sentiment;
        this.emotion = emotion;
        this.accuracy = accuracy;
        this.wordID = wordID;
    }

    @Override
    public String toString() {
        return "Detail{" +
                "id=" + id +
                ", sentiment=" + sentiment +
                ", emotion='" + emotion + '\'' +
                ", accuracy=" + accuracy +
                ", wordID=" + wordID +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSentiment() {
        return sentiment;
    }

    public void setSentiment(double sentiment) {
        this.sentiment = sentiment;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public int getWordID() {
        return wordID;
    }

    public void setWordID(int wordID) {
        this.wordID = wordID;
    }
}

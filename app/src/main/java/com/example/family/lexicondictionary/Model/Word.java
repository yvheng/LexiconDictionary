package com.example.family.lexicondictionary.Model;

import java.sql.Date;

public class Word {
    private int id;
    private String originalContent, translatedContent, originalLanguage, translatedLanguage, status;
    private Date dateTimeAdded=null;
    private int userID= 0;

    public Word(int id, String originalContent, String translatedContent,
                String originalLanguage, String translatedLanguage,
                String status, Date dateTimeAdded, int userID) {
        this.id = id;
        this.originalContent = originalContent;
        this.translatedContent = translatedContent;
        this.originalLanguage = originalLanguage;
        this.translatedLanguage = translatedLanguage;
        this.status = status;
        this.dateTimeAdded = dateTimeAdded;
        this.userID = userID;
    }

    public Word() {
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", originalContent='" + originalContent + '\'' +
                ", translatedContent='" + translatedContent + '\'' +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", translatedLanguage='" + translatedLanguage + '\'' +
                ", status='" + status + '\'' +
                ", dateTimeAdded=" + dateTimeAdded +
                ", userID=" + userID +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getTranslatedContent() {
        return translatedContent;
    }

    public void setTranslatedContent(String translatedContent) {
        this.translatedContent = translatedContent;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTranslatedLanguage() {
        return translatedLanguage;
    }

    public void setTranslatedLanguage(String translatedLanguage) {
        this.translatedLanguage = translatedLanguage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateTimeAdded() {
        return dateTimeAdded;
    }

    public void setDateTimeAdded(Date dateTimeAdded) {
        this.dateTimeAdded = dateTimeAdded;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}

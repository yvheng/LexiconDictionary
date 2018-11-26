package com.example.family.lexicondictionary.Model;

public class Word {
    private int id;
    private String content, language, status;

    public Word() {
    }

    public Word(int id, String content, String language, String status) {
        this.id = id;
        this.content = content;
        this.language = language;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", language='" + language + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

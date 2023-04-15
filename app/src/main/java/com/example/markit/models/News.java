package com.example.markit.models;

public class News {
    public String headline;
    public String authour;
    public String source;
    public String url;
    public String publishedAt;

    public News(String headline, String authour, String source, String url, String publishedAt) {
        this.headline = headline;
        this.authour = authour;
        this.source = source;
        this.url = url;
        this.publishedAt = publishedAt;
    }
}

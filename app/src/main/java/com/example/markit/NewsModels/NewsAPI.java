package com.example.markit.NewsModels;

import java.util.List;

public class NewsAPI {

    String status;
    String totalResults;
    List<Article> articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> Articles) {
        this.articles = Articles;
    }
}

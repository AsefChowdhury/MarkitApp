package com.example.markit.utilities;


import android.content.Context;

import com.example.markit.NewsModels.NewsAPI;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class RestApiManager {

    Context context;

    public Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RestApiManager() {
        //this.context = context;
    }

    public interface NewsApiCaller{
        @GET("everything")
        Call<NewsAPI> getArticles(
                @Query("q") String q,
                @Query("apiKey") String api_key
        );
    }
}

package com.example.markit.utilities;

import android.os.AsyncTask;

import com.example.markit.models.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetNewsArticles extends AsyncTask<String, Void, List<News>> {
    @Override
    protected List<News> doInBackground(String... strings) {

        List<News> articles = new ArrayList<>();

        // setup parameters for the url
        Date date = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

        String apiKey = "f667a9a6832b4a0ebe74778321dff8c0";
        String query = strings[0].replaceAll(" ", "%20");
        String from = formater.format(date);
        String sortBy = "popularity";
        String pageSize = "5";

        String param = String.format("q=%s&pageSize=%s&sortBy=%s&apiKey=%s", query, pageSize, sortBy, apiKey).toString();
        String newsUrl = String.format("https://newsapi.org/v2/everything?%s",param).toString();





        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(newsUrl)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();


                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.get("status").toString().equals("ok")) {
                            JSONArray articleArr = jsonObject.getJSONArray("articles");

                            for (int i = 0; i < articleArr.length(); i++) {
                                JSONObject arti = articleArr.getJSONObject(i);


                                String title = arti.getString("title");
                                String author = arti.getString("author");
                                String publishedAt = arti.getString("publishedAt");
                                String source = arti.getJSONObject("source").getString("Name");
                                String url = arti.getString("url");

                                News news = new News(title, author, source, url, publishedAt);
                                articles.add(news);
                            }




                        } else {
                            System.out.println("status not ok");
                            System.out.println(jsonObject);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("couldnt parse json");
                        System.out.println(json);
                    }


                } else {
                    System.out.println("unsucessful response");
                }
            }
        });



        return articles;
    }


}

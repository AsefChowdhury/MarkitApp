package com.example.markit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.markit.R;
import com.example.markit.adapters.NewsAdapter;
import com.example.markit.adapters.StockAdapter;
import com.example.markit.databinding.ActivityStockInformationBinding;
import com.example.markit.listeners.NewsListener;
import com.example.markit.models.News;
import com.example.markit.utilities.GetNewsArticles;
import com.example.markit.utilities.GetStockLogo;
import com.example.markit.utilities.GetStockPrice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StockInformation extends AppCompatActivity implements NewsListener {

    String name = "No name";
    String symbol = "No Symbol";
    String newsUrl = "no url";
    Bitmap image = null;
    String price = null;
    List<News> articles = new ArrayList<>();
    ActivityStockInformationBinding binding;
    NewsAdapter newsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStockInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        name = getIntent().getStringExtra("stockName");
        symbol = getIntent().getStringExtra("stockSymbol");

        setViews();


        newsUrl = getUrl(symbol+" ");
        setListenrs();





    }

    private List<News> parseJson(String json) {
        List<News> articles = new ArrayList<>();
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

        return articles;
    }

    private String getUrl(String item) {
        Date date = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

        String apiKey = "f667a9a6832b4a0ebe74778321dff8c0";
        String query = item.replaceAll(" ", "%20");
        String from = formater.format(date);
        String sortBy = "popularity";

        String param = String.format("q=%s&sortBy=%s&apiKey=%s", query, sortBy, apiKey).toString();
        String newsUrl = String.format("https://newsapi.org/v2/everything?%s",param).toString();

        return newsUrl;
    }

    private void setListenrs() {
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
                    articles = parseJson(json);
                    System.out.println(articles.size());
                    if (articles.size() > 0) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newsAdapter.setArticles(articles);
                                binding.newsRecyclerView.setVisibility(View.VISIBLE);
                            }
                        });


                    }

                } else {
                    System.out.println("unsucessful response");
                }
            }
        });
    }

    private void setViews() {

        GetStockLogo logo = new GetStockLogo();
        logo.execute(symbol);

        try {
            image = logo.get();
        } catch (InterruptedException | ExecutionException e) {
            image = null;
        }

        GetStockPrice symbolPrice = new GetStockPrice();
        symbolPrice.execute(symbol);

        try {
            price = "$"+symbolPrice.get();
        } catch (ExecutionException | InterruptedException e) {
            price = "N/A";
        }

        binding.textName.setText(name);
        binding.textSymbol.setText(symbol);
        binding.imageLogo.setImageBitmap(image);
        binding.textPrice.setText(price);

        newsAdapter = new NewsAdapter(getApplicationContext(), articles, this);
        binding.newsRecyclerView.setAdapter(newsAdapter);



    }

    @Override
    public void onNewsClick(int position) {
        String url = articles.get(position).url;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
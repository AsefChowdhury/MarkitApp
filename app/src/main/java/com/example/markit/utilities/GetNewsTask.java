package com.example.markit.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markit.NewsModels.NewsAPI;
import com.example.markit.adapters.NewsAdapter;
import com.example.markit.listeners.NewsListener;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetNewsTask extends AsyncTask<Void, Void, NewsAPI> {
    private NewsListener newsListener;

    private Context context;
    private String query = "(stocks OR shares)";

    RecyclerView recyclerView;
    public GetNewsTask(NewsListener listener, String query, Context context, RecyclerView recyclerView){
        this.newsListener = listener;
        this.query = "(stocks OR shares) AND " + query;
        this.context = context;
        this.recyclerView = recyclerView;
    }
    public GetNewsTask(NewsListener listener, Context context, RecyclerView recyclerView){
        this.newsListener = listener;
        this.context = context;
        this.recyclerView = recyclerView;

    }
    @Override
    protected NewsAPI doInBackground(Void... voids) {
        RestApiManager restApiManager = new RestApiManager();
        Retrofit retrofit = restApiManager.retrofit;

        RestApiManager.NewsApiCaller newsApiCaller = retrofit.create(RestApiManager.NewsApiCaller.class);
        Call<NewsAPI> call = newsApiCaller.getArticles(this.query, "8a57375a854d4340a519c369659f324a");

        try {
            Response<NewsAPI> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("myTag", "Error: " + response.code());
            }
        } catch (Exception e) {
            Log.e("myTag", "Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(NewsAPI newsAPI) {
        if (newsAPI != null) {
            NewsAdapter newsAdapter = new NewsAdapter(context, newsAPI.getArticles(), newsListener);
            //recyclerView = getView().findViewById(R.id.recycler_main);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
            recyclerView.setAdapter(newsAdapter);
        } else {
            Toast.makeText(context, "Failed to fetch news", Toast.LENGTH_SHORT).show();
        }
    }
}
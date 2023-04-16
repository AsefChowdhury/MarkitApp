package com.example.markit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.markit.NewsModels.Article;
import com.example.markit.databinding.ActivityStockInformationBinding;
import com.example.markit.listeners.NewsListener;
import com.example.markit.utilities.GetNewsTask;
import com.example.markit.utilities.GetStockLogo;
import com.example.markit.utilities.GetStockPrice;

import java.util.concurrent.ExecutionException;

public class StockInformation extends AppCompatActivity implements NewsListener {

    String name = "No name";
    String symbol = "No Symbol";
    Bitmap image = null;
    String price = null;
    ActivityStockInformationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStockInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        name = getIntent().getStringExtra("stockName");
        symbol = getIntent().getStringExtra("stockSymbol");
        setViews();
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
        new GetNewsTask(this, name, this, binding.newsRecyclerView).execute();
        binding.newsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(Article article) {
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse(article.getUrl()));
        startActivity(viewIntent);
    }
}
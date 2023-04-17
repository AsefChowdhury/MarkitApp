package com.example.markit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markit.NewsModels.Article;
import com.example.markit.R;
import com.example.markit.listeners.NewsListener;
import com.example.markit.utilities.CustomViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<CustomViewHolder>{

    private Context context;
    private List<Article> Articles;

    private NewsListener listener;
    public NewsAdapter(Context context, List<Article> Articles, NewsListener listener) {
        this.context = context;
        this.Articles = Articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.news_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.title.setText(Articles.get(position).getTitle());
        holder.source.setText(Articles.get(position).getSource().getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(Articles.get(position));
            }
        });

        if (Articles.get(position).getUrlToImage() != null && !Articles.get(position).getUrlToImage().isEmpty()){
            Picasso.get().load(Articles.get(position).getUrlToImage()).into(holder.image);
        }else{
            holder.image.setImageResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return Articles.size();
    }
}

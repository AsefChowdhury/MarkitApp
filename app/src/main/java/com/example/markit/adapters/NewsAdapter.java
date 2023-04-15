package com.example.markit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markit.R;
import com.example.markit.listeners.NewsListener;
import com.example.markit.models.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    Context context;
    List<News> articles;
    NewsListener newsListener;

    public NewsAdapter(Context context, List<News> articles, NewsListener newsListener) {
        this.newsListener = newsListener;
        this.context = context;
        this.articles = articles;
    }

    public void setArticles(List<News> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.news_recycler_view_row, parent, false);

        return new NewsAdapter.MyViewHolder(view, newsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.MyViewHolder holder, int position) {
        String headline = articles.get(position).headline.trim();
        String author = articles.get(position).authour.trim();
        String source = articles.get(position).source.trim();
        String publishDate = articles.get(position).publishedAt.trim();

        holder.textHeadline.setText(headline);
        holder.textAuthor.setText(author);
        holder.textSource.setText(source);
        holder.textPublish.setText(publishDate);

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.zoom_out_slide));

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textHeadline;
        TextView textAuthor;
        TextView textSource;
        TextView textPublish;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, NewsListener newsListener) {
            super(itemView);

            textHeadline = itemView.findViewById(R.id.textHeadline);
            textAuthor = itemView.findViewById(R.id.textAuthor);
            textSource = itemView.findViewById(R.id.textSource);
            textPublish = itemView.findViewById(R.id.textPublishedDate);
            cardView = itemView.findViewById(R.id.cardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newsListener != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            newsListener.onNewsClick(pos);
                        }
                    }
                }
            });

        }



    }
}


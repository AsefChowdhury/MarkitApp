package com.example.markit.utilities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markit.R;

public class CustomViewHolder extends RecyclerView.ViewHolder{

    public TextView title;
    public TextView source;
    public ImageView image;
    public CardView cardView;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        source = itemView.findViewById(R.id.source);
        image = itemView.findViewById(R.id.image);
        cardView = itemView.findViewById(R.id.main_container);

    }
}

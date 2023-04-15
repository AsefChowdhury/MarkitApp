package com.example.markit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markit.R;
import com.example.markit.listeners.StockListenr;
import com.example.markit.models.Stock;
import com.example.markit.utilities.GetStockLogo;
import com.example.markit.utilities.GetStockPrice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.MyViewHolder> {
    Context context;
    List<Stock> stockList;
    StockListenr stockListenr;

    public StockAdapter(Context context, List<Stock> stockList, StockListenr stockListenr) {
        this.context = context;
        this.stockList = stockList;
        this.stockListenr = stockListenr;

    }

    public void setFilteredList(List<Stock> stockList) {
        this.stockList = stockList;
        notifyDataSetChanged();
    }

    public Stock getItem(int position) {
        return stockList.get(position);
    }


    @NonNull
    @Override
    public StockAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.stock_recycler_view_row, parent, false);

        return new StockAdapter.MyViewHolder(view, stockListenr);
    }

    @Override
    public void onBindViewHolder(@NonNull StockAdapter.MyViewHolder holder, int position) {

        String symbol = stockList.get(position).ticker;
        String name = stockList.get(position).name;



        holder.textSymbol.setText(symbol);
        holder.textName.setText(name);

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.zoom_out_slide));

    }

    @Override
    public int getItemCount() {

        return stockList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textSymbol;
        CardView cardView;


        public MyViewHolder(@NonNull View itemView, StockListenr stockListenr) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textSymbol = itemView.findViewById(R.id.textSymbol);
            cardView = itemView.findViewById(R.id.cardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (stockListenr != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            stockListenr.onItemClick(position);
                        }
                    }


                }
            });
        }

    }


}

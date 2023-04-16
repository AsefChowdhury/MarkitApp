package com.example.markit.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.markit.R;
import com.example.markit.activities.StockInformation;
import com.example.markit.adapters.StockAdapter;
import com.example.markit.listeners.StockListener;
import com.example.markit.models.Stock;
import com.example.markit.utilities.GetStockNameDataTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class FragmentSearch extends Fragment implements StockListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private List<Stock> stockList;
    private GetStockNameDataTask task = new GetStockNameDataTask();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private StockAdapter stockAdapter;


    public FragmentSearch() {
    }

    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView == null) {
            loadList(getView());
        }

        setListeners();

    }

    private void loadList(View view) {
        task.execute();
        try {
            stockList = task.get();
        } catch (InterruptedException | ExecutionException e) {
            stockList = new ArrayList<>();
        }

        if (stockList.size() > 0) {
            recyclerView = view.findViewById(R.id.searchRecyclerView);

            stockAdapter = new StockAdapter(view.getContext(),stockList, this);
            recyclerView.setAdapter(stockAdapter);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    private void setListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
    }

    private void filterList(String newText) {

        List<Stock> filterdList = new ArrayList<>();

        for (Stock stock : stockList) {
            if ((stock.name.toLowerCase().contains(newText.toLowerCase())) || (stock.ticker.toLowerCase().contains(newText.toLowerCase()))) {
                filterdList.add(stock);
            }
        }

        stockAdapter.setFilteredList(filterdList);
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity().getApplicationContext(), StockInformation.class);
        Stock clickedStock = stockAdapter.getItem(position);

        intent.putExtra("stockName", clickedStock.name);
        intent.putExtra("stockSymbol", clickedStock.ticker);


        startActivity(intent);
    }

}
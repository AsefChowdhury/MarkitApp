package com.example.markit.utilities;

import android.os.AsyncTask;

import com.example.markit.models.Stock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetStockNameDataTask extends AsyncTask<String, Void, List<Stock>> {
    @Override
    protected List<Stock> doInBackground(String... strings) {
        String apiKey = "ab0635aab3a8434a8edb5382a666cc92";
        String param = "exchange=NASDAQ";
        String urlString = String.format("https://api.twelvedata.com/stocks?%s&apikey=%s", param, apiKey);

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            List<Stock> stockList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject stock = jsonArray.getJSONObject(i);
                String symbol = stock.getString("symbol");
                String name = stock.getString("name");

               // System.out.println(name + ":" + symbol);

                Stock stockData = new Stock(symbol, name);
                stockList.add(stockData);

            }



            return stockList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<Stock> result) {
        if (result != null) {
            //System.out.println(result);

        } else {
           // System.out.println("error");

        }
    }
}

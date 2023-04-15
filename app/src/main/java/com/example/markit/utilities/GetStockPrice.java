package com.example.markit.utilities;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetStockPrice extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        String apiKey = "ab0635aab3a8434a8edb5382a666cc92";
        String symbol = strings[0];
        String urlString = String.format("https://api.twelvedata.com/price?symbol=%s&apikey=%s", symbol, apiKey);
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            connection.setRequestMethod("GET");


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.has("price")) {
                String price = jsonObject.get("price").toString();
                System.out.println(price);
                return price;
            } else {
                System.out.println(jsonObject);
                return null;
            }




        } catch (Exception e) {
            return null;
        }
    }
}

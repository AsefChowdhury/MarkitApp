package com.example.markit.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetStockLogo extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... strings) {

        String apiKey = "ab0635aab3a8434a8edb5382a666cc92";
        String symbol = strings[0];
        String urlString = String.format("https://api.twelvedata.com/logo?symbol=%s&apikey=%s", symbol, apiKey);

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

            JSONObject jsonObject = new JSONObject(response.toString());
            //jsonObject.get("url");
            if (jsonObject.has("url")) {
                String imageUrl = jsonObject.get("url").toString();
                System.out.println(imageUrl);

                URL imgUrl = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream input = conn.getInputStream();
                Bitmap imageBitmap = BitmapFactory.decodeStream(input);
                input.close();
                conn.disconnect();
                return imageBitmap;

            } else {
                System.out.println(jsonObject);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}

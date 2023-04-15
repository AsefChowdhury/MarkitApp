package com.example.markit.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class PerferenceManager {
    private final SharedPreferences sharedPreferences;

    public PerferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        Boolean value = sharedPreferences.getBoolean(key, false);
        return value;
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void printAll(){
        Map<String, Object> data = (Map<String, Object>) sharedPreferences.getAll();
        if (data.size() == 0) {
            System.out.println("Data is empty");
        } else{
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                System.out.println(key + "-->" + value);
            }
        }

    }

}

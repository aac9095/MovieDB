package adapter;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.ayush.popularmovies.R;

public class Utility {

    public static void setUserChoice(Context context, String choice) {
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(R.string.content_authority), Context.MODE_PRIVATE);
        SharedPreferences.Editor e = preferences.edit();
        e.putString("view", choice);
        e.apply();
    }

    public static String getPreferredChoice(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(R.string.content_authority), Context.MODE_PRIVATE);
        return preferences.getString("view", "popular");
    }

}
package com.example.ayush.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ayush.popularmovies.sync.MoviesSyncAdapter;

public class MainActivity extends AppCompatActivity {
    public static String app_key = "a86956e0e2f98096b78d60b4ba9bcbb4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.app_key),app_key);
        MoviesFragment moviesFragment = new MoviesFragment();
        moviesFragment.setArguments(bundle);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, moviesFragment)
                    .commit();
        }
        MoviesSyncAdapter.initializeSyncAdapter(this);
    }
}

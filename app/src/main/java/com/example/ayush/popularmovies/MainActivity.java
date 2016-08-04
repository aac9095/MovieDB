package com.example.ayush.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.ayush.popularmovies.sync.MoviesSyncAdapter;
import com.facebook.stetho.Stetho;

import adapter.Utility;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback{
    public static String app_key = "a86956e0e2f98096b78d60b4ba9bcbb4";
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mOrder;
    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Stetho.initializeWithDefaults(this);
        mOrder = Utility.getPreferredChoice(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movies_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        if(!mTwoPane)
            getSupportActionBar().setElevation(0f);
        MoviesFragment ff = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);

        //ff.setOnType(!mTwoPane);
        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String order = Utility.getPreferredChoice( this );
        // update the location in our second pane using the fragment manager
        if (order != null && !order.equals(mOrder)) {
            MoviesFragment ff = (MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if ( null != ff ) {
                ff.updateMovies();
            }
            mOrder=order;
        }

    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, movieUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.e("onDestroy","Called");
//        mOrder = Utility.getPreferredChoice(this);
//        if(!mOrder.equals(getString(R.string.popular_tag))) {
//            mOrder = getString(R.string.popular_tag);
//            Utility.setUserChoice(this,mOrder);
//        }
//    }
}
